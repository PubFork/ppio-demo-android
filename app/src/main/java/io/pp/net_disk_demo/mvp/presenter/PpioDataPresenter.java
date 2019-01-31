package io.pp.net_disk_demo.mvp.presenter;

import io.pp.net_disk_demo.data.FileInfo;
import io.pp.net_disk_demo.data.TaskInfo;

import java.util.ArrayList;
import java.util.HashMap;

public interface PpioDataPresenter {

    void link();

    void showLinking();

    void stopShowLinking();

    void showLinkFail(String failMessage);

    void showNotLogIn();


    void refreshAllFileList();

    void showRefreshingMyFileList();

    void showRefreshAllFileListFail(String failStr);

    void showAllFileList(HashMap<String, TaskInfo> uploadingTaskHashMap, ArrayList<FileInfo> mMyFileList);


    void showUploadGet();


    void startUpload();


    void startGet();


    void onDestroy();
}