package io.pp.net_disk_demo.mvp.presenter.presenterimpl;

import android.content.Context;
import android.util.Log;

import io.pp.net_disk_demo.data.DeletingInfo;
import io.pp.net_disk_demo.mvp.model.DeleteModel;
import io.pp.net_disk_demo.mvp.model.modelimpl.DeleteModelImpl;
import io.pp.net_disk_demo.mvp.presenter.DeletePresenter;
import io.pp.net_disk_demo.mvp.view.DeleteView;

public class DeletePresenterImpl implements DeletePresenter {

    private static final String TAG = "DeletePresenterImpl";

    private Context mContext;
    private DeleteView mDeleteView;
    private DeleteModel mDeleteModel;

    public DeletePresenterImpl(Context context, DeleteView deleteView) {
        mContext = context;
        mDeleteView = deleteView;
        mDeleteModel = new DeleteModelImpl(mContext, DeletePresenterImpl.this);
    }

    @Override
    public void delete(String bucket, String key, String status) {
        if (mDeleteModel != null) {
            mDeleteModel.delete(bucket, key, status);
        }
    }

    @Override
    public void deleteSilently(String bucket, String key) {
        //
        Log.e(TAG, "++++++ deleteSilently() " + bucket + key);
        //
        if (mDeleteModel != null) {
            mDeleteModel.deleteSilently(bucket, key);
        }
    }

    @Override
    public void onDeletePrepare() {
        if (mDeleteView != null) {
            mDeleteView.onDeletePrepare();
        }
    }

    @Override
    public void onDeleteError(String errMsg) {
        if (mDeleteView != null) {
            mDeleteView.onDeleteError(errMsg);
        }
    }

    @Override
    public void onDeleteFinish(DeletingInfo deletingInfo) {
        if (mDeleteView != null) {
            mDeleteView.onDeleteFinish(deletingInfo);
        }
    }

    @Override
    public void onDeleteSilentlyFinish(String bucket, String key) {
        if (mDeleteView != null) {
            mDeleteView.onDeleteSilentlyFinish(bucket, key);
        }
    }

    @Override
    public void onDestroy() {
        if (mDeleteModel != null) {
            mDeleteModel.onDestroy();
        }

        mContext = null;
        mDeleteView = null;
        mDeleteModel = null;
    }
}
