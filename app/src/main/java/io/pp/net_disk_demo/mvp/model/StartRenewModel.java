package io.pp.net_disk_demo.mvp.model;

public interface StartRenewModel {

    void startRenew(String bucket, String key);


    void onDestroy();
}