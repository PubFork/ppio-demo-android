package io.pp.net_disk_demo.widget.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.pp.net_disk_demo.R;
import io.pp.net_disk_demo.data.RecordInfo;
import io.pp.net_disk_demo.util.Util;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordItemHolder> {

    private Context mContext;

    private ArrayList<RecordInfo> mRecordList;

    public RecordAdapter(Context context, ArrayList<RecordInfo> recordList) {
        mContext = context;
        mRecordList = recordList;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordItemHolder recordItemHolder, int i) {
        RecordInfo recordInfo = mRecordList.get(i);
        if (recordInfo != null) {
            recordItemHolder.setFileName(recordInfo.getFileName());
            recordItemHolder.setRecordDate("" + recordInfo.getRecordDate());
            recordItemHolder.setCost("" + recordInfo.getRecordCost());
            recordItemHolder.setIsFund(recordInfo.isFund());
        }
    }

    @NonNull
    @Override
    public RecordItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecordItemHolder(mContext, viewGroup);
    }

    @Override
    public int getItemCount() {
        if (mRecordList != null) {
            return mRecordList.size();
        }

        return 0;
    }

    public void setRecordList(ArrayList<RecordInfo> recordList) {
        mRecordList = recordList;

        notifyDataSetChanged();
    }

    public class RecordItemHolder extends RecyclerView.ViewHolder {

        private View mItemLayout = null;

        private TextView mFileNameTv = null;
        private TextView mRecordDateTv = null;
        private TextView mRecordCostTv = null;
        private TextView mIsFundTv = null;

        public RecordItemHolder(@NonNull View itemView) {
            super(itemView);
        }

        public RecordItemHolder(Context context, ViewGroup viewGroup) {
            super(LayoutInflater.from(context).inflate(R.layout.item_recordlist_layout, viewGroup, false));

            mItemLayout = itemView;

            ViewGroup.LayoutParams layoutParams = mItemLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = Util.dp2px(mContext, 62);

            mItemLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    Util.dp2px(mContext, 62)));

            mFileNameTv =  mItemLayout.findViewById(R.id.record_file_name_tv);
            mRecordDateTv =  mItemLayout.findViewById(R.id.record_date_tv);
            mRecordCostTv =  mItemLayout.findViewById(R.id.record_cost_tv);
            mIsFundTv =  mItemLayout.findViewById(R.id.record_fund_tv);
        }

        public void setFileName(String fileName) {
            mFileNameTv.setText(fileName);
        }

        public void setRecordDate(String modifiedDate) {
            mRecordDateTv.setText(modifiedDate);
        }

        public void setCost(String cost) {
            mRecordCostTv.setText(cost);
        }

        public void setIsFund(boolean isFund) {
            if (isFund) {
                mRecordCostTv.setTextColor(0xFFFF9E0E);
                mIsFundTv.setText("PPCoin(Fund)");
            } else {
                mRecordCostTv.setTextColor(0xFF303133);
                mIsFundTv.setText("PPCoin");
            }
        }
    }
}
