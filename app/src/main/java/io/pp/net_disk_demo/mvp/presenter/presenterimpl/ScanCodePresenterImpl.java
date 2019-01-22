package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;

import io.pp.net_disk_demo.mvp.model.ScanCodeModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.ScanCodeModelImpl;
import io.pp.net_disk_demo.mvp.presenter.ScanCodePresenter;
import io.pp.net_disk_demo.mvp.view.ScanCodeView;

public class ScanCodePresenterImpl implements ScanCodePresenter {

    private Context mContext = null;
    private ScanCodeView mScanCodeView = null;
    private ScanCodeModel mScanCodeModel = null;

    public ScanCodePresenterImpl(Context context, ScanCodeView scanCodeView) {
        mContext = null;

        mScanCodeView = scanCodeView;
        mScanCodeModel = new ScanCodeModelImpl(context, ScanCodePresenterImpl.this);
    }

    @Override
    public void decodeBitmapCode(String filePath) {
        if (mScanCodeModel != null) {
            mScanCodeModel.decodeBitmapCode(filePath);
        }
    }

    @Override
    public void showInDecodeBitmapCode() {
        if(mScanCodeView != null) {
            mScanCodeView.showInDecodeBitmapCodeView();
        }
    }

    @Override
    public void stopShowInDecodeBitmapCode() {
        if(mScanCodeView != null) {
            mScanCodeView.stopShowInDecodeBitmapCodeView();
        }
    }

    @Override
    public void decodeBitmapCodeFail(String errMsg) {
        if(mScanCodeView != null) {
            mScanCodeView.showDecodeBitmapCodeFailedView(errMsg);
        }
    }

    @Override
    public void decodeBitmapCodeSucceed(String result) {
        if(mScanCodeView != null) {
            mScanCodeView.showDecodeBitmapCodeSucceedView(result);
        }
    }

    @Override
    public void onDestroy() {
        if (mScanCodeModel != null) {
            mScanCodeModel.onDestroy();
            mScanCodeModel = null;
        }

        mContext = null;

        mScanCodeView = null;
    }
}
