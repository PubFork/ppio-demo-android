package io.pp.net_disk_demo.mvp.presenter;

import io.pp.net_disk_demo.data.DateInfo;
import io.pp.net_disk_demo.data.FileInfo;

public interface RenewPresenter {

    void back();

    void setRenewFile(FileInfo fileInfo);

    void showRenewSettings();

    void showFileName(String name);

    void showSecure(boolean isSecure);

    void showExpiredTime(String expiredTime);

    void showChiPrice(String chiPrice);

    void showCopies(int copies);

    void showSetExpiredTime();

    void showSetCopies();

    void showSetChiPrice();

    void setSecure(boolean isSecure);

    void setExpiredTime(DateInfo dateInfo);

    void setChiPrice(String chiPrice);

    void setCopies(int copies);


    void renew();


    void showRenewing();

    void showRenewError(String errMsg);

    void renewComplete();


    void onDestroy();
}