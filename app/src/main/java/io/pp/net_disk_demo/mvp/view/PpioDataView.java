package io.pp.net_disk_demo.mvp.view;

import io.pp.net_disk_demo.data.FileInfo;

import java.util.ArrayList;

public interface PpioDataView {

    void showLinkingView();

    void stopShowLinkingView();

    void showLinkFailView(String failMessage);

    void showNotLogInView();


    void showRefreshingAllFileListView();

    void showRefreshAllFileListFailView(String failStr);

    void showAllFileList(ArrayList<FileInfo> mMyFileList);


    void showUploadGet();

    void startUpload();

    void startGet();
}