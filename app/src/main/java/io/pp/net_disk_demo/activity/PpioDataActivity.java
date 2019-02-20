package io.pp.net_disk_demo.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.DeletingInfo;
import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.data.FileInfo;
import io.pp.net_disk_demo.data.ObjectStatus;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.dialog.BlockFileOptionsBottomDialog;
import io.pp.net_disk_demo.dialog.DeleteDialog;
import io.pp.net_disk_demo.dialog.FeedbackDialog;
import io.pp.net_disk_demo.dialog.PpioDataUploadGetDialog;
import io.pp.net_disk_demo.dialog.RemindDialog;
import io.pp.net_disk_demo.dialog.RenameDialog;
import io.pp.net_disk_demo.dialog.SetChiPriceDialog;
import io.pp.net_disk_demo.dialog.ShowDetailDialog;
import io.pp.net_disk_demo.dialog.ShowShareCodeDialog;
import io.pp.net_disk_demo.mvp.presenter.AccountInfoPresenter;
import io.pp.net_disk_demo.mvp.presenter.DeletePresenter;
import io.pp.net_disk_demo.mvp.presenter.ExecuteTaskPresenter;
import io.pp.net_disk_demo.mvp.presenter.PpioDataPresenter;
import io.pp.net_disk_demo.mvp.presenter.ShowShareCodePresenter;
import io.pp.net_disk_demo.mvp.presenter.ShowStatusPresenter;
import io.pp.net_disk_demo.mvp.presenter.StartRenewPresenter;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.AccountInfoPresenterImpl;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.DeletePresenterImpl;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.ExecuteTaskPresenterImpl;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.PpioDataPresenterImpl;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.ShowShareCodePresenterImpl;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.ShowStatusPresenterImpl;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.StartRenewPresenterImpl;
import io.pp.net_disk_demo.mvp.view.AccountInfoView;
import io.pp.net_disk_demo.mvp.view.DeleteView;
import io.pp.net_disk_demo.mvp.view.ExecuteTaskView;
import io.pp.net_disk_demo.mvp.view.PpioDataView;
import io.pp.net_disk_demo.mvp.view.ShareCodeView;
import io.pp.net_disk_demo.mvp.view.StartRenewView;
import io.pp.net_disk_demo.mvp.view.StatusView;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.service.DownloadService;
import io.pp.net_disk_demo.service.UploadLogService;
import io.pp.net_disk_demo.service.UploadService;
import io.pp.net_disk_demo.util.SystemUtil;
import io.pp.net_disk_demo.util.ToastUtil;
import io.pp.net_disk_demo.util.Util;
import io.pp.net_disk_demo.util.XPermissionUtils;
import io.pp.net_disk_demo.widget.LeftDrawerLayout;
import io.pp.net_disk_demo.widget.StatusBarUtil;
import io.pp.net_disk_demo.widget.recyclerview.CustomLinearLayoutManager;
import io.pp.net_disk_demo.widget.recyclerview.DownloadTaskAdapter;
import io.pp.net_disk_demo.widget.recyclerview.MyFileAdapter;
import io.pp.net_disk_demo.widget.recyclerview.UploadTaskAdapter;

