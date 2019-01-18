package io.pp.net_disk_demo.mvp.presenter;

import io.pp.net_disk_demo.data.ObjectStatus;

public interface ShowStatusPresenter {

    void startStatus(String bucket, String key);


    void showGettingStatus();

    void showGettingStatusError(String errMsg);

    void showStatus(ObjectStatus objectStatus);


    void onDestroy();
}