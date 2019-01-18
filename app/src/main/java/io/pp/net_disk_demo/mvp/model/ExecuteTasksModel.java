package io.pp.net_disk_demo.mvp.model;

import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.service.ExecuteTaskService;

public interface ExecuteTasksModel {

    void bindExecuteTaskService(ExecuteTaskService executeTasksService);


    void startRefreshTasks();

    void deleteTask(String taskId);

    void pauseTask(String taskId);

    void resumeTask(String taskId);


    void startUpload(UploadInfo uploadInfo);


    void startDownload(DownloadInfo downloadInfo);


    void stopAllTask();


    void onDestroy();
}