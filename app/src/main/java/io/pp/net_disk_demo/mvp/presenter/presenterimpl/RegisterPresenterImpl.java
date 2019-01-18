package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;
import android.text.TextUtils;

import io.pp.net_disk_demo.mvp.presenter.RegisterPresenter;
import io.pp.net_disk_demo.mvp.view.RegisterView;
import io.pp.net_disk_demo.mvp.model.RegisterModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.RegisterModelImpl;

public class RegisterPresenterImpl implements RegisterPresenter {

    private static final String TAG = "RegisterPresenterImpl";

    private Context mContext;
    private RegisterModel mRegisterModel;
    private RegisterView mRegisterView;

    private String mMnemonic;

    public RegisterPresenterImpl(Context context, RegisterView registerView) {
        mRegisterView = registerView;

        mRegisterModel = new RegisterModelImpl(context, RegisterPresenterImpl.this);
    }

    @Override
    public void registerClick(String mnemonic) {
        if (mRegisterModel != null) {
            if (!mRegisterModel.isConfirm()) {
                mRegisterModel.setConfirm();
                rememberPrivateKey(mnemonic);
            } else {
                if (!TextUtils.isEmpty(mMnemonic) && mMnemonic.equals(mnemonic)) {
                    register(mnemonic);
                }

//                if (mRegisterView != null) {
//                    mRegisterView.showSetPasswordView();
//                }
            }
        }
    }

    @Override
    public void generatePrivateKey() {
        if (mRegisterModel != null) {
            mRegisterModel.generatePrivateKey();
        }
    }

    @Override
    public void showSeedPhrase(String seedPhrase) {
        if (mRegisterView != null) {
            mRegisterView.showRegisterView(seedPhrase);
        }
    }

    @Override
    public void rememberPrivateKey(String mnemonic) {
        mMnemonic = mnemonic;

        if (mRegisterView != null) {
            mRegisterView.showConfirmView();
        }
    }

    @Override
    public void showSetPassword() {
        if (mRegisterView != null) {
            mRegisterView.showSetPasswordView();
        }
    }

    @Override
    public void register(String mnemonic) {
        if (mRegisterModel != null) {
            mRegisterModel.register(mnemonic, "");
        }
    }

    @Override
    public void showInLogIn() {
        if (mRegisterView != null) {
            mRegisterView.showInLogInView();
        }
    }

    @Override
    public void stopShowInLogIn() {
        if (mRegisterView != null) {
            mRegisterView.stopShowInLogInView();
        }
    }

    @Override
    public void showLogInSucceed() {
        if (mRegisterView != null) {
            mRegisterView.showLogInSucceedView();
        }
    }

    @Override
    public void showLogInFail() {
        if (mRegisterView != null) {
            mRegisterView.showLogInFailView();
        }
    }

    @Override
    public void onDestroy() {
        if (mRegisterModel != null) {
            mRegisterModel.onDestroy();
        }

        mContext = null;
        mRegisterView = null;
    }
}