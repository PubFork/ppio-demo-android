package io.pp.net_disk_demo.mvp.presenter;

public interface LoadingPresenter {

    void checkHasLogin();

    void showCheckingHasLoginView();


    void showHasLoginView();

    void showNotLoginView();


    void onDestroy();
}