package io.pp.net_disk_demo.mvp.view;

public interface RenewView {

    void back();

    void showFileName(String fileName);

    void showSecure(boolean isSecure);

    void showExpiredTime(String expiredTime);

    void showCopies(int copies);

    void showChiPrice(String gasPrice);


    void showSetExpiredTime();

    void showSetCopies();

    void showSetChiPrice();


    void showRenewingView();

    void showRenewErrorView(String errMsg);

    void showRenewCompleteView();
}