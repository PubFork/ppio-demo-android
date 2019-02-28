package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;

import io.pp.net_disk_demo.data.DateInfo;
import io.pp.net_disk_demo.mvp.view.RenewView;
import io.pp.net_disk_demo.data.FileInfo;
import io.pp.net_disk_demo.mvp.model.RenewModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.RenewModelImpl;
import io.pp.net_disk_demo.mvp.presenter.RenewPresenter;

public class RenewPresenterImpl implements RenewPresenter {

    private Context mContext;
    private RenewView mRenewView;
    private RenewModel mRenewModel;

    public RenewPresenterImpl(Context context, RenewView renewView) {
        mContext = context;
        mRenewView = renewView;
        mRenewModel = new RenewModelImpl(context, RenewPresenterImpl.this);
    }

    @Override
    public void back() {
        if (mRenewView != null) {
            mRenewView.back();
        }
    }

    @Override
    public void setRenewFile(FileInfo fileInfo) {
        if (mRenewModel != null) {
            mRenewModel.setRenewFile(fileInfo);

            showRenewSettings();
        }
    }

    @Override
    public void showRenewSettings() {
        if (mRenewModel != null) {
            showFileName(mRenewModel.getUIFileName());
            showSecure(mRenewModel.isSecure());
            showExpiredTime(mRenewModel.getExpiredTime());
            showCopies(mRenewModel.getCopies());
            showChiPrice(mRenewModel.getChiPrice());

            mRenewModel.requestStorageChi();
        }
    }

    @Override
    public void showFileName(String name) {
        if (mRenewView != null) {
            mRenewView.showFileName(name);
        }
    }

    @Override
    public void showSecure(boolean isSecure) {
        if (mRenewView != null) {
            mRenewView.showSecure(isSecure);
        }
    }

    @Override
    public void showExpiredTime(String expiredTime) {
        if (mRenewView != null) {
            mRenewView.showExpiredTime(expiredTime);
        }
    }

    @Override
    public void showChiPrice(String chiPrice) {
        if (mRenewView != null && mRenewModel != null) {
            mRenewView.showChiPrice(chiPrice);
        }
    }

    @Override
    public void showCopies(int copies) {
        if (mRenewView != null) {
            mRenewView.showCopies(copies);
        }
    }

    @Override
    public void showSetExpiredTime() {
        if (mRenewView != null && mRenewModel != null) {
            mRenewView.showSetExpiredTime(mRenewModel.getDateInfo());
        }
    }

    @Override
    public void showSetCopies() {
        if (mRenewView != null && mRenewModel != null) {
            mRenewView.showSetCopies(mRenewModel.getCopies());
        }
    }

    @Override
    public void showSetChiPrice() {
        if (mRenewView != null && mRenewModel != null) {
            mRenewView.showSetChiPrice(mRenewModel.getChiPrice(), mRenewModel.getFileSize(), mRenewModel.getDateInfo(), mRenewModel.getCopies());
        }
    }

    @Override
    public void setSecure(boolean isSecure) {
        if (mRenewModel != null) {
            mRenewModel.setSecure(isSecure);
        }
    }

    @Override
    public void setExpiredTime(DateInfo dateInfo) {
        if (mRenewModel != null) {
            mRenewModel.setExpiredTime(dateInfo);
        }
    }

    @Override
    public void setChiPrice(String chiPrice) {
        if (mRenewModel != null) {
            mRenewModel.setChiPrice(chiPrice);
        }
    }

    @Override
    public void setCopies(int copies) {
        if (mRenewModel != null) {
            mRenewModel.setCopies(copies);
        }
    }

    @Override
    public void renew() {
        if (mRenewModel != null) {
            mRenewModel.renew();
        }
    }

    @Override
    public void showRenewing() {
        if (mRenewView != null) {
            mRenewView.showRenewingView();
        }
    }

    @Override
    public void showRenewError(String errMsg) {
        if (mRenewView != null) {
            mRenewView.showRenewErrorView(errMsg);
        }
    }

    @Override
    public void renewComplete() {
        if (mRenewView != null) {
            mRenewView.showRenewCompleteView();
        }
    }

    @Override
    public void requestStorageChi() {
        if (mRenewModel != null) {
            mRenewModel.requestStorageChi();
        }
    }

    @Override
    public void showRequestTotalChi() {
        if (mRenewView != null) {
            mRenewView.showRequestTotalChiView();
        }
    }

    @Override
    public void showGetTotalChi(long totalChi) {
        if (mRenewView != null) {
            mRenewView.showGetTotalChiView(totalChi);
        }
    }

    @Override
    public void showGetTotalChiFailed(String errMsg) {
        if (mRenewView != null) {
            mRenewView.showGetTotalChiFailedView(errMsg);
        }
    }

    @Override
    public String getFileName() {
        if (mRenewModel != null) {
            return mRenewModel.getFileName();
        }
        return "";
    }

    @Override
    public long getFileSize() {
        if (mRenewModel != null) {
            return mRenewModel.getFileSize();
        }
        return 0l;
    }

    @Override
    public String getExpiredTime() {
        if (mRenewModel != null) {
            return mRenewModel.getExpiredTime();
        }
        return "";
    }

    @Override
    public int getCopies() {
        if (mRenewModel != null) {
            return mRenewModel.getCopies();
        }
        return 0;
    }

    @Override
    public String getChiPrice() {
        if (mRenewModel != null) {
            return mRenewModel.getChiPrice();
        }
        return "";
    }

    @Override
    public void onDestroy() {
        if (mRenewModel != null) {
            mRenewModel.onDestroy();
            mRenewModel = null;
        }

        mContext = null;
        mRenewView = null;
    }
}