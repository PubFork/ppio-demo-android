package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;

import io.pp.net_disk_demo.data.DateInfo;
import io.pp.net_disk_demo.mvp.model.ProphecyModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.ProphecyModelImpl;
import io.pp.net_disk_demo.mvp.presenter.ProphecyPresenter;
import io.pp.net_disk_demo.mvp.view.ProphecyView;

public class ProphecyPresenterImpl implements ProphecyPresenter {

    private Context mContext = null;
    private ProphecyView mProphecyView = null;
    private ProphecyModel mProphecyModel = null;

    public ProphecyPresenterImpl(Context context, ProphecyView prophecyView) {
        mContext = context;
        mProphecyView = prophecyView;

        mProphecyModel = new ProphecyModelImpl(context, ProphecyPresenterImpl.this);
    }

    @Override
    public void requestStorageChi(long fileSize, DateInfo expiredTime) {
        if (mProphecyModel != null) {
            mProphecyModel.requestStorageChi(fileSize, expiredTime);
        }
    }

    @Override
    public void requestDownloadChi(long fileSize) {
        if (mProphecyModel != null) {
            mProphecyModel.requestDownloadChi(fileSize);
        }
    }

    @Override
    public void requestDownloadShareChi(String shareCode) {
        if (mProphecyModel != null) {
            mProphecyModel.requestDownloadShareChi(shareCode);
        }
    }

    @Override
    public void showRequestTotalChi() {
        if (mProphecyView != null) {
            mProphecyView.showRequestTotalChiView();
        }
    }

    @Override
    public void showGetTotalChi(long totalChi) {
        if (mProphecyView != null) {
            mProphecyView.showGetTotalChiView(totalChi);
        }
    }

    @Override
    public void showGetTotalChiFailed(String errMsg) {
        if (mProphecyView != null) {
            mProphecyView.showGetTotalChiFailedView(errMsg);
        }
    }

    @Override
    public void onDestroy() {
        if (mProphecyModel != null) {
            mProphecyModel.onDestroy();
            mProphecyModel = null;
        }

        mContext = null;
        mProphecyView = null;
    }
}