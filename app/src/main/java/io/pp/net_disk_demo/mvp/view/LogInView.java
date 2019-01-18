package io.pp.net_disk_demo.mvp.view;

public interface LogInView {

    void showSetPasswordView();

    void showInLogInView();

    void stopShowInLogInView();

    void showLogInSucceedView();

    void showLogInFailView(String failStr);

    void showSignUpView();
}