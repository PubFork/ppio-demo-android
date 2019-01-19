package io.pp.net_disk_demo.mvp.presenter;

public interface CheckHasKeyStorePresenter {

    void checkHasKeyStore();

    void showCheckingHasKeyStore();


    void showCheckHasKeyStoreFail(String errMsg);


    void showHasUser();

    void showHasKeyStore();

    void showNotHasKeyStore();


    void onDestroy();

}