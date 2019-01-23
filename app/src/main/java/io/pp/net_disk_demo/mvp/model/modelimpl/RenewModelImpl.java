package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.pp.net_disk_demo.data.DateInfo;
import io.pp.net_disk_demo.data.FileInfo;
import io.pp.net_disk_demo.data.RenewInfo;
import io.pp.net_disk_demo.mvp.model.RenewModel;
import io.pp.net_disk_demo.mvp.presenter.RenewPresenter;
import io.pp.net_disk_demo.mvp.presenter.presenterimpl.RenewPresenterImpl;
import io.pp.net_disk_demo.ppio.PossUtil;

public class RenewModelImpl implements RenewModel {

    private static final String TAG = "RenewModelImpl";

    private Context mContext;
    private final RenewInfo mRenewInfo;
    private DateInfo mDateInfo;
    private RenewPresenter mRenewPresenter;

    public RenewModelImpl(Context context, RenewPresenterImpl renewPresenterImpl) {
        mContext = context;
        mRenewPresenter = renewPresenterImpl;

        mRenewInfo = new RenewInfo();
        mDateInfo = new DateInfo();
    }

    @Override
    public void setRenewFile(FileInfo fileInfo) {
        mRenewInfo.setBucket(fileInfo.getBucketName());
        mRenewInfo.setKey(fileInfo.getName());

        Calendar calendar = Calendar.getInstance();
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .parse(fileInfo.getStorageTime());
            calendar.setTime(date);
        } catch (ParseException e) {
            Log.e(TAG, "setRenewFile() error: " + e.getMessage());

            e.printStackTrace();
        }

        mDateInfo = new DateInfo(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public String getFileName() {
        return mRenewInfo.getFileName();
    }

    @Override
    public boolean isSecure() {
        return true;
    }

    @Override
    public String getExpiredTime() {
        return mDateInfo.getDate();
    }

    @Override
    public DateInfo getDateInfo() {
        return mDateInfo;
    }

    @Override
    public int getCopies() {
        return mRenewInfo.getCopiesCount();
    }

    @Override
    public String getChiPrice() {
        return mRenewInfo.getChiPrice();
    }

    @Override
    public void setSecure(boolean secure) {
    }

    @Override
    public void setExpiredTime(DateInfo dateInfo) {
        mDateInfo = dateInfo;
        mRenewInfo.setExpiredTime(mDateInfo.getDate());

        if (mRenewPresenter != null) {
            mRenewPresenter.showExpiredTime(mRenewInfo.getExpiredTime());
        }
    }

    @Override
    public void setCopies(int copies) {
        mRenewInfo.setCopiesCount(copies);

        if (mRenewPresenter != null) {
            mRenewPresenter.showCopies(mRenewInfo.getCopiesCount());
        }
    }

    @Override
    public void setChiPrice(String gasPrice) {
        mRenewInfo.setChiPrice(gasPrice);

        if (mRenewPresenter != null) {
            mRenewPresenter.showChiPrice(mRenewInfo.getChiPrice());
        }
    }

    @Override
    public void renew() {
        new RenewAsyncTask(RenewModelImpl.this).execute();
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mRenewPresenter = null;
    }

    public Context getContext() {
        return mContext;
    }

    public RenewInfo getRenewInfo() {
        return mRenewInfo;
    }

    private void showRenewing() {
        if (mRenewPresenter != null) {
            mRenewPresenter.showRenewing();
        }
    }

    private void showRenewError(String errMsg) {
        if (mRenewPresenter != null) {
            mRenewPresenter.showRenewError(errMsg);
        }
    }

    private void showRenewComplete() {
        if (mRenewPresenter != null) {
            mRenewPresenter.renewComplete();
        }
    }

    static class RenewAsyncTask extends AsyncTask<String, String, Boolean> {

        private WeakReference<RenewModelImpl> mRenewAsyncTaskWeakReference;

        public RenewAsyncTask(RenewModelImpl renewModel) {
            mRenewAsyncTaskWeakReference = new WeakReference<>(renewModel);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mRenewAsyncTaskWeakReference.get() != null) {
                mRenewAsyncTaskWeakReference.get().showRenewing();
            }
        }

        @Override
        protected Boolean doInBackground(String[] values) {
            if (mRenewAsyncTaskWeakReference.get() != null) {
                RenewInfo renewInfo = mRenewAsyncTaskWeakReference.get().getRenewInfo();

                if (renewInfo != null) {
                    final String bucket = renewInfo.getBucket();
                    final String key = renewInfo.getKey();
                    final String expiredTime = renewInfo.getExpiredTime();
                    final long copies = renewInfo.getCopiesCount();
                    final String chiPriceStr = renewInfo.getChiPrice();

                    if (copies < 1) {
                        publishProgress("copies is less than 1");

                        return false;
                    }

                    int chiPrice;
                    try {
                        chiPrice = Integer.parseInt(chiPriceStr);
                        if (chiPrice < 1) {
                            publishProgress("chi price can not be less than 1!");

                            return false;
                        }
                    } catch (NumberFormatException e) {
                        publishProgress("chi price format is incorrect, " + e.getMessage());

                        return false;
                    }

                    return PossUtil.renewObject(bucket,
                            key,
                            chiPriceStr,
                            copies,
                            expiredTime,
                            new PossUtil.RenewObjectListener() {
                                @Override
                                public void onRenewObjectError(String errMsg) {
                                    publishProgress(errMsg);
                                }
                            });
                } else {
                    publishProgress("renewInfo == null");

                    return false;
                }
            } else {
                publishProgress("mRenewAsyncTaskWeakReference.get() == null");

                return false;
            }
        }

        @Override
        protected void onProgressUpdate(String[] values) {
            super.onProgressUpdate(values);

            if (mRenewAsyncTaskWeakReference.get() != null) {
                mRenewAsyncTaskWeakReference.get().showRenewError(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if (mRenewAsyncTaskWeakReference.get() != null && success) {
                mRenewAsyncTaskWeakReference.get().showRenewComplete();
            }
        }
    }
}