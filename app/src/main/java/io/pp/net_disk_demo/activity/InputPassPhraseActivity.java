package io.pp.net_disk_demo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.mvp.presenter.InputPassPhrasePresenter;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.InputPassPhrasePresenterImpl;
import io.pp.net_disk_demo.mvp.view.InputPassPhraseView;
import io.pp.net_disk_demo.util.ToastUtil;

public class InputPassPhraseActivity extends BaseActivity implements InputPassPhraseView {

    private EditText mPassPhraseEdit = null;
    private Button mConfirmBtn = null;
    private Button mNewAccountBtn = null;
    private Button mCancelBtn = null;

    private ProgressDialog mProgressDialog = null;

    private InputPassPhrasePresenter mInputPassPhrasePresenter = null;

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

        if (mInputPassPhrasePresenter != null) {
            mInputPassPhrasePresenter.onDestroy();
            mInputPassPhrasePresenter = null;
        }

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

        mPassPhraseEdit = findViewById(R.id.keystore_passphrase_edit);
        mConfirmBtn = findViewById(R.id.confirm_btn);
        mNewAccountBtn = findViewById(R.id.new_account_btn);
        mCancelBtn = findViewById(R.id.cancel_btn);

        mInputPassPhrasePresenter = new InputPassPhrasePresenterImpl(InputPassPhraseActivity.this,
                InputPassPhraseActivity.this);

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePassPhraseEditKeyBoard();

                if (mInputPassPhrasePresenter != null) {
                    mInputPassPhrasePresenter.logIn(mPassPhraseEdit.getText().toString());
                }
            }
        });

        mNewAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePassPhraseEditKeyBoard();

                startActivity(new Intent(InputPassPhraseActivity.this, KeyStoreLogInActivity.class));
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePassPhraseEditKeyBoard();

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

    private void hidePassPhraseEditKeyBoard() {
        InputMethodManager imm = (InputMethodManager) InputPassPhraseActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(mPassPhraseEdit.getWindowToken(), 0);
        }
    }
}