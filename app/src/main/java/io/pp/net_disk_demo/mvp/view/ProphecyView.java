package io.pp.net_disk_demo.mvp.view;

public interface ProphecyView {

    void showRequestTotalChiView();

    void showGetTotalChiView(long totalChi);

    void showGetTotalChiFailedView(String errMsg);

}