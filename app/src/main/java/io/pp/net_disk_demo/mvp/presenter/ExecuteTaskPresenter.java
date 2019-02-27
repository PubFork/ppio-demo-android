package io.pp.net_disk_demo.mvp.presenter;

import java.util.ArrayList;

import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.service.DownloadService;
import io.pp.net_disk_demo.service.UploadService;

public interface ExecuteTaskPresenter {

    void bindUploadService(UploadService uploadService);

    void bindDownloadService(DownloadService downloadService);


    void refreshAllTasks();

    void showRefreshTasksError(String errMsg);


    void deleteTask(String taskId, String downloadPath);

    void deleteUploadingTask(String bucket, String key, String taskId);

    void pauseTask(String taskId);

    void resumeTask(String taskId);

    void showOperateTaskPrepare(String message);

    void showOperateFinished();

    void showOperateError(String errMsg);

    void showDeleteUploadingTaskFinished(String bucket, String key);


    void refreshFileList();


    void stopAllTask();


    void showUploadTaskList(ArrayList<TaskInfo> uploadTaskList, boolean allRefresh);

    void showDownloadTaskList(ArrayList<TaskInfo> downloadTaskList, boolean allRefresh);


    void onDestroy();
}