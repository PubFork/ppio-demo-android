package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;

import io.pp.net_disk_demo.mvp.model.LoadingModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.LoadingModelImpl;
import io.pp.net_disk_demo.mvp.view.LoadingView;
import io.pp.net_disk_demo.mvp.presenter.LoadingPresenter;

public class LoadingPresenterImpl implements LoadingPresenter {

    private static final String TAG = "LoadingPresenterImpl";

    private Context mContext;
    private LoadingModel mLoadingModel;
    private LoadingView mLoadingView;

    public LoadingPresenterImpl(Context context, LoadingView loadingView) {
        mContext = context;
        mLoadingView = loadingView;

        mLoadingModel = new LoadingModelImpl(mContext, LoadingPresenterImpl.this);
    }

    @Override
    public void checkHasLogin() {
        if (mLoadingModel != null) {
            mLoadingModel.checkHasLogin();
        }
    }

    @Override
    public void showCheckingHasLoginView() {
        if (mLoadingView != null) {
            mLoadingView.showCheckingHasLogInView();
        }
    }

    @Override
    public void showHasLoginView() {
        if (mLoadingView != null) {
            mLoadingView.showHasLogInView();
        }
    }

    @Override
    public void showNotLoginView() {
        if (mLoadingView != null) {
            mLoadingView.showNotLogInView();
        }
    }

    @Override
    public void onDestroy() {
        if (mLoadingModel != null) {
            mLoadingModel.onDestroy();
            mLoadingModel = null;
        }

        mContext = null;
        mLoadingView = null;
    }
}