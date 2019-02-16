package io.pp.net_disk_demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.DateInfo;
import io.pp.net_disk_demo.mvp.presenter.ProphecyPresenter;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.ProphecyPresenterImpl;
import io.pp.net_disk_demo.mvp.view.ProphecyView;
import io.pp.net_disk_demo.util.ToastUtil;

public class SetChiPriceDialog extends Dialog implements ProphecyView {

    final String TAG = "SeChiPriceDialog";

    final int STORAGE = 0x01;
    final int DOWNLOAD = 0x02;
    final int DOWNLOAD_SHARE = 0x03;

    private Context mContext;

    private EditText mChiPriceEt;
    private TextView mRecommendedChiPriceTv;

    private TextView mProphecyTotalChiTv = null;
    private ImageView mRequestProphecyTotalChiStatusIv = null;
    private TextView mRequestProphecyTotalChiStatusTv = null;

    private TextView mExpectedCostTv = null;

    private RelativeLayout mCancelLayout;
    private RelativeLayout mOkLayout;

    private RotateAnimation mRequestProphecyTotalChiRotateAnimation = null;

    private String mDefaultChiPrice = "";

    private ProphecyPresenter mProphecyPresenter = null;

    private OnSetChiPriceOnClickListener mOnSetChiPriceOnClickListener;
    private OnDismissListener mOnDismissListener;

    private Handler mHandler = null;

    private int mType;

    private DateInfo mExpiredDateInfo;

    private long mFileSize;
    private int mCopies;
    private String mShareCodeStr;
    private long mTotalChi;

    private DecimalFormat mDecimalFormat = null;

    public SetChiPriceDialog(Context context, String defaultChiPrice, OnSetChiPriceOnClickListener onSetChiPriceOnClickListener, OnDismissListener onDismissListener,
                             int totalChi, long fileSize) {
        super(context, R.style.MyDialog);

        this.mContext = context;

        mDefaultChiPrice = defaultChiPrice;

        mOnSetChiPriceOnClickListener = onSetChiPriceOnClickListener;
        mOnDismissListener = onDismissListener;

        mProphecyPresenter = new ProphecyPresenterImpl(getContext(), SetChiPriceDialog.this);

        mTotalChi = totalChi;
        mCopies = 1;
        mFileSize = fileSize;

        mType = DOWNLOAD;

        mHandler = new Handler();

        mDecimalFormat = new DecimalFormat("0.000000000000000000");
    }

    public SetChiPriceDialog(Context context, String defaultChiPrice, OnSetChiPriceOnClickListener onSetChiPriceOnClickListener, OnDismissListener onDismissListener,
                             int totalChi, String shareCode) {
        super(context, R.style.MyDialog);

        this.mContext = context;

        mDefaultChiPrice = defaultChiPrice;

        mOnSetChiPriceOnClickListener = onSetChiPriceOnClickListener;
        mOnDismissListener = onDismissListener;

        mProphecyPresenter = new ProphecyPresenterImpl(getContext(), SetChiPriceDialog.this);

        mTotalChi = totalChi;
        mCopies = 1;

        mShareCodeStr = shareCode;

        mType = DOWNLOAD_SHARE;

        mHandler = new Handler();

        mDecimalFormat = new DecimalFormat("0.000000000000000000");
    }

    public SetChiPriceDialog(Context context, String defaultChiPrice, OnSetChiPriceOnClickListener onSetChiPriceOnClickListener, OnDismissListener onDismissListener,
                             long totalChi, long fileSize, DateInfo expiredDateInfo, int copies) {
        super(context, R.style.MyDialog);

        this.mContext = context;

        mDefaultChiPrice = defaultChiPrice;

        mOnSetChiPriceOnClickListener = onSetChiPriceOnClickListener;
        mOnDismissListener = onDismissListener;

        mProphecyPresenter = new ProphecyPresenterImpl(getContext(), SetChiPriceDialog.this);

        mTotalChi = totalChi;

        mFileSize = fileSize;
        mExpiredDateInfo = expiredDateInfo;
        mCopies = copies;

        mType = STORAGE;

        mHandler = new Handler();

        mDecimalFormat = new DecimalFormat("0.000000000000000000");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View mLayoutView;

        mLayoutView = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_setchiprice_layout, new RelativeLayout(this.mContext));

        setContentView(mLayoutView);

        mChiPriceEt = mLayoutView.findViewById(R.id.chi_price_edit);

        mRecommendedChiPriceTv = findViewById(R.id.recommended_chi_price_tv);

        mProphecyTotalChiTv = findViewById(R.id.total_chi_value_tv);
        mRequestProphecyTotalChiStatusIv = findViewById(R.id.request_total_chi_status_iv);
        mRequestProphecyTotalChiStatusTv = findViewById(R.id.request_total_chi_status_tv);

        mExpectedCostTv = findViewById(R.id.expected_cost_value_tv);

        mOkLayout = mLayoutView.findViewById(R.id.ok_layout);
        mCancelLayout = mLayoutView.findViewById(R.id.cancel_layout);

