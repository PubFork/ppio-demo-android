package io.pp.net_disk_demo.mvp.view;

public interface InputPassPhraseView {

    void showInLogInView();

    void stopShowInLogInView();

    void showLogInFailView(String errMsg);

    void showLogInSucceedView();

}