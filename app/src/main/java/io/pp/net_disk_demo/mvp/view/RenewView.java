package io.pp.net_disk_demo.mvp.view;

import io.pp.net_disk_demo.data.DateInfo;

public interface RenewView {

    void back();

    void showFileName(String fileName);

    void showSecure(boolean isSecure);

    void showExpiredTime(String expiredTime);

    void showCopies(int copies);

    void showChiPrice(String gasPrice);


    void showSetExpiredTime(DateInfo defaultDateInfo);

    void showSetCopies(int defaultCopies);

    void showSetChiPrice(String defaultChiPrice, long fileSize, DateInfo expiredTime, int copies);


    void showRenewingView();

    void showRenewErrorView(String errMsg);

    void showRenewCompleteView();


    void showRequestTotalChiView();

    void showGetTotalChiView(long totalChi);

    void showGetTotalChiFailedView(String errMsg);
}