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
    public void requestStorageChi(long fileSize, DateInfo dateInfo) {
        mRequestStorageChiPool.execute(new RequestStorageChiRunnable(ProphecyModelImpl.this, fileSize, dateInfo));
    }

    @Override
    public void requestDownloadChi(long fileSize) {
        mRequestDownloadChiPool.execute(new RequestDownloadChiRunnable(ProphecyModelImpl.this, fileSize));
    }


    @Override
    public void requestDownloadShareChi(String shareCode) {
        mRequestDownloadShareChiPool.execute(new RequestDownloadShareChiRunnable(ProphecyModelImpl.this, shareCode));
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

    private void showGetTotalChi(int totalChi) {
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

        private long mFileSize;
        private long mDuration;

        public RequestStorageChiRunnable(ProphecyModelImpl prophecyInfoModelImpl, long fileSize, DateInfo dateInfo) {
            mModelImplWeakReference = new WeakReference<>(prophecyInfoModelImpl);

            Calendar calendar = Calendar.getInstance();
            long currentSeconds = calendar.getTimeInMillis() / 1000;
            calendar.set(dateInfo.getYear(), dateInfo.getMonthOfYear(), dateInfo.getDayOfMonth(), 0, 0, 0);
            long expiredSeconds = calendar.getTimeInMillis() / 1000;

            mFileSize = fileSize;
            mDuration = expiredSeconds - currentSeconds;
        }

        @Override
        public void run() {
            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showInRequestTotalChi();
            }

            final int chunkCount = (int) ((double) mFileSize / 1024 / 1024 / 16);
            if (chunkCount > 1) {
                final long chunkSize1 = 1024 * 1024 * 16;
                final long chunkSize2 = mFileSize - chunkSize1 * (chunkCount - 1);

                int chunkChi1 = RpcUtil.getStorageChi(chunkSize1, mDuration, new RpcUtil.QueryAccountListener() {
                    @Override
                    public void onQueryAccountError(String errMsg) {
                        Log.e(TAG, "getStorageChi error : " + errMsg);
                        if (mModelImplWeakReference.get() != null) {
                            mModelImplWeakReference.get().showGetTotalChiFailed(errMsg);
                        }
                    }
                });

                if (chunkChi1 > 0) {
                    int chunkChi2 = RpcUtil.getStorageChi(chunkSize2, mDuration, new RpcUtil.QueryAccountListener() {
                        @Override
                        public void onQueryAccountError(String errMsg) {
                            Log.e(TAG, "getStorageChi error : " + errMsg);
                            if (mModelImplWeakReference.get() != null) {
                                mModelImplWeakReference.get().showGetTotalChiFailed(errMsg);
                            }
                        }
                    });

                    int totalChi = chunkChi1 * (chunkCount - 1) + chunkChi2;

                    if (totalChi > 0 && mModelImplWeakReference.get() != null) {
                        mModelImplWeakReference.get().showGetTotalChi(totalChi);
                    }
                }
            } else {
                int totalChi = RpcUtil.getStorageChi(mFileSize, mDuration, new RpcUtil.QueryAccountListener() {
                    @Override
                    public void onQueryAccountError(String errMsg) {
                        Log.e(TAG, "getStorageChi error : " + errMsg);
                        if (mModelImplWeakReference.get() != null) {
                            mModelImplWeakReference.get().showGetTotalChiFailed(errMsg);
                        }
                    }
                });

                if (totalChi > 0 && mModelImplWeakReference.get() != null) {
                    mModelImplWeakReference.get().showGetTotalChi(totalChi);
                }
            }
        }
    }

    static class RequestDownloadChiRunnable implements Runnable {
        final WeakReference<ProphecyModelImpl> mModelImplWeakReference;

        private long mFileSize;

        public RequestDownloadChiRunnable(ProphecyModelImpl prophecyInfoModelImpl, long fileSize) {
            mModelImplWeakReference = new WeakReference<>(prophecyInfoModelImpl);

            mFileSize = fileSize;
        }

        @Override
        public void run() {
            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showInRequestTotalChi();
            }

            final int chunkCount = (int) ((double) mFileSize / 1024 / 1024 / 16);
            Log.e(TAG, "chunkCount = " + chunkCount);
            if (chunkCount > 1) {
                final long chunkSize1 = 1024 * 1024 * 16;
                final long chunkSize2 = mFileSize - chunkSize1 * (chunkCount - 1);

                int chunkChi1 = RpcUtil.getDownloadChi(chunkSize1, new RpcUtil.QueryAccountListener() {
                    @Override
                    public void onQueryAccountError(String errMsg) {
                        Log.e(TAG, "getStorageChi error : " + errMsg);
                        if (mModelImplWeakReference.get() != null) {
                            mModelImplWeakReference.get().showGetTotalChiFailed(errMsg);
                        }
                    }
                });

                if (chunkChi1 > 0) {
                    int chunkChi2 = RpcUtil.getDownloadChi(chunkSize2, new RpcUtil.QueryAccountListener() {
                        @Override
                        public void onQueryAccountError(String errMsg) {
                            Log.e(TAG, "getStorageChi error : " + errMsg);
                            if (mModelImplWeakReference.get() != null) {
                                mModelImplWeakReference.get().showGetTotalChiFailed(errMsg);
                            }
                        }
                    });

                    int totalChi = chunkChi1 * (chunkCount - 1) + chunkChi2;

                    if (totalChi > 0 && mModelImplWeakReference.get() != null) {
                        mModelImplWeakReference.get().showGetTotalChi(totalChi);
                    }
                }
            } else {
                int totalChi = RpcUtil.getDownloadChi(mFileSize, new RpcUtil.QueryAccountListener() {
                    @Override
                    public void onQueryAccountError(String errMsg) {
                        Log.e(TAG, "getStorageChi error : " + errMsg);
                        if (mModelImplWeakReference.get() != null) {
                            mModelImplWeakReference.get().showGetTotalChiFailed(errMsg);
                        }
                    }
                });

                if (totalChi > 0 && mModelImplWeakReference.get() != null) {
                    mModelImplWeakReference.get().showGetTotalChi(totalChi);
                }
            }
        }
    }

    static class RequestDownloadShareChiRunnable implements Runnable {
        final WeakReference<ProphecyModelImpl> mModelImplWeakReference;

        private String mShareCode;

        public RequestDownloadShareChiRunnable(ProphecyModelImpl prophecyInfoModelImpl, String shareCode) {
            mModelImplWeakReference = new WeakReference<>(prophecyInfoModelImpl);

            mShareCode = shareCode;
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

                final int chunkCount = (int) ((double) fileSize / 1024 / 1024 / 16);
                if (chunkCount > 1) {
                    final long chunkSize1 = 1024 * 1024 * 16;
                    final long chunkSize2 = fileSize - chunkSize1 * (chunkCount - 1);

                    int chunkChi1 = RpcUtil.getDownloadChi(chunkSize1, new RpcUtil.QueryAccountListener() {
                        @Override
                        public void onQueryAccountError(String errMsg) {
                            Log.e(TAG, "getStorageChi error : " + errMsg);
                            if (mModelImplWeakReference.get() != null) {
                                mModelImplWeakReference.get().showGetTotalChiFailed(errMsg);
                            }
                        }
                    });

                    if (chunkChi1 > 0) {
                        int chunkChi2 = RpcUtil.getDownloadChi(chunkSize2, new RpcUtil.QueryAccountListener() {
                            @Override
                            public void onQueryAccountError(String errMsg) {
                                Log.e(TAG, "getStorageChi error : " + errMsg);
                                if (mModelImplWeakReference.get() != null) {
                                    mModelImplWeakReference.get().showGetTotalChiFailed(errMsg);
                                }
                            }
                        });

                        int totalChi = chunkChi1 * (chunkCount - 1) + chunkChi2;

                        if (totalChi > 0 && mModelImplWeakReference.get() != null) {
                            mModelImplWeakReference.get().showGetTotalChi(totalChi);
                        }
                    }
                } else {
                    int totalChi = RpcUtil.getDownloadChi(fileSize, new RpcUtil.QueryAccountListener() {
                        @Override
                        public void onQueryAccountError(String errMsg) {
                            Log.e(TAG, "getStorageChi error : " + errMsg);
                            if (mModelImplWeakReference.get() != null) {
                                mModelImplWeakReference.get().showGetTotalChiFailed(errMsg);
                            }
                        }
                    });

                    if (totalChi > 0 && mModelImplWeakReference.get() != null) {
                        mModelImplWeakReference.get().showGetTotalChi(totalChi);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                if (mModelImplWeakReference.get() != null) {
                    mModelImplWeakReference.get().showGetTotalChiFailed(e.getMessage());
                }
            }
        }
    }
}