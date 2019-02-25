package io.pp.net_disk_demo.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.util.FileUtil;
import io.pp.net_disk_demo.util.SystemUtil;
import io.pp.net_disk_demo.util.ToastUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadLogService extends Service {

    private static final String TAG = "UploadLogService";

    private static Context mContext = null;

    private final UploadLogServiceBinder mBinder = new UploadLogServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = UploadLogService.this;
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void uploadLog(String description) {
        new UploadLogAsyncTask().execute(description);
    }

    public static boolean uploadFile(File uploadFile, String RequestURL) {
        boolean uploadSuccess = false;
        long uploadTime = 0l;

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .build();

            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            String authorizationJsonStr = "{\n" +
                    "  \"address\":\"" + PossUtil.getAccount() + "\",\n" +
                    "  \"filename\": \"" + uploadFile.getName() + "\"\n" +
                    "}";

            RequestBody authorizationBody = RequestBody.create(JSON, authorizationJsonStr);

            Request authorizationRequest = new Request.Builder()
                    .url(RequestURL)
                    .header("Content-Type", "application/json")
                    .post(authorizationBody)
                    .build();

            Response authorizationResponse = client.newCall(authorizationRequest).execute();

            String authorizationResult = authorizationResponse.body().string();

            JSONObject authorizationResponseJSONObject = new JSONObject(authorizationResult);
            JSONObject dataJSONObject = authorizationResponseJSONObject.getJSONObject("data");
            final String signature = dataJSONObject.getString("signature");
            final String policy = dataJSONObject.getString("policy");
            final String key = dataJSONObject.getString("key");
            final String bucket = dataJSONObject.getString("bucket");
            final String amzCredential = dataJSONObject.getString("amzCredential");
            final String amzDate = dataJSONObject.getString("amzDate");

//            Log.e(TAG, "signature = " + signature);
//            Log.e(TAG, "policy = " + policy);
//            Log.e(TAG, "key = " + key);
//            Log.e(TAG, "bucket = " + bucket);
//            Log.e(TAG, "amzCredential = " + amzCredential);
//            Log.e(TAG, "amzDate = " + amzDate);

            RequestBody uploadBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("key", key)
                    .addFormDataPart("acl", "authenticated-read")
                    .addFormDataPart("x-amz-server-side-encryption", "AES256")
                    .addFormDataPart("X-Amz-Credential", amzCredential)
                    .addFormDataPart("X-Amz-Algorithm", "AWS4-HMAC-SHA256")
                    .addFormDataPart("X-Amz-Date", amzDate)
                    .addFormDataPart("Policy", policy)
                    .addFormDataPart("X-Amz-Signature", signature)
                    .addFormDataPart("file", uploadFile.getName(),
                            RequestBody.create(MediaType.parse("multipart/form-data"), uploadFile))
                    .build();

            Request uploadRequest = new Request.Builder()
                    .url("https://" + bucket + ".s3.amazonaws.com/")
                    .header("Content-Type", "application/multipart/form-data")
                    .post(uploadBody)
                    .build();

            uploadTime = System.currentTimeMillis();

            Response uploadResponse = client.newCall(uploadRequest).execute();
            String uploadResult = uploadResponse.body().string();

            if (uploadResponse.code() == 204) {
                uploadSuccess = true;
            } else {
                Log.e(TAG, "response.code() = " + uploadResponse.code() + ", uploadResult = " + uploadResult);
                uploadTime = System.currentTimeMillis() - uploadTime;
                Log.e(TAG, "upload to server fail, upload time = " + uploadTime + "millisecond, ");
            }
        } catch (Exception e) {
            e.getMessage();
            uploadTime = System.currentTimeMillis() - uploadTime;
            Log.e(TAG, "upload time = " + uploadTime + "millisecond, upload to server error: " + e.getMessage());
        } finally {
            if (!SystemUtil.isApkInDebug(mContext)) {
                uploadFile.delete();
            }
        }

        return uploadSuccess;
    }

    static class UploadLogAsyncTask extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... values) {
            final String description = values[0];
            try {
                String cacheDirPath = PossUtil.getCacheDir();
                File cacheDir = new File(cacheDirPath);

                if (cacheDir.exists()) {
                    ArrayList<String> logFilePathList = new ArrayList<>();
                    for (int i = 3; i >= 0; i--) {
                        String logFilePath = cacheDirPath + "/gojni." + i + ".log";
                        if (i == 0) {
                            logFilePath = cacheDirPath + "/gojni.log";
                        }

                        if (new File(logFilePath).exists()) {
                            logFilePathList.add(logFilePath);
                        }
                    }

                    long currentTime = System.currentTimeMillis();
                    String reportLogFilePath = cacheDirPath + "/report-" + currentTime + ".log";
                    String zipFilePath = cacheDirPath + "/log-report-" + currentTime + ".zip";
                    if (FileUtil.mergeFiles(logFilePathList, reportLogFilePath)) {
                        String reportJson = "";
                        try {
                            String appVersion = SystemUtil.getAppVersion(mContext);
                            String arch = SystemUtil.getABIS();

                            reportJson = "{\n" +
                                    "  \"systemInfo\": {\n" +
                                    "    \"arch\": \"" + arch + "\",\n" +
                                    "    \"platform\": \"" + "Android" + "\",\n" +
                                    "    \"version\": \"" + Build.VERSION.SDK_INT + "\"\n" +
                                    "  },\n" +
                                    "  \"desc\": \"" + description + "\",\n" +
                                    "  \"demoVersion\": \"" + appVersion + "\"\n}";
                        } catch (Exception e) {
                            Log.e(TAG, "reportJson error: " + e.getMessage());
                            e.printStackTrace();
                        }

                        String reportJsonFilePath = cacheDirPath + "/desc.json";
                        FileUtil.createJsonFile(reportJson, reportJsonFilePath);
                        ArrayList<String> mSrcFilePathList = new ArrayList();
                        mSrcFilePathList.add(reportJsonFilePath);
                        mSrcFilePathList.add(reportLogFilePath);
                        FileUtil.zipMultiFile(mSrcFilePathList, zipFilePath);

                        new File(reportJsonFilePath).delete();
                        new File(reportLogFilePath).delete();

                        return uploadFile(new File(zipFilePath), Constant.UploadLog.UPLOAD_URL_FORMAL);
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG, "upload log error: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean succeed) {
            super.onPostExecute(succeed);

            if (succeed) {
                ToastUtil.showToast(mContext, "upload log succeed!", Toast.LENGTH_LONG);
            } else {
                ToastUtil.showToast(mContext, "upload log failed!", Toast.LENGTH_LONG);
            }
        }
    }

    public class UploadLogServiceBinder extends Binder {
        public UploadLogService getUploadLogService() {
            return UploadLogService.this;
        }
    }
}