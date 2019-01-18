package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.ref.WeakReference;

import io.pp.net_disk_demo.mvp.model.KeyStoreLogInModel;
import io.pp.net_disk_demo.mvp.presenter.KeyStoreLogInPresenter;
import io.pp.net_disk_demo.ppio.KeyStoreUtil;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.ppio.PpioAccountUtil;
import io.pp.net_disk_demo.util.FileUtil;

public class KeyStoreLogInModelImpl implements KeyStoreLogInModel {

    private static final String TAG = "KeyStoreLogInModelImpl";

    private Context mContext = null;
    private KeyStoreLogInPresenter mKeyStoreLogInPresenter = null;

    public KeyStoreLogInModelImpl(Context context, KeyStoreLogInPresenter keyStoreLogInPresenter) {
        mContext = context;

        mKeyStoreLogInPresenter = keyStoreLogInPresenter;
    }

    @Override
    public void logIn(String keyStore, String passPhrase) {
        new LoginAsyncTask(KeyStoreLogInModelImpl.this).execute(keyStore, passPhrase);
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mKeyStoreLogInPresenter = null;
    }

    private Context getContext() {
        return mContext;
    }

    private void showInLogIn() {
        if (mKeyStoreLogInPresenter != null) {
            mKeyStoreLogInPresenter.showInLogIn();
        }
    }

    private void showLogInFail(String errMsg) {
        if (mKeyStoreLogInPresenter != null) {
            mKeyStoreLogInPresenter.showLogInFail(errMsg);
        }
    }

    public void stopShowLogIn() {
        if (mKeyStoreLogInPresenter != null) {
            mKeyStoreLogInPresenter.stopShowInLogIn();
        }
    }

    private void showLogInSucceed() {
        if (mKeyStoreLogInPresenter != null) {
            mKeyStoreLogInPresenter.showLogInSucceed();
        }
    }

    static class LoginAsyncTask extends AsyncTask<String, String, Boolean> {

        private WeakReference<KeyStoreLogInModelImpl> mLogInModelImplWeakReference;

        public LoginAsyncTask(KeyStoreLogInModelImpl keyStoreLogInModelImpl) {
            mLogInModelImplWeakReference = new WeakReference<>(keyStoreLogInModelImpl);
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
            final String keyStoreStr = params[0];
            final String passPhrase = params[1];
            if (!TextUtils.isEmpty(keyStoreStr) && !TextUtils.isEmpty(passPhrase)) {

                final String privateKeyStr = KeyStoreUtil.logInByKeyStore(keyStoreStr, passPhrase);
                final String addressStr = PpioAccountUtil.generatePpioAddressStr(privateKeyStr);

                boolean loginSucceed = PossUtil.logIn(privateKeyStr, new PossUtil.LogInListener() {
                    @Override
                    public void onLogInError(String errMsg) {
                        publishProgress(errMsg);
                    }
                });

                if (loginSucceed) {
                    if (mLogInModelImplWeakReference.get().getContext() != null) {
                        KeyStoreUtil.rememberKeyStore(mLogInModelImplWeakReference.get().getContext(),
                                privateKeyStr, passPhrase);
                        KeyStoreUtil.exportKeyStoreFile(privateKeyStr, passPhrase);
                    }

                    PossUtil.setPasswordStr(passPhrase);
                    PossUtil.setPrivateKeyStr(privateKeyStr);
                    PossUtil.setAddressStr(addressStr);

                    Log.e(TAG, "log in end...");
                    return true;
                } else {
                    return false;
                }
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