package io.pp.net_disk_demo.mvp.presenter;

public interface DeletePresenter {

    void delete(String bucket, String key);


    void onDeletePrepare();

    void onDeleteError(String errMsg);

    void onDeleteFinish();


    void onDestroy();
}