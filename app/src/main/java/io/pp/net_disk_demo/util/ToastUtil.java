package io.pp.net_disk_demo.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.dovar.dtoast.DToast;
import com.dovar.dtoast.inner.IToast;

import io.pp.net_disk_demo.R;

public class ToastUtil {

    private static IToast toast = null;

    public static void showToast(Context context, String text, int time) {

        if (toast == null) {
            toast = DToast.make(context);
        }

        TextView tv_text = (TextView) toast.getView().findViewById(R.id.tv_content);
        if (tv_text != null) {
            tv_text.setText(text);
        }

        toast.setGravity(Gravity.CENTER, 0, 30).show();
    }
}