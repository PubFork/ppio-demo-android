package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;

import io.pp.net_disk_demo.mvp.model.GetShareCodeModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.GetShareCodeModelImpl;
import io.pp.net_disk_demo.mvp.presenter.ShowShareCodePresenter;
import io.pp.net_disk_demo.mvp.view.ShareCodeView;

public class ShowShareCodePresenterImpl implements ShowShareCodePresenter {

    private final String TAG = "ShowShareCodePresenterImpl";

    private Context mContext;
    private ShareCodeView mShareCodeView;
    private GetShareCodeModel mGetShareCodeModel;

    public ShowShareCodePresenterImpl(Context context, ShareCodeView shareCodeView) {
        mContext = context;
        mShareCodeView = shareCodeView;
        mGetShareCodeModel = new GetShareCodeModelImpl(context, ShowShareCodePresenterImpl.this);
    }

    @Override
    public void getShareCode(String bucket, String key) {
        if (mGetShareCodeModel != null) {
            mGetShareCodeModel.getShareCode(bucket, key);
        }
    }

    @Override
    public void showGettingShareCode() {
        if (mShareCodeView != null) {
            mShareCodeView.showGettingShareCodeView();
        }
    }

    @Override
    public void showGettingShareCodeError(String errMsg) {
        if (mShareCodeView != null) {
            mShareCodeView.showGettingShareCodeErrorView(errMsg);
        }
    }

    @Override
    public void showShareCode(String shareCode) {
        if (mShareCodeView != null) {
            mShareCodeView.showShareCode(shareCode);
        }
    }

    @Override
    public void onDestroy() {
        if (mGetShareCodeModel != null) {
            mGetShareCodeModel.onDestroy();
            mGetShareCodeModel = null;
        }

        mContext = null;
        mShareCodeView = null;
    }
}