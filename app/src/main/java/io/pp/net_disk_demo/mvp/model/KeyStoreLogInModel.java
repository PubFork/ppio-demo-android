package io.pp.net_disk_demo.mvp.model;

public interface KeyStoreLogInModel {

    void logIn(String keyStore, String passPhrase);

    void onDestroy();
}
