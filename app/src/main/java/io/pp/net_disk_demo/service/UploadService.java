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
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.ppio.PossUtil;

public class UploadService extends Service {

    private static final String TAG = "UploadService";

    private final UploadServiceBinder mBinder = new UploadServiceBinder();

    private NotificationManager mNotificationManager;

    private UploadListener mUploadListener = null;

    private int mUploadingNotificationId;
    private int mUploadingCount;

    @Override
    public void onCreate() {
        super.onCreate();

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mUploadingNotificationId = android.os.Process.myPid() + 100;
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
        double progress2digits = new BigDecimal(progress * 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

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

        return notification;
    }

    public void upload(UploadInfo uploadInfo) {
        if (uploadInfo != null) {
            new UploadAsyncTask(UploadService.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uploadInfo);
        } else {
            showUploadFail(null, "dealInfo is null!");
        }
    }

    public void updateNotification(int uploadingCount, double progress) {
        if (uploadingCount > 0) {
            Notification notification = getUploadingNotification(uploadingCount, progress);
            mNotificationManager.notify(mUploadingNotificationId, notification);

            if (mUploadingCount == 0) {
                startForeground(mUploadingNotificationId, notification);
            }
        } else {
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


    public class UploadServiceBinder extends Binder {
        public UploadService getExecuteTaskService() {
            return UploadService.this;
        }
    }

    public interface UploadListener {
        void onUploadStart();

        void onUploadStartSucceed();

        void onUploadStartFailed(String errMsg);
    }
}