        mRequestProphecyTotalChiRotateAnimation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRequestProphecyTotalChiRotateAnimation.setInterpolator(new LinearInterpolator());
        mRequestProphecyTotalChiRotateAnimation.setDuration(1000l);
        mRequestProphecyTotalChiRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRequestProphecyTotalChiRotateAnimation.setRepeatMode(Animation.RESTART);

        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = window.getAttributes();

            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            window.setAttributes(params);
        }

        mChiPriceEt.setText(mDefaultChiPrice);
        mChiPriceEt.setSelection(mChiPriceEt.getText().length());

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null &&
                        !TextUtils.isEmpty(s.toString()) &&
                        mTotalChi > 0) {
                    try {
                        long chiPrice = Long.parseLong(s.toString());
                        if (chiPrice > 0) {
                            double expectedCost = (double) (chiPrice * mTotalChi) / 1000000000000000000l;
                            mExpectedCostTv.setText(mDecimalFormat.format(expectedCost));
                        } else {
                            mExpectedCostTv.setText("0");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "show expected cost error: " + e.getMessage());
                        e.printStackTrace();

                        mExpectedCostTv.setText("0");
                    }
                } else {
                    mExpectedCostTv.setText("0");
                }
            }
        };

        mChiPriceEt.addTextChangedListener(textWatcher);

        mOkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSetChiPriceOnClickListener != null) {
                    if (mChiPriceEt.getText() != null) {
                        String chiPriceStr = mChiPriceEt.getText().toString();
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

                                ToastUtil.showToast(mContext, "please input correct format chi price!", Toast.LENGTH_SHORT);
                                mChiPriceEt.setText("");
                            }
                        } else {
                            ToastUtil.showToast(mContext, "please input chi price!", Toast.LENGTH_SHORT);
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

        if (mTotalChi <= 0) {
            if (mProphecyPresenter != null) {
                switch (mType) {
                    case STORAGE:
                        mProphecyPresenter.requestStorageChi(mFileSize, mExpiredDateInfo);
                        break;
                    case DOWNLOAD:
                        mProphecyPresenter.requestDownloadChi(mFileSize);
                        break;
                    case DOWNLOAD_SHARE:
                        mProphecyPresenter.requestDownloadShareChi(mShareCodeStr);
                        break;
                }
            }
        } else {
            mProphecyTotalChiTv.setText("" + mTotalChi);

            if (mChiPriceEt.getText() != null &&
                    !TextUtils.isEmpty(mChiPriceEt.getText().toString()) &&
                    mTotalChi > 0) {
                try {
                    long chiPrice = Long.parseLong(mChiPriceEt.getText().toString());
                    if (chiPrice > 0) {
                        double expectedCost = (double) (chiPrice * mTotalChi) / 1000000000000000000l;
                        mExpectedCostTv.setText(mDecimalFormat.format(expectedCost));
                    } else {
                        mExpectedCostTv.setText("0");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "show expected cost error: " + e.getMessage());
                    e.printStackTrace();

                    mExpectedCostTv.setText("0");
                }
            } else {
                mExpectedCostTv.setText("0");
            }
        }

        setOnShowListener(new OnShowListener() {
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mChiPriceEt, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    @Override
    protected void onStop() {
        if (mProphecyPresenter != null) {
            mProphecyPresenter.onDestroy();
            mProphecyPresenter = null;
        }

        super.onStop();
    }

    @Override
    public void showRequestTotalChiView() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mRequestProphecyTotalChiStatusTv.setText("");
                mRequestProphecyTotalChiStatusTv.setVisibility(View.INVISIBLE);

                mRequestProphecyTotalChiStatusIv.setVisibility(View.VISIBLE);
                mRequestProphecyTotalChiStatusIv.setBackgroundResource(R.mipmap.blue_loading);
                mRequestProphecyTotalChiStatusIv.startAnimation(mRequestProphecyTotalChiRotateAnimation);
            }
        });
    }

    @Override
    public void showGetTotalChiView(int totalChi) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mRequestProphecyTotalChiStatusTv.setText("");
                mRequestProphecyTotalChiStatusTv.setVisibility(View.INVISIBLE);

                mRequestProphecyTotalChiStatusIv.clearAnimation();
                mRequestProphecyTotalChiStatusIv.setVisibility(View.INVISIBLE);

                mTotalChi = totalChi * mCopies;
                mProphecyTotalChiTv.setText("" + mTotalChi);

                if (mChiPriceEt.getText() != null && !TextUtils.isEmpty(mChiPriceEt.getText().toString())) {
                    int chiPrice = Integer.parseInt(mChiPriceEt.getText().toString());

                    double expectedCost = (double) (chiPrice * mTotalChi) / 1000000000000000000l;
                    mExpectedCostTv.setText(mDecimalFormat.format(expectedCost));
                } else {
                    mExpectedCostTv.setText("0");
                }
            }
        });
    }

    @Override
    public void showGetTotalChiFailedView(String errMsg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mRequestProphecyTotalChiStatusTv.setText("prophecy failed");
                mRequestProphecyTotalChiStatusTv.setVisibility(View.VISIBLE);

                mRequestProphecyTotalChiStatusIv.clearAnimation();
                mRequestProphecyTotalChiStatusIv.setBackgroundResource(R.mipmap.task_error_icon);
                mRequestProphecyTotalChiStatusIv.setVisibility(View.VISIBLE);
            }
        });
    }

    public interface OnSetChiPriceOnClickListener {
        void onCancel();

        void onSet(int chiPrice);
    }
}