package io.pp.net_disk_demo.ppio;

import android.util.Log;

import org.alexd.jsonrpc.JSONRPCHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.pp.net_disk_demo.Constant;
import io.pp.net_disk_demo.data.OracleChiPrice;
import io.pp.net_disk_demo.data.RecordInfo;
import io.pp.net_disk_demo.util.TimeConverterUtil;

public class RpcUtil {

    private static final String TAG = "RpcUtil";
    //private static final String mRpcUrlStr = "http://ad04b30b910c311e9b71c02d26ce9aff-567092461.us-west-2.elb.amazonaws.com:18030/rpc";
    private static final String mRpcUrlStr = Constant.URL.RPC_URL;
    private static JSONRPCHttpClient mBalanceRpcClient;
    private static JSONRPCHttpClient mFundRpcClient;
    private static JSONRPCHttpClient mOracleRpcClient;
    private static JSONRPCHttpClient mStorageRpcClient;
    private static JSONRPCHttpClient mDownloadRpcClient;
    private static JSONRPCHttpClient mTransferRecordRpcClient;
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
        mBalanceRpcClient = new JSONRPCHttpClient(mRpcUrlStr);
        mBalanceRpcClient.setDebug(true);

        String queryAccountResultStr;
        String balanceStr = "";

        try {
            Log.e(TAG, "address = " + PossUtil.getAccount());
            queryAccountResultStr = mBalanceRpcClient.callString("queryAccount", PossUtil.getAccount());
            JSONObject queryAccountJSONObject = new JSONObject(queryAccountResultStr);
            balanceStr = queryAccountJSONObject.getString("Balance");
        } catch (Exception e) {
            if (queryAccountListener != null) {
                queryAccountListener.onQueryAccountError(e.getMessage());
            }
            Log.e(TAG, "queryAccount() err = " + e.getMessage());
            e.printStackTrace();
        }

        mBalanceRpcClient = null;

