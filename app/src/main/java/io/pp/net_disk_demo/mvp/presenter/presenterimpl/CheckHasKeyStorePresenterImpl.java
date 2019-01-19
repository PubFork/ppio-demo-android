package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;

import io.pp.net_disk_demo.mvp.model.CheckHasKeyStoreModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.CheckHasKeyStoreModelImpl;
import io.pp.net_disk_demo.mvp.presenter.CheckHasKeyStorePresenter;
import io.pp.net_disk_demo.mvp.view.CheckHasKeyStoreView;

public class CheckHasKeyStorePresenterImpl implements CheckHasKeyStorePresenter {

    private static final String TAG = "CheckHasKeyStorePresenterImpl";

    private Context mContext = null;
    private CheckHasKeyStoreView mCheckHasKeyStoreView = null;
    private CheckHasKeyStoreModel mCheckHasKeyStoreModel = null;

    public CheckHasKeyStorePresenterImpl(Context context, CheckHasKeyStoreView checkHasKeyStoreView) {
        mContext = context;

        mCheckHasKeyStoreView = checkHasKeyStoreView;
        mCheckHasKeyStoreModel = new CheckHasKeyStoreModelImpl(mContext, CheckHasKeyStorePresenterImpl.this);
    }

    @Override
    public void checkHasKeyStore() {
        if (mCheckHasKeyStoreModel != null) {
            mCheckHasKeyStoreModel.checkHasKeyStore();
        }
    }

    @Override
    public void showCheckingHasKeyStore() {
        if (mCheckHasKeyStoreView != null) {
            mCheckHasKeyStoreView.showCheckingHasKeyStoreView();
        }
    }

    @Override
    public void showCheckHasKeyStoreFail(String errMsg) {
        if (mCheckHasKeyStoreView != null) {
            mCheckHasKeyStoreView.showCheckHasKeyStoreFailView(errMsg);
        }
    }

    @Override
    public void showHasUser() {
        if (mCheckHasKeyStoreView != null) {
            mCheckHasKeyStoreView.showHasUserView();
        }
    }

    @Override
    public void showHasKeyStore() {
        if (mCheckHasKeyStoreView != null) {
            mCheckHasKeyStoreView.showHasKeyStoreView();
        }
    }

    @Override
    public void showNotHasKeyStore() {
        if (mCheckHasKeyStoreView != null) {
            mCheckHasKeyStoreView.showNotHasKeyStoreView();
        }
    }

    @Override
    public void onDestroy() {
        if (mCheckHasKeyStoreModel != null) {
            mCheckHasKeyStoreModel.onDestroy();
            mCheckHasKeyStoreModel = null;
        }

        mContext = null;
        mCheckHasKeyStoreView = null;
    }
}