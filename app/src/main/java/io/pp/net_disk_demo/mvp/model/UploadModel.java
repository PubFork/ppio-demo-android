package io.pp.net_disk_demo.mvp.model;

import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.service.ExecuteTaskService;

public interface UploadModel {

    void bindService(ExecuteTaskService executeTaskService);

    void setLocalFile(String filePath);

    void setUploadInfo(UploadInfo uploadInfo);

    String getFileName();

    boolean isSecure();

    String getExpiredTime();

    int getCopies();

    String getChiPrice();


    void setSecure(boolean secure);

    void setExpiredTime(String expiredTime);

    void setCopies(int copies);

    void setChiPrice(String chiPrice);

    void upload();


    void onDestroy();
}