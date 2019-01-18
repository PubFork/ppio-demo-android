package io.pp.net_disk_demo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    // database name
    private static final String DATABASE_NAME = "account.db";

    // table name
    public static final String ACCOUNT_TABLE_NAME = "account";

    //database version
    private static final int DATABASE_VERSION = 1;

    public static final String PRIVATE_KEY = "PRIVATE_KEY";
    public static final String MNEMONIC = "MNEMONIC";
    public static final String PUBLIC_KEY = "PUBLIC_KEY";
    public static final String ADDRESS = "ADDRESS";
    public static final String BALANCE = "BALANCE";
    public static final String LOGIN = "LOGIN";

    public static final int IS_LOGIN = 1;
    public static final int IS_LOGOUT = 0;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                ACCOUNT_TABLE_NAME +
                "(" + PRIVATE_KEY + " TEXT, " +
                MNEMONIC + " TEXT, " +
                PUBLIC_KEY + " TEXT, " +
                ADDRESS + " TEXT, " +
                BALANCE + " TEXT, " +
                LOGIN + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}