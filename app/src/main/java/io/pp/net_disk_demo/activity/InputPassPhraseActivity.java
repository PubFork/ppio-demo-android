package io.pp.net_disk_demo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.dialog.CustomProgressDialog;
import io.pp.net_disk_demo.dialog.RemindDialog;
import io.pp.net_disk_demo.mvp.presenter.InputPassPhrasePresenter;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.InputPassPhrasePresenterImpl;
import io.pp.net_disk_demo.mvp.view.InputPassPhraseView;
import io.pp.net_disk_demo.util.ActivityUtil;
import io.pp.net_disk_demo.util.ToastUtil;
import io.pp.net_disk_demo.util.Util;

public class InputPassPhraseActivity extends BaseActivity implements InputPassPhraseView {

    private EditText mPassPhraseEdit = null;
    private Button mConfirmBtn = null;
    private TextView mImportKeyStoreTv = null;

    private CustomProgressDialog mCustomProgressDialog = null;
    private RemindDialog mRemindDialog = null;

    private InputPassPhrasePresenter mInputPassPhrasePresenter = null;

    private BroadcastReceiver mBroadcastReceiver = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_input_passphrase_layout);

        setupUI(findViewById(R.id.input_passphrase_layout));

        init();

        Util.runStorageOperation(InputPassPhraseActivity.this, new Util.RunNetOperationCallBack() {
            @Override
            public void onRunOperation() {

            }

            @Override
            public void onCanceled() {
                if (ActivityUtil.hasFinishedForNoStorage()) {
                    finish();
                } else {
                    mRemindDialog = new RemindDialog(InputPassPhraseActivity.this,
                            "Because has no storage permission, so can not login.",
                            "Please open storage permission",
                            new RemindDialog.OnOkClickListener() {
                                @Override
                                public void onOk() {
                                    finish();
                                }
                            });
                    mRemindDialog.setCancelable(false);
                    mRemindDialog.setCanceledOnTouchOutside(false);
                    mRemindDialog.show();
                }
            }
        });
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

        if (mRemindDialog != null) {
            mRemindDialog.dismiss();
            mRemindDialog = null;
        }

        if (mInputPassPhrasePresenter != null) {
            mInputPassPhrasePresenter.onDestroy();
            mInputPassPhrasePresenter = null;
        }

        unregisterReceiver(mBroadcastReceiver);

        super.onDestroy();
    }

    @Override
    public void showInLogInView() {
        showProgressDialog();
    }

    @Override
    public void stopShowInLogInView() {
        hideProgressDialog();
    }

    @Override
    public void showLogInFailView(String errMsg) {
        hideProgressDialog();

        ToastUtil.showToast(InputPassPhraseActivity.this, errMsg, Toast.LENGTH_SHORT);
    }

    @Override
    public void showLogInSucceedView() {
        hideProgressDialog();

        startActivity(new Intent(InputPassPhraseActivity.this, PpioDataActivity.class));
        finish();
    }

    private void init() {
        setImmersiveStatusBar();

        mPassPhraseEdit = findViewById(R.id.passphrase_edit);
        mConfirmBtn = findViewById(R.id.confirm_btn);
        mImportKeyStoreTv = findViewById(R.id.import_keystore_tv);

        mInputPassPhrasePresenter = new InputPassPhrasePresenterImpl(InputPassPhraseActivity.this,
                InputPassPhraseActivity.this);

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hidePassPhraseEditKeyBoard();
                hideSoftKeyboard(v);

                Util.runNetStorageOperation(InputPassPhraseActivity.this, new Util.RunNetOperationCallBack() {
                    @Override
                    public void onRunOperation() {
                        if (mInputPassPhrasePresenter != null) {
                            mInputPassPhrasePresenter.logIn(mPassPhraseEdit.getText().toString());
                        }
                    }

                    @Override
                    public void onCanceled() {

                    }
                });

            }
        });

        mImportKeyStoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);

                startActivity(new Intent(InputPassPhraseActivity.this, KeyStoreLogInActivity.class));
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
    }

    private void showProgressDialog() {
        hideProgressDialog();

        mCustomProgressDialog = new CustomProgressDialog(InputPassPhraseActivity.this, "Logging in...");

        mCustomProgressDialog.setCancelable(false);
        mCustomProgressDialog.setCanceledOnTouchOutside(false);

        mCustomProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mCustomProgressDialog != null) {
            mCustomProgressDialog.dismiss();
            mCustomProgressDialog = null;
        }
    }
}