package io.pp.net_disk_demo.mvp.model;

import io.pp.net_disk_demo.service.DownloadService;

public interface GetModel {

    void bindDownloadService(DownloadService downloadService);


    void setShareCode(String shareCode);

    void setChiPrice(int chiPrice);


    void startGet();


    void onDestroy();
}
