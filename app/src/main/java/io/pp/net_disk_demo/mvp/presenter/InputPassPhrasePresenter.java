package io.pp.net_disk_demo.mvp.presenter;

public interface InputPassPhrasePresenter {

    void logIn(String passPhrase);

    void showInLogIn();

    void stopShowInLogIn();

    void showLogInFail(String errMsg);

    void showLogInSucceed();


    void onDestroy();
}