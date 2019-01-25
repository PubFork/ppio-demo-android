package io.pp.net_disk_demo.data;

public class RecordInfo {

    private String mItem;
    private String mRecordDate;
    private long mRecordCostWei;

    public RecordInfo(String item, String recordDate, long recordCost) {
        mItem = item;
        mRecordDate = recordDate;
        mRecordCostWei = recordCost;
    }

    public void setItem(String item) {
        mItem = item;
    }

    public void setRecordDate(String recordDate) {
        mRecordDate = recordDate;
    }

    public void setRecordCost(long costWei) {
        mRecordCostWei = costWei;
    }

    public String getItem() {
        return mItem;
    }

    public String getRecordDate() {
        return mRecordDate;
    }

    public long getRecordCost() {
        return mRecordCostWei;
    }
}