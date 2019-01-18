package io.pp.net_disk_demo.data;

public class RenewInfo {

    private String mBucket = "";
    private String mKey = "";
    private boolean mIsSecure = false;
    private String mExpiredTime = "";
    private int mCopiesCount = 0;
    private String mChiPrice = "0";

    public RenewInfo() {

    }

    public void setBucket(String bucketName) {
        mBucket = bucketName;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public void setExpiredTime(String expiredTime) {
        mExpiredTime = expiredTime;
    }

    public void setCopiesCount(int copies) {
        mCopiesCount = copies;
    }

    public void setChiPrice(String chiPrice) {
        mChiPrice = chiPrice;
    }


    public String getBucket() {
        return mBucket;
    }

    public String getKey() {
        return mKey;
    }

    public String getExpiredTime() {
        return mExpiredTime;
    }

    public int getCopiesCount() {
        return mCopiesCount;
    }

    public String getChiPrice() {
        return mChiPrice;
    }

    public String getFileName() {
        return mBucket + mKey;
    }
}