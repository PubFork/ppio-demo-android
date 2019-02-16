package io.pp.net_disk_demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.util.ToastUtil;

public class SetCopiesDialog extends Dialog {

    final String TAG = "SetCopiesDialog";

    private Context mContext;

    private OnSetCopiesOnClickListener mOnSetCopiesOnClickListener;
    private OnDismissListener mOnDismissListener;

    private int mDefaultCopies;

    public SetCopiesDialog(Context context, int defaultCopies, OnSetCopiesOnClickListener onSetCopiesOnClickListener, OnDismissListener onDismissListener) {
        super(context, R.style.MyDialog);

        this.mContext = context;

        mDefaultCopies = defaultCopies;

        mOnSetCopiesOnClickListener = onSetCopiesOnClickListener;
        mOnDismissListener = onDismissListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View mLayoutView;

        RelativeLayout mCancelLayout;
        RelativeLayout mOkLayout;

        mLayoutView = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_setcopies_layout, new RelativeLayout(this.mContext));

        setContentView(mLayoutView);

        final EditText mNameEditText = mLayoutView.findViewById(R.id.copies_edittext);
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

        mNameEditText.setText("" + mDefaultCopies);
        mNameEditText.setSelection(mNameEditText.getText().length());

        mOkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSetCopiesOnClickListener != null) {
                    if (mNameEditText.getText() != null) {
                        String copiesStr = mNameEditText.getText().toString();
                        if (!TextUtils.isEmpty(copiesStr)) {
                            int copies;
                            try {
                                copies = Integer.parseInt(copiesStr);

                                if (copies >= 1) {
                                    mOnSetCopiesOnClickListener.onSet(copies);
                                } else {
                                    ToastUtil.showToast(mContext, "copies can not be less than 1!", Toast.LENGTH_SHORT);
                                }
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "mOkLayout onClick() error: " + e.getMessage());
                                e.printStackTrace();

                                ToastUtil.showToast(mContext, "please input correct format copies!", Toast.LENGTH_SHORT);
                                mNameEditText.setText("");
                            }
                        } else {
                            ToastUtil.showToast(mContext, "please input copies!", Toast.LENGTH_SHORT);
                        }
                    }
                }
            }
        });

        mCancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSetCopiesOnClickListener != null) {
                    mOnSetCopiesOnClickListener.onCancel();
                }
            }
        });

        setOnDismissListener(mOnDismissListener);

        setOnShowListener(new OnShowListener() {
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mNameEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    public interface OnSetCopiesOnClickListener {
        void onCancel();

        void onSet(int copies);
    }
}