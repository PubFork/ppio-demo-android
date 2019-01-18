package io.pp.net_disk_demo.mvp.view;

public interface GetView {

    void back();

    void showSetChiPriceView();

    void showRequestingGetView();

    void showGetFailView(final String errMsg);

    void showRequestGetFinishedView();
}