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
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.DownloadInfo;
import io.pp.net_disk_demo.data.FileInfo;
import io.pp.net_disk_demo.data.ObjectStatus;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.dialog.BlockFileOptionsBottomDialog;
import io.pp.net_disk_demo.dialog.DeleteDialog;
import io.pp.net_disk_demo.dialog.PpioDataUploadGetDialog;
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
import io.pp.net_disk_demo.service.ExecuteTaskService;
import io.pp.net_disk_demo.util.ToastUtil;
import io.pp.net_disk_demo.util.Util;
import io.pp.net_disk_demo.util.XPermissionUtils;
import io.pp.net_disk_demo.widget.LeftDrawerLayout;
import io.pp.net_disk_demo.widget.StatusBarUtil;
import io.pp.net_disk_demo.widget.recyclerview.DownloadTaskAdapter;
import io.pp.net_disk_demo.widget.recyclerview.MyFileAdapter;
import io.pp.net_disk_demo.widget.recyclerview.UploadTaskAdapter;

public class PpioDataActivity extends BaseActivity implements PpioDataView,
        AccountInfoView,
        ExecuteTaskView,
        StatusView, StartRenewView, ShareCodeView, DeleteView {

    private static final String TAG = "PpioDataActivity";

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

    private RelativeLayout mRechargeLayout = null;
    private RelativeLayout mRecordLayout = null;
    private RelativeLayout mCheckVersionLayout = null;
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
    private RecyclerView mDownloadingFileRecyclerView = null;

    private FloatingActionButton mUploadGetBtn = null;

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

    private ExecuteTaskService mExecuteTaskService = null;

    private ExecuteTaskServiceConnection mExecuteTaskServiceConnection = null;

    private DecimalFormat mDecimalFormat = null;

    private int mCurrentShowView = ALLFILE_VIEW;
    private boolean mShowSide = false;
    private boolean mBackFromUpload = false;
    private boolean mBackFromDownload = false;

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

        mExecuteTaskServiceConnection = new ExecuteTaskServiceConnection(PpioDataActivity.this);

        startService(new Intent(PpioDataActivity.this, ExecuteTaskService.class));

        bindService(new Intent(PpioDataActivity.this, ExecuteTaskService.class),
                mExecuteTaskServiceConnection,
                BIND_AUTO_CREATE);

        setContentView(R.layout.activity_ppiodata);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (mPpioDataPresenter != null) {
            mPpioDataPresenter.link();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == Constant.Code.REQUEST_UPLOAD &&
                resultCode == Constant.Code.RESULT_UPLOAD_OK) ||
                (requestCode == Constant.Code.REQUEST_RENEW &&
                        resultCode == Constant.Code.RESULT_RENEW_OK)) {
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_SHOW_VIEW, mCurrentShowView);
        outState.putBoolean(SHOW_SIDE, mShowSide);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //dialog
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
        unbindService(mExecuteTaskServiceConnection);

        mExecuteTaskService = null;
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
        Toast.makeText(PpioDataActivity.this, "not login!", Toast.LENGTH_SHORT).show();

        //startActivity(new Intent(PpioDataActivity.this, LogInOrRegisterActivity.class));
        startActivity(new Intent(PpioDataActivity.this, KeyStoreLogInActivity.class));
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
    public void showAllFileList(final ArrayList<FileInfo> mMyFileInfoList) {
        stopShowNetWorkingView();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);

                mCurrentShowView = ALLFILE_VIEW;

                mWActionBarTitleTv.setText("all file");

                mMyFileAdapter.refreshFileList(mMyFileInfoList);

                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                mUploadingFileRecyclerView.setVisibility(View.GONE);
                mDownloadingFileRecyclerView.setVisibility(View.GONE);

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

                mUsedValueTv.setText(used + "G");
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
        ToastUtil.showToast(PpioDataActivity.this, "Not implemented!", Toast.LENGTH_SHORT);
        //startActivity(new Intent(PpioDataActivity.this, VersionActivity.class));
        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL.UPDATE_URL)));
    }

    @Override
    public void showFeedbackView() {
        //ToastUtil.showToast(PpioDataActivity.this, "Not implemented!", Toast.LENGTH_SHORT);
        //startActivity(new Intent(PpioDataActivity.this, FeedbackActivity.class));
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL.FEEDBACK_URL)));
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

        Toast.makeText(PpioDataActivity.this, "not login!", Toast.LENGTH_SHORT).show();

        //startActivity(new Intent(PpioDataActivity.this, LogInOrRegisterActivity.class));
        startActivity(new Intent(PpioDataActivity.this, KeyStoreLogInActivity.class));
        finish();
    }

    @Override
    public void showRefreshTasksError(String errMsg) {
        showNetWorkingErrorView("refresh tasks error: ", errMsg);
    }

    @Override
    public void showUploadingTasks(final ArrayList<TaskInfo> uploadingTaskList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mUploadTaskAdapter.refreshUploadingList(uploadingTaskList);

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
    public void showDownloadingTasks(final ArrayList<TaskInfo> downloadingTaskList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDownloadTaskAdapter.refreshUploadingList(downloadingTaskList);

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
            mPpioDataPresenter.refreshAllFileList();
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
    public void onDeleteFinish() {
        stopShowNetWorkingView();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPpioDataPresenter != null) {
                    mPpioDataPresenter.refreshAllFileList();
                }

                if (mAccountInfoPresenter != null) {
                    mAccountInfoPresenter.requestUsed();
                    mAccountInfoPresenter.requestBalance();
                    mAccountInfoPresenter.requestFund();
                }
            }
        });
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

        mAccountInfoLayout = findViewById(R.id.account_info_layout);
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
        mDownloadingFileRecyclerView = findViewById(R.id.downloadingfile_recyclerview);

        StatusBarUtil.setColorNoTranslucentForLeftDrawerLayout(this, mLeftDrawerLayout, getResources().getColor(R.color.account_background_blue));

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.account_background_blue));
        //mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.account_background_blue));

        mMyFileRecyclerView.setLayoutManager(new LinearLayoutManager(PpioDataActivity.this));
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

        mUploadingFileRecyclerView.setLayoutManager(new LinearLayoutManager(PpioDataActivity.this));
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

        mDownloadingFileRecyclerView.setLayoutManager(new LinearLayoutManager(PpioDataActivity.this));
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

        mDecimalFormat = new DecimalFormat("0.000000000000000000");
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
                    mAccountInfoPresenter.showCheckVersion();
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
                            mPpioDataPresenter.refreshAllFileList();
                        }
                    }
                });
            }
        });

        mAllLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllFileView();
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
                                            mShowStatusPresenter.startStatus(Constant.Data.DEFAULT_BUCKET, mMyFileAdapter.getFileHash(position));
                                        }
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
                                        if (mExecuteTaskPresenter != null) {
                                            DownloadInfo downloadInfo = new DownloadInfo();
                                            downloadInfo.setBucket(mMyFileAdapter.getFileInfoBucket(position));
                                            downloadInfo.setKey(mMyFileAdapter.getFileInfoKey(position));
                                            downloadInfo.setChiPrice("" + chiPrice);

                                            mExecuteTaskPresenter.startDownload(downloadInfo);
                                            mSetChiPriceDialog.dismiss();
                                        }
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

                                Util.runNetOperation(PpioDataActivity.this, new Util.RunNetOperationCallBack() {
                                    @Override
                                    public void onRunOperation() {
                                        if (mShowShareCodePresenter != null) {
                                            mShowShareCodePresenter.getShareCode(Constant.Data.DEFAULT_BUCKET, mMyFileAdapter.getFileHash(position));
                                        }
                                    }
                                });
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

