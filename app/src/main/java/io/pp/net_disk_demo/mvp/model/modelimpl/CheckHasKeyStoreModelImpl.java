package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import io.pp.net_disk_demo.mvp.model.CheckHasKeyStoreModel;
import io.pp.net_disk_demo.mvp.presenter.CheckHasKeyStorePresenter;
import io.pp.net_disk_demo.ppio.KeyStoreUtil;

public class CheckHasKeyStoreModelImpl implements CheckHasKeyStoreModel {

    private static Context mContext = null;
    private static CheckHasKeyStorePresenter mCheckHasKeyStorePresenter = null;

    public CheckHasKeyStoreModelImpl(Context context, CheckHasKeyStorePresenter checkHasKeyStorePresenter) {
        mContext = context;
        mCheckHasKeyStorePresenter = checkHasKeyStorePresenter;
    }

    @Override
    public void checkHasKeyStore() {
        new CheckAsyncTask(CheckHasKeyStoreModelImpl.this).execute();
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mCheckHasKeyStorePresenter = null;
    }

    private static Context getContext() {
        return mContext;
    }

    private static void showCheckPrepare() {
        if (mCheckHasKeyStorePresenter != null) {
            mCheckHasKeyStorePresenter.showCheckingHasKeyStore();
        }
    }

    private static void showCheckFail(String errMsg) {
        if (mCheckHasKeyStorePresenter != null) {
            mCheckHasKeyStorePresenter.showCheckHasKeyStoreFail(errMsg);
        }
    }

    private static void showHasKeyStore() {
        if (mCheckHasKeyStorePresenter != null) {
            mCheckHasKeyStorePresenter.showHasKeyStore();
        }
    }

    private static void showNotHas() {
        if (mCheckHasKeyStorePresenter != null) {
            mCheckHasKeyStorePresenter.showNotHasKeyStore();
        }
    }


    static class CheckAsyncTask extends AsyncTask<String, String, Boolean> {

        final WeakReference<CheckHasKeyStoreModelImpl> mCheckModelWeakReference;

        public CheckAsyncTask(CheckHasKeyStoreModelImpl checkModelImpl) {
            mCheckModelWeakReference = new WeakReference<>(checkModelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mCheckModelWeakReference.get() != null) {
                mCheckModelWeakReference.get().showCheckPrepare();
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (mCheckModelWeakReference.get() != null) {
                return KeyStoreUtil.checkHasRememberKeyStore(mCheckModelWeakReference.get().getContext(),
                        new KeyStoreUtil.CheckHasKeyStoreListener() {
                            @Override
                            public void onCheckFail(String errMsg) {
                                publishProgress(errMsg);
                            }
                        });
            } else {
                publishProgress("model is null");
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if (mCheckModelWeakReference.get() != null) {
                mCheckModelWeakReference.get().showCheckFail(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean has) {
            super.onPostExecute(has);

            if (mCheckModelWeakReference.get() != null) {
                if (has) {
                    mCheckModelWeakReference.get().showHasKeyStore();
                } else {
                    mCheckModelWeakReference.get().showNotHas();
                }

            }
        }
    }
}