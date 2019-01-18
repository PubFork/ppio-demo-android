package io.pp.net_disk_demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import io.pp.net_disk_demo.R;

public class BlockFileOptionsBottomDialog extends Dialog {

    static private final String TAG = "FileOptionsBottomDialog";

    private Context mContext;

    private OnBlockFileOptionsOnClickListener mOnBlockFileOptionsOnClickListener;
    private OnDismissListener mOnDismissListener;

    public BlockFileOptionsBottomDialog(Context context, OnBlockFileOptionsOnClickListener blockFileOptionsOnClickListener, OnDismissListener onDismissListener) {
        super(context, R.style.MyDialog);

        this.mContext = context;

        mOnBlockFileOptionsOnClickListener = blockFileOptionsOnClickListener;
        mOnDismissListener = onDismissListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View mLayoutView;

        Button mDetailBtn;
        Button mDownloadBtn;
        Button mShareBtn;
        Button mRenameBtn;
        Button mRenewBtn;
        Button mDeleteBtn;
        Button mCancelBtn;

        mLayoutView = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_blockfileoptions_layout, new RelativeLayout(this.mContext));

        setContentView(mLayoutView);

        mDetailBtn = mLayoutView.findViewById(R.id.blockfile_detail_btn);
        mDownloadBtn = mLayoutView.findViewById(R.id.blockfile_download_btn);
        mShareBtn = mLayoutView.findViewById(R.id.blockfile_share_btn);
        mRenameBtn = mLayoutView.findViewById(R.id.blockfile_rename_btn);
        mRenewBtn = mLayoutView.findViewById(R.id.blockfile_renew_btn);
        mDeleteBtn = mLayoutView.findViewById(R.id.blockfile_delete_btn);
        mCancelBtn = mLayoutView.findViewById(R.id.blockfile_cancel_btn);

        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = window.getAttributes();

            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            window.setAttributes(params);
        }

        mDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBlockFileOptionsOnClickListener != null) {
                    mOnBlockFileOptionsOnClickListener.onDetail();
                }
            }
        });

        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "mDownloadBtn onClick()");

                if (mOnBlockFileOptionsOnClickListener != null) {
                    Log.e(TAG, "mDownloadBtn onClick() if(mOnBlockFileOptionsOnClickListener != null)");

                    mOnBlockFileOptionsOnClickListener.onDownload();
                }
            }
        });

        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBlockFileOptionsOnClickListener != null) {
                    mOnBlockFileOptionsOnClickListener.onShareUnShare();
                }
            }
        });

        mRenameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBlockFileOptionsOnClickListener != null) {
                    mOnBlockFileOptionsOnClickListener.onRename();
                }
            }
        });

        mRenewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBlockFileOptionsOnClickListener != null) {
                    mOnBlockFileOptionsOnClickListener.onRenew();
                }
            }
        });

        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBlockFileOptionsOnClickListener != null) {
                    mOnBlockFileOptionsOnClickListener.onDelete();
                }
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBlockFileOptionsOnClickListener != null) {
                    mOnBlockFileOptionsOnClickListener.onCancel();
                }
            }
        });

        setOnDismissListener(mOnDismissListener);
    }

    public interface OnBlockFileOptionsOnClickListener {
        void onDetail();

        void onDownload();

        void onShareUnShare();

        void onRename();

        void onRenew();

        void onDelete();

        void onCancel();
    }
}