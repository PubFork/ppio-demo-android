package io.pp.net_disk_demo.dialog;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.util.ToastUtil;

public class ShowShareCodeDialog extends Dialog {

    final String TAG = "ShowDetailDialog";

    private Context mContext;

    private View mLayoutView;
    private TextView mShareCodeTv;
    private RelativeLayout mCancelLayout;
    private RelativeLayout mOkLayout;

    public ShowShareCodeDialog(Context context) {
        super(context, R.style.MyDialog);

        this.mContext = context;

        mLayoutView = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_sharecode_layout, new RelativeLayout(this.mContext));
        mShareCodeTv = mLayoutView.findViewById(R.id.sharecode_textview);
        mOkLayout = mLayoutView.findViewById(R.id.ok_layout);
        mCancelLayout = mLayoutView.findViewById(R.id.cancel_layout);
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

        mOkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("shareCode", mShareCodeTv.getText());
                cm.setPrimaryClip(mClipData);

                ToastUtil.showToast(mContext, "shareCode copy succeed!", Toast.LENGTH_SHORT);

                dismiss();
            }
        });

        mCancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setShareCodeText(String shareCode) {
        mShareCodeTv.setText(shareCode);
    }
}