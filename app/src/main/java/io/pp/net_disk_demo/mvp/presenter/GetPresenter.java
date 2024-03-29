package io.pp.net_disk_demo.mvp.presenter;

import io.pp.net_disk_demo.service.DownloadService;

public interface GetPresenter {

    void back();

    void bindDownloadService(DownloadService downloadService);

    void setShareCode(String shareCode);

    void setChiPrice(int chiPrice);


    void startGet();


    void showSetChiPrice();

    void showRequestingGet();

    void showStartGetSucceed();

    void showStartGetFail(String errNsg);


    void onDestroy();
}