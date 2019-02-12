package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.pp.net_disk_demo.data.RecordInfo;
import io.pp.net_disk_demo.mvp.model.RecordModel;
import io.pp.net_disk_demo.mvp.presenter.RecordPresenter;
import io.pp.net_disk_demo.ppio.RpcUtil;

public class RecordModelImpl implements RecordModel {

    private RecordPresenter mRecordPresenter = null;

    public RecordModelImpl(RecordPresenter recordPresenter) {
        mRecordPresenter = recordPresenter;
    }

    @Override
    public void requestRecord() {
        new RequestRecordAsyncTask(RecordModelImpl.this).execute();
    }

    @Override
    public void onDestroy() {
        mRecordPresenter = null;
    }

    public void requestRecordPrepare(){
        if (mRecordPresenter != null) {
            mRecordPresenter.showRequestingRecord();
        }
    }

    public void requestRecordsFail(String errMsg) {
        if (mRecordPresenter != null) {
            mRecordPresenter.showRequestRecordFail(errMsg);
        }
    }

    public void requestRecordsSucceed(ArrayList<RecordInfo> recordInfoList) {
        if (mRecordPresenter != null) {
            mRecordPresenter.showRequestRecordFinished(recordInfoList);
        }
    }

    private static class RequestRecordAsyncTask extends AsyncTask<Void, String, ArrayList<RecordInfo>> {

        private WeakReference<RecordModelImpl> mRecordModelImplWeakReference = null;

        public RequestRecordAsyncTask(RecordModelImpl recordModelImpl) {
            mRecordModelImplWeakReference = new WeakReference<>(recordModelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mRecordModelImplWeakReference.get() != null) {
                mRecordModelImplWeakReference.get().requestRecordPrepare();
            }
        }

        @Override
        protected ArrayList<RecordInfo> doInBackground(Void values[]) {
            if (mRecordModelImplWeakReference.get() != null) {
                return RpcUtil.transferRecord(new RpcUtil.QueryAccountListener() {
                    @Override
                    public void onQueryAccountError(String errMsg) {
                        onProgressUpdate(new String[]{errMsg});
                    }
                });
            } else {
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String[] values) {
            super.onProgressUpdate(values);

            if (mRecordModelImplWeakReference.get() != null) {
                mRecordModelImplWeakReference.get().requestRecordsFail(values[0]);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<RecordInfo> recordInfoList) {
            super.onPostExecute(recordInfoList);

            if (mRecordModelImplWeakReference.get() != null) {
                mRecordModelImplWeakReference.get().requestRecordsSucceed(recordInfoList);
            }
        }
    }
}