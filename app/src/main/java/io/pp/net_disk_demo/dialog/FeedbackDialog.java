package io.pp.net_disk_demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.util.Util;

public class FeedbackDialog extends Dialog {

    private static final String TAG = "FeedbackDialog";
    private Context mContext = null;
    private UploadLogClickListener mUploadLogClickListener = null;

    public FeedbackDialog(Context context, UploadLogClickListener uploadLogClickListner) {
        super(context, R.style.MyDialog);

        mContext = context;
        mUploadLogClickListener = uploadLogClickListner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View mLayoutView;
        TextView mLinkTv;
        TextView mCancelTv;
        TextView mSubmitTv;

        mLayoutView = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_feedback_layout, new RelativeLayout(this.mContext));

        setContentView(mLayoutView);

        final EditText mDescriptionEt = mLayoutView.findViewById(R.id.description_edittext);
        mLinkTv = mLayoutView.findViewById(R.id.link_tv);
        mCancelTv = mLayoutView.findViewById(R.id.cancel_tv);
        mSubmitTv = mLayoutView.findViewById(R.id.submit_tv);

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

        mLinkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL.JOIN_DISCORD_URL)));
            }
        });

        mSubmitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadLogClickListener != null) {
                    if (mDescriptionEt.getText() != null) {
                        mUploadLogClickListener.onSubmit(mDescriptionEt.getText().toString());
                    }

                    dismiss();
                }
            }
        });

        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface UploadLogClickListener {
        void onSubmit(String description);
    }
}