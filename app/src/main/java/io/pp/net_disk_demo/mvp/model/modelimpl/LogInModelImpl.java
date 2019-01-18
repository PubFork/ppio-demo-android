package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.ref.WeakReference;

import io.pp.net_disk_demo.database.AccountDatabaseManager;
import io.pp.net_disk_demo.mvp.model.LogInModel;
import io.pp.net_disk_demo.mvp.presenter.LogInPresenter;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.ppio.PpioAccountUtil;
import io.pp.net_disk_demo.util.FileUtil;

public class LogInModelImpl implements LogInModel {

    private static final String TAG = "LoginModelImpl";

    private Context mContext;

    private LogInPresenter mLogInPresenter;

    public LogInModelImpl(@NonNull Context context, @NonNull LogInPresenter loginPresenter) {
        mContext = context;
        mLogInPresenter = loginPresenter;
    }

    @Override
    public void logIn(String mnemonic, String password) {
        new LoginAsyncTask(LogInModelImpl.this).execute(mnemonic, password);
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mLogInPresenter = null;
    }

    private Context getContext() {
        return mContext;
    }

    private void showInLogIn() {
        if (mLogInPresenter != null) {
            mLogInPresenter.showInLogIn();
        }
    }

    private void showLogInFail(String errMsg) {
        if (mLogInPresenter != null) {
            mLogInPresenter.showLogInFail(errMsg);
        }
    }

    public void stopShowLogIn() {
        if (mLogInPresenter != null) {
            mLogInPresenter.stopShowInLogIn();
        }
    }

    private void showLogInSucceed() {
        if (mLogInPresenter != null) {
            mLogInPresenter.showLogInSucceed();
        }
    }

    static class LoginAsyncTask extends AsyncTask<String, String, Boolean> {

        private WeakReference<LogInModelImpl> mLogInModelImplWeakReference;

        public LoginAsyncTask(LogInModelImpl logInModelImpl) {
            mLogInModelImplWeakReference = new WeakReference<>(logInModelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mLogInModelImplWeakReference.get() != null) {
                mLogInModelImplWeakReference.get().showInLogIn();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            final String mnemonicStr = params[0];
            final String passwordStr = params[1];
            if (!TextUtils.isEmpty(mnemonicStr)) {
                final String privateKetStr = PpioAccountUtil.generate64PrivateKeyStr(mnemonicStr, passwordStr);
                final String addressStr = PpioAccountUtil.generatePpioAddressStr(privateKetStr);

                boolean loginSucceed = PossUtil.logIn(privateKetStr, new PossUtil.LogInListener() {
                    @Override
                    public void onLogInError(String errMsg) {
                        publishProgress(errMsg);
                    }
                });

                if (loginSucceed) {
                    if (mLogInModelImplWeakReference.get() != null) {
                        Context context = mLogInModelImplWeakReference.get().getContext();
                        if (context != null) {
                            AccountDatabaseManager.recordAccount(context, privateKetStr, mnemonicStr, passwordStr, addressStr);
                        }
                    }

                    PossUtil.setMnemonicStr(mnemonicStr);
                    PossUtil.setPasswordStr(passwordStr);
                    PossUtil.setPrivateKeyStr(privateKetStr);
                    PossUtil.setAddressStr(addressStr);

                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(FileUtil.getLogInRecordFile()), false));
                        writer.write("mnemonic: (" + mnemonicStr + ")" +
                                "\npassword: (" + passwordStr + ")" +
                                "\nprivate key: " + privateKetStr +
                                "\naddress: " + addressStr
                        );
                        writer.close();
                    } catch (Exception e) {
                        Log.e(TAG, "write private key and address in log_in record file error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                Log.e(TAG, "log in end...");
                return loginSucceed;
            } else {
                return false;
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if (mLogInModelImplWeakReference.get() != null) {
                mLogInModelImplWeakReference.get().showLogInFail(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean loginSuccess) {
            super.onPostExecute(loginSuccess);

            if (mLogInModelImplWeakReference.get() != null) {
                mLogInModelImplWeakReference.get().stopShowLogIn();

                if (loginSuccess) {
                    mLogInModelImplWeakReference.get().showLogInSucceed();
                }
            }
        }
    }
}