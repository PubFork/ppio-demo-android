package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.data.DeletingInfo;
import io.pp.net_disk_demo.mvp.model.DeleteModel;
import io.pp.net_disk_demo.mvp.presenter.DeletePresenter;
import io.pp.net_disk_demo.ppio.PossUtil;
import poss.Progress;

public class DeleteModelImpl implements DeleteModel {

    private static final String TAG = "DeleteModelImpl";

    private Context mContext;
    private DeletePresenter mDeletePresenter;
    private ExecutorService mFixedThreadPool;

    public DeleteModelImpl(Context context, DeletePresenter deletePresenter) {
        mContext = context;
        mDeletePresenter = deletePresenter;
        mFixedThreadPool = Executors.newFixedThreadPool(3);
    }

    @Override
    public void delete(String bucket, String key, String status) {
        new DeleteAsyncTask(DeleteModelImpl.this).execute(bucket, key, status);
    }

    @Override
    public void deleteSilently(String bucket, String key) {
        mFixedThreadPool.execute(new DeleteSilentRunnable(bucket, key, DeleteModelImpl.this));
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mDeletePresenter = null;
    }

    private Context getContext() {
        return mContext;
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

    private void deleteSilentlyFinish(String bucket, String key) {
        if (mDeletePresenter != null) {
            mDeletePresenter.onDeleteSilentlyFinish(bucket, key);
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
            String status = strings[2];
            String taskId = PossUtil.deleteObject(strings[0], strings[1], new PossUtil.DeleteObjectListener() {
                @Override
                public void onDeleteObjectError(String errMsg) {
                    publishProgress(errMsg);
                }
            });

            if (!TextUtils.isEmpty(taskId) &&
                    !Constant.TaskState.PENDING.equals(status)) {
                return new DeletingInfo(name, taskId);
            } else {
                return null;
            }
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

            if (mDeleteModelWeakReference.get() != null && deletingInfo != null) {
                mDeleteModelWeakReference.get().showDeleteFinish(deletingInfo);
            }
        }
    }

    static class DeleteSilentRunnable implements Runnable {

        private final String mBucket;
        private final String mKey;
        private WeakReference<DeleteModelImpl> mDeleteModelWeakReference;

        public DeleteSilentRunnable(String bucket, String key, DeleteModelImpl deleteModel) {
            mBucket = bucket;
            mKey = key;
            mDeleteModelWeakReference = new WeakReference<>(deleteModel);
        }

        @Override
        public void run() {

            if (mDeleteModelWeakReference.get() != null) {
            }

            String taskId = PossUtil.deleteObject(mBucket, mKey, new PossUtil.DeleteObjectListener() {
                @Override
                public void onDeleteObjectError(String errMsg) {
                    Log.e(TAG, "");
                }
            });

            if (TextUtils.isEmpty(taskId)) {
                taskId = PossUtil.deleteObject(mBucket, mKey, new PossUtil.DeleteObjectListener() {
                    @Override
                    public void onDeleteObjectError(String errMsg) {
                        Log.e(TAG, "DeleteSilentRunnable deleteObject error: " + errMsg);
                    }
                });

                if (TextUtils.isEmpty(taskId)) {
                    if (mDeleteModelWeakReference.get() != null) {
                        mDeleteModelWeakReference.get().deleteSilentlyFinish(mBucket, mKey);
                    }

                    return;
                }
            }

            String deleteState = Constant.ProgressState.RUNNING;
            while (!Constant.ProgressState.ERROR.equals(deleteState) &&
                    !Constant.ProgressState.FINISHED.equals(deleteState)) {
                try {
                    Progress progress = PossUtil.getTaskProgress(taskId);
                    deleteState = progress.getJobState();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(1000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (Constant.ProgressState.ERROR.equals(deleteState)) {
                taskId = PossUtil.deleteObject(mBucket, mKey, new PossUtil.DeleteObjectListener() {
                    @Override
                    public void onDeleteObjectError(String errMsg) {
                        Log.e(TAG, "DeleteSilentRunnable deleteObject error: " + errMsg);
                    }
                });

                deleteState = Constant.ProgressState.RUNNING;
                while (!Constant.ProgressState.ERROR.equals(deleteState) &&
                        !Constant.ProgressState.FINISHED.equals(deleteState)) {
                    try {
                        Progress progress = PossUtil.getTaskProgress(taskId);
                        deleteState = progress.getJobState();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(1000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (Constant.ProgressState.FINISHED.equals(deleteState)) {
                if (mDeleteModelWeakReference.get() != null) {
                    mDeleteModelWeakReference.get().deleteSilentlyFinish(mBucket, mKey);
                }
            }
        }
    }
}