package io.pp.net_disk_demo.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.dialog.VerifyPassPhraseDialog;
import io.pp.net_disk_demo.ppio.KeyStoreUtil;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.util.ToastUtil;

public class PrivateKeyLogInActivity extends BaseActivity {

    private static final String TAG = "PrivateKeyLogInActivity";

    private EditText mPrivateKeyEdit = null;
    private Button mConfirmBtn = null;

    private VerifyPassPhraseDialog mVerifyPassPhraseDialog = null;
    private ProgressDialog mProgressDialog = null;

    private String mPrivateKeyStr = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "onCreate()");

        setContentView(R.layout.activity_privatekey_login_layout);

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
        hideProgressDialog();

        if (mVerifyPassPhraseDialog != null) {
            mVerifyPassPhraseDialog.dismiss();
            mVerifyPassPhraseDialog = null;
        }

        super.onDestroy();
    }

    private void init() {
        setImmersiveStatusBar();

        mPrivateKeyEdit = findViewById(R.id.privatekey_login_edit);
        mConfirmBtn = findViewById(R.id.privatekey_login_btn);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPrivateKeyEdit.getText() == null) {
                    ToastUtil.showToast(PrivateKeyLogInActivity.this, "private-key can't be null!", Toast.LENGTH_SHORT);
                }

                mPrivateKeyStr = mPrivateKeyEdit.getText().toString();

                if (TextUtils.isEmpty(mPrivateKeyStr)) {
                    ToastUtil.showToast(PrivateKeyLogInActivity.this, "private-key can't be empty!", Toast.LENGTH_SHORT);
                }

                if (mVerifyPassPhraseDialog != null) {
                    mVerifyPassPhraseDialog.dismiss();
                    mVerifyPassPhraseDialog = null;
                }

                mVerifyPassPhraseDialog = new VerifyPassPhraseDialog(PrivateKeyLogInActivity.this,
                        new VerifyPassPhraseDialog.OnVerifyPassPhraseClickListener() {
                            @Override
                            public void onCancel() {
                                mVerifyPassPhraseDialog.dismiss();
                            }

                            @Override
                            public void onVerify(final String passPhrase) {
                                mVerifyPassPhraseDialog.dismiss();

                                showProgressDialog();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        PossUtil.logIn(mPrivateKeyStr, new PossUtil.LogInListener() {
                                            @Override
                                            public void onLogInError(final String errMsg) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        hideProgressDialog();

                                                        ToastUtil.showToast(PrivateKeyLogInActivity.this,
                                                                "log in fail: " + errMsg, Toast.LENGTH_SHORT);
                                                    }
                                                });
                                            }
                                        });

                                        KeyStoreUtil.rememberFromPrivateKey(PrivateKeyLogInActivity.this, mPrivateKeyStr, passPhrase);

                                        sendBroadcast(new Intent(Constant.Intent.LOGIN_SUCCEED));
                                        startActivity(new Intent(PrivateKeyLogInActivity.this, PpioDataActivity.class));
                                        finish();
                                    }
                                }).start();
                            }
                        }, new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mVerifyPassPhraseDialog = null;
                    }
                });
                mVerifyPassPhraseDialog.show();
            }
        });
    }

    private void showProgressDialog() {
        hideProgressDialog();

        mProgressDialog = new ProgressDialog(PrivateKeyLogInActivity.this);
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