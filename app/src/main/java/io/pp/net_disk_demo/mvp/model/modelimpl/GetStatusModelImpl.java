package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import io.pp.net_disk_demo.data.ObjectStatus;
import io.pp.net_disk_demo.mvp.model.GetStatusModel;
import io.pp.net_disk_demo.mvp.presenter.ShowStatusPresenter;
import io.pp.net_disk_demo.ppio.PossUtil;

public class GetStatusModelImpl implements GetStatusModel {

    private static final String TAG = "GetDetailModelImpl";

    private Context mContext;
    private ShowStatusPresenter mShowStatusPresenter;

    public GetStatusModelImpl(Context context, ShowStatusPresenter showDetailPresenter) {
        mContext = context;
        mShowStatusPresenter = showDetailPresenter;
    }

    @Override
    public void getObjectStatus(String bucket, String key) {
        new GetStatusSyncTask(GetStatusModelImpl.this).execute(bucket, key);

    }

    public void onDestroy() {
        mContext = null;
        mShowStatusPresenter = null;
    }

    public void showGettingStatus() {
        if (mShowStatusPresenter != null) {
            mShowStatusPresenter.showGettingStatus();
        }
    }

    public void showGettingStatusError(String errMsg) {
        if (mShowStatusPresenter != null) {
            mShowStatusPresenter.showGettingStatusError(errMsg);
        }
    }

    public void showStatus(ObjectStatus objectStatus) {
        if (mShowStatusPresenter != null) {
            mShowStatusPresenter.showStatus(objectStatus);
        }
    }

    static class GetStatusSyncTask extends AsyncTask<String, String, ObjectStatus> {

        boolean mGetStatusSucceed;

        WeakReference<GetStatusModelImpl> mGetDetailModelImplWeakReference;

        public GetStatusSyncTask(GetStatusModelImpl getDetailModelImpl) {
            mGetDetailModelImplWeakReference = new WeakReference<>(getDetailModelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mGetDetailModelImplWeakReference.get() != null) {
                mGetDetailModelImplWeakReference.get().showGettingStatus();
            }
        }

        @Override
        protected ObjectStatus doInBackground(String[] values) {
            mGetStatusSucceed = true;

           return PossUtil.getObjectStatus(values[0], values[1], new PossUtil.GetObjectStatusListener() {
                @Override
                public void onGetObjectStatusError(String errMsg) {
                    mGetStatusSucceed = false;

                    publishProgress(errMsg);
                }
            });
        }

        @Override
        protected void onProgressUpdate(String[] values) {
            super.onProgressUpdate(values);

            if (mGetDetailModelImplWeakReference.get() != null) {
                mGetDetailModelImplWeakReference.get().showGettingStatusError(values[0]);
            }
        }

        @Override
        protected void onPostExecute(ObjectStatus value) {
            super.onPostExecute(value);

            if (mGetDetailModelImplWeakReference.get() != null) {
                if (value != null && mGetStatusSucceed) {
                    mGetDetailModelImplWeakReference.get().showStatus(value);
                }
            }
        }
    }
}