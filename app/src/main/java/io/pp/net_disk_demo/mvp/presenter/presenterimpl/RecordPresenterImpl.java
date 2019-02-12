package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;

import java.util.ArrayList;

import io.pp.net_disk_demo.data.RecordInfo;
import io.pp.net_disk_demo.mvp.model.RecordModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.RecordModelImpl;
import io.pp.net_disk_demo.mvp.presenter.RecordPresenter;
import io.pp.net_disk_demo.mvp.view.RecordView;

public class RecordPresenterImpl implements RecordPresenter {

    private RecordView mRecordView = null;
    private RecordModel mRecordModel = null;

    public RecordPresenterImpl(RecordView recordView) {
        mRecordView = recordView;
        mRecordModel = new RecordModelImpl(RecordPresenterImpl.this);
    }

    @Override
    public void startRequestRecord() {
        if (mRecordModel != null) {
            mRecordModel.requestRecord();
        }
    }

    @Override
    public void showRequestingRecord() {
        if (mRecordView != null) {
            mRecordView.showRequestingRecordView();
        }
    }

    @Override
    public void showRequestRecordFail(String errMsg) {
        if (mRecordView != null) {
            mRecordView.showRequestRecordFailView(errMsg);
        }
    }

    @Override
    public void showRequestRecordFinished(ArrayList<RecordInfo> recordInfoList) {
        if (mRecordView != null) {
            mRecordView.showRequestRecordFinishedView(recordInfoList);
        }
    }

    @Override
    public void onDestroy() {
        if (mRecordModel != null) {
            mRecordModel.onDestroy();
            mRecordModel = null;
        }

        mRecordView = null;
    }
}