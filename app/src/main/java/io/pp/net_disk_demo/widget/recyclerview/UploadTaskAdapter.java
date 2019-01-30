package io.pp.net_disk_demo.widget.recyclerview;

import android.content.Context;
import android.graphics.Color;
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

public class UploadTaskAdapter extends RecyclerView.Adapter<UploadTaskAdapter.UploadTaskItemHolder> {

    private static final String TAG = "UploadTaskAdapter";

    private Context mContext;
    private ArrayList<TaskInfo> mUploadTaskList;

    private UploadTaskItemClickListener mUploadTaskItemClickListener;

    public UploadTaskAdapter(Context context, ArrayList<TaskInfo> uploadTaskList) {
        mContext = context;

        mUploadTaskList = uploadTaskList;
    }

    @Override
    public void onBindViewHolder(@NonNull UploadTaskItemHolder uploadTaskItemHolder, int i) {
        TaskInfo taskInfo = mUploadTaskList.get(i);

        if (taskInfo != null) {
            uploadTaskItemHolder.setFileName(taskInfo.getTo());
            uploadTaskItemHolder.setState(taskInfo.getState(), taskInfo.getError());
            double progress2digits = 0;
            if (taskInfo.getTotal() != 0) {
                //progress2digits = new BigDecimal((double) taskInfo.getFinished() / taskInfo.getTotal()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                progress2digits = new BigDecimal(taskInfo.getProgress()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            }

            uploadTaskItemHolder.setProgress(progress2digits);

            uploadTaskItemHolder.setPauseResume(taskInfo.getId(), taskInfo.getState());
            uploadTaskItemHolder.setDelete(taskInfo.getId());
        }

        uploadTaskItemHolder.setFooterItem(i == (getItemCount() - 1));
    }

    @NonNull
    @Override
    public UploadTaskItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new UploadTaskItemHolder(mContext, viewGroup);
    }

    @Override
    public int getItemCount() {
        if (mUploadTaskList != null) {
            return mUploadTaskList.size();
        }

        return 0;
    }

    public void refreshUploadingList(ArrayList<TaskInfo> uploadTaskList) {
        mUploadTaskList = uploadTaskList;

        notifyDataSetChanged();
    }

    public void setUploadTaskItemClickListener(UploadTaskItemClickListener uploadTaskItemClickListener) {
        mUploadTaskItemClickListener = uploadTaskItemClickListener;
    }

    public class UploadTaskItemHolder extends RecyclerView.ViewHolder {

        private Context mContext = null;

        private View mItemLayout = null;
        private LinearLayout mFooterLayout = null;

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

        public UploadTaskItemHolder(@NonNull View itemView) {
            super(itemView);
        }

        public UploadTaskItemHolder(Context context, ViewGroup viewGroup) {
            super(LayoutInflater.from(context).inflate(R.layout.item_uploading_downloading_layout, viewGroup, false));

            mContext = context;

            mItemLayout = itemView;
            mItemLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    Util.dp2px(context, 74)));

            mFooterLayout = mItemLayout.findViewById(R.id.footer_layout);
            mFooterLayout.setVisibility(View.GONE);

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

            mUploadingIv.setBackgroundResource(R.mipmap.uploading);

            mDefaultStatusTvTextColor = mTaskStatusTv.getCurrentTextColor();
        }

        public void setFileName(String bucketKey) {
            String fileName = "";
            if (!TextUtils.isEmpty(bucketKey)) {
                if (bucketKey.startsWith(Constant.Data.DEFAULT_BUCKET + "//")) {
                    fileName = bucketKey.replaceFirst(Constant.Data.DEFAULT_BUCKET + "//", "");
                } else {
                    fileName = bucketKey;
                }
            }
            mFileNameTv.setText(fileName);
        }

        public void setState(String status, String error) {
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
                mProgressBar.setVisibility(View.GONE);
                mTaskStatusLayout.setVisibility(View.VISIBLE);
                mTaskErrorIv.setVisibility(View.GONE);
                mTaskStatusTv.setText(status);
                mTaskStatusTv.setTextColor(mContext.getResources().getColor(R.color.account_background_blue));
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
                    if (mUploadTaskItemClickListener != null) {
                        if (Constant.TaskState.RUNNING.equals(state)) {
                            mUploadTaskItemClickListener.onPause(taskId);
                        } else if (Constant.TaskState.PAUSED.equals(state)) {
                            mUploadTaskItemClickListener.onResume(taskId);
                        }
                    }
                }
            };

            mTaskPauseResumeLayout.setOnClickListener(taskPauseResumeOnClickListener);
            mTaskPauseResumeIv.setOnClickListener(taskPauseResumeOnClickListener);
        }

        public void setDelete(final String taskId) {
            View.OnClickListener deleteOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUploadTaskItemClickListener != null) {
                        mUploadTaskItemClickListener.onDelete(taskId);
                    }
                }
            };

            mTaskDeleteLayout.setOnClickListener(deleteOnClickListener);
            mDeleteIv.setOnClickListener(deleteOnClickListener);
        }

        public void setFooterItem(boolean isFooter) {
            if (isFooter) {
                mItemLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        Util.dp2px(mContext, 151)));
                mFooterLayout.setVisibility(View.VISIBLE);
            } else {
                mItemLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        Util.dp2px(mContext, 74)));
                mFooterLayout.setVisibility(View.GONE);
            }
        }
    }

    public interface UploadTaskItemClickListener {
        void onDelete(String taskId);

        void onPause(String taskId);

        void onResume(String taskId);
    }
}