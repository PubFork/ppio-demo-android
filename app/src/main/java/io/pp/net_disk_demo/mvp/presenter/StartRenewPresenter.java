package io.pp.net_disk_demo.mvp.presenter;

import io.pp.net_disk_demo.data.FileInfo;

public interface StartRenewPresenter {

    void startRenew(String bucket, String key);


    void showStartingRenew();

    void showStartRenewError(String errMsg);

    void showRenewView(FileInfo fileInfo);


    void onDestroy();
}