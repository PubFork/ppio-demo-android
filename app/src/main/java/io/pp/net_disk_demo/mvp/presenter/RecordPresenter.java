package io.pp.net_disk_demo.mvp.presenter;

import java.util.ArrayList;

import io.pp.net_disk_demo.data.RecordInfo;

public interface RecordPresenter {

    void startRequestRecord();

    void showRequestingRecord();

    void showRequestRecordFail(String errMsg);

    void showRequestRecordFinished(ArrayList<RecordInfo> recordInfoList);


    void onDestroy();
}