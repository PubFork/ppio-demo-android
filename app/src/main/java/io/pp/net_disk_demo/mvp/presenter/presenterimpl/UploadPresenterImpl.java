package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;

import io.pp.net_disk_demo.data.DateInfo;
import io.pp.net_disk_demo.mvp.presenter.UploadPresenter;
import io.pp.net_disk_demo.mvp.view.UploadView;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.mvp.model.UploadModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.UploadModelImpl;
import io.pp.net_disk_demo.service.UploadService;

public class UploadPresenterImpl implements UploadPresenter {

    private final String TAG = "UploadPresenterImpl";

    private Context mContext;

    private UploadModel mUploadModel;
    private UploadView mUploadView;

    public UploadPresenterImpl(Context context, UploadView uploadView) {
        mContext = context;

        mUploadView = uploadView;
        mUploadModel = new UploadModelImpl(mContext, UploadPresenterImpl.this);
    }

    @Override
    public void bindUploadService(UploadService uploadService) {
        if (mUploadModel != null) {
            mUploadModel.bindUploadService(uploadService);
        }
    }

    @Override
    public void back() {
        if (mUploadView != null) {
            mUploadView.back();
        }
    }

    @Override
    public void generateUploadModel() {
        mUploadModel = new UploadModelImpl(mContext, UploadPresenterImpl.this);
    }

    @Override
    public void setLocalFile(String filePath) {
        if (mUploadModel != null) {
            mUploadModel.setLocalFile(filePath);
        }
    }

    @Override
    public void setUploadInfo(UploadInfo uploadInfo) {
        if (mUploadModel != null) {
            mUploadModel.setUploadInfo(uploadInfo);
        }
    }

    @Override
    public void showUploadSettings() {
        if (mUploadModel != null) {
            showFileName(mUploadModel.getFileName());
            showSecure(mUploadModel.isSecure());
            showExpiredTime(mUploadModel.getExpiredTime());
            showCopies(mUploadModel.getCopies());
            showChiPrice(mUploadModel.getChiPrice());

            mUploadModel.requestStorageChi();
        }
    }

    @Override
    public void showFileName(String name) {
        if (mUploadView != null) {
            mUploadView.showFileName(name);
        }
    }

    @Override
    public void showSecure(boolean isSecure) {
        if (mUploadModel != null) {
            mUploadModel.setSecure(isSecure);
        }
    }

    @Override
    public void showExpiredTime(String storageTime) {
        if (mUploadView != null) {
            mUploadView.showExpiredTime(storageTime);
        }
    }

    @Override
    public void showCopies(int copies) {
        if (mUploadView != null) {
            mUploadView.showCopies(copies);
        }
    }

    @Override
    public void showChiPrice(String chiPrice) {
        if (mUploadView != null) {
            mUploadView.showChiPrice(chiPrice);
        }
    }

    @Override
    public void showSetExpiredTime() {
        if (mUploadView != null && mUploadModel != null) {
            mUploadView.showSetExpiredTime(mUploadModel.getDateInfo());
        }
    }

    @Override
    public void showSetCopies() {
        if (mUploadView != null && mUploadModel != null) {
            mUploadView.showSetCopies(mUploadModel.getCopies());
        }
    }

    @Override
    public void showSetChiPrice() {
        if (mUploadView != null && mUploadModel != null) {
            mUploadView.showSetChiPrice(mUploadModel.getChiPrice(), mUploadModel.getFileSize(), mUploadModel.getDateInfo(), mUploadModel.getCopies());
        }
    }

    @Override
    public void setSecure(boolean isSecure) {
        if (mUploadModel != null) {
            mUploadModel.setSecure(isSecure);
        }
    }

    @Override
    public void setExpiredTime(DateInfo dateInfo) {
        if (mUploadModel != null) {
            mUploadModel.setExpiredTime(dateInfo);
        }
    }

    @Override
    public void setCopies(int copies) {
        if (mUploadModel != null) {
            mUploadModel.setCopies(copies);
        }
    }

    @Override
    public void setChiPrice(String chiPrice) {
        if (mUploadModel != null) {
            mUploadModel.setChiPrice(chiPrice);
        }
    }

    @Override
    public void confirm() {
        if (mUploadModel != null) {
            mUploadModel.upload();
        }
    }


    @Override
    public void showRequestingUpload() {
        if (mUploadView != null) {
            mUploadView.showRequestingUploadView();
        }
    }

    @Override
    public void showStartUploadSucceed() {
        if (mUploadView != null) {
            mUploadView.showRequestUploadFinishedView();
        }
    }

    @Override
    public void showStartUploadFail(String errMsg) {
        if (mUploadView != null) {
            mUploadView.showUploadFailView(errMsg);
        }
    }

    @Override
    public void requestStorageChi() {
        if (mUploadModel != null) {
            mUploadModel.requestStorageChi();
        }
    }

    @Override
    public void showRequestTotalChi() {
        if (mUploadView != null) {
            mUploadView.showRequestTotalChiView();
        }
    }

    @Override
    public void showGetTotalChi(long totalChi) {
        if (mUploadView != null) {
            mUploadView.showGetTotalChiView(totalChi);
        }
    }

    @Override
    public void showGetTotalChiFailed(String errMsg) {
        if (mUploadView != null) {
            mUploadView.showGetTotalChiFailedView(errMsg);
        }
    }

    @Override
    public String getFilePath() {
        if (mUploadModel != null) {
            return mUploadModel.getFilePath();
        }
        return "";
    }

    @Override
    public String getExpiredTime() {
        if (mUploadModel != null) {
            return mUploadModel.getExpiredTime();
        }
        return "";
    }

    @Override
    public int getCopies() {
        if (mUploadModel != null) {
            return mUploadModel.getCopies();
        }
        return 0;
    }

    @Override
    public String getChiPrice() {
        if (mUploadModel != null) {
            return mUploadModel.getChiPrice();
        }
        return "";
    }

    @Override
    public void onDestroy() {
        if (mUploadModel != null) {
            mUploadModel.onDestroy();
            mUploadModel = null;
        }

        mContext = null;
        mUploadView = null;
    }
}