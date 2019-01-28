package io.pp.net_disk_demo.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Calendar;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.DateInfo;
import io.pp.net_disk_demo.data.FileInfo;
import io.pp.net_disk_demo.dialog.SetChiPriceDialog;
import io.pp.net_disk_demo.dialog.SetCopiesDialog;
import io.pp.net_disk_demo.mvp.presenter.RenewPresenter;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.RenewPresenterImpl;
import io.pp.net_disk_demo.mvp.view.RenewView;
import io.pp.net_disk_demo.util.ToastUtil;
import io.pp.net_disk_demo.util.Util;
import io.pp.net_disk_demo.widget.CustomSwitchButton;

public class RenewActivity extends BaseActivity implements RenewView {

    private final String TAG = "RenewActivity";

    private Toolbar mRenewToolBar = null;
    private LinearLayout mToolBarLeftTvLayout = null;
    private TextView mToolBarTitleTv = null;

    private ImageView mFileIconIv = null;
    private TextView mFileNameTv = null;

    private RelativeLayout mExpiredTimeLayout = null;
    private RelativeLayout mCopiesLayout = null;
    private RelativeLayout mChiPriceLayout = null;

    private CustomSwitchButton mSecureSwitch = null;
    private TextView mExpiredTimeValueTv = null;
    private TextView mCopiesValueTv = null;
    private TextView mChiPriceValueTv = null;

    private TextView mProphecyTotalChiTv = null;
    private ImageView mRequestProphecyTotalChiStatusIv = null;
    private TextView mRequestProphecyTotalChiStatusTv = null;

    private TextView mExpectedCostTv = null;

    private Button mIssueConfirmBtn = null;

    private SetCopiesDialog mSetCopiesDialog = null;
    private SetChiPriceDialog mSetChiPriceDialog = null;
    private ProgressDialog mProgressDialog = null;

    private RotateAnimation mRequestProphecyTotalChiRotateAnimation = null;

    private RenewPresenter mRenewPresenter = null;

    private String mOperationAction = "";


    private DecimalFormat mDecimalFormat = null;

