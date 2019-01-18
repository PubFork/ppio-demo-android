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

public class PpioDataUploadGetDialog extends Dialog {

    private Context mContext;

    private OnUploadGetOnClickListener mOnUploadGetOnClickListener;
    private OnDismissListener mOnDismissListener;

    public PpioDataUploadGetDialog(Context context, OnUploadGetOnClickListener onUploadGetOnClickListener, OnDismissListener onDismissListener) {
        super(context, R.style.MyDialogNoShadow);

        this.mContext = context;

        mOnUploadGetOnClickListener = onUploadGetOnClickListener;
        mOnDismissListener = onDismissListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View mLayoutView;

        TextView mUploadTv;
        TextView mGetTv;

        mLayoutView = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_ppiodata_uploadget_layout, new RelativeLayout(this.mContext));

        setContentView(mLayoutView);

        Window window = this.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            window.setAttributes(params);
        }

        mUploadTv = mLayoutView.findViewById(R.id.upload_tv);
        mGetTv = mLayoutView.findViewById(R.id.get_tv);

        mUploadTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnUploadGetOnClickListener != null) {
                    mOnUploadGetOnClickListener.onUpload();
                }
            }
        });

        mGetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnUploadGetOnClickListener != null) {
                    mOnUploadGetOnClickListener.onGet();
                }
            }
        });

        setOnDismissListener(mOnDismissListener);
    }

    public void setShowBottomRightGravity() {
        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM | Gravity.END);
            WindowManager.LayoutParams params = window.getAttributes();

            window.setAttributes(params);
        }
    }

    public void setShowCoordinate(int x, int y) {
        Window window = getWindow();
        if (window != null) {
            //window.setWindowAnimations(R.style.dialogWindowAnim); //Set the popup window to animate
            //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //Set the dialog box background to transparent
            WindowManager.LayoutParams params = window.getAttributes();
            //Set the position of the window to be displayed according to the x and y coordinates
            params.x = x; //x less than 0 to the left, greater than 0 to the right
            params.y = y; //y less than 0 up, greater than 0 down
            //wl.alpha = 0.6f; //Set transparency
            //wl.gravity = Gravity.BOTTOM; //Set the gravity
            window.setAttributes(params);
        }
    }

    public interface OnUploadGetOnClickListener {
        void onUpload();

        void onGet();
    }
}