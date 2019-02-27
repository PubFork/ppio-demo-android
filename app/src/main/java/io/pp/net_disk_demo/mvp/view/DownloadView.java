package io.pp.net_disk_demo.mvp.view;

public interface DownloadView {

    void showRequestingDownloadView();

    void showDownloadFailView(String errMsg);

    void showRequestDownloadFinishedView();
}