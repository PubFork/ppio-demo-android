package io.pp.net_disk_demo.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.mvp.presenter.LoadingPresenter;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.LoadingPresenterImpl;
import io.pp.net_disk_demo.mvp.view.LoadingView;
import io.pp.net_disk_demo.util.Util;
import io.pp.net_disk_demo.util.XPermissionUtils;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class LoadingActivity extends BaseActivity implements LoadingView {

    private static final String TAG = "LoadingActivity";

    private LoadingPresenter mLoadingPresenter;

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
        }

        //Whether all permissions are given, if given, check if has log in, not given, direct finish
        if (can_write_storage &&
                can_read_storage &&
                can_use_internet &&
                can_listen_internet) {
            Util.runNetOperation(LoadingActivity.this, new Util.RunNetOperationCallBack() {
                @Override
                public void onRunOperation() {
                    if (mLoadingPresenter != null) {
                        mLoadingPresenter.checkHasLogin();
                    }
                }
            });
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLoadingPresenter != null) {
            mLoadingPresenter.onDestroy();
        }
    }

    @Override
    public void showCheckingHasLogInView() {

    }

    @Override
    public void showHasLogInView() {
        startActivity(new Intent(LoadingActivity.this, PpioDataActivity.class));
        finish();
    }

    @Override
    public void showNotLogInView() {
        startActivity(new Intent(LoadingActivity.this, LogInOrRegisterActivity.class));
        finish();
    }

    private void init() {
        setImmersiveStatusBar();

        mLoadingPresenter = new LoadingPresenterImpl(LoadingActivity.this, LoadingActivity.this);

        if (!XPermissionUtils.checkPermissionsForActivity(LoadingActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE})) {

            //If there are permissions not given, apply for these permissions
            XPermissionUtils.requestPermissionsForActivity(LoadingActivity.this,
                    1,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE},
                    new XPermissionUtils.OnPermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            Util.runNetOperation(LoadingActivity.this, new Util.RunNetOperationCallBack() {
                                @Override
                                public void onRunOperation() {
                                    if (mLoadingPresenter != null) {
                                        mLoadingPresenter.checkHasLogin();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onPermissionDenied() {
                            finish();
                        }
                    });
        } else {
            //Whether all permissions are given, if given, check if has log in
            Util.runNetOperation(LoadingActivity.this, new Util.RunNetOperationCallBack() {
                @Override
                public void onRunOperation() {
                    if (mLoadingPresenter != null) {
                        mLoadingPresenter.checkHasLogin();
                    }
                }
            });
        }
    }
}