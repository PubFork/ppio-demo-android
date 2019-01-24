package io.pp.net_disk_demo.mvp.view;

public interface AccountInfoView {

    void showAddress(String address);


    void showRequestUsedView();

    void showUsedView(String used);

    void showGetUsedFailView(String errMsg);


    void showRequestBalanceView();

    void showBalanceView(String balance);

    void showGetBalanceFailView(String errMsg);


    void showRequestFundView();

    void showFundView(final String fund);

    void showGetFundFailView(String errMsg);


    void showRechargeView();

    void showRecordView();

    void showCheckVersionView();

    void showFeedbackView();

    void showLogOutPrepareView();

    void showLogOutErrorView(String erMsg);

    void showLogOutFinishView();
}