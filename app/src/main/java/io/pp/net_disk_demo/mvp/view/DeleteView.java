package io.pp.net_disk_demo.mvp.view;

import io.pp.net_disk_demo.data.DeletingInfo;

public interface DeleteView {

    void onDeletePrepare();

    void onDeleteError(String errMsg);

    void onDeleteFinish(DeletingInfo deletingInfo);
}