package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;

import java.util.ArrayList;

import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.mvp.model.ExecuteTasksModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.ExecuteTaskModelImpl;
import io.pp.net_disk_demo.mvp.presenter.ExecuteTaskPresenter;
import io.pp.net_disk_demo.mvp.view.ExecuteTaskView;
import io.pp.net_disk_demo.service.ExecuteTaskService;

public class ExecuteTaskPresenterImpl implements ExecuteTaskPresenter {

    private static final String TAG = "ExecuteTaskPresenterImpl";

    private Context mContext;
    private ExecuteTasksModel mExecuteTaskModel;
    private ExecuteTaskView mExecuteTaskView;

    public ExecuteTaskPresenterImpl(Context context, ExecuteTaskView executeView) {
        mContext = context;
        mExecuteTaskView = executeView;
        mExecuteTaskModel = new ExecuteTaskModelImpl(context, ExecuteTaskPresenterImpl.this);
    }

    @Override
    public void bindExecuteTaskService(ExecuteTaskService executeTasksService) {
        if (mExecuteTaskModel != null) {
            mExecuteTaskModel.bindExecuteTaskService(executeTasksService);
        }
    }

    @Override
    public void startRefreshTasks() {
        if (mExecuteTaskModel != null) {
            mExecuteTaskModel.startRefreshTasks();
        }
    }

    @Override
    public void deleteTask(String taskId) {
        if (mExecuteTaskModel != null) {
            mExecuteTaskModel.deleteTask(taskId);
        }
    }

    @Override
    public void pauseTask(String taskId) {
        if (mExecuteTaskModel != null) {
            mExecuteTaskModel.pauseTask(taskId);
        }
    }

    @Override
    public void resumeTask(String taskId) {
        if (mExecuteTaskModel != null) {
            mExecuteTaskModel.resumeTask(taskId);
        }
    }

    @Override
    public void showOperateTaskPrepare() {
        if (mExecuteTaskView != null) {
            mExecuteTaskView.showOperateTaskPrepareView();
        }
    }

    @Override
    public void showOperateFinished() {
        if (mExecuteTaskView != null) {
            mExecuteTaskView.showOperateTaskFinishedView();
        }
    }

    @Override
    public void showOperateError(String errMsg) {
        if (mExecuteTaskView != null) {
            mExecuteTaskView.showOperateTaskErrorView(errMsg);
        }
    }

    @Override
    public void showRefreshTasksError(String errMsg) {
        if (mExecuteTaskView != null) {
            mExecuteTaskView.showRefreshTasksError(errMsg);
        }
    }

    @Override
    public void startUpload(UploadInfo uploadInfo) {
        if (mExecuteTaskModel != null) {
            mExecuteTaskModel.startUpload(uploadInfo);
        }
    }

    @Override
    public void showUploadingTasks(ArrayList<TaskInfo> uploadingTaskList) {
        if (mExecuteTaskView != null) {
            mExecuteTaskView.showUploadingTasks(uploadingTaskList);
        }
    }

    @Override
    public void showUploadTaskError(String errMsg) {
        if (mExecuteTaskView != null) {
            mExecuteTaskView.showUploadTaskError(errMsg);
        }
    }

    @Override
    public void showRequestUploadFinished() {
        if (mExecuteTaskView != null) {
            mExecuteTaskView.showRequestUploadFinishedView();
        }
    }

    @Override
    public void refreshFileList() {
        if (mExecuteTaskView != null) {
            mExecuteTaskView.showRefreshFileListView();
        }
    }

    @Override
    public void startDownload(DownloadInfo downloadInfo) {
        if (mExecuteTaskModel != null) {
            mExecuteTaskModel.startDownload(downloadInfo);
        }
    }

    @Override
    public void showDownloadingTasks(ArrayList<TaskInfo> downloadingTaskList) {
        if (mExecuteTaskView != null) {
            mExecuteTaskView.showDownloadingTasks(downloadingTaskList);
        }
    }

    @Override
    public void showRequestDownloadError(String errMsg) {
        if (mExecuteTaskView != null) {
            mExecuteTaskView.showDownloadTaskError(errMsg);
        }
    }

    @Override
    public void showRequestDownloadFinished() {
        if (mExecuteTaskView != null) {
            mExecuteTaskView.showRequestDownloadFinishedView();
        }
    }


    @Override
    public void stopAllTask() {
        if (mExecuteTaskModel != null) {
            mExecuteTaskModel.stopAllTask();
        }
    }

    @Override
    public void onDestroy() {
        if (mExecuteTaskModel != null) {
            mExecuteTaskModel.onDestroy();
            mExecuteTaskModel = null;
        }

        mContext = null;
        mExecuteTaskView = null;
    }
}