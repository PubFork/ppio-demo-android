package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;

import io.pp.net_disk_demo.mvp.model.StartRenewModel;
import io.pp.net_disk_demo.mvp.presenter.StartRenewPresenter;
import io.pp.net_disk_demo.mvp.view.StartRenewView;
import io.pp.net_disk_demo.data.FileInfo;
import io.pp.net_disk_demo.mvp.model.modelimpl.StartRenewModelImpl;

public class StartRenewPresenterImpl implements StartRenewPresenter {

    private Context mContext;
    private StartRenewView mStartRenewView;
    private StartRenewModel mStartRenewModel;

    public StartRenewPresenterImpl(Context context, StartRenewView startRenewView) {
        mContext = null;
        mStartRenewView = startRenewView;
        mStartRenewModel = new StartRenewModelImpl(context, StartRenewPresenterImpl.this);
    }

    @Override
    public void startRenew(String bucket, String key) {
        if (mStartRenewModel != null) {
            mStartRenewModel.startRenew(bucket, key);
        }
    }

    @Override
    public void showStartingRenew() {
        if (mStartRenewView != null) {
            mStartRenewView.showStartingRenewView();
        }
    }

    @Override
    public void showStartRenewError(String errMsg) {
        if (mStartRenewView != null) {
            mStartRenewView.showStartRenewErrorView(errMsg);
        }
    }

    @Override
    public void showRenewView(FileInfo fileInfo) {
        if (mStartRenewView != null) {
            mStartRenewView.showRenewView(fileInfo);
        }
    }

    @Override
    public void onDestroy() {
        if (mStartRenewModel != null) {
            mStartRenewModel = null;
        }

        mContext = null;
        mStartRenewView = null;
    }
}