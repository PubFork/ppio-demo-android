package io.pp.net_disk_demo.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
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

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.Calendar;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.DateInfo;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.dialog.SetChiPriceDialog;
import io.pp.net_disk_demo.dialog.SetCopiesDialog;
import io.pp.net_disk_demo.mvp.presenter.UploadPresenter;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.UploadPresenterImpl;
import io.pp.net_disk_demo.mvp.view.UploadView;
import io.pp.net_disk_demo.service.ExecuteTaskService;
import io.pp.net_disk_demo.util.FileUtil;
import io.pp.net_disk_demo.util.ToastUtil;
import io.pp.net_disk_demo.util.Util;
import io.pp.net_disk_demo.util.XPermissionUtils;
import io.pp.net_disk_demo.widget.CustomSwitchButton;

public class UploadActivity extends BaseActivity implements UploadView {

    private final String TAG = "UploadActivity";

    private final String HAS_CREATED = " HAS_CREATED";
    private final String FILE_PATH = "FILE_PATH";
    private final String FILE_NAME = "FILE_NAME";
    private final String FILE_SIZE = "FILE_SIZE";
    private final String IS_SECURE = "IS_SECURE";
    private final String STORAGE_TIME = "EXPIRED_TIME";
    private final String COPIES = "COPIES";
    private final String GAS_PRICE = "CHI_PRICE";
    private final String TOTAL_GAS = "TOTAL_GAS";
    private final String EXPECTED_COST = "EXPECTED_COST";

    private Toolbar mUploadToolBar = null;
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

    private UploadPresenter mUploadPresenter = null;

    private ExecuteTaskService mExecuteTaskService = null;
    private ExecuteTaskServiceConnection mExecuteTaskServiceConnection = null;

    private String mOperationAction = "";

    private boolean mHasCreated = false;

    private String mFilePath = "";
    private String mFileName = "";
    private long mFileSize = 0L;
    private boolean mIsSecure = false;
    private String mExpiredTime = "";
    private int mCopies = 5;
    private int mChiPrice = 0;

    private UploadInfo mUploadInfo = null;

