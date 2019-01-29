package io.pp.net_disk_demo.mvp.model;

import io.pp.net_disk_demo.service.DownloadService;
import io.pp.net_disk_demo.service.ExecuteTaskService;

public interface GetModel {

    void bindGetService(ExecuteTaskService executeTaskService);

    void bindDownloadService(DownloadService downloadService);


    void setShareCode(String shareCode);

    void setChiPrice(int chiPrice);


    void startGet();


    void onDestroy();
}
