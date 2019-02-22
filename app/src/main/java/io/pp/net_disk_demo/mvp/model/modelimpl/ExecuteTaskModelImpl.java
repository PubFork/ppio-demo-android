package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.mvp.model.ExecuteTasksModel;
import io.pp.net_disk_demo.mvp.presenter.ExecuteTaskPresenter;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.service.DownloadService;
import io.pp.net_disk_demo.service.UploadService;

public class ExecuteTaskModelImpl implements ExecuteTasksModel,
        DownloadService.DownloadListener,
        UploadService.ShowUploadTaskListListener,
        DownloadService.ShowDownloadTaskListListener,
        UploadService.ShowUploadedListener {

    private static final String TAG = "ExecuteTaskModelImpl";

    private Context mContext;
    private ExecuteTaskPresenter mExecuteTasksPresenter;

    private UploadService mUploadService = null;
    private DownloadService mDownloadService = null;

    private ArrayList<String> mTaskIdList = null;

    public ExecuteTaskModelImpl(Context context, ExecuteTaskPresenter executeTasksPresenter) {
        mContext = context;
        mExecuteTasksPresenter = executeTasksPresenter;

        mTaskIdList = new ArrayList<>();
    }

    @Override
    public void bindUploadService(UploadService uploadService) {
        mUploadService = uploadService;

        mUploadService.setShowUploadTaskListListener(ExecuteTaskModelImpl.this);
        mUploadService.setShowUploadedListener(ExecuteTaskModelImpl.this);
    }

    @Override
    public void bindDownloadService(DownloadService downloadService) {
        mDownloadService = downloadService;

        mDownloadService.setShowDownloadTaskListListener(ExecuteTaskModelImpl.this);
        mDownloadService.setDownloadListener(ExecuteTaskModelImpl.this);
    }

    @Override
    public void startRefreshTasks() {
        if (mUploadService != null) {
            mUploadService.startShowUploadTaskList();
        }

        if (mDownloadService != null) {
            mDownloadService.startShowDownloadTaskList();
        }
    }

    @Override
    public void deleteTask(String taskId, String downloadPath) {
        new DeleteTaskAsyncTask(ExecuteTaskModelImpl.this).execute(taskId, downloadPath);
    }

    @Override
    public void deleteUploadingTask(String bucket, String key, String taskId) {
        new DeleteUploadingTaskAsyncTask(bucket, key, ExecuteTaskModelImpl.this).execute(taskId);
    }

    @Override
    public void pauseTask(String taskId) {
        new PauseTaskAsyncTask(ExecuteTaskModelImpl.this).execute(taskId);
    }

    @Override
    public void resumeTask(String taskId) {
        new ResumeTaskAsyncTask(ExecuteTaskModelImpl.this).execute(taskId);
    }

    @Override
    public void startUpload(UploadInfo uploadInfo) {
        if (mUploadService != null) {
            mUploadService.upload(uploadInfo);
        }
    }

    @Override
    public void startDownload(DownloadInfo downloadInfo) {
        if (mDownloadService != null) {
            mDownloadService.download(downloadInfo);
        }
    }

    @Override
    public void stopAllTask() {
//        if (mExecuteTasksService != null) {
//            mExecuteTasksService.stopAllTask();
//        }
    }

    @Override
    public void onStartingDownload() {

    }

    @Override
    public void onDownloadStartSucceed() {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showRequestDownloadFinished();
        }
    }

    @Override
    public void onDownloadStartFailed(String errMsg) {

    }

    @Override
    public void showUploadTaskList(ArrayList<TaskInfo> taskInfoList, boolean allRefresh) {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showUploadTaskList(taskInfoList, allRefresh);
        }
    }

    @Override
    public void showDownloadTaskList(ArrayList<TaskInfo> taskInfoList, boolean allRefresh) {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showDownloadTaskList(taskInfoList, allRefresh);
        }
    }

    @Override
    public void onUploaded() {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.refreshFileList();
        }
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mExecuteTasksPresenter = null;
        mUploadService = null;
        mDownloadService = null;
    }

    public void showPrepareOperateTask(String message) {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showOperateTaskPrepare(message);
        }
    }

    public void showOperateTaskFinished() {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showOperateFinished();
        }
    }

    public void showDeleteUploadingTaskFinished(String bucket, String key) {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showDeleteUploadingTaskFinished(bucket, key);
        }
    }

    public void showOperateTaskError(String errMsg) {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showOperateError(errMsg);
        }
    }

    static class DeleteTaskAsyncTask extends AsyncTask<String, String, Boolean> {

        final WeakReference<ExecuteTaskModelImpl> mExecuteTaskModelImplWeakReference;

        public DeleteTaskAsyncTask(ExecuteTaskModelImpl executeTaskModelImpl) {
            mExecuteTaskModelImplWeakReference = new WeakReference<>(executeTaskModelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mExecuteTaskModelImplWeakReference.get() != null) {
                mExecuteTaskModelImplWeakReference.get().showPrepareOperateTask("Deleting task, For large file, it will take a while");
            }
        }

        @Override
        protected Boolean doInBackground(String[] values) {
            final String taskId = values[0];
            final String downloadPath = values[1];

            boolean deleteTask = PossUtil.deleteTask(taskId, new PossUtil.DeleteTaskListener() {
                @Override
                public void onDeleteTaskError(String errMsg) {
                    publishProgress(errMsg);
                }
            });

            if (deleteTask && !TextUtils.isEmpty(downloadPath)) {
                File downloadFile = new File(downloadPath);
                if (downloadFile.exists()) {
                    if (!downloadFile.delete()) {
                        publishProgress("downloaded file delete failed!");
                    }
                }
            }

            return deleteTask;
        }

        @Override
        protected void onProgressUpdate(String[] values) {
            super.onProgressUpdate(values);

            if (mExecuteTaskModelImplWeakReference.get() != null) {
                mExecuteTaskModelImplWeakReference.get().showOperateTaskError(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean succeed) {
            super.onPostExecute(succeed);

            if (succeed) {

            }

            if (mExecuteTaskModelImplWeakReference.get() != null) {
                mExecuteTaskModelImplWeakReference.get().showOperateTaskFinished();
            }
        }
    }

    static class DeleteUploadingTaskAsyncTask extends AsyncTask<String, String, Boolean> {

        final String mBucket;
        final String mKey;
        final WeakReference<ExecuteTaskModelImpl> mExecuteTaskModelImplWeakReference;

        public DeleteUploadingTaskAsyncTask(String bucket, String key, ExecuteTaskModelImpl executeTaskModelImpl) {
            mBucket = bucket;
            mKey = key;
            mExecuteTaskModelImplWeakReference = new WeakReference<>(executeTaskModelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mExecuteTaskModelImplWeakReference.get() != null) {
                mExecuteTaskModelImplWeakReference.get().showPrepareOperateTask("Deleting task, For large file, it will take a while");
            }
        }

        @Override
        protected Boolean doInBackground(String[] values) {
            return PossUtil.deleteTask(values[0], new PossUtil.DeleteTaskListener() {
                @Override
                public void onDeleteTaskError(String errMsg) {
                    publishProgress(errMsg);
                }
            });
        }

        @Override
        protected void onProgressUpdate(String[] values) {
            super.onProgressUpdate(values);

            if (mExecuteTaskModelImplWeakReference.get() != null) {
                mExecuteTaskModelImplWeakReference.get().showOperateTaskError(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean succeed) {
            super.onPostExecute(succeed);

            if (mExecuteTaskModelImplWeakReference.get() != null && succeed) {
                mExecuteTaskModelImplWeakReference.get().showDeleteUploadingTaskFinished(mBucket, mKey);
            }
        }
    }


    static class PauseTaskAsyncTask extends AsyncTask<String, String, Boolean> {

        final WeakReference<ExecuteTaskModelImpl> mExecuteTaskModelImplWeakReference;

        public PauseTaskAsyncTask(ExecuteTaskModelImpl executeTaskModelImpl) {
            mExecuteTaskModelImplWeakReference = new WeakReference<>(executeTaskModelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mExecuteTaskModelImplWeakReference.get() != null) {
                mExecuteTaskModelImplWeakReference.get().showPrepareOperateTask("Pausing task...");
            }
        }

        @Override
        protected Boolean doInBackground(String[] values) {
            return PossUtil.pauseTask(values[0], new PossUtil.PauseTaskListener() {
                @Override
                public void onPauseTaskError(String errMsg) {
                    publishProgress(errMsg);
                }
            });
        }

        @Override
        protected void onProgressUpdate(String[] values) {
            super.onProgressUpdate(values);

            if (mExecuteTaskModelImplWeakReference.get() != null) {
                mExecuteTaskModelImplWeakReference.get().showOperateTaskError(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean succeed) {
            super.onPostExecute(succeed);

            if (mExecuteTaskModelImplWeakReference.get() != null) {
                mExecuteTaskModelImplWeakReference.get().showOperateTaskFinished();
            }
        }
    }

    static class ResumeTaskAsyncTask extends AsyncTask<String, String, Boolean> {

        final WeakReference<ExecuteTaskModelImpl> mExecuteTaskModelImplWeakReference;

        public ResumeTaskAsyncTask(ExecuteTaskModelImpl executeTaskModelImpl) {
            mExecuteTaskModelImplWeakReference = new WeakReference<>(executeTaskModelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mExecuteTaskModelImplWeakReference.get() != null) {
                mExecuteTaskModelImplWeakReference.get().showPrepareOperateTask("Resuming task...");
            }
        }

        @Override
        protected Boolean doInBackground(String[] values) {
            return PossUtil.resumeTask(values[0], new PossUtil.ResumeTaskListener() {
                @Override
                public void onResumeTaskError(String errMsg) {
                    publishProgress(errMsg);
                }
            });
        }

        @Override
        protected void onProgressUpdate(String[] values) {
            super.onProgressUpdate(values);

            if (mExecuteTaskModelImplWeakReference.get() != null) {
                mExecuteTaskModelImplWeakReference.get().showOperateTaskError(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean succeed) {
            super.onPostExecute(succeed);

            if (mExecuteTaskModelImplWeakReference.get() != null) {
                mExecuteTaskModelImplWeakReference.get().showOperateTaskFinished();
            }
        }
    }
}