package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;

import io.pp.net_disk_demo.mvp.model.KeyStoreLogInModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.KeyStoreLogInModelImpl;
import io.pp.net_disk_demo.mvp.presenter.KeyStoreLogInPresenter;
import io.pp.net_disk_demo.mvp.view.KeyStoreLogInView;

public class KeyStoreLogInPresenterImpl implements KeyStoreLogInPresenter {

    private static final String TAG = "KeyStoreLogInPresenterImpl";

    private Context mContext;
    private KeyStoreLogInView mKeyStoreLogInView = null;
    private KeyStoreLogInModel mKeyStoreLogInModel = null;

    public KeyStoreLogInPresenterImpl(Context context, KeyStoreLogInView keyStoreLogInView) {
        mContext = context;

        mKeyStoreLogInView = keyStoreLogInView;
        mKeyStoreLogInModel = new KeyStoreLogInModelImpl(context, KeyStoreLogInPresenterImpl.this);
    }

    @Override
    public void logIn(String keyStore, String passPhrase) {
        if (mKeyStoreLogInModel != null) {
            mKeyStoreLogInModel.logIn(keyStore, passPhrase);
        }
    }

    @Override
    public void showInLogIn() {
        if (mKeyStoreLogInView != null) {
            mKeyStoreLogInView.showInLogInView();
        }
    }

    @Override
    public void stopShowInLogIn() {
        if (mKeyStoreLogInView != null) {
            mKeyStoreLogInView.stopShowInLogInView();
        }
    }

    @Override
    public void showLogInSucceed() {
        if (mKeyStoreLogInView != null) {
            mKeyStoreLogInView.showLogInSucceedView();
        }
    }

    @Override
    public void showLogInFail(String failStr) {
        if (mKeyStoreLogInView != null) {
            mKeyStoreLogInView.showLogInSucceedView();
        }
    }

    @Override
    public void onDestroy() {
        if (mKeyStoreLogInModel != null) {
            mKeyStoreLogInModel.onDestroy();
            mKeyStoreLogInModel = null;
        }

        mContext = null;
        mKeyStoreLogInView = null;
    }
}