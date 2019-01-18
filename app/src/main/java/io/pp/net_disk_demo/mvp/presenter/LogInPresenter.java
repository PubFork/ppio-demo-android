package io.pp.net_disk_demo.mvp.presenter;

public interface LogInPresenter {

    void logIn(String mnemonic, String password);


    void showSetPassword();

    void showInLogIn();

    void stopShowInLogIn();

    void signUp();

    void showLogInSucceed();

    void showLogInFail(String failStr);


    void onDestroy();
}