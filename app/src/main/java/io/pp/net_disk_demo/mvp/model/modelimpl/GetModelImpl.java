package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;

import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.mvp.model.GetModel;
import io.pp.net_disk_demo.mvp.presenter.GetPresenter;
import io.pp.net_disk_demo.service.DownloadService;
import io.pp.net_disk_demo.service.ExecuteTaskService;

public class GetModelImpl implements GetModel,
        //ExecuteTaskService.GetListener,
        DownloadService.DownloadSharedListener {

    private static final String TAG = "GetModelImpl";

    private GetPresenter mGetPresenter;
    //private ExecuteTaskService mExecuteTaskService;
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
    public void bindGetService(ExecuteTaskService executeTaskService) {
//        mExecuteTaskService = executeTaskService;
//
//        if (mExecuteTaskService != null) {
//            //mExecuteTaskService.setGetListener(GetModelImpl.this);
//        }
    }

    @Override
    public void bindDownloadService(DownloadService downloadService) {
        mDownloadService = downloadService;

        if (mDownloadService != null) {
            //mDownloadService.setDownloadListener(GetModelImpl.this);
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
//        if (mExecuteTaskService != null) {
//            mExecuteTaskService.startGet(mDownloadInfo);
//        }

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