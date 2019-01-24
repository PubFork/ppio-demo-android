package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;

import io.pp.net_disk_demo.mvp.model.AccountInfoModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.AccountInfoModelImpl;
import io.pp.net_disk_demo.mvp.presenter.AccountInfoPresenter;
import io.pp.net_disk_demo.mvp.view.AccountInfoView;

public class AccountInfoPresenterImpl implements AccountInfoPresenter {

    private Context mContext;
    private AccountInfoView mAccountInfoView;
    private AccountInfoModel mAccountInfoModel;

    public AccountInfoPresenterImpl(Context context, AccountInfoView accountInfoView) {
        mContext = context;
        mAccountInfoView = accountInfoView;

        mAccountInfoModel = new AccountInfoModelImpl(mContext, AccountInfoPresenterImpl.this);
    }

    @Override
    public void requestAddress() {
        if (mAccountInfoModel != null) {
            mAccountInfoModel.requestAddress();
        }
    }

    @Override
    public void showAddress(String address) {
        if (mAccountInfoView != null) {
            mAccountInfoView.showAddress(address);
        }
    }

    @Override
    public void requestUsed() {
        if (mAccountInfoModel != null) {
            mAccountInfoModel.requestUsed();
        }
    }

    @Override
    public void showInRequestUsed() {
        if (mAccountInfoView != null) {
            //mAccountInfoView.showRequestUsedView();
        }
    }

    @Override
    public void showUsed(String used) {
        if (mAccountInfoView != null) {
            mAccountInfoView.showUsedView(used);
        }
    }

    @Override
    public void showGetUsedFail(String errMsg) {
        if (mAccountInfoView != null) {
            mAccountInfoView.showGetUsedFailView(errMsg);
        }
    }

    @Override
    public void requestBalance() {
        if (mAccountInfoModel != null) {
            mAccountInfoModel.requestBalance();
        }
    }

    @Override
    public void showInRequestBalance() {
        if (mAccountInfoView != null) {
            mAccountInfoView.showRequestBalanceView();
        }
    }

    @Override
    public void showBalance(String balance) {
        if (mAccountInfoView != null) {
            mAccountInfoView.showBalanceView(balance);
        }
    }

    @Override
    public void showGetBalanceFail(String errMsg) {
        if (mAccountInfoView != null) {
            mAccountInfoView.showGetBalanceFailView(errMsg);
        }
    }

    @Override
    public void requestFund() {
        if (mAccountInfoModel != null) {
            mAccountInfoModel.requestFund();
        }
    }

    @Override
    public void showInRequestFund() {
        if (mAccountInfoView != null) {
            mAccountInfoView.showRequestFundView();
        }
    }

    @Override
    public void showFund(String fund) {
        if (mAccountInfoView != null) {
            mAccountInfoView.showFundView(fund);
        }
    }

    @Override
    public void showGetFundFail(String errMsg) {
        if (mAccountInfoView != null) {
            mAccountInfoView.showGetFundFailView(errMsg);
        }
    }

    @Override
    public void requestOracleChiPrice() {
        if (mAccountInfoModel != null) {
            mAccountInfoModel.requestOracleChiPrice();
        }
    }

    @Override
    public void showRecharge() {
        if (mAccountInfoView != null) {
            mAccountInfoView.showRechargeView();
        }
    }

    @Override
    public void showRecord() {
        if (mAccountInfoView != null) {
            mAccountInfoView.showRecordView();
        }
    }

    @Override
    public void showCheckVersion() {
        if (mAccountInfoView != null) {
            mAccountInfoView.showCheckVersionView();
        }
    }

    @Override
    public void showFeedback() {
        if (mAccountInfoView != null) {
            mAccountInfoView.showFeedbackView();
        }
    }

    @Override
    public void startLogOut() {
        if (mAccountInfoModel != null) {
            mAccountInfoModel.logOut();
        }
    }

    @Override
    public void showLogOutPrepare() {
        if (mAccountInfoView != null) {
            mAccountInfoView.showLogOutPrepareView();
        }
    }

    @Override
    public void showLogOutError(String erMsg) {
        if (mAccountInfoView != null) {
            mAccountInfoView.showLogOutErrorView(erMsg);
        }
    }

    @Override
    public void showLogOutFinish() {
        if (mAccountInfoView != null) {
            mAccountInfoView.showLogOutFinishView();
        }
    }

    @Override
    public void onDestroy() {
        if (mAccountInfoModel != null) {
            mAccountInfoModel.onDestroy();
            mAccountInfoModel = null;
        }

        mContext = null;
        mAccountInfoView = null;
    }
}