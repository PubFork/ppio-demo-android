package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import io.pp.net_disk_demo.data.FileInfo;
import io.pp.net_disk_demo.data.ObjectStatus;
import io.pp.net_disk_demo.mvp.model.StartRenewModel;
import io.pp.net_disk_demo.mvp.presenter.StartRenewPresenter;
import io.pp.net_disk_demo.ppio.PossUtil;

public class StartRenewModelImpl implements StartRenewModel {

    private Context mContext;
    private StartRenewPresenter mStartRenewPresenter;

    public StartRenewModelImpl(Context context, StartRenewPresenter startRenewPresenter) {
        mContext = context;
        mStartRenewPresenter = startRenewPresenter;
    }

    @Override
    public void startRenew(String bucket, String key) {
        new GetStatusSyncTask(StartRenewModelImpl.this).execute(bucket, key);
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mStartRenewPresenter = null;
    }

    public void showStartingRenew() {
        if (mStartRenewPresenter != null) {
            mStartRenewPresenter.showStartingRenew();
        }
    }

    public void showStartRenewError(String errMsg) {
        if (mStartRenewPresenter != null) {
            mStartRenewPresenter.showStartRenewError(errMsg);
        }
    }

    public void showRenewView(FileInfo fileInfo) {
        if (mStartRenewPresenter != null) {
            mStartRenewPresenter.showRenewView(fileInfo);
        }
    }

    static class GetStatusSyncTask extends AsyncTask<String, String, FileInfo> {

        boolean mGetStatusSucceed;

        WeakReference<StartRenewModelImpl> mGetDetailModelImplWeakReference;

        public GetStatusSyncTask(StartRenewModelImpl startRenewModelImpl) {
            mGetDetailModelImplWeakReference = new WeakReference<>(startRenewModelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mGetDetailModelImplWeakReference.get() != null) {
                mGetDetailModelImplWeakReference.get().showStartingRenew();
            }
        }

        @Override
        protected FileInfo doInBackground(String[] values) {
            mGetStatusSucceed = true;

            ObjectStatus objectStatus = PossUtil.getObjectStatus(values[0], values[1], new PossUtil.GetObjectStatusListener() {
                @Override
                public void onGetObjectStatusError(String errMsg) {
                    mGetStatusSucceed = false;

                    publishProgress(errMsg);
                }
            });

            FileInfo fileInfo = new FileInfo(0L, objectStatus.getKeyStr(), true, true);
            fileInfo.setBucketName(objectStatus.getBucketStr());
            fileInfo.setStorageTime(objectStatus.getExpiresTime());

            return fileInfo;
        }

        @Override
        protected void onProgressUpdate(String[] values) {
            super.onProgressUpdate(values);

            if (mGetDetailModelImplWeakReference.get() != null) {
                mGetDetailModelImplWeakReference.get().showStartRenewError(values[0]);
            }
        }

        @Override
        protected void onPostExecute(FileInfo value) {
            super.onPostExecute(value);

            if (mGetDetailModelImplWeakReference.get() != null) {
                if (value != null && mGetStatusSucceed) {
                    mGetDetailModelImplWeakReference.get().showRenewView(value);
                }
            }
        }
    }
}