package io.pp.net_disk_demo.mvp.view;

public interface CheckHasKeyStoreView {

    void showCheckingHasKeyStoreView();

    void showCheckHasKeyStoreFailView(String errMsg);

    void showHasKeyStoreView();

    void showNotHasKeyStoreView();

}