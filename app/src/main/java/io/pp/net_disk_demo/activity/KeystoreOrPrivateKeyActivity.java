package io.pp.net_disk_demo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;

public class KeystoreOrPrivateKeyActivity extends BaseActivity {

    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_keystore_privatekey_layout);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);

        super.onDestroy();
    }

    private void init() {
        setImmersiveStatusBar();

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
    }

    public void startKeyStoreActivity(View view) {
        startActivity(new Intent(KeystoreOrPrivateKeyActivity.this, KeyStoreLogInActivity.class));
    }

    public void startPrivateKeyActivity(View view) {
        startActivity(new Intent(KeystoreOrPrivateKeyActivity.this, PrivateKeyLogInActivity.class));
    }
}