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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Calendar;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
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
    private ImageView mToolBarLeftTv = null;
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

    private Button mIssueConfirmBtn = null;

    private SetCopiesDialog mSetCopiesDialog = null;
    private SetChiPriceDialog mSetChiPriceDialog = null;
    private ProgressDialog mProgressDialog = null;

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
        mCopiesValueTv.setText("Copies: " + copies);
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
    public void showSetExpiredTime() {
        //
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(UploadActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if (mUploadPresenter != null) {
                            String monthOfYearStr;
                            String dayOfMonthStr;

                            monthOfYear = monthOfYear + 1;
                            if (monthOfYear < 10) {
                                monthOfYearStr = "0" + monthOfYear;
                            } else {
                                monthOfYearStr = "" + monthOfYear;
                            }

                            if (dayOfMonth < 10) {
                                dayOfMonthStr = "0" + dayOfMonth;
                            } else {
                                dayOfMonthStr = "" + dayOfMonth;
                            }

                            mUploadPresenter.setExpiredTime(year + "-" + monthOfYearStr + "-" + dayOfMonthStr);
                        }
                    }
                }
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)).show();
        //
    }

    @Override
    public void showSetCopies() {
        mSetCopiesDialog = new SetCopiesDialog(UploadActivity.this, new SetCopiesDialog.OnSetCopiesOnClickListener() {
            @Override
            public void onCancel() {
                mSetCopiesDialog.dismiss();
                mSetCopiesDialog = null;
            }

            @Override
            public void onSet(int copies) {
                if (mUploadPresenter != null) {
                    mUploadPresenter.setCopies(copies);
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
    public void showSetChiPrice() {
        mSetChiPriceDialog = new SetChiPriceDialog(UploadActivity.this,
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
                });

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

    private void init() {
        setImmersiveStatusBar();

        mUploadToolBar = findViewById(R.id.upload_toolbar_layout);
        mUploadToolBar.setPadding(0, 0, 0, 0);
        mUploadToolBar.setContentInsetsAbsolute(0, 0);

        setSupportActionBar(mUploadToolBar);

        mToolBarLeftTv = findViewById(R.id.upload_toolbar_left_iv);
        mToolBarTitleTv = findViewById(R.id.actionbar_title_tv);

        View.OnClickListener toolBarLeftOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadPresenter != null) {
                    mUploadPresenter.back();
                }
            }
        };

        mToolBarLeftTv.setOnClickListener(toolBarLeftOnClickListener);

        mToolBarTitleTv.setText("Upload Settings");

        mExpiredTimeLayout = findViewById(R.id.expiredtime_layout);
        mCopiesLayout = findViewById(R.id.copies_layout);
        mChiPriceLayout = findViewById(R.id.chiprice_layout);

        mFileNameTv = findViewById(R.id.filename_tv);
        mSecureSwitch = findViewById(R.id.upload_secure_switch);
        mExpiredTimeValueTv = findViewById(R.id.expiredtime_value_tv);
        mCopiesValueTv = findViewById(R.id.copies_value_tv);
        mChiPriceValueTv = findViewById(R.id.chiprice_value_tv);

        mIssueConfirmBtn = findViewById(R.id.issue_confirm_btn);

        mSecureSwitch.setTrackOffBitmap(R.mipmap.custom_switch_track_off);
        mSecureSwitch.setTrackOnBitmap(R.mipmap.custom_switch_track_on);
        mSecureSwitch.setThumbBitmap(R.mipmap.custom_switch_thumb);

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