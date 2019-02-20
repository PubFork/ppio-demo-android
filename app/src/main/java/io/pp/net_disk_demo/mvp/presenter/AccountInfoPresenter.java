package io.pp.net_disk_demo.mvp.presenter;

import io.pp.net_disk_demo.service.UploadLogService;

public interface AccountInfoPresenter {

    void bindUploadLogService(UploadLogService uploadLogService);

    void requestAddress();

    void showAddress(String address);


    void requestUsed();

    void showInRequestUsed();

    void showUsed(String used);

    void showGetUsedFail(String errMsg);


    void requestBalance();

    void showInRequestBalance();

    void showBalance(String balance);

    void showGetBalanceFail(String errMsg);


    void requestFund();

    void showInRequestFund();

    void showFund(String fund);

    void showGetFundFail(String errMsg);


    void requestOracleChiPrice();

    void uploadLog(String description);


    void showRecharge();

    void showRecord();


    void checkVersion();

    void showInCheckVersion();

    void showLatestVersion(String version);

    void showCheckVersionFail(String errMsg);


    void showFeedback();

    void startLogOut();

    void showLogOutPrepare();

    void showLogOutError(String erMsg);

    void showLogOutFinish();


    void onDestroy();
}