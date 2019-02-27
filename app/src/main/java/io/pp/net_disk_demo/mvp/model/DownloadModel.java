package io.pp.net_disk_demo.mvp.model;

import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.service.DownloadService;

public interface DownloadModel {

    void bindDownloadService(DownloadService downloadService);

    void download(DownloadInfo downloadInfo);

    void onDestroy();
}
