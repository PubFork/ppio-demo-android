package io.pp.net_disk_demo.mvp.view;

import io.pp.net_disk_demo.data.FileInfo;

public interface StartRenewView {

    void showStartingRenewView();

    void showStartRenewErrorView(String errMsg);

    void showRenewView(FileInfo fileInfo);
}