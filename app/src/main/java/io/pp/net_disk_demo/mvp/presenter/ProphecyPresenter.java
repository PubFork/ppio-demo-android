package io.pp.net_disk_demo.mvp.presenter;

import io.pp.net_disk_demo.data.DateInfo;

public interface ProphecyPresenter {

    void requestStorageChi(int chunkSize, DateInfo expiredTime, String chiPrice);

    void requestDownloadChi(long chunkSize, String chiPrice);

    void requestDownloadShareChi(String shareCode, String chiPrice);

    void showRequestTotalChi();

    void showGetTotalChi(int totalChi);

    void showGetTotalChiFailed(String errMsg);

    void onDestroy();

}