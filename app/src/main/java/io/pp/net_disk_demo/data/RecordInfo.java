package io.pp.net_disk_demo.data;

public class RecordInfo {

    private String mFileName;
    private long mRecordDate;
    private double mRecordCost;
    private boolean mIsFund;

    public RecordInfo(String fileName, long recordDate, double recordCost, boolean isFund){
        mFileName = fileName;
        mRecordDate = recordDate;
        mRecordCost = recordCost;
        mIsFund = isFund;
    }

    public String getFileName() {
        return mFileName;
    }

    public long getRecordDate() {
        return mRecordDate;
    }

    public double getRecordCost() {
        return mRecordCost;
    }

    public boolean isFund() {
        return mIsFund;
    }
}