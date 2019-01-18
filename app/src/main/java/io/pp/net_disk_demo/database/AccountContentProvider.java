package io.pp.net_disk_demo.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class AccountContentProvider extends ContentProvider {

    private Context mContext;
    //private DataBaseHelper mDbHelper = null;
    private SQLiteDatabase mSQLiteDatabase = null;

    public static final String AUTHORITY = "io.pp.net_disk_demo";

    public static final int ACCOUNT_CODE = 1;

    public final static Uri ACCOUNT_UI = Uri.parse("content://" + AUTHORITY + "/" + DataBaseHelper.ACCOUNT_TABLE_NAME);

    private static final UriMatcher mMatcher;

    static {
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mMatcher.addURI(AUTHORITY, "account", ACCOUNT_CODE);
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();

        DataBaseHelper dbHelper = new DataBaseHelper(getContext());
        mSQLiteDatabase = dbHelper.getWritableDatabase();

        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        String tableName = getTableName(uri);

        if (tableName == null) {
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }

        long id = mSQLiteDatabase.insert(tableName, null, values);

        if (id > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }

        return uri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String tableName = getTableName(uri);

        if (tableName == null) {
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }

        return mSQLiteDatabase.query(tableName, projection, selection, selectionArgs, null, null, sortOrder, null);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String tableName = getTableName(uri);

        if (tableName == null) {
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }

        int row = mSQLiteDatabase.update(tableName, values, selection, selectionArgs);

        if (row > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }

        return row;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        String tableName = getTableName(uri);

        if (tableName == null) {
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }

        int count = mSQLiteDatabase.delete(tableName, selection, selectionArgs);
        if (count > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    private String getTableName(Uri uri) {
        String tableName = null;

        switch (mMatcher.match(uri)) {
            case ACCOUNT_CODE:
                tableName = DataBaseHelper.ACCOUNT_TABLE_NAME;
                break;
            default:
                //tableName = null;
        }
        return tableName;
    }
}