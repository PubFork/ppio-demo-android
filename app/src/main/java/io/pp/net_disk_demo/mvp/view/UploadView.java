package io.pp.net_disk_demo.mvp.view;

public interface UploadView {

    void back();

    void showFileName(String fileName);

    void showSecure(boolean isSecure);

    void showExpiredTime(String expiredTime);

    void showCopies(int copies);

    void showChiPrice(String chiPrice);

    void showSetExpiredTime();

    void showSetCopies();

    void showSetChiPrice();


    void showRequestingUploadView();

    void showUploadFailView(String errMsg);

    void showRequestUploadFinishedView();
}