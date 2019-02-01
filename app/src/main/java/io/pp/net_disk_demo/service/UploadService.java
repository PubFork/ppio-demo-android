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
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.threadpool.CancelFixedThreadPool;
import poss.Progress;

public class UploadService extends Service {

    private static final String TAG = "UploadService";

    private final UploadServiceBinder mBinder = new UploadServiceBinder();

    private NotificationManager mNotificationManager;

    private CancelFixedThreadPool mRefreshTaskPool = null;

    private Handler mRefreshTaskListHandler = null;

    private ShowUploadTaskListListener mShowUploadTaskListListener = null;
    private UploadListener mUploadListener = null;

    private int mUploadingNotificationId;
    private int mUploadingCount;
    private long mUploadNotificationWhen;

    @Override
    public void onCreate() {
        super.onCreate();

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mUploadingNotificationId = android.os.Process.myPid() + 100;

        mRefreshTaskPool = new CancelFixedThreadPool(1);
        mRefreshTaskListHandler = new Handler();

        mUploadNotificationWhen = System.currentTimeMillis();
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

    private Notification getUploadingNotification(int uploadingCount, double progress) {
        progress = progress <= 1.00d ? progress : 1.00d;

        double progress2digits;
        if (progress != 0) {
            progress2digits = new BigDecimal(progress * 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else {
            progress2digits = 0d;
        }

        Notification notification;

        String contentStr;
        if (uploadingCount == 0) {
            contentStr = "one file is ready to upload";
        } else if (uploadingCount == 1) {
            contentStr = uploadingCount + " file uploaded " + progress2digits + "%";
        } else {
            contentStr = uploadingCount + " files has uploaded " + progress2digits + "%";
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

        notification.when = mUploadNotificationWhen;

        return notification;
    }

    public void upload(UploadInfo uploadInfo) {
        if (uploadInfo != null) {
            new UploadAsyncTask(UploadService.this).execute(uploadInfo);
        } else {
            showUploadFail(null, "dealInfo is null!");
        }
    }

    private void updateNotification(int uploadingCount, double progress) {
        if (uploadingCount > 0) {
            Notification notification = getUploadingNotification(uploadingCount, progress);

            mNotificationManager.notify(mUploadingNotificationId, notification);

            if (mUploadingCount == 0) {
                startForeground(mUploadingNotificationId, notification);
            }
        } else {
            stopForeground(true);
            mNotificationManager.cancel(mUploadingNotificationId);
        }

        mUploadingCount = uploadingCount;
    }

    private void showUploadFail(final String objectHash, final String failStr) {
        //ToastUtil.showToast(ExecuteTaskService.this, failStr, Toast.LENGTH_LONG);
    }

    private void showStartUpload() {
        if (mUploadListener != null) {
            mUploadListener.onUploadStart();
        }
    }

    private void showUploadStartSucceed() {
        if (mUploadListener != null) {
            mUploadListener.onUploadStartSucceed();
        }
    }

    private void showUploadStartFail(final String errMsg) {
        if (mUploadListener != null) {
            mUploadListener.onUploadStartFailed(errMsg);
        }
    }

    public void startShowUploadTaskList() {
        mRefreshTaskPool.execute(new RefreshUploadTaskRunnable(UploadService.this));
    }

    private void showUploadTaskList(ArrayList<TaskInfo> taskInfList) {
        if (mShowUploadTaskListListener != null) {
            mShowUploadTaskListListener.showUploadTaskList(taskInfList);
        }

        mRefreshTaskListHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshTaskPool.execute(new RefreshUploadTaskRunnable(UploadService.this));
            }
        }, 1000l);
    }

    private void showUploadListTaskError(String error) {

    }

    public void setShowUploadTaskListListener(ShowUploadTaskListListener showUploadTaskListListener) {
        mShowUploadTaskListListener = showUploadTaskListListener;
    }

    public void setUploadListener(UploadListener uploadListener) {
        mUploadListener = uploadListener;
    }

    static class UploadAsyncTask extends AsyncTask<UploadInfo, String, Boolean> {

        final WeakReference<UploadService> mExecuteTasksServiceWeakReference;

        public UploadAsyncTask(UploadService executeTasksService) {
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
            final String taskId = PossUtil.putObject(Constant.Data.DEFAULT_BUCKET,
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

            if (!TextUtils.isEmpty(taskId)) {

                boolean hasTaskId = false;

                while (!hasTaskId) {
                    try {
                        HashMap<String, TaskInfo> taskInfoHashMap = PossUtil.listTaskForHashMap(new PossUtil.ListTaskListener() {
                            @Override
                            public void onListTaskError(String errMsg) {

                            }
                        });

                        if (taskInfoHashMap != null) {
                            hasTaskId = taskInfoHashMap.containsKey(taskId);
                        } else {
                            hasTaskId = false;
                        }

                        Thread.sleep(1000l);
                    } catch (Exception e) {
                        e.printStackTrace();

                        hasTaskId = true;
                    }
                }
                return hasTaskId;
            } else {
                return false;
            }
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

    static class RefreshUploadTaskRunnable implements Runnable {
        final WeakReference<UploadService> mUploadServiceWeakReference;

        public RefreshUploadTaskRunnable(UploadService uploadService) {
            mUploadServiceWeakReference = new WeakReference<>(uploadService);
        }

        @Override
        public void run() {
            LinkedHashMap<String, TaskInfo> taskInfoHashMap = PossUtil.listTaskForHashMap(new PossUtil.ListTaskListener() {
                @Override
                public void onListTaskError(String errMsg) {
                    if (mUploadServiceWeakReference.get() != null) {
                        mUploadServiceWeakReference.get().showUploadListTaskError(errMsg);
                    }
                }
            });

            int uploadingCount = 0;
            double finishedUpload = 0;
            double totalUpload = 0;

            ArrayList<TaskInfo> uploadTaskList = new ArrayList<>();

            for (Map.Entry entry : taskInfoHashMap.entrySet()) {
                TaskInfo taskInfo = (TaskInfo) entry.getValue();

                if (Constant.TaskType.PUT.equals(taskInfo.getType())) {
                    if (Constant.TaskState.PENDING.equals(taskInfo.getState()) ||
                            Constant.TaskState.RUNNING.equals(taskInfo.getState()) ||
                            Constant.TaskState.PAUSED.equals(taskInfo.getState())) {
                        uploadingCount++;

                        Progress progress = PossUtil.getTaskProgress(taskInfo.getId());
                        if (progress.getTotalBytes() != 0l) {
                            taskInfo.setFinished(progress.getFinishedBytes());
                            finishedUpload = finishedUpload + taskInfo.getFinished();
                            totalUpload = totalUpload + taskInfo.getTotal();

                            taskInfo.setProgress((double) progress.getFinishedBytes() / progress.getTotalBytes());
                        }
                    }
                    uploadTaskList.add(taskInfo);
                    Log.e(TAG, "RefreshUploadTaskRunnable() taskId == " + taskInfo.getId());

                }
            }

            if (mUploadServiceWeakReference.get() != null) {
                double progress;
                if (totalUpload == 0) {
                    progress = 0;
                } else {
                    progress = (double) finishedUpload / totalUpload;
                }

                mUploadServiceWeakReference.get().updateNotification(uploadingCount, progress);
                mUploadServiceWeakReference.get().showUploadTaskList(uploadTaskList);
            }
        }
    }

    public class UploadServiceBinder extends Binder {
        public UploadService getUploadService() {
            return UploadService.this;
        }
    }

    public interface ShowUploadTaskListListener {
        void showUploadTaskList(ArrayList<TaskInfo> taskInfoList);
    }

    public interface UploadListener {
        void onUploadStart();

        void onUploadStartSucceed();

        void onUploadStartFailed(String errMsg);
    }
}