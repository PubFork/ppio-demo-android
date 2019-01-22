package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import io.pp.net_disk_demo.mvp.model.InputPassPhraseModel;
import io.pp.net_disk_demo.mvp.presenter.InputPassPhrasePresenter;
import io.pp.net_disk_demo.ppio.KeyStoreUtil;
import io.pp.net_disk_demo.ppio.PossUtil;

public class InputPassPhraseModelImpl implements InputPassPhraseModel {

    private Context mContext = null;
    private InputPassPhrasePresenter mInputPassPhrasePresenter = null;

    public InputPassPhraseModelImpl(Context context, InputPassPhrasePresenter inputPassPhrasePresenter) {
        mContext = context;
        mInputPassPhrasePresenter = inputPassPhrasePresenter;
    }

    @Override
    public void verifyPassPhrase(String passPhrase) {
        new LogInAsyncTask(InputPassPhraseModelImpl.this).execute(passPhrase);
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mInputPassPhrasePresenter = null;
    }

    private Context getContext() {
        return mContext;
    }

    private void showInLogIn() {
        if (mInputPassPhrasePresenter != null) {
            mInputPassPhrasePresenter.showInLogIn();
        }
    }

    private void stopShowLogIn() {
        if (mInputPassPhrasePresenter != null) {
            mInputPassPhrasePresenter.stopShowInLogIn();
        }
    }

    private void showLogInFail(String errMsg) {
        if (mInputPassPhrasePresenter != null) {
            mInputPassPhrasePresenter.showLogInFail(errMsg);
        }
    }

    private void showLogInSucceed() {
        if (mInputPassPhrasePresenter != null) {
            mInputPassPhrasePresenter.showLogInSucceed();
        }
    }

    static class LogInAsyncTask extends AsyncTask<String, String, Boolean> {

        private WeakReference<InputPassPhraseModelImpl> mLogInModelImplWeakReference;

        public LogInAsyncTask(InputPassPhraseModelImpl InputPassPhraseModelImpl) {
            mLogInModelImplWeakReference = new WeakReference<>(InputPassPhraseModelImpl);
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
            final String keyStoreStr = KeyStoreUtil.autoLogInByKeyStore(mLogInModelImplWeakReference.get().getContext());
            final String passPhrase = params[0];

            if (!KeyStoreUtil.checkKeyStoreAndPassPhrase(keyStoreStr, passPhrase)) {
                publishProgress("keystore or passphrase is wrong");
                return false;
            }

            if (PossUtil.logInFromKeyStore(keyStoreStr, passPhrase, new PossUtil.LogInListener() {
                @Override
                public void onLogInError(final String errMsg) {
                    publishProgress(errMsg);
                }
            })) {
                PossUtil.setPasswordStr(passPhrase);
                return true;
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