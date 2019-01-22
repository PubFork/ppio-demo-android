package io.pp.net_disk_demo.mvp.presenter;

public interface ScanCodePresenter {

    void decodeBitmapCode(String filePath);


    void showInDecodeBitmapCode();

    void stopShowInDecodeBitmapCode();

    void decodeBitmapCodeFail(String errMsg);

    void decodeBitmapCodeSucceed(String result);


    void onDestroy();
}