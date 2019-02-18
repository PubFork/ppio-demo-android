package io.pp.net_disk_demo.util;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.widget.Toast;

public class Util {

    /**
     * dp turn into px
     */
    static public int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px turn into dp
     */
    static public int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp turn into px
     */
    static public int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px turn into sp
     */
    static public int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    //Use the system TypeValue class to convert
    static public int dp2px(Context context, int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    static public int sp2px(Context context, int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

    static public void runNetOperation(@NonNull Context context, @NonNull RunNetOperationCallBack runNetOperationCallBack) {
        if (XPermissionUtils.checkPermissions(context, new String[]{Manifest.permission.INTERNET})) {
            if (NetWorkUtil.isNetConnected(context)) {
                runNetOperationCallBack.onRunOperation();
            } else {
                runNetOperationCallBack.onCanceled();
                ToastUtil.showToast(context, "network is not applicable!", Toast.LENGTH_LONG);
            }
        } else {
            ToastUtil.showToast(context, "not has android.permission.INTERNET!", Toast.LENGTH_LONG);
        }
    }

    static public void runStorageOperation(@NonNull Context context, @NonNull RunNetOperationCallBack runNetOperationCallBack) {
        if (XPermissionUtils.checkPermissions(context,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            runNetOperationCallBack.onRunOperation();
        } else {
            runNetOperationCallBack.onCanceled();
            ToastUtil.showToast(context, "not has storage permission!", Toast.LENGTH_LONG);
        }
    }

    static public void runNetStorageOperation(@NonNull Context context, @NonNull RunNetOperationCallBack runNetOperationCallBack) {
        if (XPermissionUtils.checkPermissions(context, new String[]{Manifest.permission.INTERNET})) {
            if (NetWorkUtil.isNetConnected(context)) {
                if (XPermissionUtils.checkPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})) {
                    if (XPermissionUtils.checkPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                        runNetOperationCallBack.onRunOperation();
                    } else {
                        runNetOperationCallBack.onCanceled();
                        ToastUtil.showToast(context, "not has android.permission.WRITE_EXTERNAL_STORAGE!", Toast.LENGTH_LONG);
                    }
                } else {
                    runNetOperationCallBack.onCanceled();
                    ToastUtil.showToast(context, "not has android.permission.READ_EXTERNAL_STORAGE!", Toast.LENGTH_LONG);
                }
            } else {
                runNetOperationCallBack.onCanceled();
                ToastUtil.showToast(context, "network is not applicable!", Toast.LENGTH_LONG);
            }
        } else {
            runNetOperationCallBack.onCanceled();
            ToastUtil.showToast(context, "not has android.permission.INTERNET!", Toast.LENGTH_LONG);
        }
    }

    static public void runCamaraOperation(@NonNull Context context, @NonNull RunNetOperationCallBack runNetOperationCallBack) {
        if (XPermissionUtils.checkPermissions(context, new String[]{Manifest.permission.CAMERA})) {
            runNetOperationCallBack.onRunOperation();
        } else {
            runNetOperationCallBack.onCanceled();
            ToastUtil.showToast(context, "not has android.permission.CAMERA", Toast.LENGTH_LONG);
        }
    }

    public interface RunNetOperationCallBack {
        void onRunOperation();

        void onCanceled();
    }
}