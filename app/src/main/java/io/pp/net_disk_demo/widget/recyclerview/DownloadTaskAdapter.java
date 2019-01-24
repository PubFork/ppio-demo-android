package io.pp.net_disk_demo.widget.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.TaskInfo;
import io.pp.net_disk_demo.util.Util;

public class DownloadTaskAdapter extends RecyclerView.Adapter<DownloadTaskAdapter.DownloadTaskItemHolder> {

    private static final String TAG = "DownloadTaskAdapter";

    private Context mContext;
    private ArrayList<TaskInfo> mDownloadTaskList;

    private DownloadTaskItemClickListener mDownloadTaskItemClickListener;

    public DownloadTaskAdapter(Context context, ArrayList<TaskInfo> downloadTaskList) {
        mContext = context;
        mDownloadTaskList = downloadTaskList;
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadTaskAdapter.DownloadTaskItemHolder downloadTaskItemHolder, int i) {
        TaskInfo taskInfo = mDownloadTaskList.get(i);

        if (taskInfo != null) {
            String destinationPath = taskInfo.getTo();
            if (!TextUtils.isEmpty(destinationPath)) {
                downloadTaskItemHolder.setFileName(destinationPath.replace(Environment.getExternalStorageDirectory().getPath(), ""));
            }

            downloadTaskItemHolder.setStatus(taskInfo.getState(), taskInfo.getError());

            double progress2digits = 0;
            if (taskInfo.getTotal() != 0) {
                progress2digits = new BigDecimal((double) taskInfo.getFinished() / taskInfo.getTotal()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            }
            downloadTaskItemHolder.setProgress(progress2digits);

            downloadTaskItemHolder.setPauseResume(taskInfo.getId(), taskInfo.getState());
            downloadTaskItemHolder.setDelete(taskInfo.getId());
        }
    }

    @NonNull
    @Override
    public DownloadTaskAdapter.DownloadTaskItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DownloadTaskAdapter.DownloadTaskItemHolder(mContext, viewGroup);
    }

    @Override
    public int getItemCount() {
        if (mDownloadTaskList != null) {
            return mDownloadTaskList.size();
        }

        return 0;
    }

    public void refreshUploadingList(ArrayList<TaskInfo> uploadTaskList) {
        mDownloadTaskList = uploadTaskList;

        notifyDataSetChanged();
    }

    public void setDownloadTaskItemClickListener(DownloadTaskItemClickListener downloadTaskItemClickListener) {
        mDownloadTaskItemClickListener = downloadTaskItemClickListener;
    }

    public class DownloadTaskItemHolder extends RecyclerView.ViewHolder {

        private View mItemLayout = null;

        private ImageView mUploadingIv = null;
        private TextView mFileNameTv = null;
        private ProgressBar mProgressBar = null;

        private LinearLayout mTaskStatusLayout = null;
        private ImageView mTaskErrorIv = null;
        private TextView mTaskStatusTv = null;

        private RelativeLayout mTaskPauseResumeLayout = null;
        private ImageView mTaskPauseResumeIv = null;

        private RelativeLayout mTaskDeleteLayout = null;
        private ImageView mDeleteIv = null;

        private int mDefaultStatusTvTextColor;

        public DownloadTaskItemHolder(@NonNull View itemView) {
            super(itemView);
        }

