package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import io.pp.net_disk_demo.mvp.model.GetShareCodeModel;
import io.pp.net_disk_demo.mvp.presenter.ShowShareCodePresenter;
import io.pp.net_disk_demo.ppio.PossUtil;

public class GetShareCodeModelImpl implements GetShareCodeModel {

    private static final String TAG = "GetShareCodeModelImpl";

    private Context mContext;
    private ShowShareCodePresenter mShowShareCodePresenter;

    public GetShareCodeModelImpl(Context context, ShowShareCodePresenter showShareCodePresenter) {
        mContext = context;
        mShowShareCodePresenter = showShareCodePresenter;
    }

    @Override
    public void getShareCode(String bucket, String key) {
        new GetShareCodeSyncTask(GetShareCodeModelImpl.this).execute(bucket, key);
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mShowShareCodePresenter = null;
    }

    public void showGettingShareCode() {
        if (mShowShareCodePresenter != null) {
            mShowShareCodePresenter.showGettingShareCode();
        }
    }

    public void showGettingShareCodeError(String errMsg) {
        if (mShowShareCodePresenter != null) {
            mShowShareCodePresenter.showGettingShareCodeError(errMsg);
        }
    }

    public void showShareCode(String shareCode) {
        if (mShowShareCodePresenter != null) {
            mShowShareCodePresenter.showShareCode(shareCode);
        }
    }

    static class GetShareCodeSyncTask extends AsyncTask<String, String, String> {

        boolean mGetShareCodeSucceed;

        WeakReference<GetShareCodeModelImpl> mGetDetailModelImplWeakReference;

        public GetShareCodeSyncTask(GetShareCodeModelImpl getShareCodeModelImpl) {
            mGetDetailModelImplWeakReference = new WeakReference<>(getShareCodeModelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mGetDetailModelImplWeakReference.get() != null) {
                mGetDetailModelImplWeakReference.get().showGettingShareCode();
            }
        }

        @Override
        protected String doInBackground(String[] values) {
            mGetShareCodeSucceed = true;

            return PossUtil.getShareCode(values[0], values[1], new PossUtil.GetShareCodeListener() {
                @Override
                public void onGetShareCodeError(String errMsg) {
                    mGetShareCodeSucceed = false;

                    publishProgress(errMsg);
                }
            });
        }

        @Override
        protected void onProgressUpdate(String[] values) {
            super.onProgressUpdate(values);

            if (mGetDetailModelImplWeakReference.get() != null) {
                mGetDetailModelImplWeakReference.get().showGettingShareCodeError(values[0]);
            }
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);

            if (mGetDetailModelImplWeakReference.get() != null) {
                if (value != null && mGetShareCodeSucceed) {
                    mGetDetailModelImplWeakReference.get().showShareCode(value);
                }
            }
        }
    }
}