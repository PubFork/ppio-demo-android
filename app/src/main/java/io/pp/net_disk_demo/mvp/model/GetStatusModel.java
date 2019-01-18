package io.pp.net_disk_demo.mvp.model;

public interface GetStatusModel {

    void getObjectStatus(String bucket, String key);


    void onDestroy();
}