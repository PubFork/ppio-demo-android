package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.data.OracleChiPrice;
import io.pp.net_disk_demo.database.AccountDatabaseManager;
import io.pp.net_disk_demo.mvp.model.AccountInfoModel;
import io.pp.net_disk_demo.mvp.presenter.AccountInfoPresenter;
import io.pp.net_disk_demo.ppio.KeyStoreUtil;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.ppio.RpcUtil;
import io.pp.net_disk_demo.service.UploadLogService;
import io.pp.net_disk_demo.threadpool.CancelFixedThreadPool;

public class AccountInfoModelImpl implements AccountInfoModel {

    private static final String TAG = "AccountInfoModelImpl";
    private Context mContext;
    private AccountInfoPresenter mAccountInfoPresenter;
    private CancelFixedThreadPool mRequestUsedPool;
    private CancelFixedThreadPool mRequestBalancePool;
    private CancelFixedThreadPool mRequestFundPool;

    private UploadLogService mUploadLogService = null;

    public AccountInfoModelImpl(Context context, AccountInfoPresenter accountInfoPresenter) {
        mContext = context;

        mAccountInfoPresenter = accountInfoPresenter;

        mRequestUsedPool = new CancelFixedThreadPool(1);
        mRequestBalancePool = new CancelFixedThreadPool(1);
        mRequestFundPool = new CancelFixedThreadPool(1);
    }

    @Override
    public void bindUploadService(UploadLogService uploadLogService) {
        mUploadLogService = uploadLogService;
    }

