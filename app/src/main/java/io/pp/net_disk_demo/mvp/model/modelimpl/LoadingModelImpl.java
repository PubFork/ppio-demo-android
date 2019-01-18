package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.database.AccountDatabaseManager;
import io.pp.net_disk_demo.mvp.model.LoadingModel;
import io.pp.net_disk_demo.mvp.presenter.LoadingPresenter;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.util.FileUtil;

public class LoadingModelImpl implements LoadingModel {

    private static final String TAG = "LoadingModelImpl";

    private Context mContext;
    private LoadingPresenter mLoadingPresenter;

    public LoadingModelImpl(Context context, LoadingPresenter loadingPresenter) {
        mContext = context;
        mLoadingPresenter = loadingPresenter;
    }

    @Override
    public void checkHasLogin() {
        new CheckHasLoginTask(LoadingModelImpl.this).execute();
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mLoadingPresenter = null;
    }

    private Context getContext() {
        if (mContext != null) {
            return mContext;
        } else {
            return null;
        }
    }

    private void showHasLogin() {
        if (mLoadingPresenter != null) {
            mLoadingPresenter.showHasLoginView();
        }
    }

    private void showNotLogin() {
        if (mLoadingPresenter != null) {
            mLoadingPresenter.showNotLoginView();
        }
    }

    static class CheckHasLoginTask extends AsyncTask<String, String, Boolean> {

        final private WeakReference<LoadingModelImpl> mLoadingModelImplWeakReference;

        public CheckHasLoginTask(LoadingModelImpl loadingModelImpl) {
            mLoadingModelImplWeakReference = new WeakReference<>(loadingModelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            Context context = null;
            if (mLoadingModelImplWeakReference.get() != null) {
                context = mLoadingModelImplWeakReference.get().getContext();
            }

            if (context != null) {
                boolean hasLogin;
                boolean loginSucceed = true;
                hasLogin = AccountDatabaseManager.hasLogin(context,
                        new AccountDatabaseManager.CheckHasLoginListener() {
                            @Override
                            public void onCheckFail(String failMessage) {
                                publishProgress("database", failMessage);
                            }
                        });

                if (hasLogin) {
                    if (PossUtil.getUser() == null) {
                        HashMap<String, String> privateParams = AccountDatabaseManager.getPrivateParams(context);
                        PossUtil.setMnemonicStr(privateParams.get(Constant.Data.MNEMONIC));
                        PossUtil.setPasswordStr(privateParams.get(Constant.Data.PASSWORD));
                        PossUtil.setPrivateKeyStr(privateParams.get(Constant.Data.PRIVATE_KEY));
                        PossUtil.setAddressStr(privateParams.get(Constant.Data.ADDRESS));

                        loginSucceed = PossUtil.logIn(PossUtil.getPrivateKeyStr(),
                                new PossUtil.LogInListener() {
                                    @Override
                                    public void onLogInError(String errMsg) {
                                        publishProgress("autoLogin", errMsg);
                                    }
                                });

                        try {
                            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(FileUtil.getLogInRecordFile()), false));
                            writer.write("mnemonic: (" + PossUtil.getMnemonicStr() + ")" +
                                    "\npassword: " + PossUtil.getPasswordStr() +
                                    "\nprivate key: " + PossUtil.getAccountKey() +
                                    "\naddress: " + PossUtil.getAccount()
                            );
                            writer.close();
                        } catch (Exception e) {
                            Log.e(TAG, "write private key and address in log_in record file error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }

                return hasLogin && loginSucceed;
            } else {
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (mLoadingModelImplWeakReference.get() != null) {
                if (aBoolean) {
                    mLoadingModelImplWeakReference.get().showHasLogin();
                } else {
                    mLoadingModelImplWeakReference.get().showNotLogin();
                }
            }
        }
    }
}