package io.pp.net_disk_demo.mvp.model;

import io.pp.net_disk_demo.data.DateInfo;
import io.pp.net_disk_demo.data.FileInfo;

public interface RenewModel {

    void setRenewFile(FileInfo fileInfo);

    String getFileName();

    boolean isSecure();

    String getExpiredTime();

    long getFileSize();

    DateInfo getDateInfo();

    int getCopies();

    String getChiPrice();


    void setSecure(boolean secure);

    void setExpiredTime(DateInfo dateInfo);

    void setCopies(int copies);

    void setChiPrice(String chiPrice);

    void renew();


    void requestStorageChi();

    void onDestroy();
}