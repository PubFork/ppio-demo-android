package io.pp.net_disk_demo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;

import io.pp.net_disk_demo.Constant;

public class AccountDatabaseManager {

    private static final String TAG = "AccountManager";

    public static boolean hasLogin(@NonNull Context context, CheckHasLoginListener checkHasLoginListener) {
        boolean mHasLogin = false;

        try {
            Cursor cursor = context.getContentResolver().query(AccountContentProvider.ACCOUNT_UI,
                    new String[]{DataBaseHelper.PRIVATE_KEY,
                            DataBaseHelper.MNEMONIC,
                            DataBaseHelper.PUBLIC_KEY,
                            DataBaseHelper.ADDRESS,
                            DataBaseHelper.BALANCE,
                            DataBaseHelper.LOGIN},
                    DataBaseHelper.LOGIN + " = ?",
                    new String[]{"" + DataBaseHelper.IS_LOGIN},
                    null);

            if (cursor != null && cursor.getCount() > 0) {
                mHasLogin = true;
            }

            if (cursor != null) {
                cursor.close();
            }
            cursor = null;

        } catch (Exception e) {
            if (checkHasLoginListener != null) {
                checkHasLoginListener.onCheckFail(e.getMessage());

                Log.e(TAG, "hasLogin() fail: " + e.getMessage());
            }
        }

        return mHasLogin;
    }

    public static boolean recordAccount(@NonNull Context context, String privateKey, String mnemonic, String publicKey, String address) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.PRIVATE_KEY, privateKey);
        contentValues.put(DataBaseHelper.MNEMONIC, mnemonic);
        contentValues.put(DataBaseHelper.PUBLIC_KEY, publicKey);
        contentValues.put(DataBaseHelper.ADDRESS, address);
        contentValues.put(DataBaseHelper.LOGIN, DataBaseHelper.IS_LOGIN);

        Uri result = context.getContentResolver().insert(AccountContentProvider.ACCOUNT_UI, contentValues);

        return result != null;
    }

    public static void deleteAccount(@NonNull Context context, LogOutListener logOutListener) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        SQLiteDatabase database = dataBaseHelper.getReadableDatabase();

        try {
            database.execSQL("DELETE FROM " + DataBaseHelper.ACCOUNT_TABLE_NAME);
        } catch (SQLException e) {
            if (logOutListener != null) {
                logOutListener.onLogOutFail(e.getMessage());
            }
        }
    }

    public static HashMap<String, String> getPrivateParams(@NonNull Context context) {
        HashMap<String, String> privateParams = new HashMap<>();
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(AccountContentProvider.ACCOUNT_UI,
                    new String[]{DataBaseHelper.PRIVATE_KEY,
                            DataBaseHelper.PUBLIC_KEY,
                            DataBaseHelper.MNEMONIC,
                            DataBaseHelper.ADDRESS,
                            DataBaseHelper.BALANCE,
                            DataBaseHelper.LOGIN},
                    DataBaseHelper.LOGIN + " = ?",
                    new String[]{"" + DataBaseHelper.IS_LOGIN},
                    null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                privateParams.put(Constant.Data.MNEMONIC, cursor.getString(cursor.getColumnIndex(DataBaseHelper.MNEMONIC)));
                privateParams.put(Constant.Data.PRIVATE_KEY, cursor.getString(cursor.getColumnIndex(DataBaseHelper.PRIVATE_KEY)));
                privateParams.put(Constant.Data.PASSWORD, cursor.getString(cursor.getColumnIndex(DataBaseHelper.PUBLIC_KEY)));
                privateParams.put(Constant.Data.ADDRESS, cursor.getString(cursor.getColumnIndex(DataBaseHelper.ADDRESS)));
            }
        } catch (Exception e) {
            Log.e(TAG, "getPrivateParams() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            cursor = null;
        }

        return privateParams;
    }

    public static String getMnemonic(@NonNull Context context) {
        String seedPhrase = null;

        Cursor cursor = context.getContentResolver().query(AccountContentProvider.ACCOUNT_UI,
                new String[]{DataBaseHelper.PRIVATE_KEY,
                        DataBaseHelper.PUBLIC_KEY,
                        DataBaseHelper.MNEMONIC,
                        DataBaseHelper.BALANCE,
                        DataBaseHelper.LOGIN},
                DataBaseHelper.LOGIN + " = ?",
                new String[]{"" + DataBaseHelper.IS_LOGIN},
                null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            seedPhrase = cursor.getString(cursor.getColumnIndex(DataBaseHelper.MNEMONIC));
        }

        if (cursor != null) {
            cursor.close();
        }
        cursor = null;

        return seedPhrase;
    }


    public interface CheckHasLoginListener {
        void onCheckFail(String failMessage);
    }

    public interface LogOutListener {
        void onLogOutFail(String failMessage);
    }
}