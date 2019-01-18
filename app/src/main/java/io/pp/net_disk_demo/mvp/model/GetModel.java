package io.pp.net_disk_demo.mvp.model;

import io.pp.net_disk_demo.service.ExecuteTaskService;

public interface GetModel {

    void bindGetService(ExecuteTaskService executeTaskService);


    void setShareCode(String shareCode);

    void setChiPrice(int chiPrice);


    void startGet();


    void onDestroy();
}
