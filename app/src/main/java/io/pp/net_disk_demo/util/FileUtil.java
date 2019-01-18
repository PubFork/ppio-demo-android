package io.pp.net_disk_demo.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import io.pp.net_disk_demo.Constant;

import java.util.Calendar;

public class FileUtil {

    private static final String TAG = "FileUtil";

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String res = null;
        String[] project = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, project, null, null, null);

        if (null != cursor && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }

        return res;
    }

    /**
     * Absolute path to file from Uri, designed for Android 4.4
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                // DownloadsProvider

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                // MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }

        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // File
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursor != null)
            cursor.close();


        return null;
    }

    /**
     * * @param uri The Uri to check.
     * * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getRegisterRecordFile() {
        return Constant.PPIO_File.REGISTER_RECORD_PREFIX + getCurrentTime() + ".txt";
    }

    public static String getLogInRecordFile() {
        return Constant.PPIO_File.LOGIN_RECORD_FILE;
    }

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        String monthStr;
        if ((calendar.get(Calendar.MONTH) + 1) < 10) {
            monthStr = "0" + (calendar.get(Calendar.MONTH) + 1);
        } else {
            monthStr = "" + (calendar.get(Calendar.MONTH) + 1);
        }

        String dayOfMonthStr;
        if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            dayOfMonthStr = "0" + calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            dayOfMonthStr = "" + calendar.get(Calendar.DAY_OF_MONTH);
        }

        String hourOfDayStr;
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
            hourOfDayStr = "0" + calendar.get(Calendar.HOUR_OF_DAY);
        } else {
            hourOfDayStr = "" + calendar.get(Calendar.HOUR_OF_DAY);
        }

        String minuteStr;
        if (calendar.get(Calendar.MINUTE) < 10) {
            minuteStr = "0" + calendar.get(Calendar.MINUTE);
        } else {
            minuteStr = "" + calendar.get(Calendar.MINUTE);
        }

        String secondStr;
        if (calendar.get(Calendar.SECOND) < 10) {
            secondStr = "0" + calendar.get(Calendar.SECOND);
        } else {
            secondStr = "" + calendar.get(Calendar.SECOND);
        }

        String millSecondStr;
        if (calendar.get(Calendar.MILLISECOND) < 10) {
            millSecondStr = "00" + calendar.get(Calendar.MILLISECOND);
        } else if (calendar.get(Calendar.MILLISECOND) < 100) {
            millSecondStr = "0" + calendar.get(Calendar.MILLISECOND);
        } else {
            millSecondStr = "" + calendar.get(Calendar.MILLISECOND);
        }

        return "" + calendar.get(Calendar.YEAR) +
                "-" + monthStr +
                "-" + dayOfMonthStr +
                "-" + hourOfDayStr +
                ":" + minuteStr +
                ":" + secondStr +
                ":" + millSecondStr;
    }
}