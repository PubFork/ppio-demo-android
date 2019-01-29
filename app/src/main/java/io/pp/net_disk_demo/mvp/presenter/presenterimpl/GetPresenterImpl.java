package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;

import io.pp.net_disk_demo.mvp.presenter.GetPresenter;
import io.pp.net_disk_demo.mvp.view.GetView;
import io.pp.net_disk_demo.mvp.model.GetModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.GetModelImpl;
import io.pp.net_disk_demo.service.DownloadService;
import io.pp.net_disk_demo.service.ExecuteTaskService;

public class GetPresenterImpl implements GetPresenter {

    private static final String TAG = "GetPresenterImpl";

    private GetModel mGetModel;
    private GetView mGetView;

    public GetPresenterImpl(Context context, GetView getView) {
        mGetView = getView;

        mGetModel = new GetModelImpl(context, GetPresenterImpl.this);
    }

    @Override
    public void back() {
        if (mGetView != null) {
            mGetView.back();
        }
    }

    @Override
    public void bindGetService(ExecuteTaskService executeTaskService) {
        if (mGetModel != null) {
            mGetModel.bindGetService(executeTaskService);
        }
    }

    @Override
    public void bindDownloadService(DownloadService downloadService) {
        if (mGetModel != null) {
            mGetModel.bindDownloadService(downloadService);
        }
    }

    @Override
    public void setShareCode(String shareCode) {
        if (mGetModel != null) {
            mGetModel.setShareCode(shareCode);

            showSetChiPrice();
        }
    }

    @Override
    public void setChiPrice(int chiPrice) {
        if (mGetModel != null) {
            mGetModel.setChiPrice(chiPrice);
        }
    }


    @Override
    public void startGet() {
        if (mGetModel != null) {
            mGetModel.startGet();
        }
    }

    @Override
    public void showSetChiPrice() {
        if (mGetView != null) {
            mGetView.showSetChiPriceView();
        }
    }

    @Override
    public void showRequestingGet() {
        if (mGetView != null) {
            mGetView.showRequestingGetView();
        }
    }

    @Override
    public void showStartGetFail(String errNsg) {
        if (mGetView != null) {
            mGetView.showGetFailView(errNsg);
        }
    }

    @Override
    public void showStartGetSucceed() {
        if (mGetView != null) {
            mGetView.showRequestGetFinishedView();
        }
    }

    @Override
    public void onDestroy() {
        if (mGetModel != null) {
            mGetModel.onDestroy();
            mGetModel = null;
        }
    }
}