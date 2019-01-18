package io.pp.net_disk_demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.ObjectStatus;

public class ShowDetailDialog extends Dialog {

    final String TAG = "ShowDetailDialog";

    private Context mContext;

    private View mLayoutView;
    private TextView mDetailTv;

    public ShowDetailDialog(Context context) {
        super(context, R.style.MyDialog);

        this.mContext = context;

        mLayoutView = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_detail_layout, new RelativeLayout(this.mContext));
        mDetailTv = mLayoutView.findViewById(R.id.detail_textview);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(mLayoutView);

        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = window.getAttributes();

            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            window.setAttributes(params);
        }
    }

    public void setDetailText(ObjectStatus objectStatus) {
        mDetailTv.setText(objectStatus.getKeyStr() + "(in " + objectStatus.getBucketStr() + ") "
                + "\n length: " + objectStatus.getLength()
                + "\n createdTime: " + objectStatus.getCreatedTime()
                + "\n expiredTime: " + objectStatus.getExpiresTime()
                + "\n state: " + objectStatus.getState());
    }
}