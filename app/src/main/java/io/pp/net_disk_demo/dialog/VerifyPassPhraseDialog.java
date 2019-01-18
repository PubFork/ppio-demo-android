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

public class VerifyPassPhraseDialog extends Dialog {

    final String TAG = "VerifyPassPhraseDialog";

    private Context mContext;

    private OnVerifyPassPhraseClickListener mOnVerifyPassPhraseClickListener;
    private OnDismissListener mOnDismissListener;

    private String mMnemonicStr = "";

    public VerifyPassPhraseDialog(Context context, OnVerifyPassPhraseClickListener onVerifyPassPhraseClickListener, OnDismissListener onDismissListener) {
        super(context, R.style.MyDialog);

        this.mContext = context;

        mOnVerifyPassPhraseClickListener = onVerifyPassPhraseClickListener;
        mOnDismissListener = onDismissListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View mLayoutView;

        RelativeLayout mCancelLayout;
        RelativeLayout mOkLayout;

        mLayoutView = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_verify_passphrase_layout, new RelativeLayout(this.mContext));

        setContentView(mLayoutView);

        final EditText mOriginalPassEdit = mLayoutView.findViewById(R.id.original_pass_edit);
        final EditText mVerifyPassEdit = mLayoutView.findViewById(R.id.verify_pass_edit);
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
                if (mOnVerifyPassPhraseClickListener != null) {
                    if (mOriginalPassEdit.getText() != null &&
                            !TextUtils.isEmpty(mOriginalPassEdit.getText().toString())) {
                        final String originalPassPhrase = mOriginalPassEdit.getText().toString();
                        if (mVerifyPassEdit.getText() != null &&
                                !TextUtils.isEmpty(mVerifyPassEdit.getText().toString())) {
                            if (originalPassPhrase.equals(mVerifyPassEdit.getText().toString())) {
                                mOnVerifyPassPhraseClickListener.onVerify(originalPassPhrase);
                            } else {

                            }
                        }

                    }
                }
            }
        });

        mCancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnVerifyPassPhraseClickListener != null) {
                    mOnVerifyPassPhraseClickListener.onCancel();
                }
            }
        });

        setOnDismissListener(mOnDismissListener);
    }

    public void setMnemonic(String mnemonicStr) {
        mMnemonicStr = mnemonicStr;
    }

    public interface OnVerifyPassPhraseClickListener {
        void onCancel();

        void onVerify(String passPhrase);
    }
}