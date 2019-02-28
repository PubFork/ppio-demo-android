package io.pp.net_disk_demo.mvp.presenter;

import io.pp.net_disk_demo.data.DateInfo;

public interface ProphecyPresenter {

    void requestStorageChi(long fileSize, DateInfo expiredTime);

    void requestDownloadChi(long fileSize);

    void requestDownloadShareChi(String shareCode);

    void showRequestTotalChi();

    void showGetTotalChi(long totalChi);

    void showGetTotalChiFailed(String errMsg);

    void onDestroy();

}