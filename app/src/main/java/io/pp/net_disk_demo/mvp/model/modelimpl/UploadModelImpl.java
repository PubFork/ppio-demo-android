package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;

import java.io.File;

import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.mvp.model.UploadModel;
import io.pp.net_disk_demo.mvp.presenter.UploadPresenter;
import io.pp.net_disk_demo.service.ExecuteTaskService;

public class UploadModelImpl implements UploadModel,
        ExecuteTaskService.UploadListener {

    private final String TAG = "UploadModelImpl";

    private Context mContext;
    private UploadInfo mUploadInfo;
    private UploadPresenter mUploadPresenter;

    private ExecuteTaskService mExecuteTaskService;

    public UploadModelImpl(Context context, UploadPresenter uploadPresenter) {
        mContext = context;
        mUploadPresenter = uploadPresenter;

        mUploadInfo = new UploadInfo();
    }

    @Override
    public void bindService(ExecuteTaskService executeTaskService) {
        mExecuteTaskService = executeTaskService;
        if (mExecuteTaskService != null) {
            mExecuteTaskService.setUploadListener(UploadModelImpl.this);
        }
    }

    @Override
    public void setLocalFile(String filePath) {
        File file = new File(filePath);

        mUploadInfo.setFileName(file.getName());
        mUploadInfo.setFile(filePath);

        if (mUploadPresenter != null) {
            mUploadPresenter.showUploadSettings();
        }
    }

    @Override
    public void setUploadInfo(UploadInfo uploadInfo) {
        if (uploadInfo != null) {
            mUploadInfo = uploadInfo;

            if (mUploadPresenter != null) {
                mUploadPresenter.showUploadSettings();
            }
        }
    }

    @Override
    public String getFileName() {
        return mUploadInfo.getFileName();
    }

    public String getFilePath() {
        return mUploadInfo.getFile();
    }

    @Override
    public boolean isSecure() {
        return mUploadInfo.isSecure();
    }

    @Override
    public String getExpiredTime() {
        return mUploadInfo.getExpiredTime();
    }

    @Override
    public int getCopies() {
        return mUploadInfo.getCopiesCount();
    }

    @Override
    public String getChiPrice() {
        return mUploadInfo.getChiPrice();
    }

    @Override
    public void setSecure(boolean secure) {
        mUploadInfo.setSecure(secure);
    }

    @Override
    public void setExpiredTime(String expiredTime) {
        mUploadInfo.setExpiredTime(expiredTime);

        if (mUploadPresenter != null) {
            mUploadPresenter.showExpiredTime(mUploadInfo.getExpiredTime());
        }
    }

    @Override
    public void setCopies(int copies) {
        mUploadInfo.setCopiesCount(copies);

        if (mUploadPresenter != null) {
            mUploadPresenter.showCopies(mUploadInfo.getCopiesCount());
        }
    }

    @Override
    public void setChiPrice(String chiPrice) {
        mUploadInfo.setChiPrice(chiPrice);

        if (mUploadPresenter != null) {
            mUploadPresenter.showChiPrice(mUploadInfo.getChiPrice());
        }
    }

    @Override
    public void upload() {
        if (mExecuteTaskService != null) {
            mExecuteTaskService.startUpload(mUploadInfo);
        }
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mUploadPresenter = null;
        mExecuteTaskService = null;
    }

    @Override
    public void onUploadStart() {
        if (mUploadPresenter != null) {
            mUploadPresenter.showRequestingUpload();
        }
    }

    @Override
    public void onUploadStartSucceed() {
        if (mUploadPresenter != null) {
            mUploadPresenter.showStartUploadSucceed();
        }
    }

    @Override
    public void onUploadStartFailed(String errMsg) {
        if (mUploadPresenter != null) {
            mUploadPresenter.showStartUploadFail(errMsg);
        }
    }
}