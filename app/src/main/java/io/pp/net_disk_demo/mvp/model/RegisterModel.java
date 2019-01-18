package io.pp.net_disk_demo.mvp.model;

public interface RegisterModel {

    boolean isConfirm();

    void setConfirm();

    void generatePrivateKey();

    void register(String mnemonic, String password);


    void onDestroy();
}