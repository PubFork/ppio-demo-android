package io.pp.net_disk_demo.mvp.view;

import java.util.ArrayList;
import io.pp.net_disk_demo.data.TaskInfo;

public interface ExecuteTaskView {

    void showRefreshTasksError(String errMsg);


    void showUploadingTasks(ArrayList<TaskInfo> uploadingTaskList, boolean allRefresh);

    void showUploadTaskError(String errMsg);


    void showDownloadingTasks(ArrayList<TaskInfo> downloadingTaskList, boolean allRefresh);

    void showDownloadTaskError(String errMsg);


    void showRequestUploadFinishedView();

    void showRequestDownloadFinishedView();


    void showRefreshFileListView();


    void showOperateTaskPrepareView();

    void showOperateTaskErrorView(String errMsg);

    void showOperateTaskFinishedView();

    void showDeleteUploadingTaskFinishedView(String bucket, String key);
}