        return balanceStr;
    }

    public static String getFund(QueryAccountListener queryAccountListener) {
        mFundRpcClient = new JSONRPCHttpClient(mRpcUrlStr);
        mFundRpcClient.setDebug(true);

        String queryAccountResultStr;
        String fundStr = "";

        try {
            Log.e(TAG, "queryAccount() start...");
            queryAccountResultStr = mFundRpcClient.callString("queryAccount", PossUtil.getAccount());
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

        mFundRpcClient = null;

        return fundStr;
    }

    public static OracleChiPrice oracleChiPrice(QueryAccountListener queryAccountListener) {
        mOracleRpcClient = new JSONRPCHttpClient(mRpcUrlStr);
        mOracleRpcClient.setDebug(true);

        try {
            Log.e(TAG, "oracleChiPrice() start...");
            String oracleChiPriceResultStr = mOracleRpcClient.callString("oracleChiPrice");
            Log.e(TAG, "oracleChiPrice() end...");
            Log.e(TAG, "oracleChiPrice() " + oracleChiPriceResultStr);

            mOracleRpcClient = null;

            JSONObject oracleChiPriceJSONObject = new JSONObject(oracleChiPriceResultStr);
            return new OracleChiPrice(oracleChiPriceJSONObject.getString("StorageChiPrice"),
                    oracleChiPriceJSONObject.getString("DownloadChiPrice"));
        } catch (Exception e) {
            if (queryAccountListener != null) {
                queryAccountListener.onQueryAccountError(e.getMessage());
            }
            Log.e(TAG, "oracleChiPrice() err = " + e.getMessage());
            e.printStackTrace();

            mOracleRpcClient = null;

            return null;
        }
    }

    public static long getStorageChi(long chunkSize, long duration, QueryAccountListener queryAccountListener) {
        //> curl -X POST -H 'content-type:text/json;' --data '{"id":1,"jsonrpc":"2.0","method":"StorageChi","params":[{"chunkSize":1024,"duration":120,"chiPrice":"100"}]}' http://127.0.0.1:18030/rpc
        mStorageRpcClient = new JSONRPCHttpClient(mRpcUrlStr);
        mStorageRpcClient.setDebug(true);

        Log.e(TAG, "chunkSize = " + chunkSize + ", duration = " + duration);

        try {
            JSONObject requestJSONObject = new JSONObject();
            requestJSONObject.put("chunkSize", chunkSize);
            requestJSONObject.put("duration", duration);

            Object[] params = new Object[1];
            params[0] = requestJSONObject;

            Log.e(TAG, "putObjectFunds start...");
            String putObjectFundsResultStr = mStorageRpcClient.callString("storageChi", params);
            Log.e(TAG, "putObjectFunds end...");
            Log.e(TAG, "putObjectFunds " + putObjectFundsResultStr);

            mStorageRpcClient = null;

            //{"StorageFundsChi":"24","ServiceChi":"10"}

            JSONObject storageChiJSONObject = new JSONObject(putObjectFundsResultStr);

            long storageFundsChi = Long.parseLong(storageChiJSONObject.getString("StorageFundsChi"));
            long serviceFundsChi = Long.parseLong(storageChiJSONObject.getString("ServiceChi"));

            return (storageFundsChi + serviceFundsChi);
        } catch (Exception e) {
            mStorageRpcClient = null;

            if (queryAccountListener != null) {
                queryAccountListener.onQueryAccountError(e.getMessage());
            }

            Log.e(TAG, "putObjectFunds() err = " + e.getMessage());
            e.printStackTrace();

            return 0l;
        }
    }

    public static long getDownloadChi(long chunkSize, QueryAccountListener queryAccountListener) {
        //> curl -X POST -H 'content-type:text/json;' --data '{"id":1,"jsonrpc":"2.0","method":"DownloadChi","params":[{"chunkSize":1024,"chiPrice":"100"}]}' http://127.0.0.1:18030/rpc
        mDownloadRpcClient = new JSONRPCHttpClient(mRpcUrlStr);
        mDownloadRpcClient.setDebug(true);

        try {
            JSONObject requestJSONObject = new JSONObject();
            requestJSONObject.put("chunkSize", chunkSize);

            Object[] params = new Object[1];
            params[0] = requestJSONObject;

            Log.e(TAG, "getObjectFunds start...");
            String putObjectFundsResultStr = mDownloadRpcClient.callString("downloadChi", params);
            Log.e(TAG, "getObjectFunds end...");
            Log.e(TAG, "getObjectFunds " + putObjectFundsResultStr);

            mDownloadRpcClient = null;

            //{"DownloadFundsChi":"10","ServiceChi":"10"}

            JSONObject downloadChiJSONObject = new JSONObject(putObjectFundsResultStr);

            long downloadFundsChi = Long.parseLong(downloadChiJSONObject.getString("DownloadFundsChi"));
            long serviceFundsChi = Long.parseLong(downloadChiJSONObject.getString("ServiceChi"));

            return (downloadFundsChi + serviceFundsChi);
        } catch (Exception e) {
            mDownloadRpcClient = null;

            if (queryAccountListener != null) {
                queryAccountListener.onQueryAccountError(e.getMessage());
            }
            Log.e(TAG, "getObjectFunds() err = " + e.getMessage());
            e.printStackTrace();

            return 0l;
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

            Object[] params = new Object[1];
            params[0] = storageFundsJSONObject;

            Log.e(TAG, "storageFundsResult() start...");
            Object storageFundsResult = mRpcClient.call("storageFunds", params);

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

    public static ArrayList<RecordInfo> transferRecord(QueryAccountListener queryAccountListener) {
        mTransferRecordRpcClient = new JSONRPCHttpClient(mRpcUrlStr);
        mTransferRecordRpcClient.setDebug(true);

        ArrayList<RecordInfo> transferRecordList = new ArrayList<>();

        try {
            JSONObject requestJSONObject = new JSONObject();
            requestJSONObject.put("accountID", PossUtil.getAccount());
            requestJSONObject.put("start", 0);
            requestJSONObject.put("limit", 50);

            Object[] params = new Object[1];
            params[0] = requestJSONObject;

            String transferRecordResultStr = mTransferRecordRpcClient.callString("transferRecord", params);

            JSONArray transferRecordJSONOArray = new JSONArray(transferRecordResultStr);

            if (transferRecordJSONOArray != null) {
                Calendar calendar = Calendar.getInstance();
                DateFormat format1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                String accountId = PossUtil.getAccount();

                for (int i = 0; i < transferRecordJSONOArray.length(); i++) {
                    String comment = transferRecordJSONOArray.getJSONObject(i).getString(Constant.TransferRecord.Key.COMMENT);
                    long amountWei = Long.parseLong(transferRecordJSONOArray.getJSONObject(i).getString(Constant.TransferRecord.Key.AMOUNT));
                    long time = Long.parseLong(transferRecordJSONOArray.getJSONObject(i).getString(Constant.TransferRecord.Key.TIME));
                    boolean isInCome = accountId.equals(transferRecordJSONOArray.getJSONObject(i).getString(Constant.TransferRecord.Key.TO_ACCOUNT_ID));

                    calendar.clear();
                    calendar.add(Calendar.SECOND, (int) time);

                    transferRecordList.add(new RecordInfo(comment,
                            TimeConverterUtil.utc2Local(format1.format(calendar.getTime()), "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"),
                            amountWei, isInCome));
                }
            }
        } catch (Exception e) {
            if (queryAccountListener != null) {
                queryAccountListener.onQueryAccountError(e.getMessage());
            }
            Log.e(TAG, "transferRecord() err = " + e.getMessage());
            e.printStackTrace();
        }

        return transferRecordList;
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