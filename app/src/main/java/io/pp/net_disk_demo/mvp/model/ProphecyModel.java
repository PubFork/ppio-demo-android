package io.pp.net_disk_demo.mvp.model;

import io.pp.net_disk_demo.data.DateInfo;

public interface ProphecyModel {

    void requestStorageChi(long fileSize, DateInfo dateInfo);

    void requestDownloadChi(long fileSize);

    void requestDownloadShareChi(String shareCode);

    void onDestroy();
}