//                                if (mStartRenewPresenter != null) {
//                                    FileInfo fileInfo = mMyFileAdapter.getFileInfo(position);
//                                    mStartRenewPresenter.startRenew(fileInfo.getBucketName(), fileInfo.getName());
//                                }
                            }

                            @Override
                            public void onDelete() {
                                mBlockFileOptionsBottomDialog.dismiss();

                                if (mDeleteDialog != null) {
                                    mDeleteDialog.dismiss();
                                    mDeleteDialog = null;
                                }

                                mDeleteDialog = new DeleteDialog(PpioDataActivity.this,
                                        mMyFileAdapter.getFileHash(position),
                                        new DeleteDialog.OnDeleteOnClickListener() {
                                            @Override
                                            public void onCancel() {
                                                mDeleteDialog.dismiss();
                                            }

                                            @Override
                                            public void onDelete() {
                                                Util.runNetOperation(PpioDataActivity.this, new Util.RunNetOperationCallBack() {
                                                    @Override
                                                    public void onRunOperation() {
                                                        Util.runNetOperation(PpioDataActivity.this, new Util.RunNetOperationCallBack() {
                                                            @Override
                                                            public void onRunOperation() {
                                                                if (mDeletePresenter != null) {
                                                                    mDeletePresenter.delete(mMyFileAdapter.getFileInfoBucket(position), mMyFileAdapter.getFileInfoKey(position));
                                                                }
                                                            }
                                                        });
                                                    }
                                                });

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
            public void onDelete(final String taskId) {
                if (mDeleteDialog != null) {
                    mDeleteDialog.dismiss();
                    mDeleteDialog = null;
                }

                mDeleteDialog = new DeleteDialog(PpioDataActivity.this,
                        "delete task " + taskId,
                        new DeleteDialog.OnDeleteOnClickListener() {
                            @Override
                            public void onCancel() {
                                mDeleteDialog.dismiss();
                            }

                            @Override
                            public void onDelete() {

                                Util.runNetOperation(PpioDataActivity.this, new Util.RunNetOperationCallBack() {
                                    @Override
                                    public void onRunOperation() {
                                        if (mExecuteTaskPresenter != null) {
                                            mExecuteTaskPresenter.deleteTask(taskId);
                                        }
                                    }
                                });

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

            @Override
            public void onPause(final String taskId) {
                Util.runNetOperation(PpioDataActivity.this, new Util.RunNetOperationCallBack() {
                    @Override
                    public void onRunOperation() {
                        if (mExecuteTaskPresenter != null) {
                            mExecuteTaskPresenter.pauseTask(taskId);
                        }
                    }
                });
            }

            @Override
            public void onResume(final String taskId) {
                Util.runNetOperation(PpioDataActivity.this, new Util.RunNetOperationCallBack() {
                    @Override
                    public void onRunOperation() {
                        if (mExecuteTaskPresenter != null) {
                            mExecuteTaskPresenter.resumeTask(taskId);
                        }
                    }
                });
            }
        });

        mDownloadTaskAdapter.setDownloadTaskItemClickListener(new DownloadTaskAdapter.DownloadTaskItemClickListener() {
            @Override
            public void onDelete(final String taskId) {
                if (mDeleteDialog != null) {
                    mDeleteDialog.dismiss();
                    mDeleteDialog = null;
                }

                mDeleteDialog = new DeleteDialog(PpioDataActivity.this,
                        "delete task " + taskId,
                        new DeleteDialog.OnDeleteOnClickListener() {
                            @Override
                            public void onCancel() {
                                mDeleteDialog.dismiss();
                            }

                            @Override
                            public void onDelete() {

                                Util.runNetOperation(PpioDataActivity.this, new Util.RunNetOperationCallBack() {
                                    @Override
                                    public void onRunOperation() {
                                        if (mExecuteTaskPresenter != null) {
                                            mExecuteTaskPresenter.deleteTask(taskId);
                                        }
                                    }
                                });

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

            @Override
            public void onPause(final String taskId) {
                Util.runNetOperation(PpioDataActivity.this, new Util.RunNetOperationCallBack() {
                    @Override
                    public void onRunOperation() {
                        if (mExecuteTaskPresenter != null) {
                            mExecuteTaskPresenter.pauseTask(taskId);
                        }
                    }
                });
            }

            @Override
            public void onResume(final String taskId) {
                Util.runNetOperation(PpioDataActivity.this, new Util.RunNetOperationCallBack() {
                    @Override
                    public void onRunOperation() {
                        if (mExecuteTaskPresenter != null) {
                            mExecuteTaskPresenter.resumeTask(taskId);
                        }
                    }
                });
            }
        });
    }

    private void initData() {
        if (mCurrentShowView == ALLFILE_VIEW && mPpioDataPresenter != null && !mBackFromUpload && !mBackFromDownload) {
            mPpioDataPresenter.refreshAllFileList();
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

                mWActionBarTitleTv.setText("all file");

                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                mUploadingFileRecyclerView.setVisibility(View.GONE);
                mDownloadingFileRecyclerView.setVisibility(View.GONE);

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

                mWActionBarTitleTv.setText("uploading");

                mSwipeRefreshLayout.setVisibility(View.GONE);
                mUploadingFileRecyclerView.setVisibility(View.VISIBLE);
                mDownloadingFileRecyclerView.setVisibility(View.GONE);

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

                mWActionBarTitleTv.setText("downloading");

                mSwipeRefreshLayout.setVisibility(View.GONE);
                mUploadingFileRecyclerView.setVisibility(View.GONE);
                mDownloadingFileRecyclerView.setVisibility(View.VISIBLE);

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

    public void bindExecuteTaskService(IBinder service) {

        mExecuteTaskService = ((ExecuteTaskService.ExecuteTaskBinder) service).getExecuteTaskService();

        if (mExecuteTaskPresenter != null) {

            mExecuteTaskPresenter.bindExecuteTaskService(mExecuteTaskService);

            mExecuteTaskPresenter.startRefreshTasks();
        }
    }

    static class ExecuteTaskServiceConnection implements ServiceConnection {

        final WeakReference<PpioDataActivity> PpioDataActivityWeakReference;

        public ExecuteTaskServiceConnection(PpioDataActivity ppioDataActivity) {
            PpioDataActivityWeakReference = new WeakReference<>(ppioDataActivity);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (PpioDataActivityWeakReference.get() != null) {
                PpioDataActivityWeakReference.get().bindExecuteTaskService(service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}