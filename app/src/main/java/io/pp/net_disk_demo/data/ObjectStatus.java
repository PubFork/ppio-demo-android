package io.pp.net_disk_demo.data;

public class ObjectStatus {

    private String mBucketStr;
    private String mKeyStr;
    private String mDetailStr;
    private long mLength;
    private String mCreatedTime;
    private String mExpiresTime;
    private String mState;


    public void setBucketStr(String bucketStr) {
        mBucketStr = bucketStr;
    }

    public String getBucketStr() {
        return mBucketStr;
    }

    public void setKeyStr(String keyStr) {
        mKeyStr = keyStr;
    }

    public void setLength(long length) {
        mLength = length;
    }

    public void setCreatedTime(String createdTime) {
        mCreatedTime = createdTime;
    }

    public void setExpiresTime(String expiresTime) {
        mExpiresTime = expiresTime;
    }

    public void setState(String state) {
        mState = state;
    }


    public String getKeyStr() {
        return mKeyStr;
    }

    public void setDetailStr(String detailStr) {
        mDetailStr = detailStr;
    }

    public String getDetailStr() {
        return mDetailStr;
    }

    public long getLength() {
        return mLength;
    }

    public String getCreatedTime() {
        return mCreatedTime;
    }

    public String getExpiresTime() {
        return mExpiresTime;
    }

    public String getState() {
        return mState;
    }
}