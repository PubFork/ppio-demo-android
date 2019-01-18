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

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.dialog.PasswordDialog;
import io.pp.net_disk_demo.mvp.presenter.RegisterPresenter;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.RegisterPresenterImpl;
import io.pp.net_disk_demo.mvp.view.RegisterView;
import io.pp.net_disk_demo.util.Util;


public class RegisterActivity extends BaseActivity implements RegisterView {

    private TextView mRemindTv = null;
    private TextView mHintTv = null;
    private EditText mPrivateKeyEdit = null;
    private EditText mConfirmPrivateKeyEdit = null;
    private Button mRegisterBtn = null;

    private RegisterPresenter mRegisterPresenter = null;

    private ProgressDialog mProgressDialog = null;

    private PasswordDialog mPasswordDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

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
        super.onDestroy();

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        if (mPasswordDialog != null) {
            mPasswordDialog.dismiss();
            mPasswordDialog = null;
        }

        if (mRegisterPresenter != null) {
            mRegisterPresenter.onDestroy();
        }
    }

    @Override
    public void showRegisterView(String seedPhrase) {
        mHintTv.setText("Private Key");

        mPrivateKeyEdit.setText(seedPhrase);

        mRegisterBtn.setEnabled(true);
    }

    @Override
    public void showConfirmView() {
        mHintTv.setText("Repeat your seed Private Key");

        mRemindTv.setText("DO NOT share this phrase with anyone!\nThese words can be used to steal your account.");

        mPrivateKeyEdit.setVisibility(View.GONE);
        mConfirmPrivateKeyEdit.setVisibility(View.VISIBLE);
        mConfirmPrivateKeyEdit.requestFocus();
        mConfirmPrivateKeyEdit.setClickable(true);
        mConfirmPrivateKeyEdit.setLongClickable(true);

        mRegisterBtn.setText("Confirm");
    }

    @Override
    public void showSetPasswordView() {
        if (mPasswordDialog != null) {
            mPasswordDialog.dismiss();
            mPasswordDialog = null;
        }

//        mPasswordDialog = new PasswordDialog(RegisterActivity.this, new PasswordDialog.OnSetPasswordOnClickListener() {
//            @Override
//            public void onCancel() {
//                mPasswordDialog.dismiss();
//            }
//
//            @Override
//            public void onSet(String mnemonic, String password) {
//                if (mRegisterPresenter != null) {
//                    mRegisterPresenter.register(mnemonic, password);
//                }
//
//                mPasswordDialog.dismiss();
//            }
//        }, new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                mPasswordDialog = null;
//            }
//        });
//
//        mPasswordDialog.setMnemonic(mPrivateKeyEdit.getText().toString());
//        mPasswordDialog.show();
    }

    @Override
    public void showInLogInView() {
        mRegisterBtn.setEnabled(false);

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;

        mProgressDialog = new ProgressDialog(RegisterActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    @Override
    public void stopShowInLogInView() {
        mRegisterBtn.setEnabled(true);

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
    }

    @Override
    public void showLogInSucceedView() {
        sendBroadcast(new Intent(Constant.Intent.LOGIN_SUCCEED));

        startActivity(new Intent(RegisterActivity.this, PpioDataActivity.class));

        finish();
    }

    @Override
    public void showLogInFailView() {
        mRemindTv.setText("DO NOT share this phrase with anyone!\nThese words can be used to steal your account.");

        mRegisterBtn.setText("Confirm");
    }

    private void init() {
        setImmersiveStatusBar();

        mRemindTv = findViewById(R.id.register_remind_tv);
        mHintTv = findViewById(R.id.hint_tv);
        mPrivateKeyEdit = findViewById(R.id.generate_privatekey_edit);
        mConfirmPrivateKeyEdit = findViewById(R.id.input_privatekey_edit);
        mRegisterBtn = findViewById(R.id.register_btn);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.runNetOperation(RegisterActivity.this, new Util.RunNetOperationCallBack() {
                    @Override
                    public void onRunOperation() {
                        if (mRegisterPresenter != null) {
                            mRegisterPresenter.registerClick(mPrivateKeyEdit.getText().toString());
                        }
                    }
                });
            }
        });

        mRegisterPresenter = new RegisterPresenterImpl(RegisterActivity.this, RegisterActivity.this);

        mRegisterBtn.setEnabled(false);
        mPrivateKeyEdit.setKeyListener(null);

        if (mRegisterPresenter != null) {
            mRegisterPresenter.generatePrivateKey();
        }
    }
}