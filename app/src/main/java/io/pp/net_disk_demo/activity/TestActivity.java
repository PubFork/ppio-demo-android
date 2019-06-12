package io.pp.net_disk_demo.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.DateInfo;
import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.data.FileInfo;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.ppio.KeyStoreUtil;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.service.DownloadService;
import io.pp.net_disk_demo.service.UploadService;
import io.pp.net_disk_demo.threadpool.CancelFixedThreadPool;
import io.pp.net_disk_demo.util.DateUtil;
import io.pp.net_disk_demo.util.ToastUtil;
import io.pp.net_disk_demo.util.XPermissionUtils;
import poss.Progress;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class TestActivity extends BaseActivity {

    private static final String TAG = TestActivity.class.getSimpleName();

    private static final int LOG_IN = 0x01;
    //private static final int SCAN_UPLOAD_FILE = 0x02;
    private static final int UPLOAD_IN = 0x03;
    //private static final int SCAN_DOWNLOAD_FILE = 0x04;
    private static final int DOWNLOAD_IN = 0x05;

    private Button mStartBtn = null;
    private Button mExitBtn = null;

    private TextView mStatusTv = null;

    private static ArrayList<String> mStatusStrList = null;

    private int mOperate = -1;

    private String mCurrentFileKey = null;
    private CancelFixedThreadPool mLoopThreadPool = null;
    private LoopRunnable mLoopRunnable = null;
    private LoopHandler mHandler = null;

    private UploadService mUploadService = null;
    private DownloadService mDownloadService = null;

    private UploadServiceConnection mUploadServiceConnection = null;
    private DownloadServiceConnection mDownloadServiceConnection = null;

    private Dialog mPermissionAlertDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(TestActivity.this, UploadService.class));
        startService(new Intent(TestActivity.this, DownloadService.class));

        mUploadServiceConnection = new TestActivity.UploadServiceConnection(TestActivity.this);
        mDownloadServiceConnection = new TestActivity.DownloadServiceConnection(TestActivity.this);

        bindService(new Intent(TestActivity.this, UploadService.class),
                mUploadServiceConnection,
                BIND_AUTO_CREATE);

        bindService(new Intent(TestActivity.this, DownloadService.class),
                mDownloadServiceConnection,
                BIND_AUTO_CREATE);

        setContentView(R.layout.acrivity_second_layout);

        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean can_write_storage = false;
        boolean can_read_storage = false;
        boolean can_use_internet = false;
        boolean can_listen_internet = false;
        boolean can_access_wifi_state = false;
        boolean can_read_phone_state = false;

