package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.mvp.model.ExecuteTasksModel;
import io.pp.net_disk_demo.mvp.presenter.ExecuteTaskPresenter;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.service.DownloadService;
import io.pp.net_disk_demo.service.ExecuteTaskService;
import io.pp.net_disk_demo.service.UploadService;

public class ExecuteTaskModelImpl implements ExecuteTasksModel,
        UploadService.ShowUploadTaskListListener,
        DownloadService.ShowDownloadTaskListListener {

    private static final String TAG = "ExecuteTaskModelImpl";

    private Context mContext;
    private ExecuteTaskPresenter mExecuteTasksPresenter;
    private ExecuteTaskService mExecuteTasksService;

    private UploadService mUploadService = null;
    private DownloadService mDownloadService = null;

    private ArrayList<String> mTaskIdList = null;

    public ExecuteTaskModelImpl(Context context, ExecuteTaskPresenter executeTasksPresenter) {
        mContext = context;
        mExecuteTasksPresenter = executeTasksPresenter;

        mTaskIdList = new ArrayList<>();
    }

    @Override
    public void bindExecuteTaskService(ExecuteTaskService executeTasksService) {
        mExecuteTasksService = executeTasksService;
    }

    @Override
    public void bindUploadService(UploadService uploadService) {
        mUploadService = uploadService;

        mUploadService.setShowUploadTaskListListener(ExecuteTaskModelImpl.this);
    }

    @Override
    public void bindDownloadService(DownloadService downloadService) {
        mDownloadService = downloadService;

        mDownloadService.setShowDownloadTaskListListener(ExecuteTaskModelImpl.this);
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
    public void deleteTask(String taskId) {
        new DeleteTaskAsyncTask(ExecuteTaskModelImpl.this).execute(taskId);
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
        if (mExecuteTasksService != null) {
            mExecuteTasksService.stopAllTask();
        }
    }

    @Override
    public void showUploadTaskList(ArrayList<TaskInfo> taskInfoList) {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showUploadTaskList(taskInfoList);
        }
    }

    @Override
    public void showDownloadTaskList(ArrayList<TaskInfo> taskInfoList) {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showDownloadTaskList(taskInfoList);
        }
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mExecuteTasksPresenter = null;
        mExecuteTasksService = null;
        mUploadService = null;
        mDownloadService = null;
    }

    public void showPrepareOperateTask() {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showOperateTaskPrepare();
        }
    }

    public void showOperateTaskFinished() {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showOperateFinished();
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
                mExecuteTaskModelImplWeakReference.get().showPrepareOperateTask();
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

            if (mExecuteTaskModelImplWeakReference.get() != null) {
                mExecuteTaskModelImplWeakReference.get().showOperateTaskFinished();
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
                mExecuteTaskModelImplWeakReference.get().showPrepareOperateTask();
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
                mExecuteTaskModelImplWeakReference.get().showPrepareOperateTask();
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