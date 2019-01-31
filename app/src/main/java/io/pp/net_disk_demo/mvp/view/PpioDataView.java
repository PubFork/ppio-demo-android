package io.pp.net_disk_demo.mvp.view;

import io.pp.net_disk_demo.data.FileInfo;
import io.pp.net_disk_demo.data.TaskInfo;

import java.util.ArrayList;
import java.util.HashMap;

public interface PpioDataView {

    void showLinkingView();

    void stopShowLinkingView();

    void showLinkFailView(String failMessage);

    void showNotLogInView();


    void showRefreshingAllFileListView();

    void showRefreshAllFileListFailView(String failStr);

    void showAllFileList(HashMap<String, TaskInfo> uploadingTaskHashMap, ArrayList<FileInfo> mMyFileList);

    void showUploadGet();

    void startUpload();

    void startGet();
}