    @Override
    public void requestAddress() {
        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.showAddress(PossUtil.getAccount());
        }
    }

    @Override
    public void requestUsed() {
        //new RequestUsedAsyncTask(AccountInfoModelImpl.this).execute();
        mRequestUsedPool.execute(new RequestUsedRunnable(AccountInfoModelImpl.this));
    }

    @Override
    public void requestBalance() {
        //new RequestBalanceAsyncTask(AccountInfoModelImpl.this).execute();
        mRequestBalancePool.execute(new RequestBalanceRunnable(AccountInfoModelImpl.this));
    }

    @Override
    public void requestFund() {
        //new RequestFundAsyncTask(AccountInfoModelImpl.this).execute();
        mRequestFundPool.execute(new RequestFundRunnable(AccountInfoModelImpl.this));
    }

    @Override
    public void requestOracleChiPrice() {
        new RequestOracleChiPriceAsyncTask(AccountInfoModelImpl.this).execute();
    }

    @Override
    public void uploadLog(String description) {
        if (mUploadLogService != null) {
            mUploadLogService.uploadLog(description);
        }
    }

    @Override
    public void logOut() {
        //
        //new LogOutTask(AccountInfoModelImpl.this).execute();
        new DeleteKeyStoreTask(AccountInfoModelImpl.this).execute();
        //
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mAccountInfoPresenter = null;
    }

    private void showInRequestUsed() {
        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.showInRequestUsed();
        }
    }

    private void showUsed(String used) {
        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.showUsed(used);
        }
    }

    private void showGetUsedFail(String errMsg) {
        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.showGetUsedFail(errMsg);
        }
    }

    private void showInRequestBalance() {
        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.showInRequestBalance();
        }
    }

    private void showBalance(String used) {
        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.showBalance(used);
        }
    }

    private void showGetBalanceFail(String errMsg) {
        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.showGetBalanceFail(errMsg);
        }
    }

    private void showInRequestFund() {
        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.showInRequestFund();
        }
    }

    private void showFund(String used) {
        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.showFund(used);
        }
    }

    private void showGetFundFail(String errMsg) {
        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.showGetFundFail(errMsg);
        }
    }

    private void showOracleChiPrice(String used) {
    }

    private void showOracleChiPriceFail(String errMsg) {
    }

    private void showLogOutPrepare() {
        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.showLogOutPrepare();
        }
    }

    private void showLogOutError(String errMsg) {
        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.showLogOutError(errMsg);
        }
    }

    private void showLogOutFinish() {
        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.showLogOutFinish();
        }
    }

    private Context getContext() {
        return mContext;
    }

    static class RequestUsedAsyncTask extends AsyncTask<String, String, String> {

        final WeakReference<AccountInfoModelImpl> mModelImplWeakReference;

        public RequestUsedAsyncTask(AccountInfoModelImpl modelImpl) {
            mModelImplWeakReference = new WeakReference<>(modelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return PossUtil.getUsed(Constant.Data.DEFAULT_BUCKET, new PossUtil.GetUsedListener() {
                @Override
                public void onGetUsedError(String errMsg) {
                    publishProgress(errMsg);
                }
            });
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showGetUsedFail(values[0]);
            }
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);

            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showUsed(value);
            }
        }
    }

    static class RequestBalanceAsyncTask extends AsyncTask<String, String, String> {

        final WeakReference<AccountInfoModelImpl> mModelImplWeakReference;

        public RequestBalanceAsyncTask(AccountInfoModelImpl modelImpl) {
            mModelImplWeakReference = new WeakReference<>(modelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return RpcUtil.getBalance(new RpcUtil.QueryAccountListener() {
                @Override
                public void onQueryAccountError(String errMsg) {
                    publishProgress(errMsg);
                }
            });
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showGetBalanceFail(values[0]);
            }
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);

            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showBalance(value);
            }
        }
    }

    static class RequestFundAsyncTask extends AsyncTask<String, String, String> {

        final WeakReference<AccountInfoModelImpl> mModelImplWeakReference;

        public RequestFundAsyncTask(AccountInfoModelImpl modelImpl) {
            mModelImplWeakReference = new WeakReference<>(modelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return RpcUtil.getFund(new RpcUtil.QueryAccountListener() {
                @Override
                public void onQueryAccountError(String errMsg) {
                    publishProgress(errMsg);
                }
            });
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showGetFundFail(values[0]);
            }
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);

            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showFund(value);
            }
        }
    }

    static class RequestOracleChiPriceAsyncTask extends AsyncTask<String, String, OracleChiPrice> {

        final WeakReference<AccountInfoModelImpl> mModelImplWeakReference;

        public RequestOracleChiPriceAsyncTask(AccountInfoModelImpl modelImpl) {
            mModelImplWeakReference = new WeakReference<>(modelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected OracleChiPrice doInBackground(String... params) {
            return RpcUtil.oracleChiPrice(new RpcUtil.QueryAccountListener() {
                @Override
                public void onQueryAccountError(String errMsg) {
                    publishProgress(errMsg);
                    Log.e(TAG, "get oracleChiPrice failed");
                }
            });
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(OracleChiPrice value) {
            super.onPostExecute(value);

            if (value != null &&
                    TextUtils.isEmpty(value.getStorageChiPrice()) &&
                    TextUtils.isEmpty(value.getDownloadChiPrice())) {
                PossUtil.setStorageChiPrice(value.getStorageChiPrice());
                PossUtil.setDownloadChiPrice(value.getDownloadChiPrice());
            }
        }
    }

    static class LogOutTask extends AsyncTask<String, String, Boolean> {

        final WeakReference<AccountInfoModelImpl> mModelImplWeakReference;

        public LogOutTask(AccountInfoModelImpl modelImpl) {
            mModelImplWeakReference = new WeakReference<>(modelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (mModelImplWeakReference.get() != null) {
                PossUtil.logOut(new PossUtil.LogOutListener() {
                    @Override
                    public void onLofOutError(String errMsg) {
                        publishProgress(errMsg);
                    }
                });

                AccountDatabaseManager.deleteAccount(mModelImplWeakReference.get().getContext(), new AccountDatabaseManager.LogOutListener() {
                    @Override
                    public void onLogOutFail(String failMessage) {
                        publishProgress(failMessage);
                    }
                });
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showLogOutError(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean value) {
            super.onPostExecute(value);

            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showLogOutFinish();
            }
        }
    }

    static class DeleteKeyStoreTask extends AsyncTask<String, String, Boolean> {

        final WeakReference<AccountInfoModelImpl> mModelImplWeakReference;

        public DeleteKeyStoreTask(AccountInfoModelImpl modelImpl) {
            mModelImplWeakReference = new WeakReference<>(modelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (mModelImplWeakReference.get() != null) {
                PossUtil.logOut(new PossUtil.LogOutListener() {
                    @Override
                    public void onLofOutError(String errMsg) {
                        publishProgress(errMsg);
                    }
                });

                KeyStoreUtil.deleteKeyStore(mModelImplWeakReference.get().getContext());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showLogOutError(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean value) {
            super.onPostExecute(value);

            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showLogOutFinish();
            }
        }
    }

    static class RequestUsedRunnable implements Runnable {
        final WeakReference<AccountInfoModelImpl> mModelImplWeakReference;

        public RequestUsedRunnable(AccountInfoModelImpl accountInfoModelImpl) {
            mModelImplWeakReference = new WeakReference<>(accountInfoModelImpl);
        }

        @Override
        public void run() {
            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showInRequestUsed();
            }

            String used = PossUtil.getUsed(Constant.Data.DEFAULT_BUCKET, new PossUtil.GetUsedListener() {
                @Override
                public void onGetUsedError(String errMsg) {
                    if (mModelImplWeakReference.get() != null) {
                        mModelImplWeakReference.get().showGetUsedFail(errMsg);
                    }
                }
            });

            if (!TextUtils.isEmpty(used) && mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showUsed(used);
            }
        }
    }

    static class RequestBalanceRunnable implements Runnable {
        final WeakReference<AccountInfoModelImpl> mModelImplWeakReference;

        public RequestBalanceRunnable(AccountInfoModelImpl accountInfoModelImpl) {
            mModelImplWeakReference = new WeakReference<>(accountInfoModelImpl);
        }

        @Override
        public void run() {
            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showInRequestBalance();
            }

            String balance = RpcUtil.getBalance(new RpcUtil.QueryAccountListener() {
                @Override
                public void onQueryAccountError(String errMsg) {
                    if (mModelImplWeakReference.get() != null) {
                        mModelImplWeakReference.get().showGetBalanceFail(errMsg);
                    }
                }
            });

            if (!TextUtils.isEmpty(balance) && mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showBalance(balance);
            }
        }
    }

    static class RequestFundRunnable implements Runnable {
        final WeakReference<AccountInfoModelImpl> mModelImplWeakReference;

        public RequestFundRunnable(AccountInfoModelImpl accountInfoModelImpl) {
            mModelImplWeakReference = new WeakReference<>(accountInfoModelImpl);
        }

        @Override
        public void run() {
            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showInRequestFund();
            }

            String fund = RpcUtil.getFund(new RpcUtil.QueryAccountListener() {
                @Override
                public void onQueryAccountError(String errMsg) {
                    if (mModelImplWeakReference.get() != null) {
                        mModelImplWeakReference.get().showGetFundFail(errMsg);
                    }
                }
            });

            if (!TextUtils.isEmpty(fund) && mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showFund(fund);
            }
        }
    }
}