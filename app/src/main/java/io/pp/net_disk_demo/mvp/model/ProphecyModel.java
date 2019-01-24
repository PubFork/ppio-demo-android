package io.pp.net_disk_demo.mvp.model;

import io.pp.net_disk_demo.data.DateInfo;

public interface ProphecyModel {

    void requestStorageChi(int chunkSize, DateInfo dateInfo, String chiPrice);

    void requestDownloadChi(long chunkSize, String chiPrice);

    void requestDownloadShareChi(String shareCode, String chiPrice);

    void onDestroy();
}