package io.pp.net_disk_demo.mvp.model.modelimpl;

import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.mvp.model.DownloadModel;
import io.pp.net_disk_demo.mvp.presenter.DownloadPresenter;
import io.pp.net_disk_demo.service.DownloadService;

public class DownloadModelImpl implements DownloadModel,
        DownloadService.DownloadListener {

    private static final String TAG = "DownloadModelImpl";

    private DownloadPresenter mDownloadPresenter = null;
    private DownloadService mDownloadService = null;

    public DownloadModelImpl(DownloadPresenter downloadPresenter) {
        mDownloadPresenter = downloadPresenter;
    }

    @Override
    public void bindDownloadService(DownloadService downloadService) {
        mDownloadService = downloadService;
        mDownloadService.setDownloadListener(DownloadModelImpl.this);
    }

    @Override
    public void download(DownloadInfo downloadInfo) {
        if (mDownloadService != null) {
            mDownloadService.download(downloadInfo);
        }
    }

    @Override
    public void onDestroy() {
        mDownloadService = null;
        mDownloadPresenter = null;
    }

    @Override
    public void onStartingDownload() {
        if (mDownloadPresenter != null) {
            mDownloadPresenter.showRequestingDownload();
        }
    }

    @Override
    public void onDownloadStartSucceed() {
        if (mDownloadPresenter != null) {
            mDownloadPresenter.showStartDownloadSucceed();
        }
    }

    @Override
    public void onDownloadStartFailed(String errMsg) {
        if (mDownloadPresenter != null) {
            mDownloadPresenter.showStartDownloadFail(errMsg);
        }
    }
}
