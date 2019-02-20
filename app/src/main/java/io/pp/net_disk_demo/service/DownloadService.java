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
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.threadpool.CancelFixedThreadPool;
import poss.Progress;

public class DownloadService extends Service {

    private static final String TAG = "DownloadService";

    private final DownloadServiceBinder mBinder = new DownloadServiceBinder();

    private NotificationManager mNotificationManager;

    private CancelFixedThreadPool mRefreshTaskPool = null;

    private Handler mRefreshTaskListHandler = null;

    private ShowDownloadTaskListListener mShowDownloadTaskListListener = null;
    private DownloadListener mDownloadListener = null;
    private DownloadSharedListener mDownloadSharedListener = null;

    private static HashMap<String, String> mDownloadingTaskHashMap = null;

    private int mUploadingNotificationId;
    private int mDownloadingCount;
    private long mDownloadNotificationWhen;

    @Override
    public void onCreate() {
        super.onCreate();

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mUploadingNotificationId = android.os.Process.myPid() + 150;

        mRefreshTaskPool = new CancelFixedThreadPool(1);
        mRefreshTaskListHandler = new Handler();
        mDownloadNotificationWhen = 1000000000l;

        mDownloadingTaskHashMap = new HashMap<>();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Notification getDownloadingNotification(int downloadingCount, double progress) {
        progress = progress <= 1.00d ? progress : 1.00d;

        //Log.e(TAG, "progress = " + progress);
        double progress2digits;
        if (progress != 0) {
            progress2digits = new BigDecimal(progress * 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else {
            progress2digits = 0d;
        }

        Notification notification;

        String contentStr;
        if (downloadingCount == 0) {
            contentStr = "one file is ready to download";
        } else if (downloadingCount == 1) {
            contentStr = downloadingCount + " file downloaded " + progress2digits + "%";
        } else {
            contentStr = downloadingCount + " files has downloaded " + progress2digits + "%";
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
                    .setContentTitle("Uploading")
                    .setContentText(contentStr)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true)
                    .setChannelId("ppio");//no effect
            notification = notificationBuilder.build();
        }

        notification.when = mDownloadNotificationWhen;

        return notification;
    }

    public void download(DownloadInfo downloadInfo) {
        //
        Log.e(TAG, "download download()");
        //
        if (downloadInfo != null) {
            new DownloadAsyncTask(DownloadService.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadInfo);
        } else {
            showDownloadFail(null, "dealInfo is null!");
        }
    }

    public void downloadShared(DownloadInfo downloadInfo) {
        if (downloadInfo != null) {
            new DownloadSharedAsyncTask(DownloadService.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadInfo);
        } else {
            showDownloadFail(null, "dealInfo is null!");
        }
    }

    private void showDownloadStartSucceed() {
        if (mDownloadListener != null) {
            mDownloadListener.onDownloadStartSucceed();
        }
    }

    private void showDownloadStartFail(final String errMsg) {
        if (mDownloadListener != null) {
            mDownloadListener.onDownloadStartFailed(errMsg);
        }
    }

    private void showDownloadSharedStartSucceed() {
        if (mDownloadSharedListener != null) {
            mDownloadSharedListener.onDownloadSharedStartSucceed();
        }
    }

    private void showDownloadSharedStartFail(final String errMsg) {
        if (mDownloadSharedListener != null) {
            mDownloadSharedListener.onDownloadSharedStartFailed(errMsg);
        }
    }

    public void updateNotification(int downloadingCount, double progress) {
        if (downloadingCount > 0) {
            Notification notification = getDownloadingNotification(downloadingCount, progress);
            mNotificationManager.notify(mUploadingNotificationId, notification);

            if (mDownloadingCount == 0) {
                startForeground(mUploadingNotificationId, notification);
            }
        } else {
            stopForeground(true);
            mNotificationManager.cancel(mUploadingNotificationId);
        }

        mDownloadingCount = downloadingCount;
    }

    private void showDownloadFail(final String objectHash, final String failStr) {
    }


    public void startShowDownloadTaskList() {
        reFreshAllDownloadTaskList();

        mRefreshTaskListHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshTaskPool.execute(new RefreshDownloadTaskRunnable(DownloadService.this));
            }
        }, 1000);

    }

    public void reFreshAllDownloadTaskList() {
        new Thread(new RefreshAllDownloadTaskRunnable(DownloadService.this)).start();
    }

    private void showDownloadTaskList(ArrayList<TaskInfo> taskInfList) {
        if (mShowDownloadTaskListListener != null) {
            mShowDownloadTaskListListener.showDownloadTaskList(taskInfList, false);
        }

        mRefreshTaskListHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshTaskPool.execute(new RefreshDownloadTaskRunnable(DownloadService.this));
            }
        }, 1000l);
    }

    private void showRefreshAllDownloadTaskList(ArrayList<TaskInfo> taskInfList) {
        if (mShowDownloadTaskListListener != null) {
            mShowDownloadTaskListListener.showDownloadTaskList(taskInfList, true);
        }

        mRefreshTaskListHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshTaskPool.execute(new RefreshDownloadTaskRunnable(DownloadService.this));
            }
        }, 1000l);
    }

    private void showDownloadListTaskError(String error) {

    }

    public void setShowDownloadTaskListListener(ShowDownloadTaskListListener showDownloadTaskListListener) {
        mShowDownloadTaskListListener = showDownloadTaskListListener;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        mDownloadListener = downloadListener;
    }

    public void setDownloadSharedListener(DownloadSharedListener downloadSharedListener) {
        mDownloadSharedListener = downloadSharedListener;
    }

    static class DownloadAsyncTask extends AsyncTask<DownloadInfo, String, Boolean> {

        final WeakReference<DownloadService> mExecuteTasksServiceWeakReference;

        public DownloadAsyncTask(DownloadService downloadService) {
            mExecuteTasksServiceWeakReference = new WeakReference<>(downloadService);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //
            Log.e(TAG, "download onPreExecute()");
            //
        }

        @Override
        protected Boolean doInBackground(DownloadInfo[] downloadInfos) {
            //
            Log.e(TAG, "download DownloadAsyncTask()");
            //
            File downloadDir = new File(Constant.PPIO_File.DOWNLOAD_DIR);
            boolean directoryExists = downloadDir.exists();
            if (!directoryExists) {
                directoryExists = downloadDir.mkdir();
            }

//            File downloadAccountDir = new File(Constant.PPIO_File.DOWNLOAD_PATH_SUFFIX + PossUtil.getAccount());
//            directoryExists = downloadAccountDir.exists();
//            if (!directoryExists) {
//                directoryExists = downloadAccountDir.mkdir();
//            }

            if (directoryExists) {
                final String bucket = downloadInfos[0].getBucket();
                final String key = downloadInfos[0].getKey();
                final String chiPrice = downloadInfos[0].getChiPrice();

                String fileName = key;
                if (!TextUtils.isEmpty(fileName)) {
                    String prefixNameStr = fileName;
                    String typeSuffixStr = "";
                    if (fileName.contains(".")) {
                        typeSuffixStr = fileName.substring(fileName.lastIndexOf("."), fileName.length());
                        prefixNameStr = fileName.substring(0, fileName.lastIndexOf("."));
                    }

                    int max = 0;
                    File[] downloadedFiles = downloadDir.listFiles();
                    if (downloadedFiles != null) {
                        for (int i = 0; i < downloadedFiles.length; i++) {
                            String name = downloadedFiles[i].getName();

                            if (fileName.equals(name)) {
                                if (max == 0) {
                                    max = 1;
                                }
                            } else if (!TextUtils.isEmpty(name) &&
                                    name.contains(prefixNameStr + "(") &&
                                    name.contains(")" + typeSuffixStr)) {

                                String tempStr = name.replace(prefixNameStr + "(", "");

                                tempStr = tempStr.replace(")" + typeSuffixStr, "");

                                int number = -1;
                                try {
                                    number = Integer.parseInt(tempStr);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }

                                max = number >= max ? (number + 1) : max;
                            }
                        }
                    }

                    if (max > 0) {
                        fileName = prefixNameStr + "(" + max + ")" + typeSuffixStr;
                    }

                    String file = Constant.PPIO_File.DOWNLOAD_PATH_SUFFIX + fileName;

                    return PossUtil.getObject(bucket, key, file, chiPrice, new PossUtil.GetObjectListener() {
                        @Override
                        public void onGetObjectError(String errMsg) {
                            if (mExecuteTasksServiceWeakReference.get() != null) {
                                mExecuteTasksServiceWeakReference.get().showDownloadStartFail(errMsg);
                            }
                        }
                    });
                } else {
                    return false;
                }
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
                mExecuteTasksServiceWeakReference.get().reFreshAllDownloadTaskList();
                mExecuteTasksServiceWeakReference.get().showDownloadStartSucceed();
            }
        }
    }

    static class DownloadSharedAsyncTask extends AsyncTask<DownloadInfo, String, Boolean> {

        final WeakReference<DownloadService> mDownloadServiceWeakReference;

        public DownloadSharedAsyncTask(DownloadService downloadService) {
            mDownloadServiceWeakReference = new WeakReference<>(downloadService);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(DownloadInfo[] downloadInfos) {
            File downloadDir = new File(Constant.PPIO_File.DOWNLOAD_DIR);
            boolean directoryExists = downloadDir.exists();
            if (!directoryExists) {
                directoryExists = downloadDir.mkdir();
            }

//            File downloadAccountDir = new File(Constant.PPIO_File.DOWNLOAD_PATH_SUFFIX + PossUtil.getAccount());
//            directoryExists = downloadAccountDir.exists();
//            if (!directoryExists) {
//                directoryExists = downloadAccountDir.mkdir();
//            }

            if (directoryExists) {
                final String shareCode = downloadInfos[0].getShareCode();
                final String chiPrice = downloadInfos[0].getChiPrice();

                String fileName = "";
                try {
                    String jsonStr = new String(Base64.decode((shareCode.replaceFirst("poss://", "")).getBytes(), Base64.DEFAULT));

                    JSONObject jsonObject = new JSONObject(jsonStr);
                    fileName = jsonObject.getString("name");
                } catch (JSONException e) {
                    Log.e(TAG, "" + e.getMessage());

                    publishProgress("share code is invalid:" + e.getMessage());

                    e.printStackTrace();

                    return false;
                }


                if (!TextUtils.isEmpty(fileName)) {
                    String prefixNameStr = fileName;
                    String typeSuffixStr = "";
                    if (fileName.contains(".")) {
                        typeSuffixStr = fileName.substring(fileName.lastIndexOf("."), fileName.length());
                        prefixNameStr = fileName.substring(0, fileName.lastIndexOf("."));
                    }

                    int max = 0;
                    File[] downloadedFiles = downloadDir.listFiles();
                    if (downloadedFiles != null) {
                        for (int i = 0; i < downloadedFiles.length; i++) {
                            String name = downloadedFiles[i].getName();
                            if (fileName.equals(name)) {
                                if (max == 0) {
                                    max = 1;
                                }
                            } else if (!TextUtils.isEmpty(name) &&
                                    name.contains(prefixNameStr + "(") &&
                                    name.contains(")" + typeSuffixStr)) {

                                String tempStr = name.replace(prefixNameStr + "(", "");
                                tempStr = tempStr.replace(")" + typeSuffixStr, "");

                                int number = -1;
                                try {
                                    number = Integer.parseInt(tempStr);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }

                                max = number >= max ? (number + 1) : max;
                            }
                        }
                    }

                    if (max > 0) {
                        fileName = prefixNameStr + "(" + max + ")" + typeSuffixStr;
                    }

                    final String file = Constant.PPIO_File.DOWNLOAD_PATH_SUFFIX + fileName;

                    return PossUtil.getObjectShared(shareCode, file, chiPrice, new PossUtil.GetObjectListener() {
                        @Override
                        public void onGetObjectError(String errMsg) {
                            publishProgress(errMsg);
                        }
                    });
                } else {
                    return false;
                }
            } else {
                if (mDownloadServiceWeakReference.get() != null) {
                    publishProgress("download directory not exist");
                }
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(String[] values) {
            super.onProgressUpdate(values);

            mDownloadServiceWeakReference.get().showDownloadSharedStartFail(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean succeed) {
            super.onPostExecute(succeed);

            if (mDownloadServiceWeakReference.get() != null && succeed) {
                mDownloadServiceWeakReference.get().reFreshAllDownloadTaskList();
                mDownloadServiceWeakReference.get().showDownloadSharedStartSucceed();
            }
        }
    }

    static class RefreshDownloadTaskRunnable implements Runnable {
        final WeakReference<DownloadService> mDownloadServiceWeakReference;

        public RefreshDownloadTaskRunnable(DownloadService downloadService) {
            mDownloadServiceWeakReference = new WeakReference<>(downloadService);
        }

        @Override
        public void run() {
            LinkedHashMap<String, TaskInfo> taskInfoHashMap = PossUtil.listTaskForHashMap(new PossUtil.ListTaskListener() {
                @Override
                public void onListTaskError(String errMsg) {
                    if (mDownloadServiceWeakReference.get() != null) {
                        mDownloadServiceWeakReference.get().showDownloadListTaskError(errMsg);
                    }
                }
            });

            int downloadingCount = 0;
            double finishedDownload = 0;
            double totalDownload = 0;

            HashMap<String, String> lastDownloadingTaskHashMap = new HashMap<>();
            lastDownloadingTaskHashMap.putAll(mDownloadingTaskHashMap);
            mDownloadingTaskHashMap.clear();

            ArrayList<TaskInfo> uploadTaskList = new ArrayList<>();

            for (Map.Entry entry : taskInfoHashMap.entrySet()) {
                TaskInfo taskInfo = (TaskInfo) entry.getValue();

                if (Constant.TaskType.GET.equals(taskInfo.getType())) {
                    if (Constant.TaskState.PENDING.equals(taskInfo.getState()) ||
                            Constant.TaskState.RUNNING.equals(taskInfo.getState()) ||
                            Constant.TaskState.PAUSED.equals(taskInfo.getState())) {
                        downloadingCount++;

                        Progress progress = PossUtil.getTaskProgress(taskInfo.getId());
                        if (progress.getTotalBytes() != 0l) {
                            taskInfo.setFinished(progress.getFinishedBytes());
                            finishedDownload = finishedDownload + taskInfo.getFinished();
                            totalDownload = totalDownload + taskInfo.getTotal();

                            taskInfo.setProgress((double) progress.getFinishedBytes() / progress.getTotalBytes());
                        }

                        mDownloadingTaskHashMap.put(taskInfo.getId(), taskInfo.getId());
                    } else {
                        if (lastDownloadingTaskHashMap.containsKey(taskInfo.getId())) {
                            taskInfo.setChanged();
                        }
                    }
                    uploadTaskList.add(taskInfo);
                }
            }

            if (mDownloadServiceWeakReference.get() != null) {
                double progress;
                if (totalDownload == 0) {
                    progress = 0;
                } else {
                    progress = (double) finishedDownload / totalDownload;
                }

                mDownloadServiceWeakReference.get().updateNotification(downloadingCount, progress);
                mDownloadServiceWeakReference.get().showDownloadTaskList(uploadTaskList);
            }
        }
    }

    static class RefreshAllDownloadTaskRunnable implements Runnable {
        final WeakReference<DownloadService> mDownloadServiceWeakReference;

        public RefreshAllDownloadTaskRunnable(DownloadService downloadService) {
            mDownloadServiceWeakReference = new WeakReference<>(downloadService);
        }

        @Override
        public void run() {
            LinkedHashMap<String, TaskInfo> taskInfoHashMap = PossUtil.listTaskForHashMap(new PossUtil.ListTaskListener() {
                @Override
                public void onListTaskError(String errMsg) {
                    if (mDownloadServiceWeakReference.get() != null) {
                        mDownloadServiceWeakReference.get().showDownloadListTaskError(errMsg);
                    }
                }
            });

            int downloadingCount = 0;
            double finishedDownload = 0;
            double totalDownload = 0;

            HashMap<String, String> lastDownloadingTaskHashMap = new HashMap<>();
            lastDownloadingTaskHashMap.putAll(mDownloadingTaskHashMap);
            mDownloadingTaskHashMap.clear();

            ArrayList<TaskInfo> uploadTaskList = new ArrayList<>();

            for (Map.Entry entry : taskInfoHashMap.entrySet()) {
                TaskInfo taskInfo = (TaskInfo) entry.getValue();

                if (Constant.TaskType.GET.equals(taskInfo.getType())) {
                    if (Constant.TaskState.PENDING.equals(taskInfo.getState()) ||
                            Constant.TaskState.RUNNING.equals(taskInfo.getState()) ||
                            Constant.TaskState.PAUSED.equals(taskInfo.getState())) {
                        downloadingCount++;

                        Progress progress = PossUtil.getTaskProgress(taskInfo.getId());
                        if (progress.getTotalBytes() != 0l) {
                            taskInfo.setFinished(progress.getFinishedBytes());
                            finishedDownload = finishedDownload + taskInfo.getFinished();
                            totalDownload = totalDownload + taskInfo.getTotal();

                            taskInfo.setProgress((double) progress.getFinishedBytes() / progress.getTotalBytes());
                        }
                        mDownloadingTaskHashMap.put(taskInfo.getId(), taskInfo.getId());
                    } else {
                        if (lastDownloadingTaskHashMap.containsKey(taskInfo.getId())) {
                            taskInfo.setChanged();
                        }
                    }
                    uploadTaskList.add(taskInfo);
                }
            }

            if (mDownloadServiceWeakReference.get() != null) {
                double progress;
                if (totalDownload == 0) {
                    progress = 0;
                } else {
                    progress = (double) finishedDownload / totalDownload;
                }

                mDownloadServiceWeakReference.get().updateNotification(downloadingCount, progress);
                mDownloadServiceWeakReference.get().showRefreshAllDownloadTaskList(uploadTaskList);
            }
        }
    }


    public class DownloadServiceBinder extends Binder {
        public DownloadService getDownloadService() {
            return DownloadService.this;
        }
    }

    public interface ShowDownloadTaskListListener {
        void showDownloadTaskList(ArrayList<TaskInfo> taskInfoList, boolean allRefresh);
    }

    public interface DownloadListener {
        void onStartingDownload();

        void onDownloadStartSucceed();

        void onDownloadStartFailed(String errMsg);
    }

    public interface DownloadSharedListener {
        void onStartingDownloadShared();

        void onDownloadSharedStartSucceed();

        void onDownloadSharedStartFailed(String errMsg);
    }

}
