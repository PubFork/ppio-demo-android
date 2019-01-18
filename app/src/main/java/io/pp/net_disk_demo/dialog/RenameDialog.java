package io.pp.net_disk_demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import io.pp.net_disk_demo.R;

public class RenameDialog extends Dialog {

    private Context mContext;

    private OnRenameOnClickListener mOnRenameOnClickListener;
    private OnDismissListener mOnDismissListener;

    public RenameDialog(Context context, OnRenameOnClickListener onRenameOnClickListener, OnDismissListener onDismissListener) {
        super(context, R.style.MyDialog);

        this.mContext = context;

        mOnRenameOnClickListener = onRenameOnClickListener;
        mOnDismissListener = onDismissListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View mLayoutView;

        RelativeLayout mCancelLayout;
        RelativeLayout mOkLayout;

        mLayoutView = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_rename_layout, new RelativeLayout(this.mContext));

        setContentView(mLayoutView);

        final EditText mNameEditText = mLayoutView.findViewById(R.id.rename_edittext);
        mOkLayout = mLayoutView.findViewById(R.id.ok_layout);
        mCancelLayout = mLayoutView.findViewById(R.id.cancel_layout);

        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = window.getAttributes();

            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            window.setAttributes(params);
        }

        mOkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRenameOnClickListener != null) {
                    if (mNameEditText.getText() != null) {
                        mOnRenameOnClickListener.onRename(mNameEditText.getText().toString());
                    }
                }
            }
        });

        mCancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRenameOnClickListener != null) {
                    mOnRenameOnClickListener.onCancel();
                }
            }
        });

        setOnDismissListener(mOnDismissListener);
    }

    public interface OnRenameOnClickListener {
        void onCancel();

        void onRename(String name);
    }
}