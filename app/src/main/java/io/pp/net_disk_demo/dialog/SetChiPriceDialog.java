package io.pp.net_disk_demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.util.ToastUtil;

public class SetChiPriceDialog extends Dialog {

    final String TAG = "SeChiPriceDialog";

    private Context mContext;

    private String mDefaultChiPrice = "";

    private OnSetChiPriceOnClickListener mOnSetChiPriceOnClickListener;
    private OnDismissListener mOnDismissListener;

    public SetChiPriceDialog(Context context, String defaultChiPrice, OnSetChiPriceOnClickListener onSetChiPriceOnClickListener, OnDismissListener onDismissListener) {
        super(context, R.style.MyDialog);

        this.mContext = context;

        mDefaultChiPrice = defaultChiPrice;

        mOnSetChiPriceOnClickListener = onSetChiPriceOnClickListener;
        mOnDismissListener = onDismissListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View mLayoutView;

        RelativeLayout mCancelLayout;
        RelativeLayout mOkLayout;

        mLayoutView = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_setchiprice_layout, new RelativeLayout(this.mContext));

        setContentView(mLayoutView);

        final EditText mNameEditText = mLayoutView.findViewById(R.id.setchiprice_edittext);
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

        mNameEditText.setText(mDefaultChiPrice);
        mNameEditText.setSelection(mNameEditText.getText().length());

        mOkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSetChiPriceOnClickListener != null) {
                    if (mNameEditText.getText() != null) {
                        String chiPriceStr = mNameEditText.getText().toString();
                        if (!TextUtils.isEmpty(chiPriceStr)) {
                            int chiPrice;
                            try {
                                chiPrice = Integer.parseInt(chiPriceStr);

                                if (chiPrice >= 1) {
                                    mOnSetChiPriceOnClickListener.onSet(chiPrice);
                                } else {
                                    ToastUtil.showToast(mContext, "chi price can not be less than 1!", Toast.LENGTH_SHORT);
                                }
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "mOkLayout onClick() error: " + e.getMessage());
                                e.printStackTrace();

                                Toast.makeText(mContext, "please input correct format chi price!", Toast.LENGTH_SHORT).show();
                                mNameEditText.setText("");
                            }
                        } else {
                            Toast.makeText(mContext, "please input chi price!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        mCancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSetChiPriceOnClickListener != null) {
                    mOnSetChiPriceOnClickListener.onCancel();
                }
            }
        });

        setOnDismissListener(mOnDismissListener);
    }

    public interface OnSetChiPriceOnClickListener {
        void onCancel();

        void onSet(int chiPrice);
    }
}