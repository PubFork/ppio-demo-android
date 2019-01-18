package io.pp.net_disk_demo.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.dialog.PasswordDialog;
import io.pp.net_disk_demo.mvp.presenter.LogInPresenter;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.LogInPresenterImpl;
import io.pp.net_disk_demo.mvp.view.LogInView;
import io.pp.net_disk_demo.util.Util;

public class LogInActivity extends BaseActivity implements LogInView {

    private final String TAG = "LogInActivity";

    private EditText mSeedPhraseEdit = null;
    private Button mLoginBtn = null;
    private TextView mSignUpTv = null;

    private ProgressDialog mProgressDialog = null;

    private LogInPresenter mLogInPresenter = null;
    private PasswordDialog mPasswordDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        if (mPasswordDialog != null) {
            mPasswordDialog.dismiss();
            mPasswordDialog = null;
        }

        if (mLogInPresenter != null) {
            mLogInPresenter.onDestroy();
        }
    }


    @Override
    public void showSetPasswordView() {
        if (mPasswordDialog != null) {
            mPasswordDialog.dismiss();
            mPasswordDialog = null;
        }

        mPasswordDialog = new PasswordDialog(LogInActivity.this, new PasswordDialog.OnSetPasswordOnClickListener() {
            @Override
            public void onCancel() {
                mPasswordDialog.dismiss();
            }

            @Override
            public void onSet(String mnemonic, String password) {
                if (mLogInPresenter != null) {
                    mLogInPresenter.logIn(mnemonic, password);
                }

                mPasswordDialog.dismiss();
            }
        }, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mPasswordDialog = null;
            }
        });

        mPasswordDialog.setMnemonic(mSeedPhraseEdit.getText().toString());
        mPasswordDialog.show();
    }

    @Override
    public void showInLogInView() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        mProgressDialog = new ProgressDialog(LogInActivity.this);
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
    }

    @Override
    public void showLogInSucceedView() {
        sendBroadcast(new Intent(Constant.Intent.LOGIN_SUCCEED));

        startActivity(new Intent(LogInActivity.this, PpioDataActivity.class));

        finish();
    }

    @Override
    public void showLogInFailView(String failStr) {
        mSeedPhraseEdit.setText("");

        Toast.makeText(LogInActivity.this, failStr, Toast.LENGTH_SHORT).show();
    }

    public void showSignUpView() {
        startActivity(new Intent(LogInActivity.this, RegisterActivity.class));
    }

    private void init() {
        setImmersiveStatusBar();

        mSeedPhraseEdit = findViewById(R.id.login_mnemonic_edit);
        mLoginBtn = findViewById(R.id.login_btn);
        mSignUpTv = findViewById(R.id.signup_tv);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.runNetStorageOperation(LogInActivity.this, new Util.RunNetOperationCallBack() {
                    @Override
                    public void onRunOperation() {
                        if (mLogInPresenter != null) {
                            mLogInPresenter.logIn(mSeedPhraseEdit.getText().toString(), "");
                        }
                    }
                });
            }
        });

        mSignUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLogInPresenter != null) {
                    mLogInPresenter.signUp();
                }
            }
        });

        mLogInPresenter = new LogInPresenterImpl(LogInActivity.this, LogInActivity.this);
    }
}