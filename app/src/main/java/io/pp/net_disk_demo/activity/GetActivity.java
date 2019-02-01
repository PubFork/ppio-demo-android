package io.pp.net_disk_demo.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.dialog.SetChiPriceDialog;
import io.pp.net_disk_demo.mvp.presenter.GetPresenter;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.GetPresenterImpl;
import io.pp.net_disk_demo.mvp.view.GetView;
import io.pp.net_disk_demo.service.DownloadService;
import io.pp.net_disk_demo.service.ExecuteTaskService;
import io.pp.net_disk_demo.util.ToastUtil;

public class GetActivity extends BaseActivity implements GetView {

    private final String TAG = "GetActivity";

    private ProgressDialog mProgressDialog = null;
    private SetChiPriceDialog mSetChiPriceDialog = null;

    private Toolbar mGetToolBar = null;
    private LinearLayout mToolBarLeftIvLayout = null;
    private TextView mToolBarTitleTv = null;

    private EditText mShareCodeEdit = null;
    private Button mGetBtn = null;

    private GetPresenter mGetPresenter = null;

    private ExecuteTaskService mExecuteTaskService = null;
    private ExecuteTaskServiceConnection mExecuteTaskServiceConnection = null;

    private DownloadService mDownloadService = null;
    private DownloadServiceConnection mDownloadServiceConnection = null;

    private long mTotal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_get);

        init();

        mExecuteTaskServiceConnection = new ExecuteTaskServiceConnection(GetActivity.this);
        mDownloadServiceConnection = new DownloadServiceConnection(GetActivity.this);

        startService(new Intent(GetActivity.this, ExecuteTaskService.class));
        startService(new Intent(GetActivity.this, DownloadService.class));

        bindService(new Intent(GetActivity.this, ExecuteTaskService.class),
                mExecuteTaskServiceConnection,
                BIND_AUTO_CREATE);

        bindService(new Intent(GetActivity.this, DownloadService.class),
                mDownloadServiceConnection,
                BIND_AUTO_CREATE);

        mTotal = 0;
    }

    @Override
    protected void onDestroy() {
        unbindService(mExecuteTaskServiceConnection);
        unbindService(mDownloadServiceConnection);

        mExecuteTaskService = null;
        mExecuteTaskServiceConnection = null;

        mDownloadService = null;
        mDownloadServiceConnection = null;

        if (mGetPresenter != null) {
            mGetPresenter.onDestroy();
            mGetPresenter = null;
        }

        super.onDestroy();
    }

    @Override
    public void back() {
        finish();
    }

    @Override
    public void showSetChiPriceView() {
        if (mSetChiPriceDialog != null) {
            mSetChiPriceDialog.dismiss();
        }

        mSetChiPriceDialog = new SetChiPriceDialog(GetActivity.this, Constant.DEFAULT.CHI_PRICE, new SetChiPriceDialog.OnSetChiPriceOnClickListener() {
            @Override
            public void onCancel() {
                mSetChiPriceDialog.dismiss();
                mSetChiPriceDialog = null;
            }

            @Override
            public void onSet(int chiPrice) {
                if (mGetPresenter != null) {
                    mGetPresenter.setChiPrice(chiPrice);
                    mGetPresenter.startGet();

                    mSetChiPriceDialog.dismiss();
                }
            }
        }, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mSetChiPriceDialog = null;
            }
        }, 0, mShareCodeEdit.getText().toString());

        mSetChiPriceDialog.show();
    }

    @Override
    public void showRequestingGetView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                mProgressDialog = new ProgressDialog(GetActivity.this);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
            }
        });
    }

    @Override
    public void showGetFailView(final String errMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                ToastUtil.showToast(GetActivity.this, "download shared file error: " + errMsg, Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void showRequestGetFinishedView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                setResult(Constant.Code.RESULT_DOWNLOAD_OK);

                finish();
            }
        });
    }

    private void init() {
        setImmersiveStatusBar();

        mGetToolBar = findViewById(R.id.get_toolbar_layout);
        mGetToolBar.setPadding(0, 0, 0, 0);
        mGetToolBar.setContentInsetsAbsolute(0, 0);

        setSupportActionBar(mGetToolBar);

        mToolBarLeftIvLayout = findViewById(R.id.actionbar_left_iv_layout);

        mToolBarTitleTv = findViewById(R.id.actionbar_title_tv);

        View.OnClickListener toolBarLeftOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };

        mToolBarLeftIvLayout.setOnClickListener(toolBarLeftOnClickListener);

        mToolBarTitleTv.setText("Get");

        mShareCodeEdit = findViewById(R.id.sharecode_edit);
        mGetBtn = findViewById(R.id.get_confirm_btn);

        mGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGetPresenter != null) {
                    mGetPresenter.setShareCode(mShareCodeEdit.getText().toString());
                }
            }
        });

        mGetPresenter = new GetPresenterImpl(GetActivity.this, GetActivity.this);
    }

    private void bindExecuteTaskService(IBinder service) {
        mExecuteTaskService = ((ExecuteTaskService.ExecuteTaskBinder) service).getExecuteTaskService();

        if (mGetPresenter != null) {
            mGetPresenter.bindGetService(mExecuteTaskService);
        }
    }

    private void bindDownloadService(IBinder service) {
        mDownloadService = ((DownloadService.DownloadServiceBinder) service).getDownloadService();

        if (mGetPresenter != null) {
            mGetPresenter.bindDownloadService(mDownloadService);
        }
    }

    static class ExecuteTaskServiceConnection implements ServiceConnection {

        final WeakReference<GetActivity> getActivityWeakReference;

        public ExecuteTaskServiceConnection(GetActivity getActivity) {
            getActivityWeakReference = new WeakReference<>(getActivity);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (getActivityWeakReference.get() != null) {
                getActivityWeakReference.get().bindExecuteTaskService(service);
            }
        }
    }

    static class DownloadServiceConnection implements ServiceConnection {

        final WeakReference<GetActivity> getActivityWeakReference;

        public DownloadServiceConnection(GetActivity getActivity) {
            getActivityWeakReference = new WeakReference<>(getActivity);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (getActivityWeakReference.get() != null) {
                getActivityWeakReference.get().bindDownloadService(service);
            }
        }
    }
}