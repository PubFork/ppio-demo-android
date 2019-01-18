package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;

import java.util.ArrayList;

import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.mvp.model.ExecuteTasksModel;
import io.pp.net_disk_demo.mvp.presenter.ExecuteTaskPresenter;
import io.pp.net_disk_demo.service.ExecuteTaskService;

public class ExecuteTaskModelImpl implements ExecuteTasksModel,
        ExecuteTaskService.TaskListListener,
        ExecuteTaskService.DownloadListener,
        ExecuteTaskService.ShowOperateTaskListener {

    private static final String TAG = "ExecuteTaskModelImpl";

    private Context mContext;
    private ExecuteTaskPresenter mExecuteTasksPresenter;
    private ExecuteTaskService mExecuteTasksService;

    public ExecuteTaskModelImpl(Context context, ExecuteTaskPresenter executeTasksPresenter) {
        mContext = context;
        mExecuteTasksPresenter = executeTasksPresenter;
    }

    @Override
    public void showTaskList(ArrayList<TaskInfo> taskInfoList) {
//        if (mExecuteTasksPresenter != null) {
//            mExecuteTasksPresenter.showDownloadingTasks(taskInfoList);
//        }
    }

    @Override
    public void showUploadingTaskList(ArrayList<TaskInfo> taskInfoList) {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showUploadingTasks(taskInfoList);
        }
    }

    @Override
    public void showDownloadingTaskList(ArrayList<TaskInfo> taskInfoList) {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showDownloadingTasks(taskInfoList);
        }
    }

    @Override
    public void showListTaskError(String errMsg) {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showRefreshTasksError(errMsg);
        }
    }

    @Override
    public void onTaskFinished(String taskId) {
        if (mExecuteTasksService != null) {
            mExecuteTasksService.deleteTask(taskId);
        }

        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.refreshFileList();
        }
    }

    @Override
    public void onDownloadStartSucceed() {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showRequestDownloadFinished();
        }
    }

    @Override
    public void onDownloadStartFailed(String errMsg) {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showRequestDownloadError(errMsg);
        }
    }

    @Override
    public void onOperatePrepare() {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showOperateTaskPrepare();
        }
    }

    @Override
    public void onOperateFinished() {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showOperateFinished();
        }
    }

    @Override
    public void onOperateError(String errMsg) {
        if (mExecuteTasksPresenter != null) {
            mExecuteTasksPresenter.showOperateError(errMsg);
        }
    }

    @Override
    public void bindExecuteTaskService(ExecuteTaskService executeTasksService) {
        mExecuteTasksService = executeTasksService;

        mExecuteTasksService.setShowTaskListListener(ExecuteTaskModelImpl.this);
        mExecuteTasksService.setShowOperateTaskListener(ExecuteTaskModelImpl.this);
    }

    @Override
    public void startRefreshTasks() {
        if (mExecuteTasksService != null) {
            mExecuteTasksService.startRefreshTask();
        }
    }

    @Override
    public void deleteTask(String taskId) {
        if (mExecuteTasksService != null) {
            mExecuteTasksService.deleteTask(taskId);
        }
    }

    @Override
    public void pauseTask(String taskId) {
        if (mExecuteTasksService != null) {
            mExecuteTasksService.pauseTask(taskId);
        }
    }

    @Override
    public void resumeTask(String taskId) {
        if (mExecuteTasksService != null) {
            mExecuteTasksService.resumeTask(taskId);
        }
    }

    @Override
    public void startUpload(UploadInfo uploadInfo) {
        if (mExecuteTasksService != null) {
            mExecuteTasksService.startUpload(uploadInfo);
        }
    }

    @Override
    public void startDownload(DownloadInfo downloadInfo) {
        if (mExecuteTasksService != null) {
            mExecuteTasksService.startDownload(downloadInfo);
        }
    }


    @Override
    public void stopAllTask() {
        if (mExecuteTasksService != null) {
            mExecuteTasksService.stopAllTask();
        }
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mExecuteTasksPresenter = null;
        mExecuteTasksService = null;
    }
}