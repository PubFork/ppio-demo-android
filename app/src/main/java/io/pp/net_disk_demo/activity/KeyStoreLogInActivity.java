package io.pp.net_disk_demo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.mvp.presenter.KeyStoreLogInPresenter;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.KeyStoreLogInPresenterImpl;
import io.pp.net_disk_demo.mvp.view.KeyStoreLogInView;

public class KeyStoreLogInActivity extends BaseActivity implements KeyStoreLogInView {
    private static final String TAG = "KeyStoreLogInActivity";

    private static EditText mKeyStoreEdit = null;
    private static EditText mPassPhraseEdit = null;

    private ProgressDialog mProgressDialog = null;

    private KeyStoreLogInPresenter mKeyStoreLogInPresenter = null;

    private boolean mIsConfirming = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_keystore_login_layout);

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
        super.onDestroy();

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        if (mKeyStoreLogInPresenter != null) {
            mKeyStoreLogInPresenter.onDestroy();
        }
    }

    @Override
    public void showInLogInView() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        mProgressDialog = new ProgressDialog(KeyStoreLogInActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    @Override
    public void stopShowInLogInView() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        mIsConfirming = false;
    }

    @Override
    public void showLogInSucceedView() {
        sendBroadcast(new Intent(Constant.Intent.LOGIN_SUCCEED));

        startActivity(new Intent(KeyStoreLogInActivity.this, PpioDataActivity.class));

        finish();
    }

    @Override
    public void showLogInFailView(String failStr) {
        mIsConfirming = false;

        Toast.makeText(KeyStoreLogInActivity.this, failStr, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        setImmersiveStatusBar();

        mKeyStoreEdit = findViewById(R.id.keystore_login_edit);
        mPassPhraseEdit = findViewById(R.id.keystore_password_edit);

        mKeyStoreLogInPresenter = new KeyStoreLogInPresenterImpl(KeyStoreLogInActivity.this, KeyStoreLogInActivity.this);
    }

    public void onConfirm(View view) {
        if (mKeyStoreLogInPresenter != null) {
            if (mKeyStoreEdit.getText() == null) {

            } else if (mPassPhraseEdit.getText() == null) {

            } else if (!mIsConfirming) {
                mIsConfirming = true;
                mKeyStoreLogInPresenter.logIn(mKeyStoreEdit.getText().toString(), mPassPhraseEdit.getText().toString());
            }
        }
    }
}