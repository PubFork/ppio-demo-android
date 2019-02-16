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
import io.pp.net_disk_demo.util.Util;

public class RemindDialog extends Dialog {

    private Context mContext;

    private String mRemind1Str;
    private String mRemind2Str;

    private OnOkClickListener mOnOkClickListener;

    public RemindDialog(Context context, String remind1Str, String remind2Str, OnOkClickListener onOkClickListener) {
        super(context, R.style.MyDialog);

        this.mContext = context;

        mRemind1Str = remind1Str;
        mRemind2Str = remind2Str;

        mOnOkClickListener = onOkClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View mLayoutView;

        TextView mRemind1Tv;
        TextView mRemind2Tv;
        TextView mOkTv;

        mLayoutView = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_remind_layout, new RelativeLayout(this.mContext));

        setContentView(mLayoutView);

        mRemind1Tv = mLayoutView.findViewById(R.id.remind1_tv);
        mRemind2Tv = mLayoutView.findViewById(R.id.remind2_tv);

        mOkTv = mLayoutView.findViewById(R.id.remind_ok_tv);

        mRemind1Tv.setText(mRemind1Str);
        mRemind2Tv.setText(mRemind2Str);

        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);

            WindowManager.LayoutParams params = window.getAttributes();

            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            params.horizontalMargin = 0.8f;

            window.setAttributes(params);
        }

        mOkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnOkClickListener != null) {
                    mOnOkClickListener.onOk();
                }
            }
        });

    }

    public interface OnOkClickListener {
        void onOk();
    }
}