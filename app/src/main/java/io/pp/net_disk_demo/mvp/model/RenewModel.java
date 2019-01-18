package io.pp.net_disk_demo.mvp.model;

import io.pp.net_disk_demo.data.FileInfo;

public interface RenewModel {

    void setRenewFile(FileInfo fileInfo);

    String getFileName();

    boolean isSecure();

    String getExpiredTime();

    int getCopies();

    int getChiPrice();


    void setSecure(boolean secure);

    void setExpiredTime(String expiredTime);

    void setCopies(int copies);

    void setGasPrice(int gasPrice);

    void renew();


    void onDestroy();
}