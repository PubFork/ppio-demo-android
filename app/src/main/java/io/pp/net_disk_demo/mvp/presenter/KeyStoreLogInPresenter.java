package io.pp.net_disk_demo.mvp.presenter;

public interface KeyStoreLogInPresenter {

    void logIn(String ketStore, String passPhrase);

    void showInLogIn();

    void stopShowInLogIn();

    void showLogInSucceed();

    void showLogInFail(String failStr);


    void onDestroy();
}
