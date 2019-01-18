package io.pp.net_disk_demo.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static Toast mToast;

    public static void showToast(Context context, String text, int time) {
        mToast = null;

        //It is ok to set the Toast text to null once before actually calling
        mToast = Toast.makeText(context, "", time);

        mToast.setText(text);
        mToast.show();
    }
}