    private long mTotalChi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload);

        mDecimalFormat = new DecimalFormat("0.000000000000000000");

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        if (mSetCopiesDialog != null) {
            mSetCopiesDialog.dismiss();
            mSetCopiesDialog = null;
        }

        if (mSetChiPriceDialog != null) {
            mSetChiPriceDialog.dismiss();
            mSetChiPriceDialog = null;
        }

        if (mRenewPresenter != null) {
            mRenewPresenter.onDestroy();
        }
    }

    @Override
    public void back() {
        finish();
    }

    @Override
    public void showFileName(String fileName) {
        mFileNameTv.setText(fileName);
    }

    @Override
    public void showSecure(boolean isSecure) {
        mSecureSwitch.setState(isSecure);
    }

    @Override
    public void showExpiredTime(String storageTime) {
        mExpiredTimeValueTv.setText(storageTime);
    }

    @Override
    public void showCopies(int copies) {
        mCopiesValueTv.setText("" + copies);
    }

    @Override
    public void showChiPrice(String chiPrice) {
        mChiPriceValueTv.setText(chiPrice);
    }

    @Override
    public void showSetExpiredTime(DateInfo defaultDateInfo) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(RenewActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if (mRenewPresenter != null) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());

                            boolean furtherThanToday = true;
                            if (year == calendar.get(Calendar.YEAR)) {
                                //expired this year
                                if (monthOfYear == calendar.get(Calendar.MONTH)) {
                                    //expired this month
                                    if (dayOfMonth <= calendar.get(Calendar.DAY_OF_MONTH)) {
                                        //expired today or earlier
                                        furtherThanToday = false;
                                    }
                                } else if (monthOfYear < calendar.get(Calendar.MONTH)) {
                                    //expired last month or earlier
                                    furtherThanToday = false;
                                }
                            } else if (year < calendar.get(Calendar.YEAR)) {
                                //expired last year or earlier
                                furtherThanToday = false;
                            }

                            if (furtherThanToday) {
                                mRenewPresenter.setExpiredTime(new DateInfo(year, monthOfYear, dayOfMonth));

                                mRenewPresenter.requestStorageChi();
                            } else {
                                ToastUtil.showToast(RenewActivity.this, "the expired time is earlier than today", Toast.LENGTH_SHORT);
                            }
                        }
                    }
                }
                , defaultDateInfo.getYear()
                , defaultDateInfo.getMonthOfYear()
                , defaultDateInfo.getDayOfMonth()).
                show();
    }

    @Override
    public void showSetCopies(int defaultCopies) {
        mSetCopiesDialog = new SetCopiesDialog(RenewActivity.this, defaultCopies, new SetCopiesDialog.OnSetCopiesOnClickListener() {
            @Override
            public void onCancel() {
                mSetCopiesDialog.dismiss();
            }

            @Override
            public void onSet(int copies) {
                if (mRenewPresenter != null) {
                    mRenewPresenter.setCopies(copies);

                    mRenewPresenter.requestStorageChi();
                }
                mSetCopiesDialog.dismiss();
            }
        }, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mSetCopiesDialog = null;
            }
        });

        mSetCopiesDialog.show();
    }

    @Override
    public void showSetChiPrice(String defaultChiPrice, long fileSize, DateInfo expiredTime, int copies) {
        mSetChiPriceDialog = new SetChiPriceDialog(RenewActivity.this, defaultChiPrice,
                new SetChiPriceDialog.OnSetChiPriceOnClickListener() {
                    @Override
                    public void onCancel() {
                        mSetChiPriceDialog.dismiss();
                    }

                    @Override
                    public void onSet(int chiPrice) {
                        if (mRenewPresenter != null) {
                            mRenewPresenter.setChiPrice("" + chiPrice);
                        }

                        mSetChiPriceDialog.dismiss();
                    }
                },
                new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mSetChiPriceDialog = null;
                    }
                }, mTotalChi, fileSize, expiredTime, copies);

        mSetChiPriceDialog.show();
    }

    @Override
    public void showRenewingView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                mProgressDialog = new ProgressDialog(RenewActivity.this);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
            }
        });
    }

    @Override
    public void showRenewErrorView(final String errMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                ToastUtil.showToast(RenewActivity.this, "" + errMsg, Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void showRenewCompleteView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                setResult(Constant.Code.RESULT_RENEW_OK);

                finish();
            }
        });
    }

    @Override
    public void showRequestTotalChiView() {
        runOnUiThread(new Runnable() {
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRequestProphecyTotalChiStatusTv.setText("");
                mRequestProphecyTotalChiStatusTv.setVisibility(View.INVISIBLE);

                mRequestProphecyTotalChiStatusIv.clearAnimation();
                mRequestProphecyTotalChiStatusIv.setVisibility(View.INVISIBLE);

                if (mRenewPresenter != null) {
                    mTotalChi = totalChi * mRenewPresenter.getCopies();

                    mProphecyTotalChiTv.setText("" + mTotalChi);
                    try {
                        Long chiPrice = Long.parseLong(mRenewPresenter.getChiPrice());
                        double expectedCost = (double) (chiPrice * mTotalChi) / 1000000000000000000l;
                        mExpectedCostTv.setText(mDecimalFormat.format(expectedCost));
                    } catch (Exception e) {
                        Log.e(TAG, "err: " + e.getMessage());
                        e.printStackTrace();

                        mExpectedCostTv.setText("0");
                    }
                } else {
                    mExpectedCostTv.setText("0");
                }
            }
        });
    }

    @Override
    public void showGetTotalChiFailedView(String errMsg) {
        runOnUiThread(new Runnable() {
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

    private void init() {
        setImmersiveStatusBar();

        mRenewToolBar = findViewById(R.id.upload_toolbar_layout);
        mRenewToolBar.setPadding(0, 0, 0, 0);
        mRenewToolBar.setContentInsetsAbsolute(0, 0);

        setSupportActionBar(mRenewToolBar);

        mToolBarLeftTvLayout = findViewById(R.id.upload_toolbar_left_iv_layout);
        mToolBarTitleTv = findViewById(R.id.actionbar_title_tv);

        View.OnClickListener toolBarLeftOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRenewPresenter != null) {
                    mRenewPresenter.back();
                }
            }
        };

        mToolBarLeftTvLayout.setOnClickListener(toolBarLeftOnClickListener);

        mToolBarTitleTv.setText("Upload Settings");

        mExpiredTimeLayout = findViewById(R.id.expiredtime_layout);
        mCopiesLayout = findViewById(R.id.copies_layout);
        mChiPriceLayout = findViewById(R.id.chiprice_layout);

        mFileNameTv = findViewById(R.id.filename_tv);
        mSecureSwitch = findViewById(R.id.upload_secure_switch);
        mExpiredTimeValueTv = findViewById(R.id.expiredtime_value_tv);
        mCopiesValueTv = findViewById(R.id.copies_value_tv);
        mChiPriceValueTv = findViewById(R.id.chiprice_value_tv);

        mProphecyTotalChiTv = findViewById(R.id.total_chi_value_tv);
        mRequestProphecyTotalChiStatusIv = findViewById(R.id.request_total_chi_status_iv);
        mRequestProphecyTotalChiStatusTv = findViewById(R.id.request_total_chi_status_tv);

        mExpectedCostTv = findViewById(R.id.expected_cost_value_tv);

        mIssueConfirmBtn = findViewById(R.id.issue_confirm_btn);

        mSecureSwitch.setTrackOffBitmap(R.mipmap.custom_switch_track_off);
        mSecureSwitch.setTrackOnBitmap(R.mipmap.custom_switch_track_on);
        mSecureSwitch.setThumbBitmap(R.mipmap.custom_switch_thumb);

        mRequestProphecyTotalChiRotateAnimation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRequestProphecyTotalChiRotateAnimation.setInterpolator(new LinearInterpolator());
        mRequestProphecyTotalChiRotateAnimation.setDuration(1000l);
        mRequestProphecyTotalChiRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRequestProphecyTotalChiRotateAnimation.setRepeatMode(Animation.RESTART);

        mSecureSwitch.setOnStateChange(new CustomSwitchButton.OnStateChangeListener() {
            @Override
            public void onStateChange(boolean state) {
                if (mRenewPresenter != null) {
                    mRenewPresenter.setSecure(state);
                }
            }
        });

        mExpiredTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRenewPresenter != null) {
                    mRenewPresenter.showSetExpiredTime();
                }
            }
        });

        mCopiesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRenewPresenter != null) {
                    mRenewPresenter.showSetCopies();
                }
            }
        });

        mChiPriceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRenewPresenter != null) {
                    mRenewPresenter.showSetChiPrice();
                }
            }
        });

        mIssueConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "mIssueConfirmBtn.setOnClickListener()");

                Util.runNetOperation(RenewActivity.this, new Util.RunNetOperationCallBack() {
                    @Override
                    public void onRunOperation() {
                        if (mRenewPresenter != null) {
                            mRenewPresenter.renew();
                        }
                    }
                });
            }
        });

        mRenewPresenter = new RenewPresenterImpl(RenewActivity.this, RenewActivity.this);

        Intent mIntent = getIntent();
        if (mIntent != null) {
            mOperationAction = mIntent.getAction();
            FileInfo mFileInfo = (FileInfo) mIntent.getSerializableExtra(Constant.Data.RENEW_FILE);
            if (mFileInfo != null) {
                String fileName = mFileInfo.getName();
                if (!TextUtils.isEmpty(fileName) && fileName.startsWith(Constant.Data.DEFAULT_BUCKET + "/")) {
                    fileName.replaceFirst(Constant.Data.DEFAULT_BUCKET + "/", "");
                }
                mFileNameTv.setText(fileName);
            } else {
                Log.e(TAG, "init() if (mFileInfo == null)");
            }

            if (Constant.Intent.RENEW_ACTION.equals(mOperationAction) &&
                    mFileInfo != null) {
                if (mRenewPresenter != null) {
                    mRenewPresenter.setRenewFile(mFileInfo);
                }
            }
        }
    }
}