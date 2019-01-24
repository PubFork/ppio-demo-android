package io.pp.net_disk_demo.data;

public class OracleChiPrice {

    private String mStorageChiPrice;
    private String mDownloadChiPrice;

    public OracleChiPrice(String storageChiPrice, String downloadChiPrice) {
        mStorageChiPrice = storageChiPrice;
        mDownloadChiPrice = downloadChiPrice;
    }


    public String getStorageChiPrice() {
        return mStorageChiPrice;
    }

    public String getDownloadChiPrice() {
        return mDownloadChiPrice;
    }


    public void setStorageChiPrice(String storageChiPrice) {
        mStorageChiPrice = storageChiPrice;
    }

    public void setDownloadChiPrice(String downloadChiPrice) {
        mDownloadChiPrice = downloadChiPrice;
    }
}