        public DownloadTaskItemHolder(Context context, ViewGroup viewGroup) {
            super(LayoutInflater.from(context).inflate(R.layout.item_uploading_downloading_layout, viewGroup, false));

            mItemLayout = itemView;

            mItemLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            mUploadingIv = mItemLayout.findViewById(R.id.uploading_downloading_iv);
            mFileNameTv = mItemLayout.findViewById(R.id.uploading_downloading_tv);
            mProgressBar = mItemLayout.findViewById(R.id.uploading_downloading_progress);
            mTaskStatusLayout = mItemLayout.findViewById(R.id.task_status_layout);
            mTaskErrorIv = mItemLayout.findViewById(R.id.task_error_iv);
            mTaskStatusTv = mItemLayout.findViewById(R.id.task_status_tv);
            mTaskPauseResumeLayout = mItemLayout.findViewById(R.id.task_pause_resume_layout);
            mTaskPauseResumeIv = mItemLayout.findViewById(R.id.task_pause_resume_btn);
            mTaskDeleteLayout = mItemLayout.findViewById(R.id.task_delete_layout);
            mDeleteIv = mItemLayout.findViewById(R.id.task_delete_iv);

            mUploadingIv.setBackgroundResource(R.mipmap.downloading);

            mDefaultStatusTvTextColor = mTaskStatusTv.getCurrentTextColor();
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

        public void setStatus(String status, String error) {
            mTaskPauseResumeLayout.setVisibility(View.INVISIBLE);
            mTaskStatusTv.setTextColor(mDefaultStatusTvTextColor);

            if (Constant.TaskState.PENDING.equals(status)) {
                mProgressBar.setVisibility(View.GONE);
                mTaskStatusLayout.setVisibility(View.VISIBLE);
                mTaskErrorIv.setVisibility(View.GONE);
                mTaskStatusTv.setText("Pending...");
            } else if (Constant.TaskState.RUNNING.equals(status)) {
                mProgressBar.setVisibility(View.VISIBLE);
                mTaskStatusLayout.setVisibility(View.GONE);
                mTaskPauseResumeLayout.setVisibility(View.VISIBLE);
            } else if (Constant.TaskState.PAUSED.equals(status)) {
                mProgressBar.setVisibility(View.VISIBLE);
                mTaskStatusLayout.setVisibility(View.GONE);
                mTaskPauseResumeLayout.setVisibility(View.VISIBLE);
            } else if (Constant.TaskState.FINISHED.equals(status)) {
                mProgressBar.setVisibility(View.VISIBLE);
                mTaskStatusLayout.setVisibility(View.GONE);
            } else if (Constant.TaskState.ERROR.equals(status)) {
                mProgressBar.setVisibility(View.GONE);
                mTaskStatusLayout.setVisibility(View.VISIBLE);
                mTaskErrorIv.setVisibility(View.VISIBLE);
                mTaskStatusTv.setText(error);
                mTaskStatusTv.setTextColor(Color.RED);
            }
        }

        public void setProgress(double progress) {
            mProgressBar.setMax(100);
            mProgressBar.setProgress((int) (100 * progress));
        }

        public void setPauseResume(final String taskId, final String state) {
            if (Constant.TaskState.RUNNING.equals(state)) {
                mTaskPauseResumeIv.setBackgroundResource(R.mipmap.task_pause_btn);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTaskPauseResumeIv.getLayoutParams();
                layoutParams.width = Util.dp2px(mContext, 12);
                layoutParams.height = Util.dp2px(mContext, 13);
                mTaskPauseResumeIv.setLayoutParams(layoutParams);
            } else if (Constant.TaskState.PAUSED.equals(state)) {
                mTaskPauseResumeIv.setBackgroundResource(R.mipmap.task_resume_btn);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTaskPauseResumeIv.getLayoutParams();
                layoutParams.width = Util.dp2px(mContext, 13);
                layoutParams.height = Util.dp2px(mContext, 14);
                mTaskPauseResumeIv.setLayoutParams(layoutParams);
            }

            View.OnClickListener taskPauseResumeOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDownloadTaskItemClickListener != null) {
                        if (Constant.TaskState.RUNNING.equals(state)) {
                            mDownloadTaskItemClickListener.onPause(taskId);
                        } else if (Constant.TaskState.PAUSED.equals(state)) {
                            mDownloadTaskItemClickListener.onResume(taskId);
                        }
                    }
                }
            };

            mTaskPauseResumeIv.setOnClickListener(taskPauseResumeOnClickListener);
            mTaskPauseResumeLayout.setOnClickListener(taskPauseResumeOnClickListener);
        }

        public void setDelete(final String taskId) {
            View.OnClickListener deleteClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDownloadTaskItemClickListener != null) {
                        mDownloadTaskItemClickListener.onDelete(taskId);
                    }
                }
            };

            mDeleteIv.setOnClickListener(deleteClickListener);
            mTaskDeleteLayout.setOnClickListener(deleteClickListener);
        }
    }

    public interface DownloadTaskItemClickListener {
        void onDelete(String taskId);

        void onPause(String taskId);

        void onResume(String taskId);
    }
}