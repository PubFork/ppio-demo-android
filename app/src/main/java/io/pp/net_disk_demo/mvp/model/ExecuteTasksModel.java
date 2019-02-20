package io.pp.net_disk_demo.mvp.model;

import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.service.DownloadService;
import io.pp.net_disk_demo.service.UploadService;

public interface ExecuteTasksModel {

    void bindUploadService(UploadService uploadService);

    void bindDownloadService(DownloadService downloadService);


    void startRefreshTasks();

    void deleteTask(String taskId, String downloadPath);

    void deleteUploadingTask(String bucket, String key, String taskId);

    void pauseTask(String taskId);

    void resumeTask(String taskId);


    void startUpload(UploadInfo uploadInfo);


    void startDownload(DownloadInfo downloadInfo);


    void stopAllTask();


    void onDestroy();
}