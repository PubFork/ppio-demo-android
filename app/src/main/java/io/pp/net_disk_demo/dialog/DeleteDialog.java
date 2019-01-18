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

public class DeleteDialog extends Dialog {

    private Context mContext;

    private String mTitleStr;

    private OnDeleteOnClickListener mOnDeleteOnClickListener;
    private OnDismissListener mOnDismissListener;

    public DeleteDialog(Context context, String titleStr, OnDeleteOnClickListener onDeleteOnClickListener, OnDismissListener onDismissListener) {
        super(context, R.style.MyDialog);

        this.mContext = context;

        mTitleStr = titleStr;

        mOnDeleteOnClickListener = onDeleteOnClickListener;
        mOnDismissListener = onDismissListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View mLayoutView;

        TextView mFileNameTv;
        RelativeLayout mCancelLayout;
        RelativeLayout mDeleteLayout;

        mLayoutView = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_delete_layout, new RelativeLayout(this.mContext));

        setContentView(mLayoutView);

        mFileNameTv = mLayoutView.findViewById(R.id.delete_filename_tv);

        mDeleteLayout = mLayoutView.findViewById(R.id.ok_layout);
        mCancelLayout = mLayoutView.findViewById(R.id.cancel_layout);

        mFileNameTv.setText(mTitleStr);

        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);

            WindowManager.LayoutParams params = window.getAttributes();

            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            window.setAttributes(params);
        }

        mDeleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDeleteOnClickListener != null) {
                    mOnDeleteOnClickListener.onDelete();
                }
            }
        });

        mCancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDeleteOnClickListener != null) {
                    mOnDeleteOnClickListener.onCancel();
                }
            }
        });

        setOnDismissListener(mOnDismissListener);
    }

    public interface OnDeleteOnClickListener {
        void onCancel();

        void onDelete();
    }
}