package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;

import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.mvp.model.GetModel;
import io.pp.net_disk_demo.mvp.presenter.GetPresenter;
import io.pp.net_disk_demo.service.DownloadService;

public class GetModelImpl implements GetModel,
        DownloadService.DownloadSharedListener {

    private static final String TAG = "GetModelImpl";

    private GetPresenter mGetPresenter;
    private DownloadService mDownloadService;

    private DownloadInfo mDownloadInfo;

    public GetModelImpl(Context context, GetPresenter getPresenter) {
        mGetPresenter = getPresenter;

        mDownloadInfo = new DownloadInfo();
    }

    @Override
    public void onStartingDownloadShared() {
        if (mGetPresenter != null) {
            mGetPresenter.showRequestingGet();
        }
    }

    @Override
    public void onDownloadSharedStartFailed(String errMsg) {
        if (mGetPresenter != null) {
            mGetPresenter.showStartGetFail(errMsg);
        }
    }

    @Override
    public void onDownloadSharedStartSucceed() {
        if (mGetPresenter != null) {
            mGetPresenter.showStartGetSucceed();
        }
    }

    @Override
    public void bindDownloadService(DownloadService downloadService) {
        mDownloadService = downloadService;

        if (mDownloadService != null) {
            mDownloadService.setDownloadSharedListener(GetModelImpl.this);
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
        if (mDownloadService != null) {
            mDownloadService.downloadShared(mDownloadInfo);
        }
    }

    @Override
    public void onDestroy() {
        mGetPresenter = null;
        //mExecuteTaskService = null;
        mDownloadService = null;
    }
}