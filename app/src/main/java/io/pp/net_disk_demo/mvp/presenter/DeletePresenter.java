package io.pp.net_disk_demo.mvp.presenter;

import io.pp.net_disk_demo.data.DeletingInfo;

public interface DeletePresenter {

    void delete(String bucket, String key);


    void onDeletePrepare();

    void onDeleteError(String errMsg);

    void onDeleteFinish(DeletingInfo deletingInfo);


    void onDestroy();
}