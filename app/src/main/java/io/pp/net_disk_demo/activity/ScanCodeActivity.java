package io.pp.net_disk_demo.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.dialog.RemindDialog;
import io.pp.net_disk_demo.mvp.presenter.ScanCodePresenter;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.ScanCodePresenterImpl;
import io.pp.net_disk_demo.mvp.view.ScanCodeView;
import io.pp.net_disk_demo.util.ActivityUtil;
import io.pp.net_disk_demo.util.FileUtil;
import io.pp.net_disk_demo.util.ToastUtil;
import io.pp.net_disk_demo.util.Util;

public class ScanCodeActivity extends BaseActivity implements QRCodeView.Delegate, ScanCodeView {

    private static final String TAG = "ScanCodeActivity";


    private Toolbar mScanCodeToolBar = null;
    private LinearLayout mToolbarBackIvLayout = null;
    private TextView mToolBarTitleTv = null;

    private ZXingView mZXingView = null;

    private TextView mLightOnTv = null;
    private TextView mLightOffTv = null;
    private TextView mBrowseCodeBitmapTv = null;
    private TextView mRetryTv = null;

    private ProgressDialog mProgressDialog = null;
    private RemindDialog mRemindDialog = null;

    private ScanCodePresenter mScanCodePresenter = null;

    private int mScreenWidth;
    private int mScreenHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scancode_layout);

        init();

        Util.runStorageOperation(ScanCodeActivity.this, new Util.RunNetOperationCallBack() {
            @Override
            public void onRunOperation() {

            }

            @Override
            public void onCanceled() {
                mRemindDialog = new RemindDialog(ScanCodeActivity.this,
                        "Because has no storage permission, so can not login.",
                        "Please open storage permission",
                        new RemindDialog.OnOkClickListener() {
                            @Override
                            public void onOk() {
                                ActivityUtil.setHasFinishedForNoStorage();
                                finish();
                            }
                        });
                mRemindDialog.setCancelable(false);
                mRemindDialog.setCanceledOnTouchOutside(false);
                mRemindDialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Open the rear camera and start previewing, but it doesn't start to recognize
        mZXingView.startCamera();

        // Open the front camera and start previewing, but it doesn't start to recognize
        //mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

        // Display the scan box and start identifying
        mZXingView.startSpotAndShowRect();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

            if (!TextUtils.isEmpty(filePath)) {
                if (mScanCodePresenter != null) {
                    mScanCodePresenter.decodeBitmapCode(filePath);
                }
            } else {
                Toast.makeText(ScanCodeActivity.this, "filePath is null!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        // Turn off camera preview and hide the scan box
        mZXingView.stopCamera();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mScanCodePresenter != null) {
            mScanCodePresenter.onDestroy();
            mScanCodePresenter = null;
        }

        hideProgressDialog();

        if (mRemindDialog != null) {
            mRemindDialog.dismiss();
            mRemindDialog = null;
        }

        // Destroy QR code scan control
        mZXingView.onDestroy();

        super.onDestroy();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        //ToastUtil.showToast(ScanCodeActivity.this, result, Toast.LENGTH_SHORT);

        setResult(Constant.Code.REQUEST_SCAN_CODE_OK, new Intent().putExtra(Constant.Data.KETSTORE, result));

        finish();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {

    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        ToastUtil.showToast(ScanCodeActivity.this, "open camera failed!", Toast.LENGTH_SHORT);
    }


    @Override
    public void showInDecodeBitmapCodeView() {
        showProgressDialog();
    }

    @Override
    public void stopShowInDecodeBitmapCodeView() {
        hideProgressDialog();
    }

    @Override
    public void showDecodeBitmapCodeFailedView(String errMsg) {
        showProgressDialog();
        ToastUtil.showToast(ScanCodeActivity.this, errMsg, Toast.LENGTH_SHORT);
    }

    @Override
    public void showDecodeBitmapCodeSucceedView(String result) {
        hideProgressDialog();

        setResult(Constant.Code.REQUEST_SCAN_CODE_OK, new Intent().putExtra(Constant.Data.KETSTORE, result));

        finish();
    }

    private void init() {
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        setImmersiveStatusBar();

        mScanCodeToolBar = findViewById(R.id.scancode_toolbar_layout);
        mScanCodeToolBar.setPadding(0, 0, 0, 0);
        mScanCodeToolBar.setContentInsetsAbsolute(0, 0);

        setSupportActionBar(mScanCodeToolBar);

        mToolbarBackIvLayout = findViewById(R.id.actionbar_left_iv_layout);
        mToolBarTitleTv = findViewById(R.id.actionbar_title_tv);

        mZXingView = findViewById(R.id.zxingview);
        mRetryTv = findViewById(R.id.scan_code_retry_iv);
        mLightOnTv = findViewById(R.id.scan_code_light_on_iv);
        mLightOffTv = findViewById(R.id.scan_code_light_off_iv);
        mBrowseCodeBitmapTv = findViewById(R.id.scan_code_browse_iv);

        mScanCodePresenter = new ScanCodePresenterImpl(ScanCodeActivity.this, ScanCodeActivity.this);

        mToolBarTitleTv.setText("ScanCode");

        mToolbarBackIvLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        int scanSize = mScreenWidth <= mScreenHeight ? mScreenWidth * 2 / 3 : mScreenWidth * 2 / 3;
        mZXingView.getScanBoxView().setRectWidth(scanSize);
        mZXingView.getScanBoxView().setRectHeight(scanSize);
        mZXingView.setDelegate(this);

        mRetryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mZXingView.startCamera();
            }
        });

        mLightOnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mZXingView.openFlashlight();
            }
        });

        mLightOffTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mZXingView.closeFlashlight();
            }
        });

        mBrowseCodeBitmapTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                //
            }
        });
    }

    private void showProgressDialog() {
        hideProgressDialog();

        mProgressDialog = new ProgressDialog(ScanCodeActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}