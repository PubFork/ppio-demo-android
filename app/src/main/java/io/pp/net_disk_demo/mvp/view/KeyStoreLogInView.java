package io.pp.net_disk_demo.mvp.view;

public interface KeyStoreLogInView {

    void showInLogInView();

    void stopShowInLogInView();

    void showLogInSucceedView();

    void showLogInFailView(String failStr);

}