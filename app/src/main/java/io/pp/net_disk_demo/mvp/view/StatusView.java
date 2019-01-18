package io.pp.net_disk_demo.mvp.view;

import io.pp.net_disk_demo.data.ObjectStatus;

public interface StatusView {

    void showGettingStatusView();

    void showGettingStatusErrorView(String errMsg);

    void showStatusView(ObjectStatus objectStatus);
}