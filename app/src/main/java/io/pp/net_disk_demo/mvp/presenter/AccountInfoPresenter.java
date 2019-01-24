package io.pp.net_disk_demo.mvp.presenter;

public interface AccountInfoPresenter {

    void requestAddress();

    void showAddress(String address);


    void requestUsed();

    void showUsed(String used);

    void showGetUsedFail(String errMsg);


    void requestBalance();

    void showBalance(String balance);

    void showGetBalanceFail(String errMsg);


    void requestFund();

    void showFund(String fund);

    void showGetFundFail(String errMsg);


    void requestOracleChiPrice();


    void showRecharge();

    void showRecord();

    void showCheckVersion();

    void showFeedback();

    void startLogOut();

    void showLogOutPrepare();

    void showLogOutError(String erMsg);

    void showLogOutFinish();


    void onDestroy();
}