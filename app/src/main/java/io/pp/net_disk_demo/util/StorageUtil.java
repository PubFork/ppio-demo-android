package io.pp.net_disk_demo.util;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

public class StorageUtil {

    private static final String  TAG = "StorageUtil";

    public static long getAvailableStorage() {
        String storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        StatFs statFs = new StatFs(storage);

        long availableBolocks = 0l;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBolocks = statFs.getAvailableBlocksLong();
        } else {
            availableBolocks = statFs.getAvailableBlocks();
        }

        long blockSize = 0l;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.getBlockSizeLong();
        } else {
            blockSize = statFs.getBlockSize();
        }

        long available = availableBolocks * blockSize;

        return available;
    }
}