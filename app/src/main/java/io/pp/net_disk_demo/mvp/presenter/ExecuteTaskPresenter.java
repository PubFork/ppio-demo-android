package io.pp.net_disk_demo.mvp.presenter;

import java.util.ArrayList;

import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.service.DownloadService;
import io.pp.net_disk_demo.service.ExecuteTaskService;
import io.pp.net_disk_demo.service.UploadService;

public interface ExecuteTaskPresenter {

    void bindExecuteTaskService(ExecuteTaskService executeTasksService);

    void bindUploadService(UploadService uploadService);

    void bindDownloadService(DownloadService downloadService);


    void startRefreshTasks();

    void showRefreshTasksError(String errMsg);


    void deleteTask(String taskId);

    void deleteUploadingTask(String bucket, String key, String taskId);

    void pauseTask(String taskId);

    void resumeTask(String taskId);

    void showOperateTaskPrepare();

    void showOperateFinished();

    void showOperateError(String errMsg);

    void showDeleteUploadingTaskFinished(String bucket, String key);


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


    void showUploadTaskList(ArrayList<TaskInfo> uploadTaskList, boolean allRefresh);

    void showDownloadTaskList(ArrayList<TaskInfo> downloadTaskList, boolean allRefresh);


    void onDestroy();
}