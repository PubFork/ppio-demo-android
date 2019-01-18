package io.pp.net_disk_demo.mvp.presenter;

import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.service.ExecuteTaskService;

public interface UploadPresenter {

    void back();

    void bindService(ExecuteTaskService executeTaskService);

    void generateUploadModel();

    void setLocalFile(String filePath);

    void setUploadInfo(UploadInfo uploadInfo);

    void showUploadSettings();

    void showFileName(String name);

    void showSecure(boolean isSecure);

    void showExpiredTime(String expiredTime);

    void showChiPrice(String chiPrice);

    void showCopies(int copies);

    void showSetExpiredTime();

    void showSetCopies();

    void showSetChiPrice();

    void setSecure(boolean isSecure);

    void setExpiredTime(String expiredTime);

    void setCopies(int copies);

    void setChiPrice(String chiPrice);


    void confirm();


    void showRequestingUpload();

    void showStartUploadSucceed();

    void showStartUploadFail(String errNsg);


    void onDestroy();
}