public class PpioDataActivity extends BaseActivity implements PpioDataView,
        AccountInfoView,
        ExecuteTaskView,
        StatusView, StartRenewView, ShareCodeView, DeleteView {

    private static final String TAG = "PpioDataActivity";

    private static final int SLOW_REFRESH_DURATION = 1500;
    private static final int QUICK_REFRESH_DURATION = 500;

    private static final String CURRENT_SHOW_VIEW = "CURRENT_SHOW_VIEW";
    private static final int ALLFILE_VIEW = 0x01;
    private static final int UPLOADING_VIEW = 0x02;
    private static final int DOWNLOADING_VIEW = 0x03;

    private static final String SHOW_SIDE = "SHOW_SIDE";

    private LeftDrawerLayout mLeftDrawerLayout = null;

    private RelativeLayout mWActionBarLeftIconLayout = null;
    private ImageView mWActionBarLeftIconIv = null;
    private TextView mWActionBarTitleTv = null;

    private LinearLayout mAccountInfoLayout = null;
    private ImageView mAccountInfoIv = null;
    private TextView mAccountInfoTv = null;

    private LinearLayout mUsedLayout = null;
    private TextView mUsedValueTv = null;
    private ImageView mRequestUsedStatusIv = null;
    private TextView mRequestUsedStatusTv = null;

    private LinearLayout mBalanceLayout = null;
    private TextView mBalanceValueTv = null;
    private ImageView mRequestBalanceStatusIv = null;
    private TextView mRequestBalanceStatusTv = null;

    private LinearLayout mFundLayout = null;
    private TextView mFundValueTv = null;
    private ImageView mRequestFundStatusIv = null;
    private TextView mRequestFundStatusTv = null;

    private RotateAnimation mRequestUsedRotateAnimation = null;
    private RotateAnimation mRequestBalanceRotateAnimation = null;
    private RotateAnimation mRequestFundRotateAnimation = null;
    private RotateAnimation mCheckVersionRotateAnimation = null;

    private RelativeLayout mRechargeLayout = null;
    private RelativeLayout mRecordLayout = null;

    private RelativeLayout mCheckVersionLayout = null;
    private TextView mVersionValueTv = null;
    private ImageView mCheckVersionStatusIv = null;
    private TextView mCheckVersionStatusTv = null;

    private RelativeLayout mFeedbackLayout = null;
    private TextView mLogoutBtv = null;

    private RelativeLayout mAllLayout = null;
    private RelativeLayout mUploadLayout = null;
    private RelativeLayout mDownloadLayout = null;
    private ImageView mAllFileIv = null;
    private ImageView mUploadingIv = null;
    private ImageView mDownloadingIv = null;
    private TextView mAllFileTv = null;
    private TextView mUploadingTv = null;
    private TextView mDownloadingTv = null;

    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private RecyclerView mMyFileRecyclerView = null;
    private RecyclerView mUploadingFileRecyclerView = null;
    private LinearLayout mDownloadListLayout = null;
    private TextView mDownloadDirectoryTv = null;
    private RecyclerView mDownloadingFileRecyclerView = null;

    private FloatingActionButton mUploadGetBtn = null;

    private RemindDialog mRemindDialog = null;
    private FeedbackDialog mFeedbackDialog = null;
    private BlockFileOptionsBottomDialog mBlockFileOptionsBottomDialog = null;
    private SetChiPriceDialog mSetChiPriceDialog = null;
    private RenameDialog mRenameDialog = null;
    private ShowDetailDialog mShowDetailDialog = null;
    private ShowShareCodeDialog mShowShareCodeDialog = null;
    private DeleteDialog mDeleteDialog = null;

    private ProgressDialog mProgressDialog = null;
    private PpioDataUploadGetDialog mPpioDataUploadGetDialog = null;

    private MyFileAdapter mMyFileAdapter = null;
    private UploadTaskAdapter mUploadTaskAdapter = null;
    private DownloadTaskAdapter mDownloadTaskAdapter = null;

    private PpioDataPresenter mPpioDataPresenter = null;
    private AccountInfoPresenter mAccountInfoPresenter = null;
    private ExecuteTaskPresenter mExecuteTaskPresenter = null;

    private ShowStatusPresenter mShowStatusPresenter = null;
    private StartRenewPresenter mStartRenewPresenter = null;
    private ShowShareCodePresenter mShowShareCodePresenter = null;
    private DeletePresenter mDeletePresenter = null;

    private UploadLogService mUploadLogService = null;
    private UploadService mUploadService = null;
    private DownloadService mDownloadService = null;

    private UploadLogServiceConnection mUploadLogServiceConnection = null;
    private UploadServiceConnection mUploadServiceConnection = null;
    private DownloadServiceConnection mDownloadServiceConnection = null;

    private DecimalFormat mDecimalFormat = null;

    private HashMap<String, DeletingInfo> mDeletingInfoHashMap = null;
    private HashMap<String, String> mUploadFailedInfoHashMap = null;

    private Handler mHandler = null;

    private int mRefreshDuration = SLOW_REFRESH_DURATION;

    private int mCurrentShowView = ALLFILE_VIEW;
    private boolean mShowSide = false;
    private boolean mBackFromUpload = false;
    private boolean mBackFromDownload = false;

    private static String mAppVersionStr = "";

    @Override
    public void recreate() {
        super.recreate();
    }

    /**
     * Activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        Log.e(TAG, "onCreate()");
        //

        if (savedInstanceState != null) {
            mCurrentShowView = savedInstanceState.getInt(CURRENT_SHOW_VIEW);
            mShowSide = savedInstanceState.getBoolean(SHOW_SIDE);
        }

        mPpioDataPresenter = new PpioDataPresenterImpl(PpioDataActivity.this, PpioDataActivity.this);
        mAccountInfoPresenter = new AccountInfoPresenterImpl(PpioDataActivity.this, PpioDataActivity.this);
        mExecuteTaskPresenter = new ExecuteTaskPresenterImpl(PpioDataActivity.this, PpioDataActivity.this);
        mShowStatusPresenter = new ShowStatusPresenterImpl(PpioDataActivity.this, PpioDataActivity.this);
        mStartRenewPresenter = new StartRenewPresenterImpl(PpioDataActivity.this, PpioDataActivity.this);
        mShowShareCodePresenter = new ShowShareCodePresenterImpl(PpioDataActivity.this, PpioDataActivity.this);
        mDeletePresenter = new DeletePresenterImpl(PpioDataActivity.this, PpioDataActivity.this);

        mUploadLogServiceConnection = new UploadLogServiceConnection(PpioDataActivity.this);
        mUploadServiceConnection = new UploadServiceConnection(PpioDataActivity.this);
        mDownloadServiceConnection = new DownloadServiceConnection(PpioDataActivity.this);

        startService(new Intent(PpioDataActivity.this, UploadLogService.class));
        startService(new Intent(PpioDataActivity.this, UploadService.class));
        startService(new Intent(PpioDataActivity.this, DownloadService.class));

        bindService(new Intent(PpioDataActivity.this, UploadLogService.class),
                mUploadLogServiceConnection,
                BIND_AUTO_CREATE);

        bindService(new Intent(PpioDataActivity.this, UploadService.class),
                mUploadServiceConnection,
                BIND_AUTO_CREATE);

        bindService(new Intent(PpioDataActivity.this, DownloadService.class),
                mDownloadServiceConnection,
                BIND_AUTO_CREATE);

        setContentView(R.layout.activity_ppiodata);

        mDeletingInfoHashMap = new HashMap<>();
        mUploadFailedInfoHashMap = new HashMap<>();

        mHandler = new Handler();

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //
        Log.e(TAG, "onStart()");
        //
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //
        Log.e(TAG, "onRestoreInstanceState");
        //

        if (PossUtil.getUser() == null) {
            Util.runStorageOperation(PpioDataActivity.this, new Util.RunNetOperationCallBack() {
                @Override
                public void onRunOperation() {
                    if (mPpioDataPresenter != null) {
                        mPpioDataPresenter.link();
                    }
                }

                @Override
                public void onCanceled() {
                    mRemindDialog = new RemindDialog(PpioDataActivity.this,
                            "Link disconnect because has no storage permission, and can not login.",
                            "Please open storage permission",
                            new RemindDialog.OnOkClickListener() {
                                @Override
                                public void onOk() {
                                    finish();
                                }
                            });
                    mRemindDialog.setCancelable(false);
                    mRemindDialog.setCanceledOnTouchOutside(false);
                    mRemindDialog.show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //
        Log.e(TAG, "onResume()");
        //
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == Constant.Code.REQUEST_UPLOAD &&
                resultCode == Constant.Code.RESULT_UPLOAD_OK)) {
            mBackFromUpload = true;

            if (mExecuteTaskPresenter != null) {
                mExecuteTaskPresenter.showRequestUploadFinished();
                mExecuteTaskPresenter.startRefreshTasks();
            }
        }

        if (requestCode == Constant.Code.REQUEST_DOWNLOAD &&
                resultCode == Constant.Code.RESULT_DOWNLOAD_OK) {
            mBackFromDownload = true;

            if (mExecuteTaskPresenter != null) {
                mExecuteTaskPresenter.showRequestDownloadFinished();
                mExecuteTaskPresenter.startRefreshTasks();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //
        Log.e(TAG, "onPause()");
        //
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_SHOW_VIEW, mCurrentShowView);
        outState.putBoolean(SHOW_SIDE, mShowSide);

        super.onSaveInstanceState(outState);

        //
        Log.e(TAG, "onSaveInstanceState(Bundle outState)");
        //
    }

    @Override
    protected void onStop() {
        super.onStop();

        //
        Log.e(TAG, "onStop()");
        //
    }

    @Override
    protected void onDestroy() {
        //
        Log.e(TAG, "onDestroy()");
        //

        //dialog
        if (mFeedbackDialog != null) {
            mFeedbackDialog.dismiss();
            mFeedbackDialog = null;
        }

        if (mRemindDialog != null) {
            mRemindDialog.dismiss();
            mRemindDialog = null;
        }

        if (mBlockFileOptionsBottomDialog != null) {
            mBlockFileOptionsBottomDialog.dismiss();
            mBlockFileOptionsBottomDialog = null;
        }

        if (mSetChiPriceDialog != null) {
            mSetChiPriceDialog.dismiss();
            mSetChiPriceDialog = null;
        }

        if (mRenameDialog != null) {
            mRenameDialog.dismiss();
            mRenameDialog = null;
        }

        if (mShowDetailDialog != null) {
            mShowDetailDialog.dismiss();
            mShowDetailDialog = null;
        }

        if (mShowShareCodeDialog != null) {
            mShowShareCodeDialog.dismiss();
            mShowShareCodeDialog = null;
        }

        if (mDeleteDialog != null) {
            mDeleteDialog.dismiss();
            mDeleteDialog = null;
        }

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        if (mPpioDataUploadGetDialog != null) {
            mPpioDataUploadGetDialog.dismiss();
            mPpioDataUploadGetDialog = null;
        }

        //presenter
        if (mPpioDataPresenter != null) {
            mPpioDataPresenter.onDestroy();
        }

        if (mAccountInfoPresenter != null) {

            mAccountInfoPresenter.onDestroy();
        }

        if (mExecuteTaskPresenter != null) {
            mExecuteTaskPresenter.onDestroy();
        }

        if (mShowStatusPresenter != null) {
            mShowStatusPresenter.onDestroy();
        }

        if (mStartRenewPresenter != null) {
            mStartRenewPresenter.onDestroy();
        }

        if (mDeletePresenter != null) {
            mDeletePresenter.onDestroy();
        }

        mPpioDataPresenter = null;
        mAccountInfoPresenter = null;
        mExecuteTaskPresenter = null;

        mShowStatusPresenter = null;
        mStartRenewPresenter = null;
        mDeletePresenter = null;

        //service
        unbindService(mUploadLogServiceConnection);
        unbindService(mUploadServiceConnection);
        unbindService(mDownloadServiceConnection);

        mUploadLogService = null;
        mUploadService = null;
        mDownloadService = null;

        super.onDestroy();
    }

    /**
     * PpioDataView
     */
    @Override
    public void showLinkingView() {
        showNetWorkingView();
    }

    @Override
    public void stopShowLinkingView() {
        stopShowNetWorkingView();
    }

    @Override
    public void showLinkFailView(String failMessage) {
        String functionStr = "link error: ";
        showNetWorkingErrorView(functionStr, failMessage);
    }

    public void showNotLogInView() {
        ToastUtil.showToast(PpioDataActivity.this, "not login!", Toast.LENGTH_SHORT);

        startActivity(new Intent(PpioDataActivity.this, CheckHasKeyStoreActivity.class));
        finish();
    }

    @Override
    public void showRefreshingAllFileListView() {
        showNetWorkingView();
    }

    @Override
    public void showRefreshAllFileListFailView(final String failStr) {
        String functionStr = "refresh all files error: ";

        showNetWorkingErrorView(functionStr, failStr);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void showAllFileList(HashMap<String, DeletingInfo> deletingInfoHashMap, final ArrayList<FileInfo> mMyFileInfoList, final boolean allRefresh) {
        stopShowNetWorkingView();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeletingInfoHashMap = deletingInfoHashMap;

                if (mDeletingInfoHashMap.size() != 0) {
                    mRefreshDuration = QUICK_REFRESH_DURATION;
                } else {
                    mRefreshDuration = SLOW_REFRESH_DURATION;
                }

                if (mDeletingInfoHashMap != null && mDeletingInfoHashMap.size() > 0) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mPpioDataPresenter != null) {
                                mPpioDataPresenter.refreshAllFileList(mDeletingInfoHashMap, mUploadFailedInfoHashMap, false);
                            }
                        }
                    }, mRefreshDuration);
                }

                mSwipeRefreshLayout.setRefreshing(false);

                if (allRefresh) {
                    //mMyFileAdapter.refreshDeletingInfoHashMap(mDeletingInfoHashMap);
                    mMyFileAdapter.refreshFileList(mDeletingInfoHashMap, mMyFileInfoList);
                } else {
                    mMyFileAdapter.updateDeletingFileList(mDeletingInfoHashMap, mMyFileInfoList);
                }

                if (mMyFileAdapter.getItemCount() == 0 && mSwipeRefreshLayout.getVisibility() == View.VISIBLE) {
                    mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * AccountInfoView
     */
    @Override
    public void showAddress(String address) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAccountInfoTv.setText(address);
            }
        });
    }

    @Override
    public void showRequestUsedView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRequestUsedStatusTv.setText("");
                mRequestUsedStatusTv.setVisibility(View.INVISIBLE);

                mRequestUsedStatusIv.setVisibility(View.VISIBLE);
                mRequestUsedStatusIv.setBackgroundResource(R.mipmap.blue_loading);
                mRequestUsedStatusIv.startAnimation(mRequestUsedRotateAnimation);
            }
        });
    }

    @Override
    public void showUsedView(final String used) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRequestUsedStatusTv.setText("");
                mRequestUsedStatusTv.setVisibility(View.INVISIBLE);
                mRequestUsedStatusIv.clearAnimation();
                mRequestUsedStatusIv.setVisibility(View.INVISIBLE);

                mUsedValueTv.setText(used);
            }
        });
    }

    @Override
    public void showGetUsedFailView(String errMsg) {
        String functionStr = "get used error: ";
        showNetWorkingErrorView(functionStr, errMsg);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRequestUsedStatusTv.setVisibility(View.VISIBLE);
                mRequestUsedStatusTv.setText("refresh fail");

                mRequestUsedStatusIv.setVisibility(View.VISIBLE);
                mRequestUsedStatusIv.setBackgroundResource(R.mipmap.task_error_icon);
                mRequestUsedStatusIv.clearAnimation();
            }
        });
    }

    @Override
    public void showRequestBalanceView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRequestBalanceStatusTv.setText("");
                mRequestBalanceStatusTv.setVisibility(View.INVISIBLE);

                mRequestBalanceStatusIv.setVisibility(View.VISIBLE);
                mRequestBalanceStatusIv.setBackgroundResource(R.mipmap.blue_loading);
                mRequestBalanceStatusIv.startAnimation(mRequestBalanceRotateAnimation);
            }
        });
    }

    @Override
    public void showBalanceView(final String balance) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRequestBalanceStatusTv.setText("");
                mRequestBalanceStatusTv.setVisibility(View.INVISIBLE);
                mRequestBalanceStatusIv.clearAnimation();
                mRequestBalanceStatusIv.setVisibility(View.INVISIBLE);

                try {
                    double balanceWei = Double.parseDouble(balance);
                    double balancePPCoin = balanceWei / 1000000000000000000l;
                    if (balancePPCoin >= 0) {
                        mBalanceValueTv.setText(mDecimalFormat.format(balancePPCoin) + " PPCoin");
                    } else {
                        mBalanceValueTv.setText("0 PPCoin");
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    mBalanceValueTv.setText("0 PPCoin");
                }
            }
        });
    }

    @Override
    public void showGetBalanceFailView(String errMsg) {
        String functionStr = "get balance error: ";
        //showNetWorkingErrorView(functionStr, errMsg);
        Log.e(TAG, "showGetBalanceFailView()");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRequestBalanceStatusTv.setVisibility(View.VISIBLE);
                mRequestBalanceStatusTv.setText("refresh fail");

                mRequestBalanceStatusIv.setVisibility(View.VISIBLE);
                mRequestBalanceStatusIv.setBackgroundResource(R.mipmap.task_error_icon);
                mRequestBalanceStatusIv.clearAnimation();
            }
        });
    }

    @Override
    public void showRequestFundView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRequestFundStatusTv.setText("");
                mRequestFundStatusTv.setVisibility(View.INVISIBLE);

                mRequestFundStatusIv.setVisibility(View.VISIBLE);
                mRequestFundStatusIv.setBackgroundResource(R.mipmap.blue_loading);
                mRequestFundStatusIv.startAnimation(mRequestFundRotateAnimation);
            }
        });
    }

    @Override
    public void showFundView(final String fund) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRequestFundStatusTv.setText("");
                mRequestFundStatusTv.setVisibility(View.INVISIBLE);
                mRequestFundStatusIv.clearAnimation();
                mRequestFundStatusIv.setVisibility(View.INVISIBLE);

                try {
                    double fundWei = Double.parseDouble(fund);
                    double fundPPCoin = fundWei / 1000000000000000000l;

                    if (fundPPCoin >= 0) {
                        mFundValueTv.setText(mDecimalFormat.format(fundPPCoin) + " PPCoin");
                    } else {
                        mFundValueTv.setText("0 PPCoin");
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    mFundValueTv.setText("0 PPCoin");
                }
            }
        });
    }

    @Override
    public void showGetFundFailView(String errMsg) {
        String functionStr = "get fund error: ";
        //showNetWorkingErrorView(functionStr, errMsg);
        Log.e(TAG, "showGetFundFailView");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRequestFundStatusTv.setVisibility(View.VISIBLE);
                mRequestFundStatusTv.setText("refresh fail");

                mRequestFundStatusIv.setVisibility(View.VISIBLE);
                mRequestFundStatusIv.setBackgroundResource(R.mipmap.task_error_icon);
                mRequestFundStatusIv.clearAnimation();
            }
        });
    }

    @Override
    public void showRechargeView() {
        //Uri uri = Uri.parse("http://chain-web-wallet.s3-website-us-west-2.amazonaws.com:80");
//        Uri uri = Uri.parse(Constant.URL.WALLET_URL);
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(intent);

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL.WALLET_URL)));
    }

    @Override
    public void showRecordView() {
        //ToastUtil.showToast(PpioDataActivity.this, "Not implemented!", Toast.LENGTH_SHORT);
        startActivity(new Intent(PpioDataActivity.this, RecordActivity.class));
    }

    @Override
    public void showCheckVersionView() {
        //ToastUtil.showToast(PpioDataActivity.this, "Not implemented!", Toast.LENGTH_SHORT);
        //startActivity(new Intent(PpioDataActivity.this, VersionActivity.class));
        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL.UPDATE_URL)));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCheckVersionStatusTv.setText("");
                mCheckVersionStatusTv.setVisibility(View.VISIBLE);

                mCheckVersionStatusIv.setVisibility(View.VISIBLE);
                mCheckVersionStatusIv.setBackgroundResource(R.mipmap.blue_loading);
                mCheckVersionStatusIv.startAnimation(mRequestFundRotateAnimation);
            }
        });
    }

    @Override
    public void showLatestVersionVersion(String version) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (version.equals(mAppVersionStr)) {
                    mCheckVersionStatusTv.setText("the current is latest");
                } else {
                    mCheckVersionStatusTv.setText("v" + version + "is available");
                }
                mCheckVersionStatusTv.setVisibility(View.VISIBLE);

                mCheckVersionStatusIv.clearAnimation();
                mCheckVersionStatusIv.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showCheckVersionFailView(String errMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCheckVersionStatusTv.setVisibility(View.VISIBLE);
                mCheckVersionStatusTv.setText("check fail");

                mCheckVersionStatusIv.setVisibility(View.VISIBLE);
                mCheckVersionStatusIv.setBackgroundResource(R.mipmap.task_error_icon);
                mCheckVersionStatusIv.clearAnimation();
            }
        });
    }

    @Override
    public void showFeedbackView() {
        //ToastUtil.showToast(PpioDataActivity.this, "Not implemented!", Toast.LENGTH_SHORT);
        //startActivity(new Intent(PpioDataActivity.this, FeedbackActivity.class));
        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL.FEEDBACK_URL)));
        if (mFeedbackDialog != null) {
            mFeedbackDialog.dismiss();
            mFeedbackDialog = null;
        }

        mFeedbackDialog = new FeedbackDialog(PpioDataActivity.this, new FeedbackDialog.UploadLogClickListener() {
            @Override
            public void onSubmit(String description) {
                Util.runNetStorageOperation(PpioDataActivity.this, new Util.RunNetOperationCallBack() {
                    @Override
                    public void onRunOperation() {
                        if (mAccountInfoPresenter != null) {
                            mAccountInfoPresenter.uploadLog(description);
                        }
                    }

                    @Override
                    public void onCanceled() {

                    }
                });
            }
        });

        mFeedbackDialog.setCancelable(false);
        mFeedbackDialog.setCanceledOnTouchOutside(false);
        mFeedbackDialog.show();
    }

    @Override
    public void showLogOutPrepareView() {
        showNetWorkingView();
    }

    @Override
    public void showLogOutErrorView(String errMsg) {
        String functionStr = "log out error: ";
        showNetWorkingErrorView(functionStr, errMsg);
    }

    @Override
    public void showLogOutFinishView() {
        //
        if (mExecuteTaskPresenter != null) {
            mExecuteTaskPresenter.stopAllTask();
        }
        //

        ToastUtil.showToast(PpioDataActivity.this, "log out!", Toast.LENGTH_SHORT);

        //startActivity(new Intent(PpioDataActivity.this, LogInOrRegisterActivity.class));
        startActivity(new Intent(PpioDataActivity.this, KeyStoreLogInActivity.class));
        finish();
    }

    @Override
    public void showRefreshTasksError(String errMsg) {
        showNetWorkingErrorView("refresh tasks error: ", errMsg);
    }

    @Override
    public void showUploadingTasks(final ArrayList<TaskInfo> uploadingTaskList, boolean allRefresh) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (allRefresh) {
                    mUploadTaskAdapter.refreshUploadingList(uploadingTaskList);
                } else {
                    mUploadTaskAdapter.updateUploadingList(uploadingTaskList);
                }

                if (mCurrentShowView == UPLOADING_VIEW) {
                    if (mUploadTaskAdapter.getItemCount() == 0) {
                        mUploadingFileRecyclerView.setVisibility(View.INVISIBLE);
                    } else {
                        mUploadingFileRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void showUploadTaskError(String errMsg) {
        showNetWorkingErrorView("refresh tasks error: ", errMsg);
    }

    @Override
    public void showDownloadingTasks(final ArrayList<TaskInfo> downloadingTaskList, boolean allRefresh) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (allRefresh) {
                    mDownloadTaskAdapter.refreshDownloadingList(downloadingTaskList);
                } else {
                    mDownloadTaskAdapter.updateDownloadingList(downloadingTaskList);
                }

                if (mCurrentShowView == DOWNLOADING_VIEW) {
                    if (mDownloadTaskAdapter.getItemCount() == 0) {
                        mDownloadingFileRecyclerView.setVisibility(View.INVISIBLE);
                    } else {
                        mDownloadingFileRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void showDownloadTaskError(String errMsg) {
        showNetWorkingErrorView("refresh tasks error: ", errMsg);
    }

    @Override
    public void showRequestUploadFinishedView() {
        showUploadView();

        if (mExecuteTaskPresenter != null) {
            mExecuteTaskPresenter.startRefreshTasks();
        }

        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.requestUsed();
            mAccountInfoPresenter.requestBalance();
            mAccountInfoPresenter.requestFund();
        }
    }

    @Override
    public void showRefreshFileListView() {
        if (mPpioDataPresenter != null) {
            mPpioDataPresenter.refreshAllFileList(mDeletingInfoHashMap, mUploadFailedInfoHashMap, true);
        }
    }

    @Override
    public void showRequestDownloadFinishedView() {
        showDownloadView();

        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.requestUsed();
            mAccountInfoPresenter.requestBalance();
            mAccountInfoPresenter.requestFund();
        }
    }

    @Override
    public void showOperateTaskPrepareView() {
        showNetWorkingView();
    }

    @Override
    public void showOperateTaskErrorView(String errMsg) {
        showNetWorkingErrorView("", errMsg);
    }

    @Override
    public void showOperateTaskFinishedView() {
        stopShowNetWorkingView();

        if (mExecuteTaskPresenter != null) {
            mExecuteTaskPresenter.startRefreshTasks();
        }
    }

    @Override
    public void showDeleteUploadingTaskFinishedView(String bucket, String key) {
        stopShowNetWorkingView();

        if (mDeletePresenter != null) {
            //
            Log.e(TAG, "++++++ mUploadFailedInfoHashMap.put() " + bucket + key);
            //
            mUploadFailedInfoHashMap.put(bucket + key, bucket + key);
            mDeletePresenter.deleteSilently(bucket, key);
        }

        if (mExecuteTaskPresenter != null) {
            mExecuteTaskPresenter.startRefreshTasks();
        }
    }

    /**
     * DetailView
     */
    @Override
    public void showGettingStatusView() {
        showNetWorkingView();
    }

    @Override
    public void showGettingStatusErrorView(String errMsg) {
        String functionStr = "get status error: ";
        showNetWorkingErrorView(functionStr, errMsg);
    }

    @Override
    public void showStatusView(final ObjectStatus objectStatus) {
        stopShowNetWorkingView();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mShowDetailDialog != null) {
                    mShowDetailDialog.dismiss();
                    mShowDetailDialog = null;
                }

                mShowDetailDialog = new ShowDetailDialog(PpioDataActivity.this);
                mShowDetailDialog.setDetailText(objectStatus);
                mShowDetailDialog.show();
            }
        });
    }

    /**
     * RenewView
     */
    @Override
    public void showStartingRenewView() {
        showNetWorkingView();
    }

    @Override
    public void showStartRenewErrorView(String errMsg) {
        String functionStr = "get object expired time error: ";
        showNetWorkingErrorView(functionStr, errMsg);
    }

    @Override
    public void showRenewView(FileInfo fileInfo) {
        stopShowNetWorkingView();

        startActivityForResult(new Intent(PpioDataActivity.this, RenewActivity.class)
                .setAction(Constant.Intent.RENEW_ACTION)
                .putExtra(Constant.Data.RENEW_FILE, fileInfo), Constant.Code.REQUEST_RENEW);
    }

    /**
     * ShareCodeView
     */
    @Override
    public void showGettingShareCodeView() {
        showNetWorkingView();
    }

    @Override
    public void showGettingShareCodeErrorView(String errMsg) {
        String functionStr = "get shareCode error: ";
        showNetWorkingErrorView(functionStr, errMsg);
    }

    @Override
    public void showShareCode(final String shareCode) {
        stopShowNetWorkingView();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mShowShareCodeDialog != null) {
                    mShowShareCodeDialog.dismiss();
                    mShowDetailDialog = null;
                }

                mShowShareCodeDialog = new ShowShareCodeDialog(PpioDataActivity.this);
                mShowShareCodeDialog.setShareCodeText(shareCode);
                mShowShareCodeDialog.show();
            }
        });
    }

    /**
     * DeleteView
     */
    @Override
    public void onDeletePrepare() {
        showNetWorkingView();
    }

    @Override
    public void onDeleteError(String errMsg) {
        String functionStr = "delete error: ";
        showNetWorkingErrorView(functionStr, errMsg);
    }

    @Override
    public void onDeleteFinish(DeletingInfo deletingInfo) {
        stopShowNetWorkingView();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeletingInfoHashMap.put(deletingInfo.getName(), deletingInfo);

                if (mPpioDataPresenter != null) {
                    mPpioDataPresenter.refreshAllFileList(mDeletingInfoHashMap, mUploadFailedInfoHashMap, false);
                }

                if (mAccountInfoPresenter != null) {
                    mAccountInfoPresenter.requestUsed();
                    mAccountInfoPresenter.requestBalance();
                    mAccountInfoPresenter.requestFund();
                }
            }
        });
    }

    @Override
    public void onDeleteSilentlyFinish(String bucket, String key) {
        mUploadFailedInfoHashMap.remove(bucket + key);
    }

    public void showNetWorkingView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                mProgressDialog = new ProgressDialog(PpioDataActivity.this);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
            }
        });
    }

    public void stopShowNetWorkingView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        });
    }

    public void showNetWorkingErrorView(final String functionStr, final String errMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                ToastUtil.showToast(PpioDataActivity.this, functionStr + errMsg, Toast.LENGTH_SHORT);
            }
        });
    }

    public void showUploadGet() {
        final RotateAnimation mFabRotateRightAnimation = new RotateAnimation(0.0f, 45.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mFabRotateRightAnimation.setDuration(500L);
        mFabRotateRightAnimation.setFillAfter(true);
        mUploadGetBtn.startAnimation(mFabRotateRightAnimation);

        mPpioDataUploadGetDialog = new PpioDataUploadGetDialog(PpioDataActivity.this,
                new PpioDataUploadGetDialog.OnUploadGetOnClickListener() {
                    @Override
                    public void onUpload() {
                        if (mPpioDataPresenter != null) {
                            mPpioDataPresenter.startUpload();
                        }
                    }

                    @Override
                    public void onGet() {
                        if (mPpioDataPresenter != null) {
                            mPpioDataPresenter.startGet();
                        }
                    }
                },
                new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mPpioDataUploadGetDialog = null;

                        RotateAnimation mFabRotateLeftAnimation = new RotateAnimation(45.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        mFabRotateLeftAnimation.setDuration(500L);
                        mFabRotateLeftAnimation.setFillAfter(true);

                        mUploadGetBtn.startAnimation(mFabRotateLeftAnimation);
                    }
                });

        mPpioDataUploadGetDialog.setShowBottomRightGravity();
        mPpioDataUploadGetDialog.setShowCoordinate(Util.dp2px(PpioDataActivity.this, 10),
                mUploadGetBtn.getHeight() + Util.dp2px(PpioDataActivity.this, 70));

        mPpioDataUploadGetDialog.show();
    }

    @Override
    public void startUpload() {
        startActivityForResult(new Intent(PpioDataActivity.this, UploadActivity.class)
                        .setAction(Constant.Intent.LOCAL_UPLOAD_ACTION),
                Constant.Code.REQUEST_UPLOAD);

        if (mPpioDataUploadGetDialog != null) {
            mPpioDataUploadGetDialog.dismiss();
            mPpioDataUploadGetDialog = null;
        }
    }

    @Override
    public void startGet() {
        startActivityForResult(new Intent(PpioDataActivity.this, GetActivity.class),
                Constant.Code.REQUEST_DOWNLOAD);

        if (mPpioDataUploadGetDialog != null) {
            mPpioDataUploadGetDialog.dismiss();
            mPpioDataUploadGetDialog = null;
        }
    }

    private void init() {
        //
        Log.e(TAG, "init()");
        //

        initView();

        initListener();

        initData();

        XPermissionUtils.requestPermissions(PpioDataActivity.this,
                0,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET},
                new XPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied() {

                    }
                });
    }

    private void initView() {
        setImmersiveStatusBar();

        mLeftDrawerLayout = findViewById(R.id.left_drawer_layout);

        mWActionBarLeftIconLayout = findViewById(R.id.wei_actionbar_left_layout);
        mWActionBarLeftIconIv = findViewById(R.id.wei_actionbar_left_iv);
        mWActionBarTitleTv = findViewById(R.id.wei_actionbar_title_iv);

        //mAccountInfoLayout = findViewById(R.id.account_info_layout);
        mAccountInfoIv = findViewById(R.id.user_photo_iv);
        mAccountInfoTv = findViewById(R.id.user_code_tv);

        mUsedLayout = findViewById(R.id.used_layout);
        mUsedValueTv = findViewById(R.id.used_value_tv);
        mRequestUsedStatusIv = findViewById(R.id.request_used_status_iv);
        mRequestUsedStatusTv = findViewById(R.id.request_used_status_tv);

        mBalanceLayout = findViewById(R.id.balance_layout);
        mBalanceValueTv = findViewById(R.id.balance_value_tv);
        mRequestBalanceStatusIv = findViewById(R.id.request_balance_status_iv);
        mRequestBalanceStatusTv = findViewById(R.id.request_balance_status_tv);

        mFundLayout = findViewById(R.id.fund_layout);
        mFundValueTv = findViewById(R.id.fund_value_tv);
        mRequestFundStatusIv = findViewById(R.id.request_fund_status_iv);
        mRequestFundStatusTv = findViewById(R.id.request_fund_status_tv);

        mRechargeLayout = findViewById(R.id.recharge_layout);
        mRecordLayout = findViewById(R.id.record_layout);

        mCheckVersionLayout = findViewById(R.id.checkversion_layout);
        mVersionValueTv = findViewById(R.id.version_value_tv);
        mCheckVersionStatusIv = findViewById(R.id.check_version_status_iv);
        mCheckVersionStatusTv = findViewById(R.id.check_version_status_tv);

        mFeedbackLayout = findViewById(R.id.feedback_layout);
        mLogoutBtv = findViewById(R.id.logout_tv);

        mUploadGetBtn = findViewById(R.id.upload_get_btn);

        mAllLayout = findViewById(R.id.tools_allfile_layout);
        mUploadLayout = findViewById(R.id.tools_upload_layout);
        mDownloadLayout = findViewById(R.id.tools_download_layout);
        mAllFileIv = findViewById(R.id.tools_allfile_iv);
        mUploadingIv = findViewById(R.id.tools_uploading_iv);
        mDownloadingIv = findViewById(R.id.tools_downloading_iv);
        mAllFileTv = findViewById(R.id.tools_allfile_tv);
        mUploadingTv = findViewById(R.id.tools_uploading_tv);
        mDownloadingTv = findViewById(R.id.tools_downloading_tv);

        mSwipeRefreshLayout = findViewById(R.id.refresh_layout);
        mMyFileRecyclerView = findViewById(R.id.myfile_recyclerview);
        mUploadingFileRecyclerView = findViewById(R.id.uploadingfile_recyclerview);
        mDownloadListLayout = findViewById(R.id.download_list_layout);
        mDownloadDirectoryTv = findViewById(R.id.download_directory_tv);
        mDownloadingFileRecyclerView = findViewById(R.id.downloadingfile_recyclerview);

        StatusBarUtil.setColorNoTranslucentForLeftDrawerLayout(this, mLeftDrawerLayout, getResources().getColor(R.color.account_background_blue));

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.account_background_blue));
        //mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.account_background_blue));

        mMyFileRecyclerView.setLayoutManager(new CustomLinearLayoutManager(PpioDataActivity.this));
        mMyFileRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) != 0) {
                    outRect.top = Util.dp2px(PpioDataActivity.this, 1);
                }
            }
        });

        mMyFileAdapter = new MyFileAdapter(PpioDataActivity.this);
        mMyFileRecyclerView.setAdapter(mMyFileAdapter);

        mUploadingFileRecyclerView.setLayoutManager(new CustomLinearLayoutManager(PpioDataActivity.this));
        mUploadingFileRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) != 0) {
                    outRect.top = Util.dp2px(PpioDataActivity.this, 1);
                }
            }
        });

        mUploadTaskAdapter = new UploadTaskAdapter(PpioDataActivity.this, null);
        mUploadingFileRecyclerView.setAdapter(mUploadTaskAdapter);

        mDownloadingFileRecyclerView.setLayoutManager(new CustomLinearLayoutManager(PpioDataActivity.this));
        mDownloadingFileRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) != 0) {
                    outRect.top = Util.dp2px(PpioDataActivity.this, 1);
                }
            }
        });

        mDownloadTaskAdapter = new DownloadTaskAdapter(PpioDataActivity.this, null);
        mDownloadingFileRecyclerView.setAdapter(mDownloadTaskAdapter);

        final RotateAnimation mFabRotateRightAnimation = new RotateAnimation(0.0f, 45.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mFabRotateRightAnimation.setDuration(500L);
        mFabRotateRightAnimation.setFillAfter(true);

        mRequestUsedRotateAnimation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRequestUsedRotateAnimation.setInterpolator(new LinearInterpolator());
        mRequestUsedRotateAnimation.setDuration(1000l);
        mRequestUsedRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRequestUsedRotateAnimation.setRepeatMode(Animation.RESTART);

        mRequestBalanceRotateAnimation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRequestBalanceRotateAnimation.setInterpolator(new LinearInterpolator());
        mRequestBalanceRotateAnimation.setDuration(1000l);
        mRequestBalanceRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRequestBalanceRotateAnimation.setRepeatMode(Animation.RESTART);

        mRequestFundRotateAnimation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRequestFundRotateAnimation.setInterpolator(new LinearInterpolator());
        mRequestFundRotateAnimation.setDuration(1000l);
        mRequestFundRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRequestFundRotateAnimation.setRepeatMode(Animation.RESTART);

        mCheckVersionRotateAnimation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mCheckVersionRotateAnimation.setInterpolator(new LinearInterpolator());
        mCheckVersionRotateAnimation.setDuration(1000l);
        mCheckVersionRotateAnimation.setRepeatCount(Animation.INFINITE);
        mCheckVersionRotateAnimation.setRepeatMode(Animation.RESTART);

        mDecimalFormat = new DecimalFormat("0.000000000000000000");

        mAppVersionStr = SystemUtil.getAppVersion(PpioDataActivity.this);
        mVersionValueTv.setText("v" + mAppVersionStr);
        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.checkVersion();
        }

        mDownloadDirectoryTv.setText("download to " + Constant.PPIO_File.DOWNLOAD_DIR );
    }

    private void initListener() {
        mWActionBarLeftIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLeftDrawerLayout.isOpened()) {
                    mLeftDrawerLayout.openDrawer();
                } else {
                    mLeftDrawerLayout.closeDrawer();
                }
            }
        });

        mLeftDrawerLayout.addDrawerListener(new LeftDrawerLayout.DrawerListener() {
            @Override
            public void onDrawerOpened(View contentView, View drawerView) {
                mShowSide = true;

                if (mAccountInfoPresenter != null) {
                    mAccountInfoPresenter.requestUsed();
                    mAccountInfoPresenter.requestBalance();
                    mAccountInfoPresenter.requestFund();
                }
            }

            @Override
            public void onDrawerClosed(View contentView, View drawerView) {
                mShowSide = false;
            }

            @Override
            public void onDrawerStateChanged(View contentView, View drawerView, int newState) {

            }

            @Override
            public void onDrawerSlideOffset(View contentView, View drawerView, float slideOffset) {

            }
        });

        mAccountInfoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PpioDataActivity.this, TestActivity.class));
            }
        });

        mAccountInfoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) PpioDataActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("address", mAccountInfoTv.getText());
                cm.setPrimaryClip(mClipData);

                ToastUtil.showToast(PpioDataActivity.this, "address copy succeed!", Toast.LENGTH_SHORT);
            }
        });

        mUsedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccountInfoPresenter != null) {
                    mAccountInfoPresenter.requestUsed();
                    showRequestUsedView();
                }
            }
        });

        mBalanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccountInfoPresenter != null) {
                    mAccountInfoPresenter.requestBalance();
                }
            }
        });

        mFundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccountInfoPresenter != null) {
                    mAccountInfoPresenter.requestFund();
                }
            }
        });

        mRechargeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccountInfoPresenter != null) {
                    mAccountInfoPresenter.showRecharge();
                }
            }
        });

        mRecordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccountInfoPresenter != null) {
                    mAccountInfoPresenter.showRecord();
                }
            }
        });

        mCheckVersionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccountInfoPresenter != null) {
                    mAccountInfoPresenter.checkVersion();
                }
            }
        });

        mFeedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccountInfoPresenter != null) {
                    mAccountInfoPresenter.showFeedback();
                }
            }
        });

        mLogoutBtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccountInfoPresenter != null) {
                    mAccountInfoPresenter.showLogOutPrepare();

                    mAccountInfoPresenter.startLogOut();
                }
            }
        });

        mUploadGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPpioDataPresenter != null) {
                    mPpioDataPresenter.showUploadGet();
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Util.runNetOperation(PpioDataActivity.this, new Util.RunNetOperationCallBack() {
                    @Override
                    public void onRunOperation() {
                        if (mPpioDataPresenter != null) {
                            mPpioDataPresenter.refreshAllFileList(mDeletingInfoHashMap, mUploadFailedInfoHashMap, true);
                        }
                    }

                    @Override
                    public void onCanceled() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        mAllLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllFileView();

                if (mPpioDataPresenter != null) {
                    mPpioDataPresenter.refreshAllFileList(mDeletingInfoHashMap, mUploadFailedInfoHashMap, true);
                }
            }
        });

        mUploadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUploadView();
            }
        });

        mDownloadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDownloadView();
            }
        });

        mMyFileAdapter.setOnItemListener(new MyFileAdapter.OnItemListener() {
            @Override
            public void onItemClick(final int position) {
                if (mSwipeRefreshLayout.isRefreshing()) {
                    return;
                }

                mBlockFileOptionsBottomDialog = null;

                mBlockFileOptionsBottomDialog = new BlockFileOptionsBottomDialog(PpioDataActivity.this,
                        new BlockFileOptionsBottomDialog.OnBlockFileOptionsOnClickListener() {

                            @Override
                            public void onDetail() {
                                mBlockFileOptionsBottomDialog.dismiss();

                                Util.runNetOperation(PpioDataActivity.this, new Util.RunNetOperationCallBack() {
                                    @Override
                                    public void onRunOperation() {
                                        if (mShowStatusPresenter != null) {
                                            String fileHash = mMyFileAdapter.getFileHash(position);
                                            if (!TextUtils.isEmpty(fileHash)) {
                                                mShowStatusPresenter.startStatus(Constant.Data.DEFAULT_BUCKET, fileHash);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCanceled() {

                                    }
                                });
                            }

                            @Override
                            public void onDownload() {
                                mBlockFileOptionsBottomDialog.dismiss();

                                mSetChiPriceDialog = new SetChiPriceDialog(PpioDataActivity.this, Constant.DEFAULT.CHI_PRICE, new SetChiPriceDialog.OnSetChiPriceOnClickListener() {
                                    @Override
                                    public void onCancel() {
                                        mSetChiPriceDialog.dismiss();
                                    }

                                    @Override
                                    public void onSet(int chiPrice) {

                                        Util.runNetOperation(PpioDataActivity.this, new Util.RunNetOperationCallBack() {
                                            @Override
                                            public void onRunOperation() {
                                                if (mExecuteTaskPresenter != null) {
                                                    String bucket = mMyFileAdapter.getFileInfoBucket(position);
                                                    String key = mMyFileAdapter.getFileInfoKey(position);
                                                    if (!TextUtils.isEmpty(bucket) && !TextUtils.isEmpty(key)) {
                                                        DownloadInfo downloadInfo = new DownloadInfo();
                                                        downloadInfo.setBucket(bucket);
                                                        downloadInfo.setKey(key);
                                                        downloadInfo.setChiPrice("" + chiPrice);

                                                        mExecuteTaskPresenter.startDownload(downloadInfo);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCanceled() {

                                            }
                                        });

                                        mSetChiPriceDialog.dismiss();
                                    }
                                }, new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {

                                    }
                                }, 0, mMyFileAdapter.getFileInfo(position).getLength());

                                mSetChiPriceDialog.show();
                            }

                            @Override
                            public void onShareUnShare() {
                                mBlockFileOptionsBottomDialog.dismiss();

                                if (mShowShareCodePresenter != null) {
                                    String fileHash = mMyFileAdapter.getFileHash(position);
                                    if (!TextUtils.isEmpty(fileHash)) {
                                        mShowShareCodePresenter.getShareCode(Constant.Data.DEFAULT_BUCKET, fileHash);
                                    }
                                }
                            }

                            @Override
                            public void onRename() {
                                mBlockFileOptionsBottomDialog.dismiss();

                                //
                                ToastUtil.showToast(PpioDataActivity.this, "Not implemented!", Toast.LENGTH_SHORT);
//                                mRenameDialog = new RenameDialog(PpioDataActivity.this,
//                                        new RenameDialog.OnRenameOnClickListener() {
//                                            @Override
//                                            public void onCancel() {
//                                                mRenameDialog.dismiss();
//                                            }
//
//                                            @Override
//                                            public void onRename(String name) {
//                                                Util.runNetOperation(PpioDataActivity.this, new Util.RunNetOperationCallBack() {
//                                                    @Override
//                                                    public void onRunOperation() {
//
//                                                    }
//                                                });
//
//                                                mRenameDialog.dismiss();
//                                            }
//                                        },
//                                        new DialogInterface.OnDismissListener() {
//                                            @Override
//                                            public void onDismiss(DialogInterface dialog) {
//                                                mRenameDialog = null;
//                                            }
//                                        });
//
//                                mRenameDialog.show();
                                //
                            }

                            @Override
                            public void onRenew() {
                                mBlockFileOptionsBottomDialog.dismiss();

                                FileInfo fileInfo = mMyFileAdapter.getFileInfo(position);

                                startActivityForResult(new Intent(PpioDataActivity.this, RenewActivity.class)
                                        .setAction(Constant.Intent.RENEW_ACTION)
                                        .putExtra(Constant.Data.RENEW_FILE, fileInfo), Constant.Code.REQUEST_RENEW);
                            }

                            @Override
                            public void onDelete() {
                                mBlockFileOptionsBottomDialog.dismiss();

                                if (mDeleteDialog != null) {
                                    mDeleteDialog.dismiss();
                                    mDeleteDialog = null;
                                }

                                String fileHash = mMyFileAdapter.getFileHash(position);
                                String bucket = mMyFileAdapter.getFileInfoBucket(position);
                                String key = mMyFileAdapter.getFileInfoKey(position);
                                if (!TextUtils.isEmpty(fileHash) &&
                                        !TextUtils.isEmpty(bucket) &&
                                        !TextUtils.isEmpty(key)) {
                                    mDeleteDialog = new DeleteDialog(PpioDataActivity.this,
                                            fileHash,
                                            new DeleteDialog.OnDeleteOnClickListener() {
                                                @Override
                                                public void onCancel() {
                                                    mDeleteDialog.dismiss();
                                                }

                                                @Override
                                                public void onDelete() {

                                                    if (mDeletePresenter != null) {
                                                        mDeletePresenter.delete(bucket, key);
                                                    }

                                                    mDeleteDialog.dismiss();
                                                }
                                            },
                                            new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialog) {

                                                }
                                            });

                                    mDeleteDialog.show();
                                }
                            }

                            @Override
                            public void onCancel() {
                                mBlockFileOptionsBottomDialog.dismiss();
                            }
                        },
                        new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                mBlockFileOptionsBottomDialog = null;
                            }
                        }
                );

                mBlockFileOptionsBottomDialog.show();
            }
        });

        mUploadTaskAdapter.setUploadTaskItemClickListener(new UploadTaskAdapter.UploadTaskItemClickListener() {
            @Override
            public void onDelete(final TaskInfo taskInfo) {
                if (mDeleteDialog != null) {
                    mDeleteDialog.dismiss();
                    mDeleteDialog = null;
                }

                if (Constant.TaskState.RUNNING.equals(taskInfo.getState())) {
                    ToastUtil.showToast(PpioDataActivity.this, "the task is running!", Toast.LENGTH_SHORT);
                } else {
                    final boolean isUploading = Constant.TaskType.PUT.equals(taskInfo.getType()) && !Constant.TaskState.FINISHED.equals(taskInfo.getState());
                    final String taskId = taskInfo.getId();
                    final String bucket = Constant.Data.DEFAULT_BUCKET;
                    String str = taskInfo.getTo().replaceFirst(bucket, "");
                    if (str.startsWith("//")) {
                        str = str.replaceFirst("/", "");
                    }
                    final String key = str;

                    mDeleteDialog = new DeleteDialog(PpioDataActivity.this,
                            "delete task " + taskId,
                            new DeleteDialog.OnDeleteOnClickListener() {
                                @Override
                                public void onCancel() {
                                    mDeleteDialog.dismiss();
                                }

                                @Override
                                public void onDelete() {
                                    if (mExecuteTaskPresenter != null) {
                                        if (isUploading) {
                                            mExecuteTaskPresenter.deleteUploadingTask(bucket, key, taskId);
                                        } else {
                                            mExecuteTaskPresenter.deleteTask(taskId);
                                        }
                                    }

                                    mDeleteDialog.dismiss();
                                }
                            },
                            new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    mDeleteDialog = null;
                                }
                            });

                    mDeleteDialog.show();
                }
            }

            @Override
            public void onPause(final String taskId) {
                if (mExecuteTaskPresenter != null) {
                    mExecuteTaskPresenter.pauseTask(taskId);
                }
            }

            @Override
            public void onResume(final String taskId) {
                if (mExecuteTaskPresenter != null) {
                    mExecuteTaskPresenter.resumeTask(taskId);
                }
            }
        });

        mDownloadTaskAdapter.setDownloadTaskItemClickListener(new DownloadTaskAdapter.DownloadTaskItemClickListener() {
            @Override
            public void onDelete(final TaskInfo taskInfo) {
                if (mDeleteDialog != null) {
                    mDeleteDialog.dismiss();
                    mDeleteDialog = null;
                }

                if (Constant.TaskState.RUNNING.equals(taskInfo.getState())) {
                    ToastUtil.showToast(PpioDataActivity.this, "the task is running!", Toast.LENGTH_SHORT);
                } else {
                    final String taskId = taskInfo.getId();
                    mDeleteDialog = new DeleteDialog(PpioDataActivity.this,
                            "delete task " + taskId,
                            new DeleteDialog.OnDeleteOnClickListener() {
                                @Override
                                public void onCancel() {
                                    mDeleteDialog.dismiss();
                                }

                                @Override
                                public void onDelete() {
                                    if (mExecuteTaskPresenter != null) {
                                        mExecuteTaskPresenter.deleteTask(taskId);
                                    }

                                    mDeleteDialog.dismiss();
                                }
                            },
                            new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {

                                }
                            });

                    mDeleteDialog.show();
                }
            }

            @Override
            public void onPause(final String taskId) {
                if (mExecuteTaskPresenter != null) {
                    mExecuteTaskPresenter.pauseTask(taskId);
                }
            }

            @Override
            public void onResume(final String taskId) {
                if (mExecuteTaskPresenter != null) {
                    mExecuteTaskPresenter.resumeTask(taskId);
                }
            }
        });
    }

    private void initData() {
        if (mPpioDataPresenter != null) {
            mPpioDataPresenter.refreshAllFileList(mDeletingInfoHashMap, mUploadFailedInfoHashMap, true);
        }

        if (mShowSide) {
            mLeftDrawerLayout.openDrawer();
        }

        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.requestAddress();
            mAccountInfoPresenter.requestUsed();
            mAccountInfoPresenter.requestBalance();
            mAccountInfoPresenter.requestFund();
            mAccountInfoPresenter.requestOracleChiPrice();
        }
    }

    private void showAllFileView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCurrentShowView = ALLFILE_VIEW;

                mWActionBarTitleTv.setText("Files");

                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                mUploadingFileRecyclerView.setVisibility(View.GONE);
                //
                //mDownloadingFileRecyclerView.setVisibility(View.GONE);
                mDownloadListLayout.setVisibility(View.GONE);
                //

                mAllFileIv.setBackgroundResource(R.mipmap.allfile_selected);
                mUploadingIv.setBackgroundResource(R.mipmap.uploading_unselected);
                mDownloadingIv.setBackgroundResource(R.mipmap.downloading_unselected);

                mAllFileTv.setTextColor(0xFF1989FA);
                mUploadingTv.setTextColor(0xFF606266);
                mDownloadingTv.setTextColor(0xFF606266);

                if (mMyFileAdapter.getItemCount() == 0) {
                    mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void showUploadView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCurrentShowView = UPLOADING_VIEW;

                mWActionBarTitleTv.setText("Uploading");

                mSwipeRefreshLayout.setVisibility(View.GONE);
                mUploadingFileRecyclerView.setVisibility(View.VISIBLE);
                //
                //mDownloadingFileRecyclerView.setVisibility(View.GONE);
                mDownloadListLayout.setVisibility(View.GONE);
                //

                mAllFileIv.setBackgroundResource(R.mipmap.allfile_unselected);
                mUploadingIv.setBackgroundResource(R.mipmap.uploading_selected);
                mDownloadingIv.setBackgroundResource(R.mipmap.downloading_unselected);

                mAllFileTv.setTextColor(0xFF606266);
                mUploadingTv.setTextColor(0xFF1989FA);
                mDownloadingTv.setTextColor(0xFF606266);

                if (mUploadTaskAdapter.getItemCount() == 0) {
                    mUploadingFileRecyclerView.setVisibility(View.INVISIBLE);
                } else {
                    mUploadingFileRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showDownloadView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCurrentShowView = DOWNLOADING_VIEW;

                mWActionBarTitleTv.setText("Downloading");

                mSwipeRefreshLayout.setVisibility(View.GONE);
                mUploadingFileRecyclerView.setVisibility(View.GONE);
                //
                //mDownloadingFileRecyclerView.setVisibility(View.VISIBLE);
                mDownloadListLayout.setVisibility(View.VISIBLE);
                mDownloadDirectoryTv.setText("download to " + Constant.PPIO_File.DOWNLOAD_DIR);
                //

                mAllFileIv.setBackgroundResource(R.mipmap.allfile_unselected);
                mUploadingIv.setBackgroundResource(R.mipmap.uploading_unselected);
                mDownloadingIv.setBackgroundResource(R.mipmap.downloading_selected);

                mAllFileTv.setTextColor(0xFF606266);
                mUploadingTv.setTextColor(0xFF606266);
                mDownloadingTv.setTextColor(0xFF1989FA);

                if (mDownloadTaskAdapter.getItemCount() == 0) {
                    mDownloadingFileRecyclerView.setVisibility(View.INVISIBLE);
                } else {
                    mDownloadingFileRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void bindUploadLogService(IBinder service) {
        mUploadLogService = ((UploadLogService.UploadLogServiceBinder) service).getUploadLogService();

        if (mAccountInfoPresenter != null) {
            mAccountInfoPresenter.bindUploadLogService(mUploadLogService);
        }
    }

    public void bindUploadService(IBinder service) {

        mUploadService = ((UploadService.UploadServiceBinder) service).getUploadService();

        if (mExecuteTaskPresenter != null) {
            mExecuteTaskPresenter.bindUploadService(mUploadService);
        }
    }

    public void bindDownloadService(IBinder service) {

        mDownloadService = ((DownloadService.DownloadServiceBinder) service).getDownloadService();

        if (mExecuteTaskPresenter != null) {
            mExecuteTaskPresenter.bindDownloadService(mDownloadService);

            mExecuteTaskPresenter.startRefreshTasks();
        }
    }

    static class UploadLogServiceConnection implements ServiceConnection {

        final WeakReference<PpioDataActivity> PpioDataActivityWeakReference;

        public UploadLogServiceConnection(PpioDataActivity ppioDataActivity) {
            PpioDataActivityWeakReference = new WeakReference<>(ppioDataActivity);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (PpioDataActivityWeakReference.get() != null) {
                PpioDataActivityWeakReference.get().bindUploadLogService(service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    static class UploadServiceConnection implements ServiceConnection {

        final WeakReference<PpioDataActivity> PpioDataActivityWeakReference;

        public UploadServiceConnection(PpioDataActivity ppioDataActivity) {
            PpioDataActivityWeakReference = new WeakReference<>(ppioDataActivity);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (PpioDataActivityWeakReference.get() != null) {
                PpioDataActivityWeakReference.get().bindUploadService(service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    static class DownloadServiceConnection implements ServiceConnection {

        final WeakReference<PpioDataActivity> PpioDataActivityWeakReference;

        public DownloadServiceConnection(PpioDataActivity ppioDataActivity) {
            PpioDataActivityWeakReference = new WeakReference<>(ppioDataActivity);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (PpioDataActivityWeakReference.get() != null) {
                PpioDataActivityWeakReference.get().bindDownloadService(service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}