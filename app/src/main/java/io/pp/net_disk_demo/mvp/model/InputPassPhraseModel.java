package io.pp.net_disk_demo.mvp.model;

public interface InputPassPhraseModel {

    void verifyPassPhrase(String passPhrase);

    void onDestroy();
}