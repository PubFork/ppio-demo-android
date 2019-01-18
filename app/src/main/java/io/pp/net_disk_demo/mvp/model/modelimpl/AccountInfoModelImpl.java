package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.database.AccountDatabaseManager;
import io.pp.net_disk_demo.mvp.model.AccountInfoModel;
import io.pp.net_disk_demo.mvp.presenter.AccountInfoPresenter;
import io.pp.net_disk_demo.ppio.KeyStoreUtil;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.ppio.RpcUtil;

public class AccountInfoModelImpl implements AccountInfoModel {

    private static final String TAG = "AccountInfoModelImpl";
    private Context mContext;
    private AccountInfoPresenter mAccountInfoPresenter;

    public AccountInfoModelImpl(Context context, AccountInfoPresenter accountInfoPresenter) {
        mContext = context;

        mAccountInfoPresenter = accountInfoPresenter;
    }

    @Override
    public void requestAddress() {
        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.showAddress(PossUtil.getAccount());
        }
    }

    @Override
    public void requestUsed() {
        new RequestUsedAsyncTask(AccountInfoModelImpl.this).execute();
    }

    @Override
    public void requestBalance() {
        new RequestBalanceAsyncTask(AccountInfoModelImpl.this).execute();
    }

    @Override
    public void requestFund() {
        new RequestFundAsyncTask(AccountInfoModelImpl.this).execute();
    }

    @Override
    public void logOut() {
        //
        //new LogOutTask(AccountInfoModelImpl.this).execute();
        new DeleteKeySytoreTask(AccountInfoModelImpl.this).execute();
        //
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mAccountInfoPresenter = null;
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
            return RpcUtil.getFunds(new RpcUtil.QueryAccountListener() {
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

    static class DeleteKeySytoreTask extends AsyncTask<String, String, Boolean> {

        final WeakReference<AccountInfoModelImpl> mModelImplWeakReference;

        public DeleteKeySytoreTask(AccountInfoModelImpl modelImpl) {
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
}