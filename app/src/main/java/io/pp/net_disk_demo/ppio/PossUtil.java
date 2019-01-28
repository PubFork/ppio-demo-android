package io.pp.net_disk_demo.ppio;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.data.FileInfo;
import io.pp.net_disk_demo.data.ObjectStatus;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.util.StringUtil;
import io.pp.net_disk_demo.util.TimeConverterUtil;
import poss.Config;
import poss.Poss;
import poss.User;

public class PossUtil {

    private static final String TAG = "PossUtil";

    private final static boolean mIsOffice = false;

    private static User mUser = null;

    private static String mMnemonicStr = "";
    private static String mKeyStoreStr = "";
    private static String mPasswordStr = "";
    private static String mPrivateKeyStr = "";
    private static String mAddressStr = "";
    private static String mStorageChiPrice = "100";
    private static String mDownloadChiPrice = "100";

    private static String mCacheDir = "";

    static public void setMnemonicStr(String mnemonicStr) {
        mMnemonicStr = mnemonicStr;
    }

    static public void setKeyStoreStr(String keyStoreStr) {
        mKeyStoreStr = keyStoreStr;
    }

    static public void setPasswordStr(String passwordStr) {
        mPasswordStr = passwordStr;
    }

    static public void setPrivateKeyStr(String privateKeyStr) {
        mPrivateKeyStr = privateKeyStr;
    }

    static public void setAddressStr(String addressStr) {
        mAddressStr = addressStr;
    }

    static public void setStorageChiPrice(String storageChiPrice) {
        mStorageChiPrice = storageChiPrice;
    }

    static public void setDownloadChiPrice(String downloadChiPrice) {
        mDownloadChiPrice = downloadChiPrice;
    }

    static public String getMnemonicStr() {
        return mMnemonicStr;
    }

    static public String getKeyStoreStr() {
        return mKeyStoreStr;
    }

    static public String getPasswordStr() {
        return mPasswordStr;
    }

    static public String getPrivateKeyStr() {
        return mPrivateKeyStr;
    }

    static public String getAddressStr() {
        return mAddressStr;
    }

    static public String getStorageChiPrice() {
        return mStorageChiPrice;
    }

    static public String getDownloadChiPrice() {
        return mDownloadChiPrice;
    }

    static public String getCacheDir() {
        return mCacheDir;
    }

    static boolean register() {
        return true;
    }

    public static boolean logIn(final String keyStoreStr, LogInListener logInListener) {
        return true;
    }

