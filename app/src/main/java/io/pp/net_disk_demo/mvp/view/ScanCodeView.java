package io.pp.net_disk_demo.mvp.view;

public interface ScanCodeView {

    void showInDecodeBitmapCodeView();

    void stopShowInDecodeBitmapCodeView();

    void showDecodeBitmapCodeFailedView(String errMsg);

    void showDecodeBitmapCodeSucceedView(String result);

}