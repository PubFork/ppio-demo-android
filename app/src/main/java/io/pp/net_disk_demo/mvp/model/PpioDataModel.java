package io.pp.net_disk_demo.mvp.model;

import java.util.HashMap;

import io.pp.net_disk_demo.data.DeletingInfo;

public interface PpioDataModel {

    void link();


    void refreshMyFileList(HashMap<String, DeletingInfo> deletingInfoHashMap, HashMap<String, String> uploadFailedInfoHashMap, boolean allRefresh);


    void onDestroy();
}