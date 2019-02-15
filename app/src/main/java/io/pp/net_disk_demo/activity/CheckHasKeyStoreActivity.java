package io.pp.net_disk_demo.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.CheckHasKeyStorePresenterImpl;
import io.pp.net_disk_demo.mvp.view.CheckHasKeyStoreView;
import io.pp.net_disk_demo.util.ToastUtil;
import io.pp.net_disk_demo.util.Util;
import io.pp.net_disk_demo.util.XPermissionUtils;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class CheckHasKeyStoreActivity extends BaseActivity implements CheckHasKeyStoreView {

    private static final String TAG = "LoadingActivity";

    private ProgressDialog mProgressDialog = null;

    private CheckHasKeyStorePresenterImpl mCheckHasKeyStorePresenter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading);

        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean can_write_storage = false;
        boolean can_read_storage = false;
        boolean can_use_internet = false;
        boolean can_listen_internet = false;
        boolean can_use_camera = false;
        boolean can_set_foreground_service = false;

        for (int i = 0; i < permissions.length; i++) {
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[i])) {
                try {
                    if (grantResults[i] == PERMISSION_GRANTED) {
                        can_write_storage = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permissions[i])) {
                try {
                    if (grantResults[i] == PERMISSION_GRANTED) {
                        can_read_storage = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (Manifest.permission.INTERNET.equals(permissions[i])) {
                try {
                    if (grantResults[i] == PERMISSION_GRANTED) {
                        can_use_internet = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (Manifest.permission.ACCESS_NETWORK_STATE.equals(permissions[i])) {
                try {
                    if (grantResults[i] == PERMISSION_GRANTED) {
                        can_listen_internet = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (Manifest.permission.CAMERA.equals(permissions[i])) {
                try {
                    if (grantResults[i] == PERMISSION_GRANTED) {
                        can_use_camera = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (Manifest.permission.FOREGROUND_SERVICE.equals(permissions[i])) {
                try {
                    if (grantResults[i] == PERMISSION_GRANTED) {
                        can_set_foreground_service = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //Whether all permissions are given, if given, check if has log in, not given, direct finish
        if (can_write_storage &&
                can_read_storage &&
                can_use_internet &&
                can_listen_internet &&
                can_use_camera
            //&&
            //can_set_foreground_service
                ) {
            Util.runNetOperation(CheckHasKeyStoreActivity.this, new Util.RunNetOperationCallBack() {
                @Override
                public void onRunOperation() {
                    if (mCheckHasKeyStorePresenter != null) {
                        mCheckHasKeyStorePresenter.checkHasKeyStore();
                    }
                }

                @Override
                public void onInterNetNoAvailable() {
                    hideProgressDialog();
                }
            });
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        hideProgressDialog();

        mCheckHasKeyStorePresenter = null;

        super.onDestroy();
    }

    @Override
    public void showCheckingHasKeyStoreView() {
        showProgressDialog();
    }

    @Override
    public void showCheckHasKeyStoreFailView(String errMsg) {
        hideProgressDialog();

        ToastUtil.showToast(CheckHasKeyStoreActivity.this, errMsg, Toast.LENGTH_SHORT);
    }

    @Override
    public void showHasUserView() {
        hideProgressDialog();

        startActivity(new Intent(CheckHasKeyStoreActivity.this, PpioDataActivity.class));
        finish();
    }

    @Override
    public void showHasKeyStoreView() {
        hideProgressDialog();

        startActivity(new Intent(CheckHasKeyStoreActivity.this, InputPassPhraseActivity.class));
        finish();
    }

    @Override
    public void showNotHasKeyStoreView() {
        hideProgressDialog();

        startActivity(new Intent(CheckHasKeyStoreActivity.this, KeyStoreLogInActivity.class));
        finish();
    }

    private void init() {
        setImmersiveStatusBar();

        mCheckHasKeyStorePresenter = new CheckHasKeyStorePresenterImpl(CheckHasKeyStoreActivity.this, CheckHasKeyStoreActivity.this);

        if (!XPermissionUtils.checkPermissionsForActivity(CheckHasKeyStoreActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.FOREGROUND_SERVICE})) {

            //If there are permissions not given, apply for these permissions
            XPermissionUtils.requestPermissionsForActivity(CheckHasKeyStoreActivity.this,
                    1,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.FOREGROUND_SERVICE},
                    new XPermissionUtils.OnPermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            if (mCheckHasKeyStorePresenter != null) {
                                mCheckHasKeyStorePresenter.checkHasKeyStore();
                            }
                        }

                        @Override
                        public void onPermissionDenied() {
                            finish();
                        }
                    });
        } else {
            //Whether all permissions are given, if given, check if has log in
            if (mCheckHasKeyStorePresenter != null) {
                mCheckHasKeyStorePresenter.checkHasKeyStore();
            }
        }
    }

    private void showProgressDialog() {
        hideProgressDialog();

        mProgressDialog = new ProgressDialog(CheckHasKeyStoreActivity.this);
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