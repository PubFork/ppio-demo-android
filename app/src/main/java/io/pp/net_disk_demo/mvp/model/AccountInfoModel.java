package io.pp.net_disk_demo.mvp.model;

public interface AccountInfoModel {

    void requestAddress();

    void requestUsed();

    void requestBalance();

    void requestFund();

    void requestOracleChiPrice();

    void logOut();


    void onDestroy();
}