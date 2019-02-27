package io.pp.net_disk_demo.mvp.presenter;

import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.service.DownloadService;

public interface DownloadPresenter {

    void bindDownloadService(DownloadService downloadService);

    void startDownload(DownloadInfo downloadInfo);

    void showRequestingDownload();

    void showStartDownloadSucceed();

    void showStartDownloadFail(String errNsg);

    void onDestroy();

}