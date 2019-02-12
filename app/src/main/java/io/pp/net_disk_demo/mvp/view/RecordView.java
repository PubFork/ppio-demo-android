package io.pp.net_disk_demo.mvp.view;

import java.util.ArrayList;

import io.pp.net_disk_demo.data.RecordInfo;

public interface RecordView {

    void back();

    void showRequestingRecordView();

    void showRequestRecordFailView(final String errMsg);

    void showRequestRecordFinishedView(ArrayList<RecordInfo> recordInfoList);
}