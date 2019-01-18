package io.pp.net_disk_demo.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.util.XPermissionUtils;

public class LogInOrRegisterActivity extends BaseActivity {

    private Button mNewAccountBtn;
    private Button mLogInBtn;

    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loginorregister);

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mBroadcastReceiver);
    }

    private void init() {
        setImmersiveStatusBar();

        mNewAccountBtn = findViewById(R.id.loginorregister_register_btn);
        mLogInBtn = findViewById(R.id.loginorregister_loginbtn);

        mNewAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInOrRegisterActivity.this, RegisterActivity.class));
            }
        });

        mLogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInOrRegisterActivity.this, LogInActivity.class));
            }
        });

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Constant.Intent.LOGIN_SUCCEED.equals(intent.getAction())) {
                    finish();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.Intent.LOGIN_SUCCEED);
        registerReceiver(mBroadcastReceiver, intentFilter);

        XPermissionUtils.requestPermissions(LogInOrRegisterActivity.this,
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
}