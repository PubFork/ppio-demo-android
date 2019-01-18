package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;

import io.pp.net_disk_demo.mvp.model.GetStatusModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.GetStatusModelImpl;
import io.pp.net_disk_demo.mvp.presenter.ShowStatusPresenter;
import io.pp.net_disk_demo.mvp.view.StatusView;
import io.pp.net_disk_demo.data.ObjectStatus;

public class ShowStatusPresenterImpl implements ShowStatusPresenter {

    private final String TAG = "ShowDetailPresenterImpl";

    private Context mContext;
    private StatusView mStatusView;
    private GetStatusModel mGetDetailModel;

    public ShowStatusPresenterImpl(Context context, StatusView detailView) {
        mContext = context;
        mStatusView = detailView;
        mGetDetailModel = new GetStatusModelImpl(context, ShowStatusPresenterImpl.this);
    }

    @Override
    public void startStatus(String bucket, String key) {
        if (mGetDetailModel != null) {
            mGetDetailModel.getObjectStatus(bucket, key);
        }
    }

    @Override
    public void showGettingStatus() {
        if (mStatusView != null) {
            mStatusView.showGettingStatusView();
        }
    }

    @Override
    public void showGettingStatusError(String errMsg) {
        if (mStatusView != null) {
            mStatusView.showGettingStatusErrorView(errMsg);
        }
    }

    @Override
    public void showStatus(ObjectStatus objectStatus) {
        if (mStatusView != null) {
            mStatusView.showStatusView(objectStatus);
        }
    }

    @Override
    public void onDestroy() {
        if (mGetDetailModel != null) {
            mGetDetailModel.onDestroy();
            mGetDetailModel = null;
        }

        mContext = null;
        mStatusView = null;
    }
}