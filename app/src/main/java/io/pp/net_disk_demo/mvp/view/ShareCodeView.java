package io.pp.net_disk_demo.mvp.view;

public interface ShareCodeView {

    void showGettingShareCodeView();

    void showGettingShareCodeErrorView(String errMsg);

    void showShareCode(String shareCode);
}