//        boolean can_use_camera = false;
//        boolean can_set_foreground_service = false;
//        boolean can_open_apk = false;

        for (int i = 0; i < permissions.length; i++) {
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[i])) {
                try {
                    if (grantResults[i] == PERMISSION_GRANTED) {
                        can_write_storage = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permissions[i])) {
                try {
                    if (grantResults[i] == PERMISSION_GRANTED) {
                        can_read_storage = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (Manifest.permission.INTERNET.equals(permissions[i])) {
                try {
                    if (grantResults[i] == PERMISSION_GRANTED) {
                        can_use_internet = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (Manifest.permission.ACCESS_NETWORK_STATE.equals(permissions[i])) {
                try {
                    if (grantResults[i] == PERMISSION_GRANTED) {
                        can_listen_internet = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (Manifest.permission.ACCESS_WIFI_STATE.equals(permissions[i])) {
                try {
                    if (grantResults[i] == PERMISSION_GRANTED) {
                        can_access_wifi_state = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (Manifest.permission.READ_PHONE_STATE.equals(permissions[i])) {
                try {
                    if (grantResults[i] == PERMISSION_GRANTED) {
                        can_read_phone_state = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //Whether all permissions are given, if given, check if has log in, not given, direct finish
        if (can_write_storage &&
                can_read_storage &&
                can_use_internet &&
                can_listen_internet
//                &&
//                can_use_camera
            //&&
            //can_set_foreground_service
                ) {
            init();
        } else {
            StringBuilder deniedPermission = new StringBuilder("");

            if (!can_write_storage || !can_listen_internet) {
                deniedPermission.append("internet permissions ");
            }

            if (!can_read_storage || can_use_internet) {
                deniedPermission.append("internet permissions ");
            }

            showPermissionDialog(deniedPermission.toString());
        }
    }

    @Override
    protected void onDestroy() {
        mLoopThreadPool.remove(mLoopRunnable);

        //service
        unbindService(mUploadServiceConnection);
        unbindService(mDownloadServiceConnection);

        mUploadService = null;
        mDownloadService = null;

        mUploadServiceConnection = null;
        mDownloadServiceConnection = null;

        super.onDestroy();
    }

    void init() {
        initView();

        initListener();

        initData();

        mOperate = LOG_IN;
        mHandler.sendEmptyMessageDelayed(LOG_IN, 0);
    }

    private void initView() {
        mStartBtn = findViewById(R.id.start_btn);
        mExitBtn = findViewById(R.id.exit_btn);
        mStatusTv = findViewById(R.id.status_tv);
    }

    private void initListener() {
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartBtn.setEnabled(false);
                mHandler.sendEmptyMessageDelayed(UPLOAD_IN, 0);
            }
        });

        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

    private void initData() {
        mStatusStrList = new ArrayList<>();

        mLoopThreadPool = new CancelFixedThreadPool(1);
        mLoopRunnable = new LoopRunnable(TestActivity.this);
        mHandler = new LoopHandler();

        mCurrentFileKey = "";
    }

    protected void checkPermissions() {
        //setImmersiveStatusBar();

        if (!XPermissionUtils.checkPermissionsForActivity(TestActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.READ_PHONE_STATE})) {

            //If there are permissions not given, apply for these permissions
            XPermissionUtils.requestPermissionsForActivity(TestActivity.this,
                    1,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.READ_PHONE_STATE
                    },

                    new XPermissionUtils.OnPermissionListener() {
                        @Override
                        public void onPermissionGranted() {

                        }

                        @Override
                        public void onPermissionDenied() {
                            //
                            Log.e(TAG, "init() denied!");
                            //
                            showPermissionDialog("permissions ");

                            //finish();
                        }
                    });
        } else {
            init();
        }
    }

    private void showPermissionDialog(String deniedPermission) {
        if (mPermissionAlertDialog != null && mPermissionAlertDialog.isShowing()) {
            mPermissionAlertDialog.dismiss();
        }
        mPermissionAlertDialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
        builder.setTitle("Permission Alert");
        builder.setMessage("you need " + deniedPermission + " to run the app, or the app wont' run");

        builder.setPositiveButton("Yeas, I know", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mPermissionAlertDialog != null && mPermissionAlertDialog.isShowing()) {
                    mPermissionAlertDialog.dismiss();
                }
                mPermissionAlertDialog = null;

                finish();
            }
        });

        builder.setCancelable(false);

        mPermissionAlertDialog = builder.create();
        mPermissionAlertDialog.show();
    }


    private void loop() {
        //
        Log.e(TAG, "loop() mOperate = " + mOperate);
        //

        switch (mOperate) {
            case LOG_IN:

                logIn();

                break;

            case UPLOAD_IN:

                uploadScan();

                break;

            case DOWNLOAD_IN:

                downloadScan();

                break;

            default:
                break;
        }
    }


    private void logIn() {
        //
        Log.e(TAG, "logIn()");
        //

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                disableAllBtn();
                mStatusTv.setText("log in ...");
            }
        });

        final String keyStoreStr = "{\"address\":\"ppio1XEVkjrbs2B8Xwq89e4KMij7fmgZc1yCkv\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"cipherparams\":{\"iv\":\"706c66178af93a5cd0d837ec1a90dbac\"},\"ciphertext\":\"1ebbe3071210792ac8334e3604da9f71905c9f3503087a2137f738de3d9975b4\",\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":4096,\"p\":1,\"r\":8,\"salt\":\"7f1b19ce619b5ae6afd4ca3fc73155e4f83507d2df1034baaeadf53586055a1c\"},\"mac\":\"3d98e3c06227d7df9ed69bdab543dcf0d8550dbb876b97dcd1684f7f37e1c8b9\",\"machash\":\"sha3256\",\"version\":4},\"id\":\"462699d5-be4b-4d69-a545-370b12a40847\",\"version\":4}";
        final String passPhrase = "123456";

        final String address = KeyStoreUtil.checkKeyStoreAndPassPhrase(keyStoreStr, passPhrase);

        if (TextUtils.isEmpty(address)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStatusTv.setText("log in failed");
                }
            });

            return;
        }

        boolean loginSucceed = PossUtil.logInFromKeyStore(keyStoreStr, passPhrase, address, new PossUtil.LogInListener() {
            @Override
            public void onLogInError(String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStatusTv.setText("log in failed");
                    }
                });
            }
        });

        //
        Log.e(TAG, "login() loginSucceed =  " + loginSucceed);
        //

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableAllBtn();

                if (loginSucceed) {
                    mStatusTv.setText("log in succeed");
                } else {
                    mStatusTv.setText("log in failed");
                }
            }
        });
    }

    private boolean hasRunningTask(String taskId) {
        //
        Log.e(TAG, " hasRunningTask()");
        //

        ArrayList<TaskInfo> taskInfoList = PossUtil.listTask(new PossUtil.ListTaskListener() {
            @Override
            public void onListTaskError(String errMsg) {
            }
        });

        for (int i = 0; i < taskInfoList.size(); i++) {
            TaskInfo taskInfo = taskInfoList.get(i);

            if (taskId.equals(taskInfo.getId())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Progress progress = PossUtil.getTaskProgress(taskInfo.getId());
                        double progressPercent = 0;
                        if (progress.getTotalBytes() != 0) {
                            progressPercent = (double) progress.getFinishedBytes() / progress.getTotalBytes();
                        }

                        String statusStr = DateUtil.getCurrentTimeStr() + " " + taskInfo.getType() + " to: " + taskInfo.getTo() + " " + taskInfo.getState() + " [" + taskInfo.getError() + "]\n\n";
                        mStatusStrList.add(0, statusStr);
                        if (mStatusStrList.size() > 100) {
                            mStatusStrList.remove(100);
                        }


                        String str = "";
                        for (int i = 0; i < mStatusStrList.size(); i++) {
                            str = str + mStatusStrList.get(i);
                        }

                        mStatusTv.setText(str);


//                    mStatusTv.setText("hasRunningTask() Id: " + taskInfo.getId() + "\n" +
//                            "type: " + taskInfo.getType() + "\n" +
//                            "form: " + taskInfo.getFrom() + "\n" +
//                            "to: " + taskInfo.getTo() + "\n" +
//                            "state: " + taskInfo.getState() + "\n" +
//                            "error: " + taskInfo.getError() + "\n" +
//                            "progress: " + progressPercent);

                        Log.e(TAG, "hasRunningTask()() Id: " + taskInfo.getId() + "\n" +
                                "type: " + taskInfo.getType() + "\n" +
                                "form: " + taskInfo.getFrom() + "\n" +
                                "to: " + taskInfo.getTo() + "\n" +
                                "state: " + taskInfo.getState() + "\n" +
                                "error: " + taskInfo.getError() + "\n" +
                                "progress: " + progressPercent);
                    }
                });
            }

            if ((Constant.TaskState.PENDING.equals(taskInfo.getState()) ||
                    Constant.TaskState.RUNNING.equals(taskInfo.getState()))) {

                return true;
            }
        }

        return false;
    }

    private void uploadScan() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "ppio_upload_file");

        UploadInfo uploadInfo = new UploadInfo();
        final String key = DateUtil.getCurrentTimeStr() + file.getName();
        mCurrentFileKey = key;
        uploadInfo.setFileName(key);
        uploadInfo.setFile(file.getAbsolutePath());

        Calendar calendar = Calendar.getInstance();
        //default expired date is a month later
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        DateInfo dateInfo = new DateInfo(calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH));

        uploadInfo.setExpiredTime(dateInfo.getDate());

        uploadInfo.setFileSize(file.length());
        uploadInfo.setChiPrice("" + 100);
        uploadInfo.setCopiesCount(5);

        String taskId = possUploadObject(uploadInfo, new PossUtil.PutObjectListener() {
            @Override
            public void onPutObjectError(String fileCode, String errMsg) {
                //upload fail
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStatusTv.setText("upload " + uploadInfo.getFile() + "failed, \n errpor: " + errMsg);
                    }
                });

                mHandler.sendEmptyMessageDelayed(DOWNLOAD_IN, 4000l);
            }

            @Override
            public void onPutObjectFinished(String fileCode) {

            }
        });

        String statusStr = DateUtil.getCurrentTimeStr() + " put" + " to: " + uploadInfo.getBucket() + "/" + uploadInfo.getKey() + " start upload  []\n\n";

        if (!TextUtils.isEmpty(taskId)) {
            //start upload succeed
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStatusTv.setText("uploading " + uploadInfo.getFile());
                }
            });

            while (hasRunningTask(taskId)) {
                //
                Log.e(TAG, "uploadScan() while (hasRunningTask())");
                //
                try {
                    Thread.sleep(2000l);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mHandler.sendEmptyMessageDelayed(DOWNLOAD_IN, 2000l);
        } else {
            statusStr = DateUtil.getCurrentTimeStr() + " put" + " to: " + uploadInfo.getBucket() + "/" + uploadInfo.getKey() + " start upload failed []\n\n";
        }

        mStatusStrList.add(0, statusStr);
        if (mStatusStrList.size() > 100) {
            mStatusStrList.remove(100);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String str = "";
                for (int i = 0; i < mStatusStrList.size(); i++) {
                    str = str + mStatusStrList.get(i);
                }

                mStatusTv.setText(str);
            }
        });
    }

    private void downloadScan() {
        ArrayList<FileInfo> list = PossUtil.listObject(Constant.Data.DEFAULT_BUCKET, new PossUtil.ListObjectListener() {
            @Override
            public void onListObjectError(String errMsg) {

            }
        });

        //
        Log.e(TAG, "downloadScan() download list = " + list);
        //
        if (list != null) {
            //
            Log.e(TAG, "downloadScan() list.size() = " + list.size());
            //
            for (int i = 0; i < list.size(); i++) {
                FileInfo fileInfo = list.get(i);
                Log.e(TAG, "downloadScan() list.get(" + i + "). bucket  = " + fileInfo.getBucketName());
                Log.e(TAG, "downloadScan() list.get(" + i + "). key  = " + fileInfo.getName());
            }
        }

        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setBucket(Constant.Data.DEFAULT_BUCKET);
        downloadInfo.setKey(mCurrentFileKey);
        downloadInfo.setChiPrice("" + 100);

        String taskId = possGetObject(downloadInfo, new PossUtil.GetObjectListener() {
            @Override
            public void onGetObjectError(String errMsg) {
                //start download fail
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "downloadScan() fail " + downloadInfo.getBucket() + downloadInfo.getKey() + " \n error: " + errMsg);
                        mStatusTv.setText("downloadScan() fail " + downloadInfo.getBucket() + downloadInfo.getKey() + " \n error: " + errMsg);
                    }
                });

                mHandler.sendEmptyMessageDelayed(UPLOAD_IN, 4000l);
            }
        });

        String statusStr = DateUtil.getCurrentTimeStr() + " get" + " to: " + Constant.PPIO_File.DOWNLOAD_DIR + "/" + downloadInfo.getKey() + " start download  fail[]\n\n";

        if (!TextUtils.isEmpty(taskId)) {//start download succeed
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStatusTv.setText("downloading " + downloadInfo.getBucket() + "/" + downloadInfo.getKey());
                }
            });

            while (hasRunningTask(taskId)) {
                //
                Log.e(TAG, "downloadScan() while (hasRunningTask())");
                //
                try {
                    Thread.sleep(2000l);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            mHandler.sendEmptyMessageDelayed(UPLOAD_IN, 2000l);
        } else {
            Log.e(TAG, "download fail...");
            statusStr = DateUtil.getCurrentTimeStr() + " put" + " to: " + Constant.PPIO_File.DOWNLOAD_DIR + "/" + downloadInfo.getKey() + " start download  []\n\n";
        }

        mStatusStrList.add(0, statusStr);
        if (mStatusStrList.size() > 100) {
            mStatusStrList.remove(100);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String str = "";
                for (int i = 0; i < mStatusStrList.size(); i++) {
                    str = str + mStatusStrList.get(i);
                }

                mStatusTv.setText(str);
            }
        });
    }

    private String possUploadObject(UploadInfo uploadInfo, PossUtil.PutObjectListener putObjectListener) {
        final String bucket = "bucket";
        //final String fileName = uploadInfo.getFileName();
        final String filePath = uploadInfo.getFile();
        final String key = uploadInfo.getFileName();
        final String meta = "filename=" + "fileName" + ",fileSize=" + uploadInfo.getFileSize();
        final long copies = uploadInfo.getCopiesCount();
        final String storageTime = uploadInfo.getExpiredTime();
        final String chiPriceStr = uploadInfo.getChiPrice();
        final boolean encrypt = uploadInfo.isSecure();
        final String fileCode = uploadInfo.getFileName();

        Log.e(TAG, "possUploadObject()" +
                "\n bucket :" + bucket +
                "\n, key : " + key +
                "\n, filePath : " + filePath +
                "\n, meta : " + meta +
                "\n, chiPrice : " + chiPriceStr +
                "\n, copies : " + copies +
                "\n, storage : " + storageTime +
                "\n, encrypt : " + encrypt);

        if (copies < 1) {
            //publishProgress("copies is less than 1");
            return "";
        }

        int chiPrice;
        try {
            chiPrice = Integer.parseInt(chiPriceStr);
            if (chiPrice < 1) {
                //publishProgress("chi price can not be less than 1!");

                return "";
            }
        } catch (NumberFormatException e) {
            //publishProgress("chi price format is incorrect, " + e.getMessage());

            return "";
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
                putObjectListener);

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

                    Thread.sleep(2000l);
                } catch (Exception e) {
                    e.printStackTrace();

                    hasTaskId = true;
                }
            }
            return taskId;
        }

        return "";
    }

    private String possGetObject(DownloadInfo downloadInfo, PossUtil.GetObjectListener getObjectListener) {
        File downloadDir = new File(Constant.PPIO_File.DOWNLOAD_DIR);
        boolean directoryExists = downloadDir.exists();
        if (!directoryExists) {
            directoryExists = downloadDir.mkdir();
        }

        if (directoryExists) {
            final String bucket = downloadInfo.getBucket();
            final String key = downloadInfo.getKey();
            final String chiPrice = downloadInfo.getChiPrice();

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

                return PossUtil.getObject(bucket, key, file, chiPrice, getObjectListener);
            }
        }

        return "";
    }

    private void disableAllBtn() {
//        mUploadBtn.setEnabled(false);
//        mDownloadBtn.setEnabled(false);
    }

    private void enableAllBtn() {
//        mUploadBtn.setEnabled(true);
//        mDownloadBtn.setEnabled(true);
    }

    public void bindUploadService(IBinder service) {
        mUploadService = ((UploadService.UploadServiceBinder) service).getUploadService();
        mUploadService.startNotification();
    }

    public void bindDownloadService(IBinder service) {
        mDownloadService = ((DownloadService.DownloadServiceBinder) service).getDownloadService();
        mDownloadService.startNotification();
    }


    private class LoopHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);

            //
            Log.e(TAG, "dispatchMessage() mOperate = " + mOperate);
            //

            mOperate = msg.what;
            mLoopThreadPool.execute(mLoopRunnable);
        }
    }

    static class LoopRunnable implements Runnable {
        final WeakReference<TestActivity> mTestActivityWeakReference;

        public LoopRunnable(TestActivity secondActivity) {
            mTestActivityWeakReference = new WeakReference<>(secondActivity);
        }

        @Override
        public void run() {
            //
            Log.e(TAG, "LoopRunnable()");
            //
            if (mTestActivityWeakReference.get() != null) {
                mTestActivityWeakReference.get().loop();
            }
        }
    }

    static class UploadServiceConnection implements ServiceConnection {

        final WeakReference<TestActivity> secondActivityWeakReference;

        public UploadServiceConnection(TestActivity secondActivity) {
            secondActivityWeakReference = new WeakReference<>(secondActivity);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (secondActivityWeakReference.get() != null) {
                secondActivityWeakReference.get().bindUploadService(service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    static class DownloadServiceConnection implements ServiceConnection {

        final WeakReference<TestActivity> secondActivityWeakReference;

        public DownloadServiceConnection(TestActivity secondActivity) {
            secondActivityWeakReference = new WeakReference<>(secondActivity);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (secondActivityWeakReference.get() != null) {
                secondActivityWeakReference.get().bindDownloadService(service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}