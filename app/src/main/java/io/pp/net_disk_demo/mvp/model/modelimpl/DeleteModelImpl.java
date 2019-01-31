package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

import io.pp.net_disk_demo.data.DeletingInfo;
import io.pp.net_disk_demo.mvp.model.DeleteModel;
import io.pp.net_disk_demo.mvp.presenter.DeletePresenter;
import io.pp.net_disk_demo.ppio.PossUtil;

public class DeleteModelImpl implements DeleteModel {

    private Context mContext;
    private DeletePresenter mDeletePresenter;

    public DeleteModelImpl(Context context, DeletePresenter deletePresenter) {
        mContext = context;
        mDeletePresenter = deletePresenter;
    }

    @Override
    public void delete(String bucket, String key) {
        new DeleteAsyncTask(DeleteModelImpl.this).execute(bucket, key);
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mDeletePresenter = null;
    }

    public void showDeletePrepare() {
        if (mDeletePresenter != null) {
            mDeletePresenter.onDeletePrepare();
        }
    }

    private void showDeleteError(String errMsg) {
        if (mDeletePresenter != null) {
            mDeletePresenter.onDeleteError(errMsg);
        }
    }

    private void showDeleteFinish(DeletingInfo deletingInfo) {
        if (mDeletePresenter != null) {
            mDeletePresenter.onDeleteFinish(deletingInfo);
        }
    }

    static class DeleteAsyncTask extends AsyncTask<String, String, DeletingInfo> {

        final WeakReference<DeleteModelImpl> mDeleteModelWeakReference;

        public DeleteAsyncTask(DeleteModelImpl deleteModelImpl) {
            mDeleteModelWeakReference = new WeakReference<>(deleteModelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mDeleteModelWeakReference.get() != null) {
                mDeleteModelWeakReference.get().showDeletePrepare();
            }
        }

        @Override
        protected DeletingInfo doInBackground(String... strings) {
            String name = strings[0] + strings[1];
            String taskId = PossUtil.deleteObject(strings[0], strings[1], new PossUtil.DeleteObjectListener() {
                @Override
                public void onDeleteObjectError(String errMsg) {
                    publishProgress(errMsg);
                }
            });

            return new DeletingInfo(name, taskId);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if (mDeleteModelWeakReference.get() != null) {
                mDeleteModelWeakReference.get().showDeleteError(values[0]);
            }
        }

        @Override
        protected void onPostExecute(DeletingInfo deletingInfo) {
            super.onPostExecute(deletingInfo);

            if (mDeleteModelWeakReference.get() != null) {
                mDeleteModelWeakReference.get().showDeleteFinish(deletingInfo);
            }
        }
    }
}