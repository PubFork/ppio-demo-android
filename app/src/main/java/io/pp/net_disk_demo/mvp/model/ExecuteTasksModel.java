package io.pp.net_disk_demo.mvp.model;

import io.pp.net_disk_demo.service.DownloadService;
import io.pp.net_disk_demo.service.UploadService;

public interface ExecuteTasksModel {

    void bindUploadService(UploadService uploadService);

    void bindDownloadService(DownloadService downloadService);


    void refreshAllTasks();

    void deleteTask(String taskId, String downloadPath);

    void deleteUploadingTask(String bucket, String key, String taskId);

    void pauseTask(String taskId);

    void resumeTask(String taskId);

    void stopAllTask();


    void onDestroy();
}