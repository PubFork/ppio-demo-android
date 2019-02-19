package io.pp.net_disk_demo.mvp.model;

import io.pp.net_disk_demo.service.UploadLogService;

public interface AccountInfoModel {

    void bindUploadService(UploadLogService uploadLogService);

    void requestAddress();

    void requestUsed();

    void requestBalance();

    void requestFund();

    void requestOracleChiPrice();

    void uploadLog(String description);

    void logOut();


    void onDestroy();
}