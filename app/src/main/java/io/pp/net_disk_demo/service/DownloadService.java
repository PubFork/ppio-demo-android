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
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.ppio.PossUtil;

public class DownloadService extends Service {

    private static final String TAG = "DownloadService";

    private final DownloadServiceBinder mBinder = new DownloadServiceBinder();

    private NotificationManager mNotificationManager;

    private DownloadListener mDownloadListener = null;

    private int mUploadingNotificationId;
    private int mDownloadingCount;

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

    private Notification getDownloadingNotification(int downloadingCount, double progress) {
        double progress2digits = new BigDecimal(progress * 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

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

    public void download(DownloadInfo downloadInfo) {
        if (downloadInfo != null) {
            new DownloadAsyncTask(DownloadService.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadInfo);
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

    public void updateNotification(int downloadingCount, double progress) {
        if (downloadingCount > 0) {
            Notification notification = getDownloadingNotification(downloadingCount, progress);
            mNotificationManager.notify(mUploadingNotificationId, notification);

            if (mDownloadingCount == 0) {
                startForeground(mUploadingNotificationId, notification);
            }
        } else {
            mNotificationManager.cancel(mUploadingNotificationId);
        }

        mDownloadingCount = downloadingCount;
    }

    private void showDownloadFail(final String objectHash, final String failStr) {
    }


    static class DownloadAsyncTask extends AsyncTask<DownloadInfo, String, Boolean> {

        final WeakReference<DownloadService> mExecuteTasksServiceWeakReference;

        public DownloadAsyncTask(DownloadService downloadService) {
            mExecuteTasksServiceWeakReference = new WeakReference<>(downloadService);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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

        final WeakReference<DownloadService> mDownloadServiceWeakReference;

        public GetAsyncTask(DownloadService downloadService) {
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
                        publishProgress(errMsg);
                    }
                });
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

            mDownloadServiceWeakReference.get().showDownloadStartFail(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean succeed) {
            super.onPostExecute(succeed);

            if (mDownloadServiceWeakReference.get() != null && succeed) {
                mDownloadServiceWeakReference.get().showDownloadStartSucceed();
            }
        }
    }

    public class DownloadServiceBinder extends Binder {
        public DownloadService getDownloadService() {
            return DownloadService.this;
        }
    }

    public interface DownloadListener {
        void onStartingDownload();

        void onDownloadStartSucceed();

        void onDownloadStartFailed(String errMsg);
    }

}
