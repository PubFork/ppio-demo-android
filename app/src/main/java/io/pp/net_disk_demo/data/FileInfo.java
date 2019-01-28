package io.pp.net_disk_demo.data;

import java.io.Serializable;

public class FileInfo implements Serializable {

    private long mBlockCode = 0L;
    private String mBucketName = "";
    private String mFileName = "";
    private String mObjectHash = "";
    private long mLength = 0L;
    private String mType = "";
    private String mStatus = "";
    private String mCreatedTime = "";
    private String mModifiedTime = "";
    private String mExpiredTime = "";
    private String mShareCode = "";
    private boolean mIsBucket = false;
    private boolean mIsShare = false;
    private boolean mIsSecure = false;
    private boolean mIsDir = false;
    private String mStorageTime = "";
    private int mCopiedCount = 0;
    private String mGasPrice = "";
    private int mGasLimit = 0;
    private int mModifiedDate = 0;

    private String mDealInfo = "";

    public FileInfo(String bucketName) {
        mBucketName = bucketName;
        mIsBucket = true;
    }

    public FileInfo(long blockCode, String fileName, boolean isSecure, boolean isShare) {
        mBlockCode = blockCode;
        mFileName = fileName;
        mIsSecure = isSecure;
        mIsShare = isShare;
    }

    public long getBlockCode() {
        return mBlockCode;
    }

    public String getObjectHash() {
        return mObjectHash;
    }

    public String getBucketName() {
        return mBucketName;
    }

    public String getName() {
        return mFileName;
    }

    public long getLength() {
        return mLength;
    }

    public String getType() {
        return mType;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getCreatedTime() {
        return mCreatedTime;
    }

    public String getModifiedTime() {
        return mModifiedTime;
    }

    public String getExpiredTime() {
        return mExpiredTime;
    }

    public String getShareCode() {
        return mShareCode;
    }

    public boolean isBucket() {
        return mIsBucket;
    }

    public boolean isShare() {
        return mIsShare;
    }

    public boolean isSecure() {
        return mIsSecure;
    }

    public boolean isDir() {
        return mIsDir;
    }

    public String getStorageTime() {
        return mStorageTime;
    }

    public int getCopiedCount() {
        return mCopiedCount;
    }

    public String getGasPrice() {
        return mGasPrice;
    }

    public double getGasLimit() {
        return mGasLimit;
    }

    public int getModifiedDate() {
        return mModifiedDate;
    }

    public String getDealInfo() {
        return mDealInfo;
    }


    public void setBlockCode(long blockCode) {
        mBlockCode = blockCode;
    }

    public void setObjectHash(String objectHash) {
        mObjectHash = objectHash;
    }

    public void setBucketName(String bucketName) {
        mBucketName = bucketName;
    }

    public void setName(String fileName) {
        mFileName = fileName;
    }

    public void setLength(long length) {
        mLength = length;
    }

    public void setType(String type) {
        mType = type;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public void setCreatedTime(String createdTime) {
        mCreatedTime = createdTime;
    }

    public void setModifiedTime(String modifiedTime) {
        mModifiedTime = modifiedTime;
    }

    public void setExpiredTime(String expiredTime) {
        mExpiredTime = expiredTime;
    }

    public void setShareCode(String shareCode) {
        mShareCode = shareCode;
    }

    public void setBucket(boolean isBucket) {
        mIsBucket = isBucket;
    }

    public void setShare(boolean share) {
        mIsShare = share;
    }

    public void setSecure(boolean secure) {
        mIsSecure = secure;
    }

    public void setDir(boolean isDir) {
        mIsDir = isDir;
    }

    public void setStorageTime(String storageTime) {
        mStorageTime = storageTime;
    }

    public void setCopiedCount(int copiedCount) {
        mCopiedCount = copiedCount;
    }

    public void setGasPrice(int gasPrice) {
        mGasPrice = "" + gasPrice;
    }

    public void setGasLimit(int gasLimit) {
        mGasLimit = gasLimit;
    }

    public void setModifiedDate(int modifiedDate) {
        mModifiedDate = modifiedDate;
    }

    public void setDealInfo(String dealInfo) {
        mDealInfo = dealInfo;
    }
}