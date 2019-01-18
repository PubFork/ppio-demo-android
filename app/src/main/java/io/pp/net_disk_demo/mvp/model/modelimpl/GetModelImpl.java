package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;

import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.mvp.model.GetModel;
import io.pp.net_disk_demo.mvp.presenter.GetPresenter;
import io.pp.net_disk_demo.service.ExecuteTaskService;

public class GetModelImpl implements GetModel, ExecuteTaskService.GetListener {

    private static final String TAG = "GetModelImpl";

    private GetPresenter mGetPresenter;
    private ExecuteTaskService mExecuteTaskService;

    private DownloadInfo mDownloadInfo;

    public GetModelImpl(Context context, GetPresenter getPresenter) {
        mGetPresenter = getPresenter;

        mDownloadInfo = new DownloadInfo();
    }

    @Override
    public void onStartingGet() {
        if (mGetPresenter != null) {
            mGetPresenter.showRequestingGet();
        }
    }

    @Override
    public void onGetStartFailed(String errMsg) {
        if (mGetPresenter != null) {
            mGetPresenter.showStartGetFail(errMsg);
        }
    }

    @Override
    public void onGetStartSucceed() {
        if (mGetPresenter != null) {
            mGetPresenter.showStartGetSucceed();
        }
    }


    @Override
    public void bindGetService(ExecuteTaskService executeTaskService) {
        mExecuteTaskService = executeTaskService;

        if (mExecuteTaskService != null) {
            mExecuteTaskService.setGetListener(GetModelImpl.this);
        }
    }


    @Override
    public void setShareCode(String shareCode) {
        mDownloadInfo.setShareCode(shareCode);
    }

    @Override
    public void setChiPrice(int chiPrice) {
        mDownloadInfo.setChiPrice("" + chiPrice);
    }


    @Override
    public void startGet() {
        if (mExecuteTaskService != null) {
            mExecuteTaskService.startGet(mDownloadInfo);
        }
    }

    @Override
    public void onDestroy() {
        mGetPresenter = null;
        mExecuteTaskService = null;
    }
}