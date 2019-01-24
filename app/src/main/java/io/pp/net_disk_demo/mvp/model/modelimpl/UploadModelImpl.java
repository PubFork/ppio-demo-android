package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Calendar;

import io.pp.net_disk_demo.data.DateInfo;
import io.pp.net_disk_demo.data.UploadInfo;
import io.pp.net_disk_demo.mvp.model.UploadModel;
import io.pp.net_disk_demo.mvp.presenter.UploadPresenter;
import io.pp.net_disk_demo.ppio.RpcUtil;
import io.pp.net_disk_demo.service.ExecuteTaskService;
import io.pp.net_disk_demo.threadpool.CancelFixedThreadPool;

public class UploadModelImpl implements UploadModel,
        ExecuteTaskService.UploadListener {

    private static final String TAG = "UploadModelImpl";

    private Context mContext;
    private UploadInfo mUploadInfo;
    private DateInfo mDateInfo;

    private int mChunkCount;

    private UploadPresenter mUploadPresenter;

    private ExecuteTaskService mExecuteTaskService;

    private CancelFixedThreadPool mRequestStorageChiPool;

    public UploadModelImpl(Context context, UploadPresenter uploadPresenter) {
        mContext = context;
        mUploadPresenter = uploadPresenter;

        mUploadInfo = new UploadInfo();

        mDateInfo = new DateInfo();

        mRequestStorageChiPool = new CancelFixedThreadPool(1);
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

        mChunkCount = (int) Math.ceil(((double) file.getUsableSpace()) / 1024 / 1024 / 16);

        Calendar calendar = Calendar.getInstance();
        //default expired date is a month later
        calendar.add(Calendar.MONTH, 1);
        mDateInfo = new DateInfo(calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH));

        mUploadInfo.setExpiredTime(mDateInfo.getDate());

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
        return mDateInfo.getDate();
    }

    @Override
    public int getChunkCount() {
        return 0;
    }

    @Override
    public DateInfo getDateInfo() {
        return mDateInfo;
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
    public void setExpiredTime(DateInfo dateInfo) {
        mDateInfo = dateInfo;
        mUploadInfo.setExpiredTime(dateInfo.getDate());

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
    public void requestStorageChi() {
        mRequestStorageChiPool.execute(new RequestStorageChiRunnable(UploadModelImpl.this, mChunkCount, mDateInfo, "100"));
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

    private void showInRequestTotalChi() {
        if (mUploadPresenter != null) {
            mUploadPresenter.showRequestTotalChi();
        }
    }

    private void showGetTotalChi(int totalChi, String chiPrice) {
        if (mUploadPresenter != null) {
            mUploadPresenter.showGetTotalChi(totalChi);
        }
    }

    private void showGetTotalChiFailed(String errMsg) {
        if (mUploadPresenter != null) {
            mUploadPresenter.showGetTotalChiFailed(errMsg);
        }
    }

    static class RequestStorageChiRunnable implements Runnable {
        final WeakReference<UploadModelImpl> mModelImplWeakReference;

        private int mChunkSize;
        private long mDuration;
        private String mChiPrice;

        public RequestStorageChiRunnable(UploadModelImpl prophecyInfoModelImpl, int chunkSize, DateInfo dateInfo, String chiPrice) {
            mModelImplWeakReference = new WeakReference<>(prophecyInfoModelImpl);

            Calendar calendar = Calendar.getInstance();
            long currentSeconds = calendar.getTimeInMillis() / 1000;
            calendar.set(dateInfo.getYear(), dateInfo.getMonthOfYear(), dateInfo.getDayOfMonth(), 0, 0, 0);
            long expiredSeconds = calendar.getTimeInMillis() / 1000;

            mChunkSize = chunkSize;
            mDuration = expiredSeconds - currentSeconds;
            mChiPrice = chiPrice;
        }

        @Override
        public void run() {
            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showInRequestTotalChi();
            }

            int totalChi = RpcUtil.getStorageChi(mChunkSize, mDuration, mChiPrice, new RpcUtil.QueryAccountListener() {
                @Override
                public void onQueryAccountError(String errMsg) {
                    Log.e(TAG, "getStorageChi error : " + errMsg);
                    if (mModelImplWeakReference.get() != null) {
                        mModelImplWeakReference.get().showGetTotalChiFailed(errMsg);
                    }
                }
            });

            if (totalChi > 0 && mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showGetTotalChi(totalChi, mChiPrice);
            }
        }
    }
}