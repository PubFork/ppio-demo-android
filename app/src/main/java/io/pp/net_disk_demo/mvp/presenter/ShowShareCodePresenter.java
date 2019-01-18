package io.pp.net_disk_demo.mvp.presenter;

public interface ShowShareCodePresenter {

    void getShareCode(String bucket, String key);


    void showGettingShareCode();

    void showGettingShareCodeError(String errMsg);

    void showShareCode(String shareCode);


    void onDestroy();
}