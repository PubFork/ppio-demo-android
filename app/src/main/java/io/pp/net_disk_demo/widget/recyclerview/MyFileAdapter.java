package io.pp.net_disk_demo.widget.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.DeletingInfo;
import io.pp.net_disk_demo.data.FileInfo;
import io.pp.net_disk_demo.util.FileUtil;
import io.pp.net_disk_demo.util.Util;

public class MyFileAdapter extends RecyclerView.Adapter<MyFileAdapter.MyFileItemHolder> {

    private static final String TAG = "MyFileAdapter";

    private Context mContext;

    private OnItemListener mOnItemListener = null;

    private HashMap<String, DeletingInfo> mDeletingInfoHashMap = null;
    private ArrayList<FileInfo> mMyFileList = null;

    public MyFileAdapter(Context context) {
        super();

        mContext = context;
    }

    @NonNull
    @Override
    public MyFileItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyFileItemHolder(mContext, viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull MyFileItemHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            FileInfo fileInfo = mMyFileList.get(position);

            String stateStr = "expired: " + fileInfo.getExpiredTime().substring(0, 10);

            DeletingInfo deletingInfo = mDeletingInfoHashMap.get(fileInfo.getBucketName() + fileInfo.getName());
            if (Constant.ProgressState.ERROR.equals(deletingInfo.getState())) {
                stateStr = stateStr + "   <font color='#2297F3'>delete failed</font>";
            } else {
                double progress2digits = new BigDecimal(deletingInfo.getProgress() * 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                stateStr = stateStr + "   <font color='#2297F3'>deleting: " + progress2digits + "% </font>";
            }

            holder.setFileModifiedDate(Html.fromHtml(stateStr));
        } else {
            onBindViewHolder(holder, position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyFileItemHolder myFileItemHolder, int i) {
        myFileItemHolder.setFooterItem(i == (getItemCount() - 1));

        FileInfo fileInfo = mMyFileList.get(i);
        if (fileInfo != null) {
            myFileItemHolder.setFileName(fileInfo.getName());
            myFileItemHolder.setFileIcon(fileInfo.getName());

            String stateStr = "expired: " + fileInfo.getExpiredTime().substring(0, 10);

            if (mDeletingInfoHashMap != null && mDeletingInfoHashMap.containsKey(fileInfo.getBucketName() + fileInfo.getName())) {
                DeletingInfo deletingInfo = mDeletingInfoHashMap.get(fileInfo.getBucketName() + fileInfo.getName());
                if (Constant.ProgressState.ERROR.equals(deletingInfo.getState())) {
                    stateStr = stateStr + "   <font color='#2297F3'>delete failed</font>";
                } else {
                    double progress2digits = new BigDecimal(deletingInfo.getProgress() * 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    stateStr = stateStr + "   <font color='#2297F3'>deleting: " + progress2digits + "% </font>";
                }

            } else if (Constant.ObjectState.BID.equals(fileInfo.getStatus())) {
                stateStr = stateStr + "   <font color='#FF0000'>losting</font>";
            }

            //myFileItemHolder.setFileModifiedDate("expire: " + fileInfo.getExpiredTime() + " " + fileInfo.getStatus());
            myFileItemHolder.setFileModifiedDate(Html.fromHtml(stateStr));

            if (fileInfo.isSecure()) {
                myFileItemHolder.setEncrypt();
            } else if (fileInfo.isShare()) {
                myFileItemHolder.setShared();
            } else {
                myFileItemHolder.setNormal();
            }

            myFileItemHolder.setClickListener(mOnItemListener, i);
        }
    }

    @Override
    public int getItemCount() {
        if (mMyFileList != null) {
            return mMyFileList.size();
        }

        return 0;
    }

//    public void refreshDeletingInfoHashMap(HashMap<String, DeletingInfo> deletingInfoHashMap) {
//        mDeletingInfoHashMap = deletingInfoHashMap;
//    }

    public void refreshFileList(HashMap<String, DeletingInfo> deletingInfoHashMap, ArrayList<FileInfo> myFileList) {
        mDeletingInfoHashMap = deletingInfoHashMap;
        mMyFileList = myFileList;

        notifyDataSetChanged();
    }

    public void updateDeletingFileList(HashMap<String, DeletingInfo> deletingInfoHashMap, ArrayList<FileInfo> myFileList) {
        mDeletingInfoHashMap = deletingInfoHashMap;
        mMyFileList = myFileList;

        if (mMyFileList != null && mMyFileList.size() != 0) {
            for (int i = 0; i < mMyFileList.size(); i++) {
                if (mDeletingInfoHashMap.containsKey(mMyFileList.get(i).getBucketName() + mMyFileList.get(i).getName())) {
                    notifyItemChanged(i, mMyFileList.get(i));
                }
            }
        } else {
            notifyDataSetChanged();
        }
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        mOnItemListener = onItemListener;
    }

    public String getFileHash(int position) {
        if (mMyFileList != null && position < mMyFileList.size()) {
            return mMyFileList.get(position).getName();
        } else {
            return null;
        }
    }

    public String getFileInfoBucket(int position) {
        if (mMyFileList != null && position < mMyFileList.size()) {
            return mMyFileList.get(position).getBucketName();
        } else {
            return null;
        }
    }

    public String getFileInfoKey(int position) {
        if (mMyFileList != null && position < mMyFileList.size()) {
            return mMyFileList.get(position).getName();
        } else {
            return null;
        }
    }

    public FileInfo getFileInfo(int position) {
        if (mMyFileList != null && position < mMyFileList.size()) {
            return mMyFileList.get(position);
        } else {
            return null;
        }
    }

    public class MyFileItemHolder extends RecyclerView.ViewHolder {
        private Context mContext = null;

        private View mItemLayout = null;

        private LinearLayout mFooterLayout = null;
        private ImageView mFileIv = null;
        private ImageView mFileStatusIv = null;
        private TextView mFileNameTv = null;
        private TextView mFileModifiedDateTv = null;

        public MyFileItemHolder(@NonNull View itemView) {
            super(itemView);
        }

        public MyFileItemHolder(Context context, ViewGroup viewGroup) {
            super(LayoutInflater.from(context).inflate(R.layout.item_myfilelist_layout, viewGroup, false));

            mContext = context;

            mItemLayout = itemView;
            mItemLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    Util.dp2px(mContext, 75)));

            mFooterLayout = mItemLayout.findViewById(R.id.footer_layout);
            mFooterLayout.setVisibility(View.GONE);

            mFileIv = mItemLayout.findViewById(R.id.file_iv);
            mFileStatusIv = mItemLayout.findViewById(R.id.file_status_iv);
            mFileNameTv = mItemLayout.findViewById(R.id.file_name_tv);
            mFileModifiedDateTv = mItemLayout.findViewById(R.id.file_modifieddate_tv);
        }

        public void setFileName(String fileName) {
            mFileNameTv.setText(fileName);
        }

        public void setFileIcon(String fileName) {
            int fileType = FileUtil.checkFileTypeBySuffix(fileName);

            switch (fileType) {
                case FileUtil.TXT_NO_SUFFIX_FILE:
                    mFileIv.setBackgroundResource(R.mipmap.file);
                    break;

                case FileUtil.DOC_FILE:
                    mFileIv.setBackgroundResource(R.mipmap.file_doc);
                    break;

                case FileUtil.IMAGE_FILE:
                    mFileIv.setBackgroundResource(R.mipmap.file_image);
                    break;

                case FileUtil.PDF_FILE:
                    mFileIv.setBackgroundResource(R.mipmap.file_pdf);
                    break;

                case FileUtil.PPT_FILE:
                    mFileIv.setBackgroundResource(R.mipmap.file_ppt);
                    break;

                case FileUtil.AUDIO_FILE:
                    mFileIv.setBackgroundResource(R.mipmap.file_audio);
                    break;

                case FileUtil.VIDEO_FILE:
                    mFileIv.setBackgroundResource(R.mipmap.file_video);
                    break;

                case FileUtil.XLS_FILE:
                    mFileIv.setBackgroundResource(R.mipmap.file_xls);
                    break;

                case FileUtil.ZIP_FILE:
                    mFileIv.setBackgroundResource(R.mipmap.file_zip);
                    break;

                case FileUtil.UNKNOWN_FILE:
                    mFileIv.setBackgroundResource(R.mipmap.file_unknown);
                    break;

                default:
                    mFileIv.setBackgroundResource(R.mipmap.file_unknown);
                    break;
            }
        }

        public void setFileModifiedDate(Spanned modifiedDate) {
            mFileModifiedDateTv.setText(modifiedDate);
        }

        public void setNormal() {
            mFileStatusIv.setVisibility(View.INVISIBLE);
        }

        public void setEncrypt() {
            mFileStatusIv.setBackgroundResource(R.mipmap.jiami);
            mFileStatusIv.setVisibility(View.VISIBLE);
        }

        public void setShared() {
            mFileStatusIv.setBackgroundResource(R.mipmap.fenxiang);
            mFileStatusIv.setVisibility(View.VISIBLE);
        }

        public void setClickListener(final OnItemListener onItemListener, final int position) {
            mItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemListener != null) {
                        onItemListener.onItemClick(position);
                    }
                }
            });
        }

        public void setFooterItem(boolean isFooter) {
            if (isFooter) {
                mItemLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        Util.dp2px(mContext, 150)));
                mFooterLayout.setVisibility(View.VISIBLE);
            } else {
                mItemLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        Util.dp2px(mContext, 74)));
                mFooterLayout.setVisibility(View.GONE);
            }
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}