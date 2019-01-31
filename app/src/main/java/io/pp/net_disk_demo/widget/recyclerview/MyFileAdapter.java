package io.pp.net_disk_demo.widget.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.DeletingInfo;
import io.pp.net_disk_demo.data.FileInfo;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.util.FileUtil;
import io.pp.net_disk_demo.util.Util;

public class MyFileAdapter extends RecyclerView.Adapter<MyFileAdapter.MyFileItemHolder> {

    private static final String TAG = "MyFileAdapter";

    private Context mContext;

    private OnItemListener mOnItemListener = null;

    private HashMap<String, DeletingInfo> mDeletingInfoHashMap = null;
    private HashMap<String, String> mUploadFailedInfoHashMap = null;
    private HashMap<String, TaskInfo> mUploadingTaskHashMap = null;
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
    public void onBindViewHolder(@NonNull MyFileItemHolder myFileItemHolder, int i) {
        myFileItemHolder.setFooterItem(i == (getItemCount() - 1));

        FileInfo fileInfo = mMyFileList.get(i);
        if (fileInfo != null) {

            if ((mUploadingTaskHashMap != null && mUploadingTaskHashMap.containsKey(fileInfo.getBucketName() + "/" + fileInfo.getName())) ||
                    mUploadFailedInfoHashMap != null && mUploadFailedInfoHashMap.containsKey(fileInfo.getBucketName() + fileInfo.getName())) {
                myFileItemHolder.setInVisible(true);
            } else {
                myFileItemHolder.setInVisible(false);

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
    }

    @Override
    public int getItemCount() {
        if (mMyFileList != null) {
            return mMyFileList.size();
        }

        return 0;
    }

    public void refreshUploadFailedHashMap(HashMap<String, String> uploadFailedInfoHashMap) {
        mUploadFailedInfoHashMap = uploadFailedInfoHashMap;
    }

    public void refreshDeletingInfoHashMap(HashMap<String, DeletingInfo> deletingInfoHashMap) {
        mDeletingInfoHashMap = deletingInfoHashMap;
    }

    public void refreshUploadingTaskHashMap(HashMap<String, TaskInfo> uploadingTaskHashMap) {
        mUploadingTaskHashMap = uploadingTaskHashMap;
    }

    public void refreshFileList(ArrayList<FileInfo> myFileList) {
        mMyFileList = myFileList;

        notifyDataSetChanged();
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        mOnItemListener = onItemListener;
    }

    public String getFileHash(int position) {
        return mMyFileList.get(position).getName();
    }

    public String getFileInfoBucket(int position) {
        return mMyFileList.get(position).getBucketName();
    }

    public String getFileInfoKey(int position) {
        return mMyFileList.get(position).getName();
    }

    public FileInfo getFileInfo(int position) {
        return mMyFileList.get(position);
    }

    public class MyFileItemHolder extends RecyclerView.ViewHolder {
        private Context mContext = null;

        private View mItemLayout = null;

        private LinearLayout mContentLayout = null;
        private View mBottomLine = null;
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

            mContentLayout = mItemLayout.findViewById(R.id.content_layout);
            mBottomLine = mItemLayout.findViewById(R.id.bottom_line);

            mFooterLayout = mItemLayout.findViewById(R.id.footer_layout);
            mFooterLayout.setVisibility(View.GONE);

            mFileIv = mItemLayout.findViewById(R.id.file_iv);
            mFileStatusIv = mItemLayout.findViewById(R.id.file_status_iv);
            mFileNameTv = mItemLayout.findViewById(R.id.file_name_tv);
            mFileModifiedDateTv = mItemLayout.findViewById(R.id.file_modifieddate_tv);
        }

        public void setFileName(String bucketKey) {
            String fileName = "";
            if (!TextUtils.isEmpty(bucketKey)) {
                if (bucketKey.startsWith("/")) {
                    fileName = bucketKey.replaceFirst("/", "");
                } else {
                    fileName = bucketKey;
                }
            }

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
                        Util.dp2px(mContext, 75)));
                mFooterLayout.setVisibility(View.GONE);
            }
        }

        public void setInVisible(boolean inVisible) {
            if (inVisible) {
                mContentLayout.setVisibility(View.GONE);
                mBottomLine.setVisibility(View.GONE);

                if (mFooterLayout.getVisibility() == View.GONE) {
                    mItemLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            Util.dp2px(mContext, 0)));
                } else {
                    mItemLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            Util.dp2px(mContext, 75)));
                }
            } else {
                mContentLayout.setVisibility(View.VISIBLE);
                mBottomLine.setVisibility(View.VISIBLE);

                if (mFooterLayout.getVisibility() == View.GONE) {
                    mItemLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            Util.dp2px(mContext, 75)));
                } else {
                    mItemLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            Util.dp2px(mContext, 150)));
                }
            }
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}