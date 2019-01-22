package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import io.pp.net_disk_demo.mvp.model.ScanCodeModel;
import io.pp.net_disk_demo.mvp.presenter.ScanCodePresenter;

public class ScanCodeModelImpl implements ScanCodeModel {

    private static final String TAG = "ScanCodeModelImpl";

    private Context mContext = null;
    private ScanCodePresenter mScanCodePresenter = null;

    public ScanCodeModelImpl(Context context, ScanCodePresenter scanCodePresenter) {
        mContext = context;
        mScanCodePresenter = scanCodePresenter;
    }

    @Override
    public void decodeBitmapCode(String filePath) {
        new DecodeQRCodeAsyncTask(ScanCodeModelImpl.this).execute(filePath);
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mScanCodePresenter = null;
    }

    private void showInDecodeBitmapCode() {
        if (mScanCodePresenter != null) {
            mScanCodePresenter.showInDecodeBitmapCode();
        }
    }

    private void stopShowInDecodeBitmapCode() {
        if (mScanCodePresenter != null) {
            mScanCodePresenter.stopShowInDecodeBitmapCode();
        }
    }

    private void decodeBitmapCodeFail(String errMsg) {
        if (mScanCodePresenter != null) {
            mScanCodePresenter.decodeBitmapCodeFail(errMsg);
        }
    }

    private void decodeBitmapCodeSucceed(String result) {
        if (mScanCodePresenter != null) {
            mScanCodePresenter.decodeBitmapCodeSucceed(result);
        }
    }

    private static class DecodeQRCodeAsyncTask extends AsyncTask<String, String, String> {

        final WeakReference<ScanCodeModelImpl> mScanCodeModelImplWeakReference;

        public DecodeQRCodeAsyncTask(ScanCodeModelImpl scanCodeModelImpl) {
            mScanCodeModelImplWeakReference = new WeakReference<>(scanCodeModelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mScanCodeModelImplWeakReference.get() != null) {
                mScanCodeModelImplWeakReference.get().showInDecodeBitmapCode();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            //
            Log.e(TAG, "filepath = " + params[0]);
            //
            String result = null;
            try {
                result = QRCodeDecoder.syncDecodeQRCode(params[0]);

                Log.e(TAG, "result = " + result);
            } catch (Exception e) {
                Log.e(TAG, "QRCodeDecoder.syncDecodeQRCode() error: " + e.getMessage());
                publishProgress(e.getMessage());
                result = null;
            }

            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if (mScanCodeModelImplWeakReference.get() != null) {
                mScanCodeModelImplWeakReference.get().decodeBitmapCodeFail(values[0]);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (mScanCodeModelImplWeakReference.get() != null) {
                mScanCodeModelImplWeakReference.get().decodeBitmapCodeSucceed(result);
            }
        }
    }
}