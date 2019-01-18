package io.pp.net_disk_demo.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.threadpool.CancelFixedThreadPool;
import io.pp.net_disk_demo.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class ExecuteTaskService extends Service {

    private static final String TAG = "ExecuteTaskService";
    private static final int REFRESH_TASK = 0x11;

    private CancelFixedThreadPool mRefreshTaskPool;

    private TaskListListener mTaskListListener = null;
    private ShowOperateTaskListener mShowOperateTaskListener = null;
    private UploadListener mUploadListener = null;
    private DownloadListener mDownloadListener = null;
    private GetListener mGetListener = null;

    private int mUploadingNotificationId;
    private int mDownloadingNotificationId;
    private NotificationManager mNotificationManager;

    private RefreshTaskHandler mHandler;

    private final ExecuteTaskBinder mBinder = new ExecuteTaskBinder();

    private int mUploadingCount;
    private int mDownloadingCount;

    private boolean mUploadingNotificationShowing = false;
    private boolean mDownloadingNotificationShowing = false;

    private static boolean mIsLogIn = true;

    @Override
    public void onCreate() {
        super.onCreate();

        mRefreshTaskPool = new CancelFixedThreadPool(1);

        mHandler = new RefreshTaskHandler(ExecuteTaskService.this);
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        mUploadingNotificationId = android.os.Process.myPid() + 100;
        mDownloadingNotificationId = android.os.Process.myPid() + 105;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    private Notification getUploadingNotification(double progress) {
        double progress2digits = new BigDecimal(progress * 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        Notification notification;

        String contentStr;
        if (mUploadingCount == 0) {
            contentStr = "one file is ready to upload";
        } else if (mUploadingCount == 1) {
            contentStr = mUploadingCount + " file uploaded " + progress2digits + "%";
        } else {
            contentStr = mUploadingCount + " files has uploaded " + progress2digits + "%";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("ppio", "ppio", NotificationManager.IMPORTANCE_HIGH);

            mNotificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(this, "ppio")
                    .setChannelId("ppio")
                    .setContentTitle("Uploading")
                    .setContentText(contentStr)
                    .setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher).build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "ppio")
                    .setContentTitle("Uploading")
                    .setContentText(contentStr)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true)
                    .setChannelId("ppio");//no effect
            notification = notificationBuilder.build();
        }

        return notification;
    }

    private Notification getDownloadingNotification(double progress) {
        double progress2digits = new BigDecimal(progress * 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        Notification notification;
        String contentStr;

        if (mDownloadingCount == 0) {
            contentStr = "one file is ready to download";
        } else if (mDownloadingCount == 1) {
            contentStr = mDownloadingCount + " file downloaded " + progress2digits + "%";
        } else {
            contentStr = mDownloadingCount + " files has downloaded " + progress2digits + "%";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("ppio", "ppio", NotificationManager.IMPORTANCE_HIGH);

            mNotificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(this, "ppio")
                    .setChannelId("ppio")
                    .setContentTitle("Downloading")
                    .setContentText(contentStr)
                    .setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher).build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "ppio")
                    .setContentTitle("Downloading")
                    .setContentText(contentStr)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true)
                    .setChannelId("ppio");//no effect
            notification = notificationBuilder.build();
        }

        return notification;
    }

    public void startRefreshTask() {
        refreshTask();
    }

    public void refreshTask() {
        if (mIsLogIn) {
            mRefreshTaskPool.execute(new RefreshTaskRunnable(ExecuteTaskService.this));

            mHandler.sendEmptyMessageDelayed(REFRESH_TASK, 1000L);
        }
    }

    public void deleteTask(String taskId) {
        new DeleteTaskAsyncTask(ExecuteTaskService.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        taskId);
    }

    public void pauseTask(String taskId) {
        new PauseTaskAsyncTask(ExecuteTaskService.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        taskId);
    }

    public void resumeTask(String taskId) {
        new ResumeTaskAsyncTask(ExecuteTaskService.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        taskId);
    }

    private void showPrepareOperateTask() {
        if (mShowOperateTaskListener != null) {
            mShowOperateTaskListener.onOperatePrepare();
        }
    }

    private void showOperateTaskFinished() {
        if (mShowOperateTaskListener != null) {
            mShowOperateTaskListener.onOperateFinished();
        }
    }

    private void showOperateTaskError(String errMsg) {
        if (mShowOperateTaskListener != null) {
            mShowOperateTaskListener.onOperateError(errMsg);
        }
    }

    public void setShowTaskListListener(TaskListListener taskListListener) {
        mTaskListListener = taskListListener;
    }

    public void setShowOperateTaskListener(ShowOperateTaskListener showOperateTaskListener) {
        mShowOperateTaskListener = showOperateTaskListener;
    }

    private void showTaskList(final LinkedHashMap<String, TaskInfo> taskInfoHashMap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                double finishedUpload = 0;
                double totalUpload = 0;

                double finishedDownload = 0;
                double totalDownload = 0;

                ArrayList<TaskInfo> uploadingTaskList = new ArrayList<>();
                ArrayList<TaskInfo> downloadingTaskList = new ArrayList<>();

                String finishedTaskId = "";

                for (Map.Entry entry : taskInfoHashMap.entrySet()) {
                    TaskInfo taskInfo = (TaskInfo) entry.getValue();

                    if (TextUtils.isEmpty(finishedTaskId) &&
                            Constant.TaskState.FINISHED.equals(taskInfo.getState())) {
                        finishedTaskId = taskInfo.getId();
                    }

                    if (Constant.TaskType.PUT.equals(taskInfo.getType()) &&
                            !Constant.TaskState.FINISHED.equals(taskInfo.getState())) {
                        uploadingTaskList.add(taskInfo);
                        finishedUpload = finishedDownload + taskInfo.getFinished();
                        totalUpload = totalUpload + taskInfo.getTotal();
                    } else if (Constant.TaskType.GET.equals(taskInfo.getType()) &&
                            !Constant.TaskState.FINISHED.equals(taskInfo.getState())) {
                        downloadingTaskList.add(taskInfo);
                        finishedDownload = finishedDownload + taskInfo.getFinished();
                        totalDownload = totalDownload + taskInfo.getTotal();
                    }
                }

                final ArrayList<TaskInfo> uploadingList = uploadingTaskList;
                final ArrayList<TaskInfo> downloadingList = downloadingTaskList;
                final double upLoadedPercent = totalUpload != 0 ? finishedUpload / totalUpload : 0;
                final double downLoadedPercent = totalDownload != 0 ? finishedDownload / totalDownload : 0;

                final String deleteTaskId = finishedTaskId;

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mUploadingCount = uploadingList.size();
                        mDownloadingCount = downloadingList.size();

                        if (mTaskListListener != null) {
                            if (!TextUtils.isEmpty(deleteTaskId)) {
                                mTaskListListener.onTaskFinished(deleteTaskId);
                            }

                            mTaskListListener.showUploadingTaskList(uploadingList);
                            mTaskListListener.showDownloadingTaskList(downloadingList);
                        }

                        if (mUploadingCount > 0) {
                            mNotificationManager.notify(mUploadingNotificationId, getUploadingNotification(upLoadedPercent));
                        } else {
                            mNotificationManager.cancel(mUploadingNotificationId);
                        }

                        if (mDownloadingCount > 0) {
                            mNotificationManager.notify(mDownloadingNotificationId, getDownloadingNotification(downLoadedPercent));
                        } else {
                            mNotificationManager.cancel(mDownloadingNotificationId);
                        }

                        if (mUploadingCount == 0 && mDownloadingCount == 0) {
                            stopForeground(true);
                        }
                    }
                });
            }
        }).start();
    }

    private void showListTaskError(String errMsg) {
        if (mTaskListListener != null) {
            mTaskListListener.showListTaskError(errMsg);
        }
    }

    public void startUpload(UploadInfo uploadInfo) {
        if (uploadInfo != null) {
            if (!mUploadingNotificationShowing) {
                Notification notification = getUploadingNotification(0);

                if (!mUploadingNotificationShowing &&
                        !mDownloadingNotificationShowing) {
                    startForeground(mUploadingNotificationId, notification);
                }

                mNotificationManager.notify(mUploadingNotificationId, notification);
            }
            new UploadAsyncTask(ExecuteTaskService.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uploadInfo);
        } else {
            showUploadFail(null, "dealInfo is null!");
        }
    }

    private void showUploadFail(final String objectHash, final String failStr) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showToast(ExecuteTaskService.this, failStr, Toast.LENGTH_LONG);
            }
        });
    }

    private void showStartUpload() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mUploadListener != null) {
                    mUploadListener.onUploadStart();
                }
            }
        });
    }

    private void showUploadStartSucceed() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mUploadListener != null) {
                    mUploadListener.onUploadStartSucceed();
                }
            }
        });
    }

    private void showUploadStartFail(final String errMsg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mUploadListener != null) {
                    mUploadListener.onUploadStartFailed(errMsg);
                }
            }
        });
    }

    public void setUploadListener(UploadListener uploadListener) {
        mUploadListener = uploadListener;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        mDownloadListener = downloadListener;
    }

    public void setGetListener(GetListener getListener) {
        mGetListener = getListener;
    }

    public void startDownload(DownloadInfo downloadInfo) {
        if (downloadInfo != null) {
            if (!mUploadingNotificationShowing) {
                Notification notification = getDownloadingNotification(0);
                if (!mUploadingNotificationShowing &&
                        !mDownloadingNotificationShowing) {
                    startForeground(mDownloadingNotificationId, notification);
                }
                mNotificationManager.notify(mDownloadingNotificationId, notification);

            }
            new DownloadAsyncTask(ExecuteTaskService.this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            downloadInfo);
        } else {
            showDownloadFail("", "fileInfo");
        }
    }

    private void showDownloadFail(String objectHash, final String failStr) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ExecuteTaskService.this, "download fail: " + failStr, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDownloadStartSucceed() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mDownloadListener != null) {
                    mDownloadListener.onDownloadStartSucceed();
                }
            }
        });
    }

    private void showDownloadStartFail(final String errMsg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mDownloadListener != null) {
                    mDownloadListener.onDownloadStartFailed(errMsg);
                }
            }
        });
    }

    public void startGet(DownloadInfo downloadInfo) {
        if (downloadInfo != null) {
            if (!mUploadingNotificationShowing) {
                Notification notification = getDownloadingNotification(0);
                if (!mUploadingNotificationShowing &&
                        !mDownloadingNotificationShowing) {
                    startForeground(mDownloadingNotificationId, notification);
                }
                mNotificationManager.notify(mDownloadingNotificationId, notification);

            }
            new GetAsyncTask(ExecuteTaskService.this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            downloadInfo);
        } else {
            showGetStartFail("fileInfo is null");
        }
    }

    private void showGetStarting() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mGetListener != null) {
                    mGetListener.onStartingGet();
                }
            }
        });
    }

    private void showGetStartFail(final String errMsg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mGetListener != null) {
                    mGetListener.onGetStartFailed(errMsg);
                }
            }
        });
    }

    private void showGetStartSucceed() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mGetListener != null) {
                    mGetListener.onGetStartSucceed();
                }
            }
        });
    }

    public void stopAllTask() {
        mIsLogIn = false;
    }

    static class RefreshTaskRunnable implements Runnable {
        final WeakReference<ExecuteTaskService> mExecuteTasksServiceWeakReference;

        public RefreshTaskRunnable(ExecuteTaskService executeTasksService) {
            mExecuteTasksServiceWeakReference = new WeakReference<>(executeTasksService);
        }

        @Override
        public void run() {
            LinkedHashMap<String, TaskInfo> taskInfoHashMap = PossUtil.listTaskForHashMap(new PossUtil.ListTaskListener() {
                @Override
                public void onListTaskError(String errMsg) {
                    if (mExecuteTasksServiceWeakReference.get() != null) {
                        mExecuteTasksServiceWeakReference.get().showListTaskError(errMsg);
                    }
                }
            });

            if (mExecuteTasksServiceWeakReference.get() != null) {
                mExecuteTasksServiceWeakReference.get().showTaskList(taskInfoHashMap);
            }
        }
    }

    static class DeleteTaskAsyncTask extends AsyncTask<String, String, Boolean> {

        final WeakReference<ExecuteTaskService> mExecuteTasksServiceWeakReference;

        public DeleteTaskAsyncTask(ExecuteTaskService executeTasksService) {
            mExecuteTasksServiceWeakReference = new WeakReference<>(executeTasksService);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mExecuteTasksServiceWeakReference.get() != null) {
                mExecuteTasksServiceWeakReference.get().showPrepareOperateTask();
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

            if (mExecuteTasksServiceWeakReference.get() != null) {
                mExecuteTasksServiceWeakReference.get().showOperateTaskError(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean succeed) {
            super.onPostExecute(succeed);

            if (mExecuteTasksServiceWeakReference.get() != null) {
                mExecuteTasksServiceWeakReference.get().showOperateTaskFinished();
            }
        }
    }

    static class PauseTaskAsyncTask extends AsyncTask<String, String, Boolean> {

        final WeakReference<ExecuteTaskService> mExecuteTasksServiceWeakReference;

        public PauseTaskAsyncTask(ExecuteTaskService executeTasksService) {
            mExecuteTasksServiceWeakReference = new WeakReference<>(executeTasksService);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mExecuteTasksServiceWeakReference.get() != null) {
                mExecuteTasksServiceWeakReference.get().showPrepareOperateTask();
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

            if (mExecuteTasksServiceWeakReference.get() != null) {
                mExecuteTasksServiceWeakReference.get().showOperateTaskError(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean succeed) {
            super.onPostExecute(succeed);

            if (mExecuteTasksServiceWeakReference.get() != null) {
                mExecuteTasksServiceWeakReference.get().showOperateTaskFinished();
            }
        }
    }

    static class ResumeTaskAsyncTask extends AsyncTask<String, String, Boolean> {

        final WeakReference<ExecuteTaskService> mExecuteTasksServiceWeakReference;

        public ResumeTaskAsyncTask(ExecuteTaskService executeTasksService) {
            mExecuteTasksServiceWeakReference = new WeakReference<>(executeTasksService);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mExecuteTasksServiceWeakReference.get() != null) {
                mExecuteTasksServiceWeakReference.get().showPrepareOperateTask();
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

            if (mExecuteTasksServiceWeakReference.get() != null) {
                mExecuteTasksServiceWeakReference.get().showOperateTaskError(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean succeed) {
            super.onPostExecute(succeed);

            if (mExecuteTasksServiceWeakReference.get() != null) {
                mExecuteTasksServiceWeakReference.get().showOperateTaskFinished();
            }
        }
    }

    static class RefreshTaskHandler extends Handler {

        private final WeakReference<ExecuteTaskService> mExecuteTasksServiceWeakReference;

        public RefreshTaskHandler(ExecuteTaskService executeTasksService) {
            mExecuteTasksServiceWeakReference = new WeakReference<>(executeTasksService);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int what = msg.what;

            switch (what) {
                case REFRESH_TASK:
                    if (mExecuteTasksServiceWeakReference.get() != null) {
                        mExecuteTasksServiceWeakReference.get().refreshTask();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    static class UploadAsyncTask extends AsyncTask<UploadInfo, String, Boolean> {

        final WeakReference<ExecuteTaskService> mExecuteTasksServiceWeakReference;

        public UploadAsyncTask(ExecuteTaskService executeTasksService) {
            mExecuteTasksServiceWeakReference = new WeakReference<>(executeTasksService);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mExecuteTasksServiceWeakReference.get() != null) {
                mExecuteTasksServiceWeakReference.get().showStartUpload();
            }
        }

        @Override
        protected Boolean doInBackground(UploadInfo[] uploadInfos) {
            final String bucket = "bucket";
            final String fileName = uploadInfos[0].getFileName();
            final String filePath = uploadInfos[0].getFile();
            final String key = "/" + fileName;
            final String meta = "filename=" + "fileName" + ",fileSize=" + uploadInfos[0].getFileSize();
            final long copies = uploadInfos[0].getCopiesCount();
            final String storageTime = uploadInfos[0].getExpiredTime();
            final String chiPriceStr = uploadInfos[0].getChiPrice();
            final boolean encrypt = uploadInfos[0].isSecure();
            final String fileCode = SystemClock.currentThreadTimeMillis() + "_" + fileName;

            Log.e(TAG, "PossUtil.putObject()" +
                    "\n bucket :" + bucket +
                    "\n, key : " + key +
                    "\n, filePath : " + filePath +
                    "\n, meta : " + meta +
                    "\n, chiPrice : " + chiPriceStr +
                    "\n, copies : " + copies +
                    "\n, storage : " + storageTime +
                    "\n, encrypt : " + encrypt);

            if (copies < 1) {
                publishProgress("copies is less than 1");
                return false;
            }

            int chiPrice;
            try {
                chiPrice = Integer.parseInt(chiPriceStr);
                if (chiPrice < 1) {
                    publishProgress("chi price can not be less than 1!");

                    return false;
                }
            } catch (NumberFormatException e) {
                publishProgress("chi price format is incorrect, " + e.getMessage());

                return false;
            }

            PossUtil.createBucket(Constant.Data.DEFAULT_BUCKET, new PossUtil.CreateBucketListener() {
                @Override
                public void onCreateBucketError(String errMsg) {
                    Log.e(TAG, "PossUtil.createBucket() " + errMsg);
                }
            });

            /*
             * meta should not has blank , should be AAA=BBB,CCC=DDD
             *
             * storageTime, should be 2018-01-01, the month of year should be 1~12, should be 01 if it is 1,
             * day of month should 01 if it is 1
             */
            return PossUtil.putObject(Constant.Data.DEFAULT_BUCKET,
                    key,
                    filePath,
                    meta,
                    chiPriceStr,
                    copies,
                    storageTime,
                    encrypt,
                    fileCode,
                    new PossUtil.PutObjectListener() {
                        @Override
                        public void onPutObjectError(String fileCode, String errMsg) {
                            publishProgress(errMsg);
                        }

                        @Override
                        public void onPutObjectFinished(String fileCode) {

                        }
                    });
        }

        @Override
        protected void onProgressUpdate(String[] values) {
            super.onProgressUpdate(values);

            if (mExecuteTasksServiceWeakReference.get() != null) {
                mExecuteTasksServiceWeakReference.get().showUploadStartFail(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean succeed) {
            super.onPostExecute(succeed);

            if (mExecuteTasksServiceWeakReference.get() != null && succeed) {
                mExecuteTasksServiceWeakReference.get().showUploadStartSucceed();
            }
        }
    }

    static class DownloadAsyncTask extends AsyncTask<DownloadInfo, String, Boolean> {

        final WeakReference<ExecuteTaskService> mExecuteTasksServiceWeakReference;

        public DownloadAsyncTask(ExecuteTaskService executeTasksService) {
            mExecuteTasksServiceWeakReference = new WeakReference<>(executeTasksService);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            if (mExecuteTasksServiceWeakReference.get() != null) {
//            }
        }

        @Override
        protected Boolean doInBackground(DownloadInfo[] downloadInfos) {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.US);
            String fileName = sd.format(date);

            File downloadDir = new File(Constant.PPIO_File.DOWNLOAD_DIR);
            boolean directoryExists = downloadDir.exists();
            if (!directoryExists) {
                directoryExists = downloadDir.mkdir();
            }
            if (directoryExists) {
                final String bucket = downloadInfos[0].getBucket();
                final String key = downloadInfos[0].getKey();
                final String chiPrice = downloadInfos[0].getChiPrice();
                String file = Constant.PPIO_File.DOWNLOAD_DIR + key;

                return PossUtil.getObject(bucket, key, file, chiPrice, new PossUtil.GetObjectListener() {
                    @Override
                    public void onGetObjectError(String errMsg) {
                        if (mExecuteTasksServiceWeakReference.get() != null) {
                            mExecuteTasksServiceWeakReference.get().showDownloadStartFail(errMsg);
                        }
                    }
                });
            } else {
                if (mExecuteTasksServiceWeakReference.get() != null) {
                    mExecuteTasksServiceWeakReference.get().showDownloadStartFail("download directory not exist");
                }
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(String[] values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean succeed) {
            super.onPostExecute(succeed);

            if (mExecuteTasksServiceWeakReference.get() != null && succeed) {
                mExecuteTasksServiceWeakReference.get().showDownloadStartSucceed();
            }
        }
    }

    static class GetAsyncTask extends AsyncTask<DownloadInfo, String, Boolean> {

        final WeakReference<ExecuteTaskService> mExecuteTasksServiceWeakReference;

        public GetAsyncTask(ExecuteTaskService executeTasksService) {
            mExecuteTasksServiceWeakReference = new WeakReference<>(executeTasksService);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mExecuteTasksServiceWeakReference.get() != null) {
                mExecuteTasksServiceWeakReference.get().showGetStarting();
            }
        }

        @Override
        protected Boolean doInBackground(DownloadInfo[] downloadInfos) {
            File downloadDir = new File(Constant.PPIO_File.DOWNLOAD_DIR);
            boolean directoryExists = downloadDir.exists();
            if (!directoryExists) {
                directoryExists = downloadDir.mkdir();
            }

            if (directoryExists) {
                final String shareCode = downloadInfos[0].getShareCode();
                final String chiPrice = downloadInfos[0].getChiPrice();

                String name;
                try {
                    String jsonStr = new String(Base64.decode((shareCode.replaceFirst("poss://", "")).getBytes(), Base64.DEFAULT));

                    JSONObject jsonObject = new JSONObject(jsonStr);
                    name = jsonObject.getString("name");
                } catch (JSONException e) {
                    Log.e(TAG, "" + e.getMessage());

                    publishProgress("share code is invalid:" + e.getMessage());

                    e.printStackTrace();

                    return false;
                }

                final String file = Constant.PPIO_File.DOWNLOAD_DIR + name;

                return PossUtil.getObjectShared(shareCode, file, chiPrice, new PossUtil.GetObjectListener() {
                    @Override
                    public void onGetObjectError(String errMsg) {
                        if (mExecuteTasksServiceWeakReference.get() != null) {
                            publishProgress(errMsg);
                        }
                    }
                });
            } else {
                if (mExecuteTasksServiceWeakReference.get() != null) {
                    publishProgress("download directory not exist");
                }
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(String[] values) {
            super.onProgressUpdate(values);

            mExecuteTasksServiceWeakReference.get().showGetStartFail(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean succeed) {
            super.onPostExecute(succeed);

            if (mExecuteTasksServiceWeakReference.get() != null && succeed) {
                mExecuteTasksServiceWeakReference.get().showGetStartSucceed();
            }
        }
    }

    public class ExecuteTaskBinder extends Binder {
        public ExecuteTaskService getExecuteTaskService() {
            return ExecuteTaskService.this;
        }
    }

    public interface TaskListListener {
        void showTaskList(ArrayList<TaskInfo> taskInfoList);

        void showUploadingTaskList(ArrayList<TaskInfo> taskInfoList);

        void showDownloadingTaskList(ArrayList<TaskInfo> taskInfoList);

        void showListTaskError(String errMsg);

        void onTaskFinished(String taskId);
    }

    public interface UploadListener {
        void onUploadStart();

        void onUploadStartSucceed();

        void onUploadStartFailed(String errMsg);
    }

    public interface DownloadListener {
        void onDownloadStartSucceed();

        void onDownloadStartFailed(String errMsg);
    }

    public interface GetListener {
        void onStartingGet();

        void onGetStartSucceed();

        void onGetStartFailed(String errMsg);
    }

    public interface ShowOperateTaskListener {
        void onOperatePrepare();

        void onOperateFinished();

        void onOperateError(String errMsg);
    }
}