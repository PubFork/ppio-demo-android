package io.pp.net_disk_demo.mvp.model.modelimpl;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.data.DeletingInfo;
import io.pp.net_disk_demo.data.FileInfo;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.database.AccountDatabaseManager;
import io.pp.net_disk_demo.mvp.model.PpioDataModel;
import io.pp.net_disk_demo.mvp.presenter.PpioDataPresenter;
import io.pp.net_disk_demo.ppio.PossUtil;
import io.pp.net_disk_demo.threadpool.CancelFixedThreadPool;
import poss.Progress;

public class PpioDataModelImpl implements PpioDataModel {

    private static final String TAG = "PpioDataModelImpl";

    private Context mContext;

    private PpioDataPresenter mPpioDataPresenter;

    private CancelFixedThreadPool mRefreshMyFilePool;

    public PpioDataModelImpl(Context context, PpioDataPresenter ppioDataPresenter) {
        mContext = context;

        mPpioDataPresenter = ppioDataPresenter;

        mRefreshMyFilePool = new CancelFixedThreadPool(1);
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public void link() {
        new LinkTask(PpioDataModelImpl.this).execute();
    }

    private void showLinking() {
        if (mPpioDataPresenter != null) {
            mPpioDataPresenter.showLinking();
        }
    }

    private void showLinkFail(String failMessage) {
        if (mPpioDataPresenter != null) {
            mPpioDataPresenter.showLinkFail(failMessage);
        }
    }

    private void stopShowLinking() {
        if (mPpioDataPresenter != null) {
            mPpioDataPresenter.stopShowLinking();
        }
    }

    private void showNotLogIn() {
        if (mPpioDataPresenter != null) {
            mPpioDataPresenter.showNotLogIn();
        }
    }

    @Override
    public void refreshMyFileList(HashMap<String, DeletingInfo> deletingInfoHashMap, HashMap<String, String> uploadFailedInfoHashMap) {
        Log.e(TAG, "refreshMyFileList()");

        mRefreshMyFilePool.execute(new RefreshMyFileRunnable(deletingInfoHashMap, uploadFailedInfoHashMap, PpioDataModelImpl.this));
    }

    private void showRefreshingMyFileList() {
        if (mPpioDataPresenter != null) {
            mPpioDataPresenter.showRefreshingMyFileList();
        }
    }

    private void showRefreshMyFileListFail(String failStr) {
        if (mPpioDataPresenter != null) {
            mPpioDataPresenter.showRefreshAllFileListFail(failStr);
        }
    }

    public void showRefreshMyFilListSucceed(HashMap<String, DeletingInfo> deletingInfoHashMap, ArrayList<FileInfo> fileInfoList) {
        if (mPpioDataPresenter != null) {
            mPpioDataPresenter.showAllFileList(deletingInfoHashMap, fileInfoList);
        }
    }

    @Override
    public void onDestroy() {
        mPpioDataPresenter = null;
    }

    static class LinkTask extends AsyncTask<String, String, Boolean> {

        final WeakReference<PpioDataModelImpl> mModelImplWeakReference;

        public LinkTask(PpioDataModelImpl modelImpl) {
            mModelImplWeakReference = new WeakReference<>(modelImpl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showLinking();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (mModelImplWeakReference.get() != null) {
                boolean hasLogin = AccountDatabaseManager.hasLogin(mModelImplWeakReference.get().getContext(), new AccountDatabaseManager.CheckHasLoginListener() {
                    @Override
                    public void onCheckFail(String failMessage) {
                        publishProgress(failMessage);
                    }
                });

                if (hasLogin) {
                    if (PossUtil.getUser() == null) {
                        HashMap<String, String> privateParams = AccountDatabaseManager.getPrivateParams(mModelImplWeakReference.get().getContext());
                        PossUtil.setMnemonicStr(privateParams.get(Constant.Data.MNEMONIC));
                        PossUtil.setPasswordStr(privateParams.get(Constant.Data.PASSWORD));
                        PossUtil.setPrivateKeyStr(privateParams.get(Constant.Data.PRIVATE_KEY));
                        PossUtil.setAddressStr(privateParams.get(Constant.Data.ADDRESS));

                        hasLogin = PossUtil.logIn(PossUtil.getPrivateKeyStr(),
                                new PossUtil.LogInListener() {
                                    @Override
                                    public void onLogInError(String errMsg) {
                                        publishProgress(errMsg);
                                    }
                                });
                    }
                }

                return hasLogin;
            }

            return false;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showLinkFail(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean hasLogin) {
            super.onPostExecute(hasLogin);

            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().stopShowLinking();
                if (!hasLogin) {
                    mModelImplWeakReference.get().showNotLogIn();
                }
            }
        }
    }

    static class RefreshMyFileRunnable implements Runnable {
        final HashMap<String, DeletingInfo> mDeletingInfoHashMap;
        final HashMap<String, String> mUploadFailedInfoHashMap;
        final WeakReference<PpioDataModelImpl> mModelImplWeakReference;

        public RefreshMyFileRunnable(HashMap<String, DeletingInfo> deletingInfoHashMap,
                                     HashMap<String, String> uploadFailedInfoHashMap,
                                     PpioDataModelImpl ppioDataModel) {
            mDeletingInfoHashMap = deletingInfoHashMap;
            mUploadFailedInfoHashMap = uploadFailedInfoHashMap;
            mModelImplWeakReference = new WeakReference<>(ppioDataModel);
        }

        @Override
        public void run() {
            if (mModelImplWeakReference.get() != null) {
                mModelImplWeakReference.get().showRefreshingMyFileList();

                ArrayList<FileInfo> bucketList = PossUtil.listBucket(new PossUtil.ListBucketListener() {
                    @Override
                    public void onListBucketError(String errMsg) {
                        if (mModelImplWeakReference.get() != null) {
                            mModelImplWeakReference.get().showRefreshMyFileListFail(errMsg);
                        }
                    }
                });

                if (bucketList.size() == 0) {
                    PossUtil.createBucket(Constant.Data.DEFAULT_BUCKET, new PossUtil.CreateBucketListener() {
                        @Override
                        public void onCreateBucketError(String errMsg) {
                            if (mModelImplWeakReference.get() != null) {
                                mModelImplWeakReference.get().showRefreshMyFileListFail(errMsg);
                            }
                        }
                    });
                }

                Log.e(TAG, "RefreshMyFileRunnable() " + mDeletingInfoHashMap.size());

                HashMap<String, DeletingInfo> deletingInfoHashMap = new HashMap<>();
                if (mDeletingInfoHashMap != null) {
                    for (Map.Entry entry : mDeletingInfoHashMap.entrySet()) {
                        DeletingInfo deletingInfo = (DeletingInfo) entry.getValue();

                        Log.e(TAG, "RefreshMyFileRunnable() " + deletingInfo.getName());

                        Progress progress = PossUtil.getTaskProgress(deletingInfo.getTaskId());

                        Log.e(TAG, "RefreshMyFileRunnable() " + progress.getFinishedBytes());
                        Log.e(TAG, "RefreshMyFileRunnable() " + progress.getTotalBytes());
                        Log.e(TAG, "RefreshMyFileRunnable() " + progress.getJobState());

                        if (!Constant.ProgressState.FINISHED.equals(progress.getJobState())) {
                            if (progress.getTotalBytes() != 0l) {
                                deletingInfo.setProgress((double) progress.getFinishedBytes() / progress.getTotalBytes());
                                deletingInfo.setState(progress.getJobState());
                            }

                            deletingInfoHashMap.put(deletingInfo.getName(), deletingInfo);
                        }
                    }
                }

                ArrayList<FileInfo> myFileList = PossUtil.listObject(Constant.Data.DEFAULT_BUCKET, new PossUtil.ListObjectListener() {
                    @Override
                    public void onListObjectError(String errMsg) {
                        if (mModelImplWeakReference.get() != null) {
                            mModelImplWeakReference.get().showRefreshMyFileListFail(errMsg);
                        }
                    }
                });

                HashMap<String, TaskInfo> uploadingTaskHashMap = PossUtil.listUploadingTask(new PossUtil.ListTaskListener() {
                    @Override
                    public void onListTaskError(String errMsg) {
                        if (mModelImplWeakReference.get() != null) {
                            mModelImplWeakReference.get().showRefreshMyFileListFail(errMsg);
                        }
                    }
                });

                ArrayList<FileInfo> mFileList = new ArrayList<FileInfo>();
                for(int i = 0;i<myFileList.size();i++) {
                    FileInfo fileInfo = myFileList.get(i);
                    if (!uploadingTaskHashMap.containsKey(fileInfo.getBucketName() + "/" + fileInfo.getName()) &&
                            !mUploadFailedInfoHashMap.containsKey(fileInfo.getBucketName() + fileInfo.getName())) {
                        mFileList.add(fileInfo);
                    }
                }

                if (mModelImplWeakReference.get() != null) {
                    mModelImplWeakReference.get().showRefreshMyFilListSucceed(deletingInfoHashMap, mFileList);
                }
            }
        }
    }
}