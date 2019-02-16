package io.pp.net_disk_demo.util;

public class ActivityUtil {

    public static boolean mHasActivityFinishedForNoStoragePermission = false;

    public static void setHasFinishedForNoStorage() {
        mHasActivityFinishedForNoStoragePermission = true;
    }

    public static boolean hasFinishedForNoStorage() {
        return mHasActivityFinishedForNoStoragePermission;
    }
}