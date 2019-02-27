package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.mvp.model.DownloadModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.DownloadModelImpl;
import io.pp.net_disk_demo.mvp.presenter.DownloadPresenter;
import io.pp.net_disk_demo.mvp.view.DownloadView;
import io.pp.net_disk_demo.service.DownloadService;

public class DownloadPresenterImpl implements DownloadPresenter {

    private static final String TAG = "DownloadPresenterImpl";

    private DownloadView mDownloadView = null;
    private DownloadModel mDownloadModel = null;

    public DownloadPresenterImpl(DownloadView downloadView) {
        mDownloadView = downloadView;
        mDownloadModel = new DownloadModelImpl(DownloadPresenterImpl.this);
    }

    @Override
    public void bindDownloadService(DownloadService downloadService) {
        if (mDownloadModel != null) {
            mDownloadModel.bindDownloadService(downloadService);
        }
    }

    @Override
    public void startDownload(DownloadInfo downloadInfo) {
        if (mDownloadModel != null) {
            mDownloadModel.download(downloadInfo);
        }
    }

    @Override
    public void showRequestingDownload() {
        if (mDownloadView != null) {
            mDownloadView.showRequestingDownloadView();
        }
    }

    @Override
    public void showStartDownloadSucceed() {
        if (mDownloadView != null) {
            mDownloadView.showRequestDownloadFinishedView();
        }
    }

    @Override
    public void showStartDownloadFail(String errNsg) {
        if (mDownloadView != null) {
            mDownloadView.showDownloadFailView(errNsg);
        }
    }

    @Override
    public void onDestroy() {
        if (mDownloadModel != null) {
            mDownloadModel.onDestroy();
            mDownloadModel = null;
        }

        mDownloadView = null;
    }
}
