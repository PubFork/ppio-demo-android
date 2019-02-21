package io.pp.net_disk_demo.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class XPermissionUtils {
    private static final String TAG = "XPermissionUtils";

    private static int mRequestCode = -1;
    private static OnPermissionListener mOnPermissionListener;

    public interface OnPermissionListener {
        void onPermissionGranted();

        void onPermissionDenied();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void requestPermissions(Context context, int requestCode, String[] permissions, OnPermissionListener listener) {
        if (context instanceof Activity) {
            mOnPermissionListener = listener;
            List deniedPermissions = getDeniedPermissions(context, permissions);
            if (deniedPermissions.size() > 0) {
                Log.e(TAG, "requestPermissions() if (deniedPermissions.size() > 0)");
                mRequestCode = requestCode;
                ((Activity) context).requestPermissions(permissions, requestCode);
            } else {
                if (mOnPermissionListener != null)
                    mOnPermissionListener.onPermissionGranted();
            }
        } else {
            throw new RuntimeException("Context must be an Activity");
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void requestPermissionsForActivity(Activity activity, int requestCode, String[] permissions, OnPermissionListener onPermissionListener) {
        List deniedPermissions = getDeniedPermissions(activity, permissions);
        if (deniedPermissions.size() > 0) {
            Log.e(TAG, "requestPermissionsForActivity(） if (deniedPermissions.size() > 0)");
            mRequestCode = requestCode;
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        } else {
            if (onPermissionListener != null)
                onPermissionListener.onPermissionGranted();
        }
    }

    public static boolean checkPermissions(Context context, @NonNull String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }

        return true;
    }

    public static boolean checkPermissionsForActivity(Context context, @NonNull String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the permissions that need to be granted in the request permission
     */
    private static List getDeniedPermissions(Context context, String... permissions) {
        ArrayList<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    /**
     * Gets the permissions that need to be granted in the request permission
     */
    private static List getDeniedPermissionsForActivity(Context context, String... permissions) {
        //ArrayList<String> deniedPermissions = new ArrayList<String>();
        ArrayList<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    /**
     * Request permission result，corresponding to the method onRequestPermissionsResult() in Activity
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (mRequestCode != -1 && requestCode == mRequestCode) {
            if (mOnPermissionListener != null) {
                if (verifyPermissions(grantResults)) {
                    mOnPermissionListener.onPermissionGranted();
                } else {
                    mOnPermissionListener.onPermissionDenied();
                }
            }
        }
    }

    /**
     * Verify that all permissions have been granted
     */
    private static boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}