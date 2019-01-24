package io.pp.net_disk_demo.mvp.view;

public interface ProphecyView {

    void showRequestTotalChiView();

    void showGetTotalChiView(int totalChi);

    void showGetTotalChiFailedView(String errMsg);

}