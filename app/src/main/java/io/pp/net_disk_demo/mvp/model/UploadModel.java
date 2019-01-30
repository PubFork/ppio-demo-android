package io.pp.net_disk_demo.mvp.model;

import io.pp.net_disk_demo.data.DateInfo;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.service.ExecuteTaskService;
import io.pp.net_disk_demo.service.UploadService;

public interface UploadModel {

    void bindService(ExecuteTaskService executeTaskService);

    void bindUploadService(UploadService uploadService);

    void setLocalFile(String filePath);

    void setUploadInfo(UploadInfo uploadInfo);

    String getFilePath();

    String getFileName();

    boolean isSecure();

    String getExpiredTime();

    int getFileSize();

    DateInfo getDateInfo();

    int getCopies();

    String getChiPrice();


    void setSecure(boolean secure);

    void setExpiredTime(DateInfo dateInfo);

    void setCopies(int copies);

    void setChiPrice(String chiPrice);

    void upload();


    void requestStorageChi();

    void onDestroy();
}