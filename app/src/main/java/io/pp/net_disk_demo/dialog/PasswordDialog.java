package io.pp.net_disk_demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import io.pp.net_disk_demo.R;

public class PasswordDialog extends Dialog {

    final String TAG = "SeChiPriceDialog";

    private Context mContext;

    private OnSetPasswordOnClickListener mOnSetPasswordOnClickListener;
    private OnDismissListener mOnDismissListener;

    private String mMnemonicStr = "";

    public PasswordDialog(Context context, OnSetPasswordOnClickListener onSetChiPriceOnClickListener, OnDismissListener onDismissListener) {
        super(context, R.style.MyDialog);

        this.mContext = context;

        mOnSetPasswordOnClickListener = onSetChiPriceOnClickListener;
        mOnDismissListener = onDismissListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View mLayoutView;

        RelativeLayout mCancelLayout;
        RelativeLayout mOkLayout;

        mLayoutView = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_password_layout, new RelativeLayout(this.mContext));

        setContentView(mLayoutView);

        final EditText mPasswordEditText = mLayoutView.findViewById(R.id.password_edittext);
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
                if (mOnSetPasswordOnClickListener != null) {
                    String passwordStr = "";
                    if (mPasswordEditText.getText() != null) {
                        passwordStr = mPasswordEditText.getText().toString();
                    }

                    if (!TextUtils.isEmpty(mMnemonicStr)) {
                        mOnSetPasswordOnClickListener.onSet(mMnemonicStr, passwordStr);
                    }
                }
            }
        });

        mCancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSetPasswordOnClickListener != null) {
                    mOnSetPasswordOnClickListener.onCancel();
                }
            }
        });

        setOnDismissListener(mOnDismissListener);
    }

    public void setMnemonic(String mnemonicStr) {
        mMnemonicStr = mnemonicStr;
    }

    public interface OnSetPasswordOnClickListener {
        void onCancel();

        void onSet(String mnemonic, String password);
    }
}