package io.pp.net_disk_demo.mvp.model;

public interface GetShareCodeModel {

    void getShareCode(String bucket, String key);


    void onDestroy();
}