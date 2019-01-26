package io.pp.net_disk_demo.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.mvp.presenter.InputPassPhrasePresenter;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.InputPassPhrasePresenterImpl;
import io.pp.net_disk_demo.mvp.view.InputPassPhraseView;
import io.pp.net_disk_demo.util.ToastUtil;

public class InputPassPhraseActivity extends BaseActivity implements InputPassPhraseView {

    private EditText mPassPhraseEdit = null;
    private Button mConfirmBtn = null;
    private TextView mImportKeyStoreTv = null;

    private ProgressDialog mProgressDialog = null;

    private InputPassPhrasePresenter mInputPassPhrasePresenter = null;

    private BroadcastReceiver mBroadcastReceiver = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_input_passphrase_layout);

        setupUI(findViewById(R.id.input_passphrase_layout));

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

                if (mInputPassPhrasePresenter != null) {
                    mInputPassPhrasePresenter.logIn(mPassPhraseEdit.getText().toString());
                }
            }
        });

        mImportKeyStoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hidePassPhraseEditKeyBoard();
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