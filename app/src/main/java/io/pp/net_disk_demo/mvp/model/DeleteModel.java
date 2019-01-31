package io.pp.net_disk_demo.mvp.model;

public interface DeleteModel {

    void delete(String bucket, String key);

    void deleteSilently(String bucket, String key);

    void onDestroy();
}