package io.pp.net_disk_demo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.ppio.KeyStoreUtil;
import io.pp.net_disk_demo.ppio.PossUtil;

public class InputPassPhraseActivity extends BaseActivity {

    private EditText mPassPhraseEdit = null;
    private Button mConfirmBtn = null;
    private Button mNewAccountBtn = null;
    private Button mCancelBtn = null;

    private ProgressDialog mProgressDialog = null;

    private boolean mIsConfirming = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_input_passphrase_layout);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
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

        super.onDestroy();
    }

    private void init() {
        setImmersiveStatusBar();

        mPassPhraseEdit = findViewById(R.id.keystore_passphrase_edit);
        mConfirmBtn = findViewById(R.id.confirm_btn);
        mNewAccountBtn = findViewById(R.id.new_account_btn);
        mCancelBtn = findViewById(R.id.cancel_btn);

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPassPhraseEdit.getText() != null && !mIsConfirming) {
                    mIsConfirming = true;
                    showProgressDialog();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String privateKey = KeyStoreUtil.autoLogInByKeyStore(InputPassPhraseActivity.this, mPassPhraseEdit.getText().toString());

                            if (!TextUtils.isEmpty(privateKey)) {
                                PossUtil.logIn(privateKey, new PossUtil.LogInListener() {
                                    @Override
                                    public void onLogInError(final String errMsg) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mIsConfirming = false;
                                                hideProgressDialog();
                                            }
                                        });
                                    }
                                });

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideProgressDialog();
                                        startActivity(new Intent(InputPassPhraseActivity.this, PpioDataActivity.class));
                                        finish();
                                    }
                                });

                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mIsConfirming = false;
                                        hideProgressDialog();
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        });

        mNewAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyStoreUtil.deleteKeyStore(InputPassPhraseActivity.this);

                startActivity(new Intent(InputPassPhraseActivity.this, KeystoreOrPrivateKeyActivity.class));
                finish();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showProgressDialog() {
        hideProgressDialog();

        mProgressDialog = new ProgressDialog(InputPassPhraseActivity.this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}