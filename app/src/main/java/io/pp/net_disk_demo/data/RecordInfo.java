package io.pp.net_disk_demo.data;

public class RecordInfo {

    private String mItem;
    private String mRecordDate;
    private long mRecordCostWei;
    private boolean mIsincome = false;

    public RecordInfo(String item, String recordDate, long recordCost, boolean isIncome) {
        mItem = item;
        mRecordDate = recordDate;
        mRecordCostWei = recordCost;
        mIsincome = isIncome;
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

    public void setIsincome(boolean isIncome) {
        mIsincome = isIncome;
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

    public boolean isIncome() {
        return mIsincome;
    }
}