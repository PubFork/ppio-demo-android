package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import io.pp.net_disk_demo.data.DateInfo;
import io.pp.net_disk_demo.mvp.model.ProphecyModel;
import io.pp.net_disk_demo.mvp.presenter.ProphecyPresenter;
import io.pp.net_disk_demo.ppio.RpcUtil;
import io.pp.net_disk_demo.threadpool.CancelFixedThreadPool;

public class ProphecyModelImpl implements ProphecyModel {

    private static final String TAG = "ProphecyModelImpl";

    private Context mContext = null;
    private ProphecyPresenter mProphecyPresenter = null;

    private CancelFixedThreadPool mRequestStorageChiPool;
    private CancelFixedThreadPool mRequestDownloadChiPool;
    private CancelFixedThreadPool mRequestDownloadShareChiPool;

    public ProphecyModelImpl(Context context, ProphecyPresenter prophecyPresenter) {
        mContext = context;
        mProphecyPresenter = prophecyPresenter;

        mRequestStorageChiPool = new CancelFixedThreadPool(1);
        mRequestDownloadChiPool = new CancelFixedThreadPool(1);
        mRequestDownloadShareChiPool = new CancelFixedThreadPool(1);
    }

    @Override
    public void requestStorageChi(int chunkSize, DateInfo dateInfo, String chiPrice) {
        mRequestStorageChiPool.execute(new RequestStorageChiRunnable(ProphecyModelImpl.this, chunkSize, dateInfo, chiPrice));
    }

    @Override
    public void requestDownloadChi(long chunkSize, String chiPrice) {
        mRequestDownloadChiPool.execute(new RequestDownloadChiRunnable(ProphecyModelImpl.this, chunkSize, chiPrice));
    }


    @Override
    public void requestDownloadShareChi(String shareCode, String chiPrice) {
        mRequestDownloadShareChiPool.execute(new RequestDownloadShareChiRunnable(ProphecyModelImpl.this, shareCode, chiPrice));
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mProphecyPresenter = null;
    }

    private void showInRequestTotalChi() {
        if (mProphecyPresenter != null) {
            mProphecyPresenter.showRequestTotalChi();
        }
    }

    private void showGetTotalChi(int totalChi, String chiPrice) {
        if (mProphecyPresenter != null) {
            mProphecyPresenter.showGetTotalChi(totalChi);
        }
    }

    private void showGetTotalChiFailed(String errMsg) {
        if (mProphecyPresenter != null) {
            mProphecyPresenter.showGetTotalChiFailed(errMsg);
        }
    }

    static class RequestStorageChiRunnable implements Runnable {
        final WeakReference<ProphecyModelImpl> mModelImplWeakReference;

        private int mChunkSize;
        private long mDuration;
        private String mChiPrice;

        public RequestStorageChiRunnable(ProphecyModelImpl prophecyInfoModelImpl, int chunkSize, DateInfo dateInfo, String chiPrice) {
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

    static class RequestDownloadChiRunnable implements Runnable {
        final WeakReference<ProphecyModelImpl> mModelImplWeakReference;

        private long mChunkSize;
        private String mChiPrice;

        public RequestDownloadChiRunnable(ProphecyModelImpl prophecyInfoModelImpl, long chunkSize, String chiPrice) {
            mModelImplWeakReference = new WeakReference<>(prophecyInfoModelImpl);

            mChunkSize = chunkSize;
            mChiPrice = chiPrice;
        }

        @Override
        public void run() {
            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showInRequestTotalChi();
            }

            int totalChi = RpcUtil.getDownloadChi(mChunkSize, mChiPrice, new RpcUtil.QueryAccountListener() {
                @Override
                public void onQueryAccountError(String errMsg) {
                    Log.e(TAG, "getDownloadChi error : " + errMsg);
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

    static class RequestDownloadShareChiRunnable implements Runnable {
        final WeakReference<ProphecyModelImpl> mModelImplWeakReference;

        private String mShareCode;
        private String mChiPrice;

        public RequestDownloadShareChiRunnable(ProphecyModelImpl prophecyInfoModelImpl, String shareCode, String chiPrice) {
            mModelImplWeakReference = new WeakReference<>(prophecyInfoModelImpl);

            mShareCode = shareCode;
            mChiPrice = chiPrice;
        }

        @Override
        public void run() {
            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showInRequestTotalChi();
            }

            long fileSize = 0l;
            try {
                String jsonStr = new String(Base64.decode((mShareCode.replaceFirst("poss://", "")).getBytes(), Base64.DEFAULT));

                //
                Log.e(TAG, "jsonStr = " + jsonStr);
                //

                JSONObject jsonObject = new JSONObject(jsonStr);
                fileSize = jsonObject.getLong("length");

                int totalChi = RpcUtil.getDownloadChi(fileSize, mChiPrice, new RpcUtil.QueryAccountListener() {
                    @Override
                    public void onQueryAccountError(String errMsg) {
                        Log.e(TAG, "getDownloadChi error : " + errMsg);
                        if (mModelImplWeakReference.get() != null) {
                            mModelImplWeakReference.get().showGetTotalChiFailed(errMsg);
                        }
                    }
                });

                if (totalChi > 0 && mModelImplWeakReference.get() != null) {
                    mModelImplWeakReference.get().showGetTotalChi(totalChi, mChiPrice);
                }
            } catch (JSONException e) {
                Log.e(TAG, "" + e.getMessage());
                e.printStackTrace();

                if (mModelImplWeakReference.get() != null) {
                    mModelImplWeakReference.get().showGetTotalChiFailed(e.getMessage());
                }
            }
        }
    }
}