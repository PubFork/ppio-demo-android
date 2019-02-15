package io.pp.net_disk_demo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.mvp.presenter.KeyStoreLogInPresenter;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.KeyStoreLogInPresenterImpl;
import io.pp.net_disk_demo.mvp.view.KeyStoreLogInView;
import io.pp.net_disk_demo.util.ToastUtil;
import io.pp.net_disk_demo.util.Util;

public class KeyStoreLogInActivity extends BaseActivity implements KeyStoreLogInView {
    private static final String TAG = "KeyStoreLogInActivity";

    private static EditText mKeyStoreEdit = null;
    private static EditText mPassPhraseEdit = null;

    private ProgressDialog mProgressDialog = null;

    private KeyStoreLogInPresenter mKeyStoreLogInPresenter = null;

    private boolean mHasStartScanCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_keystore_login_layout);

        setupUI(findViewById(R.id.keystore_login_layout));

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.Code.REQUEST_SCAN_CODE) {
            mHasStartScanCode = false;
        }

        if (requestCode == Constant.Code.REQUEST_SCAN_CODE &&
                resultCode == Constant.Code.REQUEST_SCAN_CODE_OK &&
                data != null) {
            String keystore = data.getStringExtra(Constant.Data.KETSTORE);

            if (!TextUtils.isEmpty(keystore)) {
                mKeyStoreEdit.setText(keystore);
                mPassPhraseEdit.requestFocus();
                mPassPhraseEdit.performClick();
            } else {
                ToastUtil.showToast(KeyStoreLogInActivity.this, "the result is null!", Toast.LENGTH_SHORT);
            }
        }
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
        hideProgressDialog();

        if (mKeyStoreLogInPresenter != null) {
            mKeyStoreLogInPresenter.onDestroy();
        }

        super.onDestroy();
    }

    @Override
    public void showInLogInView() {
        hideProgressDialog();

        showProgressDialog();
    }

    @Override
    public void stopShowInLogInView() {
        hideProgressDialog();
    }

    @Override
    public void showLogInSucceedView() {
        hideProgressDialog();

        sendBroadcast(new Intent(Constant.Intent.LOGIN_SUCCEED));

        startActivity(new Intent(KeyStoreLogInActivity.this, PpioDataActivity.class));

        finish();
    }

    @Override
    public void showLogInFailView(String failStr) {
        hideProgressDialog();

        ToastUtil.showToast(KeyStoreLogInActivity.this, failStr, Toast.LENGTH_SHORT);
    }

    private void init() {
        setImmersiveStatusBar();

        mKeyStoreEdit = findViewById(R.id.keystore_edit);
        mPassPhraseEdit = findViewById(R.id.passphrase_edit);

        mKeyStoreLogInPresenter = new KeyStoreLogInPresenterImpl(KeyStoreLogInActivity.this, KeyStoreLogInActivity.this);
    }

    public void onConfirm(View view) {
        //hidePassPhraseEditKeyBoard();
        hideSoftKeyboard(view);

        Util.runNetStorageOperation(KeyStoreLogInActivity.this, new Util.RunNetOperationCallBack() {
            @Override
            public void onRunOperation() {
                if (mKeyStoreLogInPresenter != null) {
                    mKeyStoreLogInPresenter.logIn(mKeyStoreEdit.getText().toString(), mPassPhraseEdit.getText().toString());
                }
            }

            @Override
            public void onCanceled() {

            }
        });
    }

    public void onScanCode(View view) {
        hideProgressDialog();
        //hidePassPhraseEditKeyBoard();
        hideSoftKeyboard(view);

        if (!mHasStartScanCode) {
            mHasStartScanCode = true;
            startActivityForResult(new Intent(KeyStoreLogInActivity.this, ScanCodeActivity.class), Constant.Code.REQUEST_SCAN_CODE);
        }
    }

    public void onGenerateAccount(View view) {
        hideProgressDialog();

        //Uri uri = Uri.parse("http://chain-web-wallet.s3-website-us-west-2.amazonaws.com:80");
        Uri uri = Uri.parse(Constant.URL.WALLET_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void showProgressDialog() {
        hideProgressDialog();

        mProgressDialog = new ProgressDialog(KeyStoreLogInActivity.this);
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

    private void hidePassPhraseEditKeyBoard() {
        InputMethodManager imm = (InputMethodManager) KeyStoreLogInActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (mPassPhraseEdit.hasFocus()) {
                imm.hideSoftInputFromWindow(mPassPhraseEdit.getWindowToken(), 0);
            } else {
                imm.hideSoftInputFromWindow(mKeyStoreEdit.getWindowToken(), 0);
            }
        }
    }
}