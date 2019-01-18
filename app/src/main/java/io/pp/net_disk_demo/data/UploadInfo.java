package io.pp.net_disk_demo.data;

public class UploadInfo {

    private String mBucket = "";
    private String mKey = "";
    private String mFileName = "";
    private String mFile = "";
    private long mFileSize = 0L;
    private boolean mIsSecure = true;
    private String mExpiredTime = "";
    private int mCopiesCount;
    private String mChiPrice = "";


    public UploadInfo() {
        mCopiesCount = 1;
    }

    public void setBucket(String bucket) {
        this.mBucket = bucket;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    public void setFile(String file) {
        this.mFile = file;
    }

    public void setFileSize(long fileSize) {
        mFileSize = fileSize;
    }

    public void setSecure(boolean secure) {
        mIsSecure = secure;
    }

    public void setExpiredTime(String expiredTime) {
        this.mExpiredTime = expiredTime;
    }

    public void setCopiesCount(int copiesCount) {
        this.mCopiesCount = copiesCount;
    }

    public void setChiPrice(String chiPrice) {
        this.mChiPrice = chiPrice;
    }


    public String getBucket() {
        return mBucket;
    }

    public String getKey() {
        return mKey;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getFile() {
        return mFile;
    }

    public long getFileSize() {
        return mFileSize;
    }

    public boolean isSecure() {
        return mIsSecure;
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
}