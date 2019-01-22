package io.pp.net_disk_demo.mvp.model;

public interface ScanCodeModel {

    void decodeBitmapCode(String filePath);

    void onDestroy();
}