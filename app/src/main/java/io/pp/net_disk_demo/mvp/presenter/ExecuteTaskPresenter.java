package io.pp.net_disk_demo.mvp.presenter;

import java.util.ArrayList;

import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.service.ExecuteTaskService;

public interface ExecuteTaskPresenter {

    void bindExecuteTaskService(ExecuteTaskService executeTasksService);


    void startRefreshTasks();

    void showRefreshTasksError(String errMsg);


    void deleteTask(String taskId);

    void pauseTask(String taskId);

    void resumeTask(String taskId);

    void showOperateTaskPrepare();

    void showOperateFinished();

    void showOperateError(String errMsg);


    void startUpload(UploadInfo uploadInfo);

    void showUploadingTasks(ArrayList<TaskInfo> uploadingTaskList);

    void showUploadTaskError(String errMsg);

    void showRequestUploadFinished();

    void refreshFileList();


    void startDownload(DownloadInfo downloadInfo);

    void showDownloadingTasks(ArrayList<TaskInfo> downloadingTaskList);

    void showRequestDownloadError(String errMsg);

    void showRequestDownloadFinished();


    void stopAllTask();


    void onDestroy();
}