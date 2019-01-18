package io.pp.net_disk_demo.data;

public class DownloadInfo {

    private String mBucket = "";
    private String mKey = "";
    private String mShareCode = "";
    private String mFile = "";
    private String mChiPrice = "";

    public void setBucket(String bucket) {
        mBucket = bucket;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public void setShareCode(String shareCode) {
        mShareCode = shareCode;
    }

    public void setFile(String file) {
        mFile = file;
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

    public String getShareCode() {
        return mShareCode;
    }

    public String getFile() {
        return mFile;
    }

    public String getChiPrice() {
        return mChiPrice;
    }
}