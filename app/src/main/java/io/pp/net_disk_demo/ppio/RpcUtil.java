package io.pp.net_disk_demo.ppio;

import android.util.Log;

import org.alexd.jsonrpc.JSONRPCHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.data.OracleChiPrice;

public class RpcUtil {

    private static final String TAG = "RpcUtil";
    //private static final String mRpcUrlStr = "http://ad04b30b910c311e9b71c02d26ce9aff-567092461.us-west-2.elb.amazonaws.com:18030/rpc";
    private static final String mRpcUrlStr = Constant.URL.RPC_URL;
    private static JSONRPCHttpClient mRpcClient;

    public static boolean peerAvailable() {
        initClient();

        try {
            Log.e(TAG, "peerAvailable() start...");
            Object peerAvailableResult = mRpcClient.call("peerAvailable", PossUtil.getAccount());
            Log.e(TAG, "peerAvailable() end...");
            Log.e(TAG, "peerAvailable() " + peerAvailableResult);
        } catch (Exception e) {
            Log.e(TAG, "peerAvailable() err = " + e.getMessage());
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public static String queryAccount(QueryAccountListener queryAccountListener) {
        initClient();

        String queryAccountResultStr;
        String balanceStr = "";

        try {
            Log.e(TAG, "queryAccount() start...");
            queryAccountResultStr = mRpcClient.callString("queryAccount", PossUtil.getAccount());
            Log.e(TAG, "queryAccount() end...");
            Log.e(TAG, "queryAccount() " + queryAccountResultStr);
            JSONObject queryAccountJSONObject = new JSONObject(queryAccountResultStr);
            balanceStr = queryAccountJSONObject.getString("Balance");

        } catch (Exception e) {
            if (queryAccountListener != null) {
                queryAccountListener.onQueryAccountError(e.getMessage());
            }
            Log.e(TAG, "queryAccount() err = " + e.getMessage());
            e.printStackTrace();
        }

        return balanceStr;
    }

    public static String getBalance(QueryAccountListener queryAccountListener) {
        initClient();

        String queryAccountResultStr;
        String balanceStr = "";

        try {
            Log.e(TAG, "address = " + PossUtil.getAccount());
            queryAccountResultStr = mRpcClient.callString("queryAccount", PossUtil.getAccount());
            JSONObject queryAccountJSONObject = new JSONObject(queryAccountResultStr);
            balanceStr = queryAccountJSONObject.getString("Balance");

        } catch (Exception e) {
            if (queryAccountListener != null) {
                queryAccountListener.onQueryAccountError(e.getMessage());
            }
            Log.e(TAG, "queryAccount() err = " + e.getMessage());
            e.printStackTrace();
        }

        return balanceStr;
    }

    public static String getFund(QueryAccountListener queryAccountListener) {
        initClient();

        String queryAccountResultStr;
        String fundStr = "";

        try {
            Log.e(TAG, "queryAccount() start...");
            queryAccountResultStr = mRpcClient.callString("queryAccount", PossUtil.getAccount());
            Log.e(TAG, "queryAccount() end...");
            Log.e(TAG, "queryAccount() " + queryAccountResultStr);
            JSONObject queryAccountJSONObject = new JSONObject(queryAccountResultStr);
            fundStr = queryAccountJSONObject.getString("LockedBalance");
        } catch (Exception e) {
            if (queryAccountListener != null) {
                queryAccountListener.onQueryAccountError(e.getMessage());
            }
            Log.e(TAG, "queryAccount() err = " + e.getMessage());
            e.printStackTrace();
        }

        return fundStr;
    }

    public static OracleChiPrice oracleChiPrice(QueryAccountListener queryAccountListener) {
        initClient();

        try {
            Log.e(TAG, "oracleChiPrice() start...");
            String oracleChiPriceResultStr = mRpcClient.callString("oracleChiPrice");
            Log.e(TAG, "oracleChiPrice() end...");
            Log.e(TAG, "oracleChiPrice() " + oracleChiPriceResultStr);

            JSONObject oracleChiPriceJSONObject = new JSONObject(oracleChiPriceResultStr);
            return new OracleChiPrice(oracleChiPriceJSONObject.getString("StorageChiPrice"),
                    oracleChiPriceJSONObject.getString("DownloadChiPrice"));
        } catch (Exception e) {
            if (queryAccountListener != null) {
                queryAccountListener.onQueryAccountError(e.getMessage());
            }
            Log.e(TAG, "oracleChiPrice() err = " + e.getMessage());
            e.printStackTrace();

            return null;
        }
    }

    public static int getStorageChi(int chunkSize, long duration, String chiPrice, QueryAccountListener queryAccountListener) {
        //> curl -X POST -H 'content-type:text/json;' --data '{"id":1,"jsonrpc":"2.0","method":"StorageChi","params":[{"chunkSize":1024,"duration":120,"chiPrice":"100"}]}' http://127.0.0.1:18030/rpc
        initClient();

        Log.e(TAG, "chunkSize = " + chunkSize + ", duration = " + duration + ", chiPrice = " + chiPrice);

        try {
            JSONObject requestJSONObject = new JSONObject();
            requestJSONObject.put("chunkSize", chunkSize);
            requestJSONObject.put("duration", duration);
            requestJSONObject.put("chiPrice", chiPrice);

            Log.e(TAG, "putObjectFunds start...");
            String putObjectFundsResultStr = mRpcClient.callString("storageChi", new JSONObject[]{
                    requestJSONObject
            });
            Log.e(TAG, "putObjectFunds end...");
            Log.e(TAG, "putObjectFunds " + putObjectFundsResultStr);

            //{"StorageFundsChi":"24","ServiceChi":"10"}

            JSONObject storageChiJSONObject = new JSONObject(putObjectFundsResultStr);

            int storageFundsChi = Integer.parseInt(storageChiJSONObject.getString("StorageFundsChi"));
            int serviceFundsChi = Integer.parseInt(storageChiJSONObject.getString("ServiceChi"));

            return (storageFundsChi + serviceFundsChi);
        } catch (Exception e) {
            if (queryAccountListener != null) {
                queryAccountListener.onQueryAccountError(e.getMessage());
            }

            Log.e(TAG, "putObjectFunds() err = " + e.getMessage());
            e.printStackTrace();

            return 0;
        }
    }

    public static int getDownloadChi(long chunkSize, String chiPrice, QueryAccountListener queryAccountListener) {
        //> curl -X POST -H 'content-type:text/json;' --data '{"id":1,"jsonrpc":"2.0","method":"DownloadChi","params":[{"chunkSize":1024,"chiPrice":"100"}]}' http://127.0.0.1:18030/rpc
        initClient();

        try {
            JSONObject requestJSONObject = new JSONObject();
            requestJSONObject.put("chunkSize", chunkSize);
            requestJSONObject.put("chiPrice", chiPrice);

            Log.e(TAG, "getObjectFunds start...");
            String putObjectFundsResultStr = mRpcClient.callString("downloadChi", new JSONObject[]{
                    requestJSONObject
            });
            Log.e(TAG, "getObjectFunds end...");
            Log.e(TAG, "getObjectFunds " + putObjectFundsResultStr);

            //{"DownloadFundsChi":"10","ServiceChi":"10"}

            JSONObject downloadChiJSONObject = new JSONObject(putObjectFundsResultStr);

            int downloadFundsChi = Integer.parseInt(downloadChiJSONObject.getString("DownloadFundsChi"));
            int serviceFundsChi = Integer.parseInt(downloadChiJSONObject.getString("ServiceChi"));

            return (downloadFundsChi + serviceFundsChi);
        } catch (Exception e) {
            if (queryAccountListener != null) {
                queryAccountListener.onQueryAccountError(e.getMessage());
            }
            Log.e(TAG, "getObjectFunds() err = " + e.getMessage());
            e.printStackTrace();

            return 0;
        }
    }

    public static String storageFunds() {
        initClient();

        try {
            //Object use JSONObject
            JSONObject storageFundsJSONObject = new JSONObject();
            storageFundsJSONObject.put("chunkSize", 1024);
            storageFundsJSONObject.put("duration", 120);
            storageFundsJSONObject.put("chiPrice", 100);

            Log.e(TAG, "storageFundsResult() start...");
            Object storageFundsResult = mRpcClient.call("storageFunds", new Object[]{storageFundsJSONObject});

            Log.e(TAG, "storageFundsResult() end...");
            Log.e(TAG, "storageFundsResult() " + storageFundsResult);
        } catch (Exception e) {
            Log.e(TAG, "storageFundsResult() err = " + e.getMessage());
            e.printStackTrace();

            return null;
        }

        return "";
    }

    public static String downloadFunds() {
        try {
            JSONObject downloadFundsJSONObject = new JSONObject();
            downloadFundsJSONObject.put("chunkSize", 1024);
            downloadFundsJSONObject.put("chiPrice", 100);

            Log.e(TAG, "downloadFunds() start...");
            Object downloadFundsResult = mRpcClient.call("downloadFunds", new Object[]{downloadFundsJSONObject});
            Log.e(TAG, "downloadFunds() end...");
            Log.e(TAG, "downloadFunds() " + downloadFundsResult);
        } catch (Exception e) {
            Log.e(TAG, "downloadFunds() err = " + e.getMessage());
            e.printStackTrace();

            return null;
        }

        return "";
    }


    public static String depositRecord() {
        initClient();

        try {
            JSONObject downloadFundsJSONObject = new JSONObject();
            downloadFundsJSONObject.put("accountID", PossUtil.getAccount());
            downloadFundsJSONObject.put("start", 0);
            downloadFundsJSONObject.put("limit", 10);

            Log.e(TAG, "depositRecord() start...");
            Object depositRecordResult = mRpcClient.call("depositRecord", new Object[]{downloadFundsJSONObject});
            Log.e(TAG, "depositRecord() end...");
            Log.e(TAG, "depositRecord() " + depositRecordResult);
        } catch (Exception e) {
            Log.e(TAG, "depositRecord() err = " + e.getMessage());
            e.printStackTrace();

            return null;
        }

        return "";
    }

    public static String transferRecord() {
        initClient();

        try {
            JSONObject transferRecordJSONObject = new JSONObject();
            transferRecordJSONObject.put("accountID", PossUtil.getAccount());
            transferRecordJSONObject.put("start", 0);
            transferRecordJSONObject.put("limit", 10);

            Log.e(TAG, "transferRecord() start...");
            Object transferRecordResult = mRpcClient.call("transferRecord", new Object[]{transferRecordJSONObject});
            Log.e(TAG, "transferRecord() end...");
            Log.e(TAG, "transferRecord() " + transferRecordResult);
        } catch (Exception e) {
            Log.e(TAG, "transferRecord() err = " + e.getMessage());
            e.printStackTrace();

            return null;
        }

        return "";
    }

    public static String storageDetail() {
        initClient();

        try {
            JSONObject storageDetailJSONObject = new JSONObject();
            storageDetailJSONObject.put("accountID", PossUtil.getAccount());

            //the array use JSONArray
            JSONArray contractIDs = new JSONArray();
            contractIDs.put("0104c7225bac7eff86f25c6efcb9bdc353e61020ac0ffa2056ae770d6764c036");
            storageDetailJSONObject.put("contractIDs", contractIDs);

            Log.e(TAG, "storageDetail() start...");
            Object storageDetailResult = mRpcClient.call("storageDetail", new Object[]{storageDetailJSONObject});
            Log.e(TAG, "storageDetail() end...");
            Log.e(TAG, "storageDetail() " + storageDetailResult);

        } catch (Exception e) {
            Log.e(TAG, "storageDetail() err = " + e.getMessage());
            e.printStackTrace();

            return null;
        }

        return "";
    }

    private static void initClient() {
        if (mRpcClient == null) {
            mRpcClient = new JSONRPCHttpClient(mRpcUrlStr);
            //set client debug true, so you can see the request json string in the log.e(),
            // then you can set your params  according the request str contrasting the prescribed format
            mRpcClient.setDebug(true);
        }
    }

    public interface QueryAccountListener {
        void onQueryAccountError(String errMsg);
    }
}