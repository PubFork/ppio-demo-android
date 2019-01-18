package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

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

    private void showDeleteFinish() {
        if (mDeletePresenter != null) {
            mDeletePresenter.onDeleteFinish();
        }
    }

    static class DeleteAsyncTask extends AsyncTask<String, String, Boolean> {

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
        protected Boolean doInBackground(String... strings) {
            return PossUtil.deleteObject(strings[0], strings[1], new PossUtil.DeleteObjectListener() {
                @Override
                public void onDeleteObjectError(String errMsg) {
                    publishProgress(errMsg);
                }
            });
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if (mDeleteModelWeakReference.get() != null) {
                mDeleteModelWeakReference.get().showDeleteError(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean downloadSuccess) {
            super.onPostExecute(downloadSuccess);

            if (mDeleteModelWeakReference.get() != null) {
                mDeleteModelWeakReference.get().showDeleteFinish();
            }
        }
    }
}