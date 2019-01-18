package io.pp.net_disk_demo.mvp.view;

public interface DeleteView {

    void onDeletePrepare();

    void onDeleteError(String errMsg);

    void onDeleteFinish();
}