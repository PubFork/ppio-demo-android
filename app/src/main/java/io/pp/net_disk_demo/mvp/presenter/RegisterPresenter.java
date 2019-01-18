package io.pp.net_disk_demo.mvp.presenter;

public interface RegisterPresenter {

    void registerClick(String mnemonic);

    void generatePrivateKey();

    void showSeedPhrase(String seedPhrase);

    void rememberPrivateKey(String mnemonic);

    void showSetPassword();

    void register(String mnemonic);


    void showInLogIn();

    void stopShowInLogIn();

    void showLogInSucceed();

    void showLogInFail();


    void onDestroy();
}