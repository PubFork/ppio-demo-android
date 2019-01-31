package io.pp.net_disk_demo.widget.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
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
            if (!TextUtils.isEmpty(destinationPath) && destinationPath.startsWith(Constant.PPIO_File.DOWNLOAD_DIR + "/")) {
                downloadTaskItemHolder.setFileName(destinationPath.replace(Constant.PPIO_File.DOWNLOAD_DIR + "/", ""));
            }

            downloadTaskItemHolder.setStatus(taskInfo.getState(), taskInfo.getError());

            double progress2digits = 0;
            if (taskInfo.getTotal() != 0) {
                //progress2digits = new BigDecimal((double) taskInfo.getFinished() / taskInfo.getTotal()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                progress2digits = new BigDecimal(taskInfo.getProgress()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            }
            downloadTaskItemHolder.setProgress(progress2digits);

            downloadTaskItemHolder.setPauseResume(taskInfo.getId(), taskInfo.getState());
            downloadTaskItemHolder.setDelete(taskInfo);

            if (Constant.TaskType.GET.equals(taskInfo.getType()) &&
                    Constant.TaskState.FINISHED.equals(taskInfo.getState())) {
                downloadTaskItemHolder.setOnEnterDownloadedDirectoryListener(taskInfo.getTo());
            }
        }

        downloadTaskItemHolder.setFooterItem(i == (getItemCount() - 1));
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

        public DownloadTaskItemHolder(@NonNull View itemView) {
            super(itemView);
        }

        public DownloadTaskItemHolder(Context context, ViewGroup viewGroup) {
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

            mUploadingIv.setBackgroundResource(R.mipmap.downloading);

            mDefaultStatusTvTextColor = mTaskStatusTv.getCurrentTextColor();
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

        public void setDelete(final TaskInfo taskInfo) {
            View.OnClickListener deleteClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDownloadTaskItemClickListener != null) {
                        mDownloadTaskItemClickListener.onDelete(taskInfo);
                    }
                }
            };

            mDeleteIv.setOnClickListener(deleteClickListener);
            mTaskDeleteLayout.setOnClickListener(deleteClickListener);
        }

        public void setOnEnterDownloadedDirectoryListener(String downloadedPath) {
            mItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    try {
                        File downloadedFile = new File(downloadedPath);
                        File parentFile = downloadedFile.getParentFile();

                        if (Build.VERSION.SDK_INT >= 24) {
                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());
                        }
                        Uri uri = Uri.fromFile(parentFile);

                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(uri, "*/*");
                        mContext.startActivity(intent);

                        Log.e(TAG, "uri = " + uri);
                    } catch (Exception e) {
                        Log.e(TAG, "start Activity for scan file failed!:" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
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

    public interface DownloadTaskItemClickListener {
        void onDelete(TaskInfo taskInfo);

        void onPause(String taskId);

        void onResume(String taskId);
    }
}