package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.ref.WeakReference;

import io.pp.net_disk_demo.database.AccountDatabaseManager;
import io.pp.net_disk_demo.mvp.model.RegisterModel;
import io.pp.net_disk_demo.mvp.presenter.RegisterPresenter;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.ppio.PpioAccountUtil;
import io.pp.net_disk_demo.util.FileUtil;

public class RegisterModelImpl implements RegisterModel {

    private static final String TAG = "RegisterModelImpl";

    private Context mContext;

    private RegisterPresenter mRegisterPresenter;

    private boolean mIsConfirm = false;

    public RegisterModelImpl(Context context, RegisterPresenter loginPresenter) {
        mContext = context;
        mRegisterPresenter = loginPresenter;
    }

    @Override
    public boolean isConfirm() {
        return mIsConfirm;
    }

    public void setConfirm() {
        mIsConfirm = true;
    }

    @Override
    public void generatePrivateKey() {
        if (mRegisterPresenter != null) {
            mRegisterPresenter.showSeedPhrase(PpioAccountUtil.generateMnemonics());
        }
    }

    @Override
    public void register(String mnemonic, String password) {
        new RegisterTask(RegisterModelImpl.this).execute(mnemonic, password);
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mRegisterPresenter = null;
    }

    private Context getContext() {
        return mContext;
    }

    private void showInLogIn() {
        if (mRegisterPresenter != null) {
            mRegisterPresenter.showInLogIn();
        }
    }

    private void showLogInFail() {
        if (mRegisterPresenter != null) {
            mRegisterPresenter.showLogInFail();
        }
    }

    private void stopShowLogIn() {
        if (mRegisterPresenter != null) {
            mRegisterPresenter.stopShowInLogIn();
        }
    }

    public void showLogInSucceed() {
        if (mRegisterPresenter != null) {
            mRegisterPresenter.showLogInSucceed();
        }
    }

    static class RegisterTask extends AsyncTask<String, String, Boolean> {

        private WeakReference<RegisterModelImpl> mRegisterModelImplWeakReference;

        public RegisterTask(RegisterModelImpl registerModelImpl) {
            mRegisterModelImplWeakReference = new WeakReference<>(registerModelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mRegisterModelImplWeakReference.get() != null) {
                mRegisterModelImplWeakReference.get().showInLogIn();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String mnemonicStr = params[0];
            final String passwordStr = params[1];
            if (!TextUtils.isEmpty(mnemonicStr)) {
                final String privateKeyStr = PpioAccountUtil.generate64PrivateKeyStr(mnemonicStr, passwordStr);
                final String addressStr = PpioAccountUtil.generatePpioAddressStr(privateKeyStr);

                boolean loginSucceed = PossUtil.logIn(privateKeyStr, new PossUtil.LogInListener() {
                    @Override
                    public void onLogInError(String errMsg) {
                        publishProgress(errMsg);
                    }
                });

                if (loginSucceed) {
                    if (mRegisterModelImplWeakReference.get() != null) {
                        Context context = mRegisterModelImplWeakReference.get().getContext();
                        if (context != null) {
                            AccountDatabaseManager.recordAccount(context, privateKeyStr, mnemonicStr, "", addressStr);
                        }

                        PossUtil.setMnemonicStr(mnemonicStr);
                        PossUtil.setPasswordStr(passwordStr);
                        PossUtil.setPrivateKeyStr(privateKeyStr);
                        PossUtil.setAddressStr(addressStr);

                        try {
                            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(FileUtil.getRegisterRecordFile()), false));
                            writer.write("mnemonic: (" + mnemonicStr + ")" +
                                    "\npassword: (" + passwordStr + ")" +
                                    "\nprivate key: " + privateKeyStr +
                                    "\naddress: " + addressStr
                            );
                            writer.close();
                        } catch (Exception e) {
                            Log.e(TAG, "write private key and address in register record txt file error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }

                return loginSucceed;
            } else {
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if (mRegisterModelImplWeakReference.get() != null) {
                mRegisterModelImplWeakReference.get().stopShowLogIn();
            }
        }

        @Override
        protected void onPostExecute(Boolean loginSucceed) {
            super.onPostExecute(loginSucceed);

            if (mRegisterModelImplWeakReference.get() != null) {
                mRegisterModelImplWeakReference.get().stopShowLogIn();

                if (loginSucceed) {
                    mRegisterModelImplWeakReference.get().showLogInSucceed();
                } else {
                    mRegisterModelImplWeakReference.get().showLogInFail();
                }
            }
        }
    }
}