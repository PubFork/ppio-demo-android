package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;

import io.pp.net_disk_demo.mvp.model.InputPassPhraseModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.InputPassPhraseModelImpl;
import io.pp.net_disk_demo.mvp.presenter.InputPassPhrasePresenter;
import io.pp.net_disk_demo.mvp.view.InputPassPhraseView;

public class InputPassPhrasePresenterImpl implements InputPassPhrasePresenter {

    private Context mContext = null;
    private InputPassPhraseView mInputPassPhraseView = null;
    private InputPassPhraseModel mInputPassPhraseModel = null;

    public InputPassPhrasePresenterImpl(Context context, InputPassPhraseView inputPassPhraseView) {
        mContext = context;
        mInputPassPhraseView = inputPassPhraseView;
        mInputPassPhraseModel = new InputPassPhraseModelImpl(context, InputPassPhrasePresenterImpl.this);
    }

    @Override
    public void logIn(String passPhrase) {
        if (mInputPassPhraseModel != null) {
            mInputPassPhraseModel.verifyPassPhrase(passPhrase);
        }
    }

    @Override
    public void showInLogIn() {
        if (mInputPassPhraseView != null) {
            mInputPassPhraseView.showInLogInView();
        }
    }

    @Override
    public void stopShowInLogIn() {
        if (mInputPassPhraseView != null) {
            mInputPassPhraseView.stopShowInLogInView();
        }
    }

    @Override
    public void showLogInFail(String errMsg) {
        if (mInputPassPhraseView != null) {
            mInputPassPhraseView.showLogInFailView(errMsg);
        }
    }

    @Override
    public void showLogInSucceed() {
        if (mInputPassPhraseView != null) {
            mInputPassPhraseView.showLogInSucceedView();
        }
    }

    @Override
    public void onDestroy() {
        if (mInputPassPhraseModel != null) {
            mInputPassPhraseModel.onDestroy();
            mInputPassPhraseModel = null;
        }

        mContext = null;
        mInputPassPhraseView = null;
    }
}