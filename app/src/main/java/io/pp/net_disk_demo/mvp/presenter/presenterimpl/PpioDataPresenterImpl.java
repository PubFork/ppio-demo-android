package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;
import android.support.annotation.NonNull;

import io.pp.net_disk_demo.data.DeletingInfo;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.mvp.presenter.PpioDataPresenter;
import io.pp.net_disk_demo.mvp.view.PpioDataView;
import io.pp.net_disk_demo.data.FileInfo;
import io.pp.net_disk_demo.mvp.model.PpioDataModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.PpioDataModelImpl;

import java.util.ArrayList;
import java.util.HashMap;

public class PpioDataPresenterImpl implements PpioDataPresenter {

    private final String TAG = "PpioDataPresenterImpl";

    private Context mContext;
    private PpioDataView mPpioDataView;
    private PpioDataModel mPpioDataModel;

    public PpioDataPresenterImpl(@NonNull Context context, @NonNull PpioDataView ppioDataView) {
        mContext = context;

        mPpioDataView = ppioDataView;

        mPpioDataModel = new PpioDataModelImpl(context, PpioDataPresenterImpl.this);
    }

    @Override
    public void link() {
        if (mPpioDataModel != null) {
            mPpioDataModel.link();
        }
    }

    @Override
    public void showLinking() {
        if (mPpioDataView != null) {
            mPpioDataView.showLinkingView();
        }
    }

    @Override
    public void stopShowLinking() {
        if (mPpioDataView != null) {
            mPpioDataView.stopShowLinkingView();
        }
    }

    @Override
    public void showLinkFail(String failMessage) {
        if (mPpioDataView != null) {
            mPpioDataView.showLinkFailView(failMessage);
        }
    }

    @Override
    public void showNotLogIn() {
        if (mPpioDataView != null) {
            mPpioDataView.showNotLogInView();
        }
    }

    @Override
    public void refreshAllFileList(HashMap<String , DeletingInfo> deletingInfoHashMap, HashMap<String, String> uploadFailedInfoHashMap) {
        if (mPpioDataModel != null) {
            mPpioDataModel.refreshMyFileList(deletingInfoHashMap, uploadFailedInfoHashMap);
        }
    }

    @Override
    public void showRefreshingMyFileList() {
        if (mPpioDataView != null) {
            mPpioDataView.showRefreshingAllFileListView();
        }
    }

    @Override
    public void showRefreshAllFileListFail(String failStr) {
        if (mPpioDataView != null) {
            mPpioDataView.showRefreshAllFileListFailView(failStr);
        }
    }

    @Override
    public void showAllFileList(HashMap<String, DeletingInfo> deletingInfoHashMap, ArrayList<FileInfo> mMyFileList) {
        if (mPpioDataView != null) {
            mPpioDataView.showAllFileList(deletingInfoHashMap, mMyFileList);
        }
    }

    @Override
    public void showUploadGet() {
        if (mPpioDataView != null) {
            mPpioDataView.showUploadGet();
        }
    }

    @Override
    public void startUpload() {
        if (mPpioDataView != null) {
            mPpioDataView.startUpload();
        }
    }

    @Override
    public void startGet() {
        if (mPpioDataView != null) {
            mPpioDataView.startGet();
        }
    }

    @Override
    public void onDestroy() {
        if (mPpioDataModel != null) {
            mPpioDataModel.onDestroy();
            mPpioDataModel = null;
        }

        mContext = null;
        mPpioDataView = null;
    }
}