package io.pp.net_disk_demo.mvp.presenter;

import io.pp.net_disk_demo.data.DateInfo;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.service.ExecuteTaskService;
import io.pp.net_disk_demo.service.UploadService;

public interface UploadPresenter {

    void back();

    void bindService(ExecuteTaskService executeTaskService);

    void bindUploadService(UploadService uploadService);

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

    void setExpiredTime(DateInfo dateInfo);

    void setCopies(int copies);

    void setChiPrice(String chiPrice);


    void confirm();


    void showRequestingUpload();

    void showStartUploadSucceed();

    void showStartUploadFail(String errNsg);


    void requestStorageChi();


    void showRequestTotalChi();

    void showGetTotalChi(int totalChi);

    void showGetTotalChiFailed(String errMsg);


    int getCopies();

    String getChiPrice();


    void onDestroy();
}