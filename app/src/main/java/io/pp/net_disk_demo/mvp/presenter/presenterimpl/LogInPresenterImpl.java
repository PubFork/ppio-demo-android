package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;

import io.pp.net_disk_demo.mvp.presenter.LogInPresenter;
import io.pp.net_disk_demo.mvp.view.LogInView;
import io.pp.net_disk_demo.mvp.model.LogInModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.LogInModelImpl;

public class LogInPresenterImpl implements LogInPresenter {

    private final String TAG = "LoginPresenterImpl";

    private LogInModel mLogInModel;
    private LogInView mLogInView;

    public LogInPresenterImpl(Context context, LogInView logInView) {
        mLogInView = logInView;

        mLogInModel = new LogInModelImpl(context, LogInPresenterImpl.this);
    }

    @Override
    public void logIn(String mnemonic, String password) {
        if (mLogInModel != null) {
            mLogInModel.logIn(mnemonic, password);
        }
    }


    @Override
    public void showSetPassword() {
        if (mLogInView != null) {
            mLogInView.showSetPasswordView();
        }
    }

    @Override
    public void showInLogIn() {
        if (mLogInView != null) {
            mLogInView.showInLogInView();
        }
    }

    @Override
    public void stopShowInLogIn() {
        if (mLogInView != null) {
            mLogInView.stopShowInLogInView();
        }
    }

    public void signUp() {
        if (mLogInView != null) {
            mLogInView.showSignUpView();
        }
    }

    @Override
    public void showLogInSucceed() {
        if (mLogInView != null) {
            mLogInView.showLogInSucceedView();
        }
    }

    public void showLogInFail(String failStr) {
        if (mLogInView != null) {
            mLogInView.showLogInFailView(failStr);
        }
    }

    @Override
    public void onDestroy() {
        if (mLogInModel != null) {
            mLogInModel.onDestroy();
            mLogInModel = null;
        }

        mLogInView = null;
    }
}