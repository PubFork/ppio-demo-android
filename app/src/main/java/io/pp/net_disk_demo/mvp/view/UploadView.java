package io.pp.net_disk_demo.mvp.view;

import io.pp.net_disk_demo.data.DateInfo;

public interface UploadView {

    void back();

    void showFileName(String fileName);

    void showSecure(boolean isSecure);

    void showExpiredTime(String expiredTime);

    void showCopies(int copies);

    void showChiPrice(String chiPrice);

    void showSetExpiredTime(DateInfo defaultDateInfo);

    void showSetCopies(int defaultCopies);

    void showSetChiPrice(String defaultChiPrice, long fileSize, DateInfo expiredTime, int copies);


    void showRequestingUploadView();

    void showUploadFailView(String errMsg);

    void showRequestUploadFinishedView();


    void showRequestTotalChiView();

    void showGetTotalChiView(int totalChi);

    void showGetTotalChiFailedView(String errMsg);
}