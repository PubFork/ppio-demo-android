package io.pp.net_disk_demo.mvp.model;

public interface LogInModel {

    void logIn(String mnemonic, String password);


    void onDestroy();
}