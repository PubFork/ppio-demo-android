package io.pp.net_disk_demo.mvp.view;

public interface RegisterView {

    void showRegisterView(String seedPhrase);

    void showConfirmView();

    void showSetPasswordView();

    void showInLogInView();

    void stopShowInLogInView();

    void showLogInSucceedView();

    void showLogInFailView();
}