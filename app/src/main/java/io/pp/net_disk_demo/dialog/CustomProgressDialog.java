package io.pp.net_disk_demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.util.Util;

public class CustomProgressDialog extends Dialog {
    private Context mContext = null;
    private String mContent;

    public CustomProgressDialog(Context context, String content) {
        super(context, R.style.MyDialog);

        mContext = context;
        mContent = content;

        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_progress_layout);
        ((TextView) findViewById(R.id.tvcontent)).setText(mContent);

        Window window = this.getWindow();
        if (window != null) {
            Resources resources = mContext.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();

            window.setGravity(Gravity.CENTER);

            WindowManager.LayoutParams params = window.getAttributes();

            params.width = dm.widthPixels - 2 * Util.dp2px(mContext, 24);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            window.setAttributes(params);
        }
    }
}