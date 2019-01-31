package io.pp.net_disk_demo;

import android.os.Environment;

public class Constant {

    public static class Intent {
        public static final String LOGIN_SUCCEED = "io.pp.net_disk_demo.LOGIN_SUCCEED";

        public static final String LOCAL_UPLOAD_ACTION = "io.pp.net_disk_demo.UPLOAD_LOCAL_FILE";
        public static final String RENEW_ACTION = "io.pp.net_disk_demo.RENEW_FILE";
        public static final String COPY_ACTION = "io.pp.net_disk_demo.COPY_FILE";
    }

    public static class Data {
        public static final String DEFAULT_BUCKET = "bucket1";

        public static final String RENEW_FILE = "renew_file";

        public static final String PRIVATE_KEY = "PRIVATE_KEY";
        public static final String MNEMONIC = "MNEMONIC";
        public static final String ADDRESS = "ADDRESS";
        public static final String PASSWORD = "PASSWORD";

        public static final String KETSTORE = "KEYSTORE";
    }

    public static class Code {
        public static final int REQUEST_UPLOAD = 0x01;
        public static final int RESULT_UPLOAD_OK = 0x02;

        public static final int REQUEST_RENEW = 0x03;
        public static final int RESULT_RENEW_OK = 0x04;

        public static final int REQUEST_DOWNLOAD = 0x05;
        public static final int RESULT_DOWNLOAD_OK = 0x06;

        public static final int REQUEST_SCAN_CODE = 0x07;
        public static final int REQUEST_SCAN_CODE_OK = 0x08;
    }

    public static class ObjectKey {
        public static final String BUCKET = "bucket";
        public static final String KEY = "key";
        public static final String STATUS = "status";
        public static final String LENGTH = "length";
        public static final String ISDIR = "isdir";
        public static final String CREATED = "created";
        public static final String MODIFIED = "modified";
        public static final String EXPIRES = "expires";
        public static final String SYNCHRONIZED = "synchronized";
    }

    public static class ObjectState {
        public static final String BID = "Bid";
        public static final String PART_DEAL = "Part-Deal";
        public static final String DEAL = "Deal";
        public static final String PENDING_END = "Pending-End";
        public static final String END = "End";
    }

    public static class ObjectStatusKey {
        public static final String BUCKET = "bucket";
        public static final String KEY = "key";
        public static final String LENGTH = "length";
        public static final String CREATE = "create";
        public static final String EXPIRES = "expires";
        public static final String STATE = "state";
    }

    public static class TaskKey {
        public static final String ID = "id";
        public static final String TYPE = "type";
        public static final String STATE = "state";
        public static final String FROM = "from";
        public static final String TO = "to";
        public static final String TOTAL = "total";
        public static final String FINISHED = "finished";
        public static final String CREATE = "create";
        public static final String ERROR = "error";
    }

    public static class TaskType {
        public static final String PUT = "Put";
        public static final String GET = "Get";
    }

    public static class TaskState {
        public static final String PENDING = "Pending";
        public static final String RUNNING = "Running";
        public static final String PAUSED = "Paused";
        public static final String FINISHED = "Finished";
        public static final String ERROR = "Error";
    }

    public static class ProgressState {
        public static final String RUNNING = "Running";
        public static final String FINISHED = "Finished";
        public static final String ERROR = "Error";
    }

    public static class PPIO_File {
        public static final String APP_CACHE_DIR = Environment.getExternalStorageDirectory().getPath() +
                "/io.pp.net_disk_demo";

        public static final String CACHE_DIR_PREFIX = APP_CACHE_DIR +
                "/PPIO_Cache_";//can not add '/ in end
        public static final String CACHE_QOS_DIR_SUFFIX = "/qoslog";
        public static final String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().getPath() +
                "/PPIO_Download";//can not add '/ in end

        public static final String REGISTER_RECORD_PREFIX = Constant.PPIO_File.CACHE_DIR_PREFIX + "/Register_Record";
        public static final String LOGIN_RECORD_FILE = Constant.PPIO_File.CACHE_DIR_PREFIX + "/LogIn_Record.txt";

        public static final String PRIVATE_KEYSOTRE_FILE = "ppio_keystore.json";
    }

    public static class URL {
        public static final String RPC_URL = "http://ad04b30b910c311e9b71c02d26ce9aff-567092461.us-west-2.elb.amazonaws.com:18030/rpc";
        public static final String WALLET_URL = "https://wallet.testnet.pp.io/";
        public static final String UPDATE_URL = "";
        public static final String FEEDBACK_URL = "https://gitter.im/PPIO/chat?utm_source=share-link&utm_medium=link&utm_campaign=share-link";
    }

    public static class DEFAULT {
        public static final int COPIES = 5;
        public static final String CHI_PRICE = "100";
    }

    public static class Cache {
        public static class Table {
            public static String UPLOAD_FAILED_OBJECT = "upload_failed_object";
        }
    }
}