    public static boolean logInFromKeyStore(final String keyStoreStr, final String passPhrase, final String address, LogInListener logInListener) {
        try {
            Config config = Poss.createDefaultConfig();

            config.setTCPPort(8068);
            config.setUDPPort(8068);
            config.setRPCPort(18068);

            config.setZone(2);

            boolean directoryExist;

            File appCacheDir = new File(Constant.PPIO_File.APP_CACHE_DIR);

            directoryExist = appCacheDir.exists();
            if (!directoryExist) {
                directoryExist = appCacheDir.mkdir();
            }

            if (!directoryExist) {
                if (logInListener != null) {
                    logInListener.onLogInError("create directory fail");
                }

                return false;
            }

            File cacheDir = new File(Constant.PPIO_File.CACHE_DIR_PREFIX + address);

            directoryExist = cacheDir.exists();
            if (!directoryExist) {
                directoryExist = cacheDir.mkdir();
            }

            if (directoryExist) {
                mCacheDir = cacheDir.getAbsolutePath();

                config.setDir(cacheDir.getAbsolutePath());

                config.setTestNet("test");

                config.setBootstrap("[\n" +
                        "  {\n" +
                        "    \"Name\": \"ali-bootstrap\",\n" +
                        "    \"IP\": \"47.110.88.167\",\n" +
                        "    \"TCPPort\": 8020,\n" +
                        "    \"UDPPort\": 8020,\n" +
                        "    \"PeerID\": \"\"\n" +
                        "  },\n" +

                        "  {\n" +
                        "    \"Name\": \"aws-bootstrap\",\n" +
                        "    \"IP\": \"54.202.181.27\",\n" +
                        "    \"TCPPort\": 8020,\n" +
                        "    \"UDPPort\": 8020,\n" +
                        "    \"PeerID\": \"\"\n" +
                        "  }\n" +
                        "]");

                config.getPayment().setIP("ad04b30b910c311e9b71c02d26ce9aff-567092461.us-west-2.elb.amazonaws.com");
                config.getPayment().setUDPPort(0);
                config.getPayment().setTCPPort(0);
                config.getPayment().setHTTPPort(18030);

                if (mIsOffice) {
                    config.getQosServerConfig().setEnable(true);
                    config.getQosServerConfig().setNetwork("udp");
                    config.getQosServerConfig().setAddr("192.168.50.208:9090");//if the address is incorrect, the qoslog will saved in local
                    config.getQosServerConfig().setTag("ppioqos");
                    config.getQosServerConfig().setDir(Constant.PPIO_File.CACHE_DIR_PREFIX + address + Constant.PPIO_File.CACHE_QOS_DIR_SUFFIX);
                } else {
                    config.getQosServerConfig().setEnable(true);
                    config.getQosServerConfig().setNetwork("udp");
                    config.getQosServerConfig().setAddr("ad416ba1c124611e9a39d06111ae4d23-1840383830.us-west-2.elb.amazonaws.com:80");//if the address is incorrect, the qoslog will saved in local
                    config.getQosServerConfig().setTag("ppioqos");
                    config.getQosServerConfig().setDir(Constant.PPIO_File.CACHE_DIR_PREFIX + address + Constant.PPIO_File.CACHE_QOS_DIR_SUFFIX);
                }

                //
                Log.e(TAG, "");
                //

                //Poss.initKeyStoreData(String keystoreData, String datadir)
                //the datadir is the datadir in Config
                Poss.initKeyStoreData(keyStoreStr, config.getDir());

                config.setKeyPassphrase(passPhrase);
                mUser = Poss.createUser(config);

                mUser.initKeyStoreData(keyStoreStr);

                mUser.startDaemon();

                return true;
            } else {
                if (logInListener != null) {
                    logInListener.onLogInError("create directory fail");
                }

                return false;
            }
        } catch (Exception e) {
            if (logInListener != null) {
                logInListener.onLogInError(e.getMessage());
            }

            Log.e(TAG, "login error : " + e.getMessage());

            e.printStackTrace();

            return false;
        }
    }

    public static User getUser() {
        return mUser;
    }