    private DecimalFormat mDecimalFormat = null;
    private long mTotalChi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload);

        if (savedInstanceState != null) {
            mHasCreated = savedInstanceState.getBoolean(HAS_CREATED);
            mFilePath = savedInstanceState.getString(FILE_PATH);
            mFileName = savedInstanceState.getString(FILE_NAME);
            mFileSize = savedInstanceState.getLong(FILE_SIZE);
            mIsSecure = savedInstanceState.getBoolean(IS_SECURE);
            mExpiredTime = savedInstanceState.getString(STORAGE_TIME);
            mCopies = savedInstanceState.getInt(COPIES);
            mChiPrice = savedInstanceState.getInt(GAS_PRICE);

            mUploadInfo = new UploadInfo();
            mUploadInfo.setFile(mFilePath);
            mUploadInfo.setFileName(mFileName);
            mUploadInfo.setFileSize(mFileSize);
            mUploadInfo.setSecure(mIsSecure);
            mUploadInfo.setExpiredTime(mExpiredTime);
            mUploadInfo.setCopiesCount(mCopies);
            mUploadInfo.setChiPrice("" + mChiPrice);
        }

        init();

        mExecuteTaskServiceConnection = new ExecuteTaskServiceConnection(UploadActivity.this);

        startService(new Intent(UploadActivity.this, ExecuteTaskService.class));

        bindService(new Intent(UploadActivity.this, ExecuteTaskService.class),
                mExecuteTaskServiceConnection,
                BIND_AUTO_CREATE);

        mTotalChi = 0;

        mDecimalFormat = new DecimalFormat("0.000000000000000000");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(HAS_CREATED, true);

        outState.putString(FILE_PATH, mFilePath);
        outState.putString(FILE_NAME, mFileName);
        outState.putLong(FILE_SIZE, mFileSize);
        outState.putBoolean(IS_SECURE, mIsSecure);
        outState.putString(STORAGE_TIME, mExpiredTime);
        outState.putInt(COPIES, mCopies);
        outState.putDouble(GAS_PRICE, mChiPrice);

        super.onSaveInstanceState(outState);
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

        unbindService(mExecuteTaskServiceConnection);

        if (mUploadPresenter != null) {
            mUploadPresenter.onDestroy();
            mUploadPresenter = null;
        }
    }

    @Override
    public void back() {
        finish();
    }

    @Override
    public void showFileName(String fileName) {
        mFileNameTv.setText(fileName);
        mFileName = fileName;
    }

    @Override
    public void showSecure(boolean isSecure) {
        mSecureSwitch.setState(isSecure);

        mIsSecure = isSecure;
    }

    @Override
    public void showExpiredTime(String expiredTime) {
        mExpiredTimeValueTv.setText(expiredTime);
        mExpiredTime = expiredTime;
    }

    @Override
    public void showCopies(int copies) {
        mCopiesValueTv.setText("" + copies);
        mCopies = copies;
    }

    @Override
    public void showChiPrice(String chiPrice) {
        mChiPriceValueTv.setText(chiPrice);
        try {
            mChiPrice = Integer.valueOf(chiPrice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showSetExpiredTime(DateInfo defaultDateInfo) {
        new DatePickerDialog(UploadActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
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
                            if (mUploadPresenter != null) {
                                mUploadPresenter.setExpiredTime(new DateInfo(year, monthOfYear, dayOfMonth));

                                mUploadPresenter.requestStorageChi();
                            }
                        } else {
                            ToastUtil.showToast(UploadActivity.this, "the expired time is earlier than today", Toast.LENGTH_SHORT);
                        }
                    }
                }
                , defaultDateInfo.getYear()
                , defaultDateInfo.getMonthOfYear()
                , defaultDateInfo.getDayOfMonth()).show();
    }

    @Override
    public void showSetCopies(int defaultCopies) {
        mSetCopiesDialog = new SetCopiesDialog(UploadActivity.this, defaultCopies, new SetCopiesDialog.OnSetCopiesOnClickListener() {
            @Override
            public void onCancel() {
                mSetCopiesDialog.dismiss();
                mSetCopiesDialog = null;
            }

            @Override
            public void onSet(int copies) {
                if (mUploadPresenter != null) {
                    mUploadPresenter.setCopies(copies);

                    mUploadPresenter.requestStorageChi();
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
        mSetChiPriceDialog = new SetChiPriceDialog(UploadActivity.this, defaultChiPrice,
                new SetChiPriceDialog.OnSetChiPriceOnClickListener() {
                    @Override
                    public void onCancel() {
                        mSetChiPriceDialog.dismiss();
                    }

                    @Override
                    public void onSet(int chiPrice) {
                        if (mUploadPresenter != null) {
                            mUploadPresenter.setChiPrice("" + chiPrice);
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
    public void showRequestingUploadView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                mProgressDialog = new ProgressDialog(UploadActivity.this);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setCanceledOnTouchOutside(false);

                mProgressDialog.show();
            }
        });
    }

    @Override
    public void showUploadFailView(final String errMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                ToastUtil.showToast(UploadActivity.this, "upload fail : " + errMsg, Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void showRequestUploadFinishedView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                setResult(Constant.Code.RESULT_UPLOAD_OK);

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

                if (mUploadPresenter != null) {
                    mTotalChi = totalChi * mUploadPresenter.getCopies();

                    mProphecyTotalChiTv.setText("" + mTotalChi);
                    try {
                        long chiPrice = Long.parseLong(mUploadPresenter.getChiPrice());
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

        mUploadToolBar = findViewById(R.id.upload_toolbar_layout);
        mUploadToolBar.setPadding(0, 0, 0, 0);
        mUploadToolBar.setContentInsetsAbsolute(0, 0);

        setSupportActionBar(mUploadToolBar);

        mToolBarLeftTvLayout = findViewById(R.id.upload_toolbar_left_iv_layout);
        mToolBarTitleTv = findViewById(R.id.actionbar_title_tv);

        View.OnClickListener toolBarLeftOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadPresenter != null) {
                    mUploadPresenter.back();
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
                if (mUploadPresenter != null) {
                    mUploadPresenter.setSecure(state);
                }
            }
        });

        mExpiredTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadPresenter != null) {
                    mUploadPresenter.showSetExpiredTime();
                }
            }
        });

        mCopiesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadPresenter != null) {
                    mUploadPresenter.showSetCopies();
                }
            }
        });

        mChiPriceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadPresenter != null) {
                    mUploadPresenter.showSetChiPrice();
                }
            }
        });

        mIssueConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.runNetStorageOperation(UploadActivity.this, new Util.RunNetOperationCallBack() {
                    @Override
                    public void onRunOperation() {
                        if (mUploadPresenter != null) {
                            mUploadPresenter.confirm();
                        }
                    }
                });
            }
        });

        mUploadPresenter = new UploadPresenterImpl(UploadActivity.this, UploadActivity.this);

        Intent mIntent = getIntent();
        if (mIntent != null) {
            mOperationAction = mIntent.getAction();

            if (Constant.Intent.LOCAL_UPLOAD_ACTION.equals(mOperationAction)) {
                if (mHasCreated) {
                    mSecureSwitch.setState(mIsSecure);
                    mExpiredTimeValueTv.setText(mExpiredTime);
                    mChiPriceValueTv.setText("" + mChiPrice);

                    if (mUploadPresenter != null) {
                        mUploadPresenter.generateUploadModel();
                        mUploadPresenter.setUploadInfo(mUploadInfo);
                    }
                } else {
                    loadFileFromLocal();
                }
            }
        }

        XPermissionUtils.requestPermissions(UploadActivity.this,
                0,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                new XPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied() {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            String filePath;

            if (uri != null && "file".equalsIgnoreCase(uri.getScheme())) {
                //Open with a third-party app
                filePath = uri.getPath();
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                //after Android 4.4
                filePath = FileUtil.getPath(this, uri);
            } else {
                //Android 4.4 and lower versions use this method
                filePath = FileUtil.getRealPathFromURI(this, uri);
            }

            if (filePath != null) {
                if (mUploadPresenter != null) {
                    mUploadPresenter.setLocalFile(filePath);
                }
                mFilePath = filePath;
                try {
                    mFileSize = new File(mFilePath).length();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(UploadActivity.this, "filePAth is null!", Toast.LENGTH_LONG).show();
                finish();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
    }

    private void loadFileFromLocal() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    private void bindExecuteTaskService(IBinder service) {
        mExecuteTaskService = ((ExecuteTaskService.ExecuteTaskBinder) service).getExecuteTaskService();

        if (mUploadPresenter != null) {
            mUploadPresenter.bindService(mExecuteTaskService);
        }
    }

    static class ExecuteTaskServiceConnection implements ServiceConnection {

        final WeakReference<UploadActivity> uploadActivityWeakReference;

        public ExecuteTaskServiceConnection(UploadActivity uploadActivity) {
            uploadActivityWeakReference = new WeakReference<>(uploadActivity);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (uploadActivityWeakReference.get() != null) {
                uploadActivityWeakReference.get().bindExecuteTaskService(service);
            }
        }
    }
}