package io.pp.net_disk_demo.widget.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.FileInfo;

public class MyFileAdapter extends RecyclerView.Adapter<MyFileAdapter.MyFileItemHolder> {

    private static final String TAG = "MyFileAdapter";

    private Context mContext;

    private OnItemListener mOnItemListener = null;

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
        FileInfo fileInfo = mMyFileList.get(i);
        if (fileInfo != null) {
            myFileItemHolder.setFileName(fileInfo.getName());
            myFileItemHolder.setFileModifiedDate("" + fileInfo.getModifiedTime() + " " + fileInfo.getStatus());

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

        private View mItemLayout = null;
        private ImageView mFileIv = null;
        private ImageView mFileStatusIv = null;
        private TextView mFileNameTv = null;
        private TextView mFileModifiedDateTv = null;

        public MyFileItemHolder(@NonNull View itemView) {
            super(itemView);
        }

        public MyFileItemHolder(Context context, ViewGroup viewGroup) {
            super(LayoutInflater.from(context).inflate(R.layout.item_myfilelist_layout, viewGroup, false));

            mItemLayout = itemView;

            mItemLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            mFileIv = mItemLayout.findViewById(R.id.file_iv);
            mFileStatusIv = mItemLayout.findViewById(R.id.file_status_iv);
            mFileNameTv = mItemLayout.findViewById(R.id.file_name_tv);
            mFileModifiedDateTv = mItemLayout.findViewById(R.id.file_modifieddate_tv);
        }

        public void setFileName(String key) {
            if (!TextUtils.isEmpty(key)) {
                String fileName = key;
                if (key.startsWith("/")) {
                    fileName = key.replaceFirst("/", "");
                }

                mFileNameTv.setText(fileName);
            }
        }

        public void setFileModifiedDate(String modifiedDate) {
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
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}