    public static String getAccountKey() {
        try {
            return mUser.exportRootHash();
            //return mUser.exportWalletAccount();
        } catch (Exception e) {
            Log.e(TAG, "getAccountKey() error: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    public static String getAccount() {
        try {
            return mUser.exportWalletAccount();
        } catch (Exception e) {
            Log.e(TAG, "getAccount() error: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    public static boolean logOut(LogOutListener logOutListener) {
        if (mUser != null) {
            try {
                mUser.stopDaemon();
                mUser = null;
            } catch (Exception e) {
                if (logOutListener != null) {
                    logOutListener.onLofOutError(e.getMessage());
                }
            }
        } else {

        }

        return false;
    }

    /*
     * the bucket name should not has uppercase or  underline, you can use lowercase and number
     */
    public static boolean createBucket(String bucket, CreateBucketListener createBucketListener) {
        if (mUser != null) {
            try {
                mUser.createBucket(bucket);
            } catch (Exception e) {
                if (createBucketListener != null) {
                    createBucketListener.onCreateBucketError(e.getMessage());
                }
            }
        } else {

        }

        return true;
    }

    public static boolean putObject(String bucket, String key, String file, String meta, String chiPrice, long copies, String expires, boolean encrypt, String fileCode, PutObjectListener putObjectListener) {
        Log.e(TAG, "putObject() chiPrice = " + chiPrice);

        if (mUser != null) {
            try {
                mUser.putObject(bucket, key, file, meta, chiPrice, copies, expires, encrypt);
            } catch (Exception e) {
                if (putObjectListener != null) {
                    putObjectListener.onPutObjectError(fileCode, e.getMessage());
                }

                Log.e(TAG, "putObject() error: " + e.getMessage());

                return false;
            }
        } else {

        }

        return true;
    }

    public static boolean putObjectSync(String bucket, String key, String file, String meta, String chiprice, long copies, String expires, boolean encrypt, String fileCode, PutObjectListener putObjectListener) {
        if (mUser != null) {
            try {
                mUser.putObjectSync(bucket, key, file, meta, chiprice, copies, expires, encrypt);

                if (putObjectListener != null) {
                    putObjectListener.onPutObjectFinished(fileCode);
                }
            } catch (Exception e) {
                if (putObjectListener != null) {
                    putObjectListener.onPutObjectError(fileCode, e.getMessage());
                }

                return false;
            }
        } else {

        }

        return true;
    }

    public static ArrayList<FileInfo> listBucket(ListBucketListener listBucketListener) {
        String listBucketStr = "";
        ArrayList<FileInfo> bucketList = new ArrayList<>();

        if (mUser != null) {
            try {
                listBucketStr = mUser.listBuckets();

                try {
                    JSONArray array = new JSONArray(listBucketStr);
                    int length = array.length();
                    String[] bucketStrArray = new String[length];
                    for (int i = 0; i < length; i++) {
                        bucketStrArray[i] = (String) array.get(i);
                    }

                    bucketStrArray = StringUtil.sortStrings(bucketStrArray);

                    for (int i = 0; i < length; i++) {
                        bucketList.add(new FileInfo(bucketStrArray[i]));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "listBucket() " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                if (listBucketListener != null) {
                    listBucketListener.onListBucketError(e.getMessage());
                }
            }
        } else {

        }

        //
        Log.e(TAG, "listObject() listObjectStr = " + listBucketStr);
        //

        return bucketList;
    }

    public static ArrayList<FileInfo> listObject(String bucket, ListObjectListener listObjectListener) {
        String listObjectStr = "";
        ArrayList<FileInfo> fileInfoList = new ArrayList<>();

        if (mUser != null) {
            try {
                listObjectStr = mUser.listObjects(bucket);
                JSONArray jsonArray = new JSONArray(listObjectStr);
                int length = jsonArray.length();
                FileInfo[] fileInfos = new FileInfo[length];
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    FileInfo fileInfo = new FileInfo("");

                    fileInfo.setBucketName(jsonObject.getString(Constant.ObjectKey.BUCKET));
                    fileInfo.setName(jsonObject.getString(Constant.ObjectKey.KEY));
                    fileInfo.setStatus(jsonObject.getString(Constant.ObjectKey.STATUS));
                    fileInfo.setLength(jsonObject.getLong(Constant.ObjectKey.LENGTH));
                    fileInfo.setDir(jsonObject.getBoolean(Constant.ObjectKey.ISDIR));
                    fileInfo.setCreatedTime(TimeConverterUtil.GTMToLocal(jsonObject.getString(Constant.ObjectKey.CREATED)));
                    fileInfo.setModifiedTime(TimeConverterUtil.GTMToLocal(jsonObject.getString(Constant.ObjectKey.MODIFIED)));
                    fileInfo.setExpiredTime(TimeConverterUtil.GTMToLocal(jsonObject.getString(Constant.ObjectKey.EXPIRES)));
                    jsonObject.getString((Constant.ObjectKey.SYNCHRONIZED));

                    fileInfos[i] = fileInfo;
                }

                for (int i = 0; i < length - 1; i++) {
                    for (int j = 0; j < length - i - 1; j++) {
                        String preModifiedTime = fileInfos[j].getModifiedTime();
                        String nextModifiedTime = fileInfos[j + 1].getModifiedTime();

                        if (StringUtil.isBiggerThan(preModifiedTime, nextModifiedTime)) {
                            FileInfo tempFileInfo = fileInfos[j];
                            fileInfos[j] = fileInfos[j + 1];
                            fileInfos[j + 1] = tempFileInfo;
                        }
                    }
                }

                fileInfoList = new ArrayList<>(Arrays.asList(fileInfos));
            } catch (Exception e) {
                if (listObjectListener != null) {
                    listObjectListener.onListObjectError(e.getMessage());
                }
            }
        } else {

        }

        //
        Log.e(TAG, "listObject() listObjectStr = " + listObjectStr);
        //

        return fileInfoList;
    }


    public static ObjectStatus getObjectStatus(String bucket, String key, GetObjectStatusListener getObjectStatusListener) {
        ObjectStatus objectStatus = new ObjectStatus();
        String objectStatusStr = "";

        if (mUser != null) {
            try {
                objectStatusStr = mUser.objectStatus(bucket, key);
                JSONObject jsonObject = new JSONObject(objectStatusStr);
                objectStatus.setBucketStr(jsonObject.getString(Constant.ObjectStatusKey.BUCKET));
                objectStatus.setKeyStr(jsonObject.getString(Constant.ObjectStatusKey.KEY));
                objectStatus.setLength(jsonObject.getLong(Constant.ObjectStatusKey.LENGTH));
                objectStatus.setCreatedTime(TimeConverterUtil.GTMToLocal(jsonObject.getString(Constant.ObjectStatusKey.CREATE)));
                objectStatus.setExpiresTime(TimeConverterUtil.GTMToLocal(jsonObject.getString(Constant.ObjectStatusKey.EXPIRES)));
                objectStatus.setState(jsonObject.getString(Constant.ObjectStatusKey.STATE));
            } catch (Exception e) {
                if (getObjectStatusListener != null) {
                    getObjectStatusListener.onGetObjectStatusError(e.getMessage());
                }

                Log.e(TAG, "getObjectStatus() error = " + e.getMessage());
            }
        } else {

        }

        Log.e(TAG, "getObjectStatus() objectStatusStr = " + objectStatusStr);

        objectStatus.setBucketStr(bucket);
        objectStatus.setKeyStr(key);
        objectStatus.setDetailStr(objectStatusStr);

        return objectStatus;
    }

    public static boolean getObject(String bucket, String key, String file, String chiPrice, GetObjectListener getObjectListener) {
        if (mUser != null) {
            try {
                mUser.getObject(bucket, key, "", file, chiPrice);
            } catch (Exception e) {
                if (getObjectListener != null) {
                    getObjectListener.onGetObjectError(e.getMessage());
                }

                return false;
            }
        } else {

        }

        return true;
    }

    public static boolean getObjectShared(String shareCode, String file, String chiPrice, GetObjectListener getObjectListener) {
        if (mUser != null) {
            try {
                mUser.getObject("", "", shareCode, file, chiPrice);
            } catch (Exception e) {
                if (getObjectListener != null) {
                    getObjectListener.onGetObjectError(e.getMessage());
                }

                e.printStackTrace();

                return false;
            }
        } else {

        }

        return true;
    }

    public static boolean renewObject(String bucket, String key, String chiPrice, long copies, String expires, RenewObjectListener renewObjectListener) {
        if (mUser != null) {
            try {
                mUser.renewObject(bucket, key, chiPrice, copies, expires);
            } catch (Exception e) {
                if (renewObjectListener != null) {
                    renewObjectListener.onRenewObjectError(e.getMessage());
                }

                return false;
            }
        } else {

        }

        return true;
    }

    public static String getShareCode(String bucket, String key, GetShareCodeListener getShareCodeListener) {
        String shareCode = "";

        if (mUser != null) {
            try {
                shareCode = mUser.shareObject(bucket, key);
            } catch (Exception e) {
                if (getShareCodeListener != null) {
                    getShareCodeListener.onGetShareCodeError(e.getMessage());
                }
            }
        } else {

        }

        return shareCode;
    }

    public static boolean copyObject(String bucket, String key, String source, String meta, String chiPrice, long copies, String expires, boolean encrypt, CopyObjectListener copyObjectListener) {
        if (mUser != null) {
            try {
                mUser.copyObject(bucket, key, source, meta, chiPrice, copies, expires, encrypt);
            } catch (Exception e) {
                if (copyObjectListener != null) {
                    copyObjectListener.onCopyObjectError(e.getMessage());
                }
            }
        } else {

        }

        return true;
    }

    public static boolean deleteObject(String bucket, String key, DeleteObjectListener deleteObjectListener) {
        try {
            mUser.deleteObject(bucket, key);
        } catch (Exception e) {
            if (deleteObjectListener != null) {
                deleteObjectListener.onDeleteObjectError(e.getMessage());
            }

            return false;
        }

        return true;
    }

    public static ArrayList<TaskInfo> listTask(ListTaskListener listTaskListener) {
        ArrayList<TaskInfo> taskInfoList = new ArrayList<>();
        String taskStr;

        if (mUser != null) {
            try {
                taskStr = mUser.listTasks();

                JSONArray jsonArray = new JSONArray(taskStr);
                int length = jsonArray.length();
                TaskInfo[] taskInfos = new TaskInfo[length];
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    TaskInfo taskInfo = new TaskInfo();

                    taskInfo.setId(jsonObject.getString(Constant.TaskKey.ID));
                    taskInfo.setType(jsonObject.getString(Constant.TaskKey.TYPE));
                    taskInfo.setState(jsonObject.getString(Constant.TaskKey.TYPE));
                    taskInfo.setFrom(jsonObject.getString(Constant.TaskKey.FROM));
                    taskInfo.setTo(jsonObject.getString(Constant.TaskKey.TO));
                    taskInfo.setTotal(jsonObject.getLong(Constant.TaskKey.TOTAL));
                    taskInfo.setFinished(jsonObject.getLong(Constant.TaskKey.FINISHED));
                    taskInfo.setCreated(jsonObject.getString(Constant.TaskKey.CREATE));
                    taskInfo.setError(jsonObject.getString(Constant.TaskKey.ERROR));

                    taskInfos[i] = taskInfo;
                }

                for (int i = 0; i < length - 1; i++) {
                    for (int j = 0; j < length - i - 1; j++) {
                        String preModifiedTime = taskInfos[j].getCreated();
                        String nextModifiedTime = taskInfos[j + 1].getCreated();

                        if (StringUtil.isBiggerThan(preModifiedTime, nextModifiedTime)) {
                            TaskInfo tempTaskInfo = taskInfos[j];
                            taskInfos[j] = taskInfos[j + 1];
                            taskInfos[j + 1] = tempTaskInfo;
                        }
                    }
                }

                taskInfoList = new ArrayList<>(Arrays.asList(taskInfos));
            } catch (Exception e) {
                if (listTaskListener != null) {
                    listTaskListener.onListTaskError(e.getMessage());
                }

                Log.e(TAG, "listTask() error: " + e.getMessage());

                e.printStackTrace();
            }
        } else {

        }

        return taskInfoList;
    }

    public static LinkedHashMap<String, TaskInfo> listTaskForHashMap(ListTaskListener listTaskListener) {
        LinkedHashMap<String, TaskInfo> taskInfoMap = new LinkedHashMap<>();
        String taskStr = "";

        if (mUser != null) {
            try {
                taskStr = mUser.listTasks();

                JSONArray jsonArray = new JSONArray(taskStr);
                int length = jsonArray.length();
                TaskInfo[] taskInfos = new TaskInfo[length];
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    TaskInfo taskInfo = new TaskInfo();

                    taskInfo.setId(jsonObject.getString(Constant.TaskKey.ID));
                    taskInfo.setType(jsonObject.getString(Constant.TaskKey.TYPE));
                    taskInfo.setState(jsonObject.getString(Constant.TaskKey.STATE));
                    taskInfo.setFrom(jsonObject.getString(Constant.TaskKey.FROM));
                    taskInfo.setTo(jsonObject.getString(Constant.TaskKey.TO));
                    taskInfo.setTotal(jsonObject.getLong(Constant.TaskKey.TOTAL));
                    taskInfo.setFinished(jsonObject.getLong(Constant.TaskKey.FINISHED));
                    taskInfo.setCreated(jsonObject.getString(Constant.TaskKey.CREATE));
                    taskInfo.setError(jsonObject.getString(Constant.TaskKey.ERROR));

                    taskInfos[i] = taskInfo;
                }

                for (int i = 0; i < length - 1; i++) {
                    for (int j = 0; j < length - i - 1; j++) {
                        String preModifiedTime = taskInfos[j].getCreated();
                        String nextModifiedTime = taskInfos[j + 1].getCreated();

                        if (StringUtil.isBiggerThan(preModifiedTime, nextModifiedTime)) {
                            TaskInfo tempTaskInfo = taskInfos[j];
                            taskInfos[j] = taskInfos[j + 1];
                            taskInfos[j + 1] = tempTaskInfo;
                        }
                    }
                }

                for (int i = 0; i < length; i++) {
                    taskInfoMap.put(taskInfos[i].getId(), taskInfos[i]);
                }
            } catch (Exception e) {
                if (listTaskListener != null) {
                    listTaskListener.onListTaskError(e.getMessage());
                }

                Log.e(TAG, "listTask() error: " + e.getMessage());

                e.printStackTrace();
            }
        } else {

        }

        //Log.e(TAG, "listTask() taskStr = " + taskStr);

        return taskInfoMap;
    }


    public static boolean deleteTask(String taskId, DeleteTaskListener deleteTaskListener) {
        if (mUser != null) {
            try {
                mUser.deleteTask(taskId);
            } catch (Exception e) {
                if (deleteTaskListener != null) {
                    deleteTaskListener.onDeleteTaskError(e.getMessage());
                }

                Log.e(TAG, "deleteTask() error: " + e.getMessage());

                e.printStackTrace();
            }
        } else {

        }

        return true;
    }

    public static boolean pauseTask(String taskId, PauseTaskListener pauseTaskListener) {
        if (mUser != null) {
            try {
                mUser.pauseTask(taskId);
            } catch (Exception e) {
                if (pauseTaskListener != null) {
                    pauseTaskListener.onPauseTaskError(e.getMessage());
                }

                Log.e(TAG, "pauseTask() error: " + e.getMessage());

                return false;
            }
        } else {

        }

        return true;
    }

    public static boolean resumeTask(String taskId, ResumeTaskListener resumeTaskListener) {
        if (mUser != null) {
            try {
                mUser.resumeTask(taskId);
            } catch (Exception e) {
                if (resumeTaskListener != null) {
                    resumeTaskListener.onResumeTaskError(e.getMessage());
                }

                Log.e(TAG, "resumeTask() error: " + e.getMessage());

                return false;
            }
        } else {

        }

        return true;
    }

    public static String getUsed(String bucket, GetUsedListener getUsedListener) {
        String mUsedStr = "";

        if (mUser != null) {
            try {
                String listObjectStr = mUser.listObjects(bucket);
                JSONArray jsonArray = new JSONArray(listObjectStr);
                int length = jsonArray.length();
                long totalSize = 0L;
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    totalSize = totalSize + jsonObject.getLong(Constant.ObjectKey.LENGTH);
                }

                double totalSizeGB = ((double) totalSize / 1024) / 1024 / 1024;
                mUsedStr = "" + totalSizeGB;
            } catch (Exception e) {
                if (getUsedListener != null) {
                    getUsedListener.onGetUsedError(e.getMessage());
                }

                Log.e(TAG, "getUsed() error: " + e.getMessage());
            }
        } else {

        }

        return mUsedStr;
    }

    public interface LogInListener {
        void onLogInError(String errMsg);
    }

    public interface LogOutListener {
        void onLofOutError(String errMsg);
    }

    public interface CreateBucketListener {
        void onCreateBucketError(String errMsg);
    }

    public interface PutObjectListener {
        void onPutObjectError(String fileCode, String errMsg);

        void onPutObjectFinished(String fileCode);
    }

    public interface ListBucketListener {
        void onListBucketError(String errMsg);
    }

    public interface ListObjectListener {
        void onListObjectError(String errMsg);
    }

    public interface GetObjectStatusListener {
        void onGetObjectStatusError(String errMsg);
    }

    public interface GetObjectListener {
        void onGetObjectError(String errMsg);
    }

    public interface ObjectStatusListener {
        void onObjectStatusError(String errMsg);
    }

    public interface RenewObjectListener {
        void onRenewObjectError(String errMsg);
    }

    public interface GetShareCodeListener {
        void onGetShareCodeError(String errMsg);
    }

    public interface CopyObjectListener {
        void onCopyObjectError(String errMsg);
    }

    public interface DeleteObjectListener {
        void onDeleteObjectError(String errMsg);
    }

    public interface ListTaskListener {
        void onListTaskError(String errMsg);
    }

    public interface DeleteTaskListener {
        void onDeleteTaskError(String errMsg);
    }

    public interface PauseTaskListener {
        void onPauseTaskError(String errMsg);
    }

    public interface ResumeTaskListener {
        void onResumeTaskError(String errMsg);
    }

    public interface GetUsedListener {
        void onGetUsedError(String errMsg);
    }
}