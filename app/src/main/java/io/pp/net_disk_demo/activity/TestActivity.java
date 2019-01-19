package io.pp.net_disk_demo.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.dialog.VerifyPassPhraseDialog;
import io.pp.net_disk_demo.ppio.KeyStoreUtil;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.util.ToastUtil;

public class TestActivity extends BaseActivity {

    private final String TAG = "TestActivity";

    private Toolbar mTestToolBar = null;
    private ImageView mToolBarLeftTv = null;
    private TextView mToolBarTitleTv = null;

    private TextView mKeyStoreTv = null;
    private TextView mPassPhraseTv = null;
    private TextView mPrivateKeyTv = null;
    private TextView mAddressTv = null;
    private TextView mKeyStoreFileTv = null;

    private Button mExportNewKeyStoreBtn = null;

    private VerifyPassPhraseDialog mVerifyPassPhraseDialog = null;
    private ProgressDialog mProgressDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        init();
    }

    @Override
    protected void onDestroy() {
        hideVerifyPassPhraseDialog();
        hideProgressDialog();

        super.onDestroy();
    }

    private void init() {
        setImmersiveStatusBar();

        mTestToolBar = findViewById(R.id.test_toolbar_layout);
        mTestToolBar.setPadding(0, 0, 0, 0);
        mTestToolBar.setContentInsetsAbsolute(0, 0);

        setSupportActionBar(mTestToolBar);

        mToolBarLeftTv = findViewById(R.id.actionbar_left_iv);
        mToolBarTitleTv = findViewById(R.id.actionbar_title_tv);

        View.OnClickListener toolBarLeftOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };

        mToolBarLeftTv.setOnClickListener(toolBarLeftOnClickListener);

        mToolBarTitleTv.setText("Account");

        mKeyStoreTv = findViewById(R.id.keystore_tv);
        mPassPhraseTv = findViewById(R.id.passphrase_tv);
        mPrivateKeyTv = findViewById(R.id.private_key_tv);
        mAddressTv = findViewById(R.id.address_tv);

        mExportNewKeyStoreBtn = findViewById(R.id.export_keystore_btn);
        mKeyStoreFileTv = findViewById(R.id.keystore_file_tv);

        mExportNewKeyStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideVerifyPassPhraseDialog();

                mVerifyPassPhraseDialog = new VerifyPassPhraseDialog(TestActivity.this,
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
                                        String filePath = KeyStoreUtil.exportKeyStoreFile(PossUtil.getPrivateKeyStr(), passPhrase);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!TextUtils.isEmpty(filePath)) {
                                                    mKeyStoreFileTv.setText(filePath);
                                                    ToastUtil.showToast(TestActivity.this, "exported a new keystore file!", Toast.LENGTH_SHORT);
                                                } else {
                                                    ToastUtil.showToast(TestActivity.this, "export keystore file failed!", Toast.LENGTH_SHORT);
                                                }
                                            }
                                        });

                                    }
                                }).start();


                                hideProgressDialog();

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

        mKeyStoreTv.setText(PossUtil.getKeyStoreStr());
        mPassPhraseTv.setText(PossUtil.getPasswordStr());
        mPrivateKeyTv.setText(PossUtil.getAccountKey());
        mAddressTv.setText(PossUtil.getAccount());

        mKeyStoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) TestActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("mnemonic", mKeyStoreTv.getText());
                cm.setPrimaryClip(mClipData);

                ToastUtil.showToast(TestActivity.this, "keystore copy succeed!", Toast.LENGTH_SHORT);
            }
        });

        mPassPhraseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) TestActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("password", mPassPhraseTv.getText());
                cm.setPrimaryClip(mClipData);

                ToastUtil.showToast(TestActivity.this, "password copy succeed!", Toast.LENGTH_SHORT);
            }
        });

        mPrivateKeyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) TestActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("private key", mPrivateKeyTv.getText());
                cm.setPrimaryClip(mClipData);

                ToastUtil.showToast(TestActivity.this, "private key copy succeed!", Toast.LENGTH_SHORT);
            }
        });

        mAddressTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) TestActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("address", mAddressTv.getText());
                cm.setPrimaryClip(mClipData);

                ToastUtil.showToast(TestActivity.this, "address copy succeed!", Toast.LENGTH_SHORT);
            }
        });
    }

    private void showProgressDialog() {
        hideProgressDialog();

        mProgressDialog = new ProgressDialog(TestActivity.this);
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

    private void hideVerifyPassPhraseDialog() {
        if (mVerifyPassPhraseDialog != null) {
            mVerifyPassPhraseDialog.dismiss();
            mVerifyPassPhraseDialog = null;
        }
    }
}