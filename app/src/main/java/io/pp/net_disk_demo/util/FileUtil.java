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
import android.text.TextUtils;
import android.util.Log;

import io.pp.net_disk_demo.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    private static final String TAG = "FileUtil";

    public static final int UNKNOWN_FILE = 0x00;
    public static final int TXT_NO_SUFFIX_FILE = 0x01;
    public static final int DOC_FILE = 0x02;
    public static final int IMAGE_FILE = 0x03;
    public static final int PDF_FILE = 0x04;
    public static final int PPT_FILE = 0x05;
    public static final int AUDIO_FILE = 0x06;
    public static final int VIDEO_FILE = 0x07;
    public static final int XLS_FILE = 0x08;
    public static final int ZIP_FILE = 0x09;

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

        Log.e(TAG, "getPath() uri = " + uri);

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
                // DownloadsProvider;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    return getDataColumn(context, uri);
                } else {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
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
            Log.e(TAG, "getDataColumn() error:  " + e.getMessage());
            e.printStackTrace();
        }

        if (cursor != null)
            cursor.close();

        return null;
    }

    public static String getDataColumn(Context context, Uri uri) {
        //String[] project = {MediaStore.Images.Media.DATA};
        //String[] project = {"document_id", "mime_type", "_display_name", "summary", "last_modified", "flags", "_size"};

        String filePath = "";
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            //int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int columnIndex = cursor.getColumnIndexOrThrow("document_id");
            cursor.moveToFirst();
            Log.e(TAG, "getDataColumn() cursor.getColumnNames() = " + cursor.getColumnNames());
            String[] names = cursor.getColumnNames();
            for (int i = 0; i < names.length; i++) {
                Log.e(TAG, "getDataColumn() names[" + i + "] = " + names[i]);
            }
            filePath = cursor.getString(columnIndex);
            Log.e(TAG, "getDataColumn() filePath = " + filePath);
        } catch (Exception e) {
            Log.e(TAG, "getDataColumn() getDataColumn() error:  " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return filePath;
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

    public static int checkFileTypeBySuffix(String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            fileName = fileName.toLowerCase();

            if (fileName.endsWith(".txt") || !fileName.contains(".")) {
                return TXT_NO_SUFFIX_FILE;
            }

            if (fileName.endsWith(".doc") ||
                    fileName.endsWith(".docx")) {
                return DOC_FILE;
            }

            //1 Webp//2 BMP//3 PCX//4 TIF//5 GIF
            //6 JPEG//7 TGA//8 EXIF//9 FPX//10 SVG
            //11 PSD//12 CDR//13 PCD//14 DXF//15 UFO
            //16 EPS//17 AI//18 PNG//19 HDRI//20 RAW
            //21 WMF//22 FLIC//23 EMF//24 ICO
            //BMP、JPG、JPEG、PNG、GIF、TIF、PSD
            if (fileName.endsWith(".webp") || fileName.endsWith(".bmp") || fileName.endsWith(".pcx") || fileName.endsWith(".tif") || fileName.endsWith(".gif") ||
                    fileName.endsWith(".jpeg") || fileName.endsWith(".tga") || fileName.endsWith(".exif") || fileName.endsWith(".fpx") || fileName.endsWith(".svg") ||
                    fileName.endsWith(".psd") || fileName.endsWith(".cdr") || fileName.endsWith(".pcd") || fileName.endsWith(".dxf") || fileName.endsWith(".ufo") ||
                    fileName.endsWith(".eps") || fileName.endsWith(".ai") || fileName.endsWith(".png") || fileName.endsWith(".hdri") || fileName.endsWith(".raw") ||
                    fileName.endsWith(".wmf") || fileName.endsWith(".flic") || fileName.endsWith(".emf") || fileName.endsWith(".ico") || fileName.endsWith(".jpg")) {
                return IMAGE_FILE;
            }

            if (fileName.endsWith(".pdf")) {
                return PDF_FILE;
            }

            if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
                return PPT_FILE;
            }

            // wav, mp3, ogg, midi, aac,
            //ape, flac, wma, amr
            if (fileName.endsWith(".wav") || fileName.endsWith(".mp3") || fileName.endsWith(".ogg") || fileName.endsWith(".midi") || fileName.endsWith(".aac") ||
                    fileName.endsWith(".ape") || fileName.endsWith(".flac") || fileName.endsWith(".wma") || fileName.endsWith(".amr")) {
                return AUDIO_FILE;
            }

            //avi, rmvb, rm, asf, divx,
            //mpg, mpeg, mpe, wmv, mp4,
            //mkv, vob
            if (fileName.endsWith(".avi") || fileName.endsWith(".rmvb") || fileName.endsWith(".rm") || fileName.endsWith(".asf") || fileName.endsWith(".divx") ||
                    fileName.endsWith(".mpg") || fileName.endsWith(".mpeg") || fileName.endsWith(".mpe") || fileName.endsWith(".wmv") || fileName.endsWith(".mp4") ||
                    fileName.endsWith(".mkv") || fileName.endsWith(".vob")) {
                return VIDEO_FILE;
            }

            //xls  xlsx
            if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                return XLS_FILE;
            }

            //RAR, ZIP, 7Z, GZ, BZ,
            //ACE, UHA, UDA, ZPAQ
            if (fileName.endsWith(".rar") || fileName.endsWith(".zip") || fileName.endsWith(".7z") || fileName.endsWith(".gz") || fileName.endsWith(".bz") ||
                    fileName.endsWith(".ace") || fileName.endsWith(".uha") || fileName.endsWith(".uda") || fileName.endsWith(".zpaq")) {
                return ZIP_FILE;
            }
        }

        return UNKNOWN_FILE;
    }

    public static boolean mergeFiles(ArrayList<String> filePathList, String resultPath) {
        if (filePathList == null || filePathList.size() < 1 || TextUtils.isEmpty(resultPath)) {
            return false;
        }

        File[] files = new File[filePathList.size()];
        for (int i = 0; i < filePathList.size(); i++) {
            files[i] = new File(filePathList.get(i));
            if (TextUtils.isEmpty(filePathList.get(i)) || !files[i].exists() || !files[i].isFile()) {
                return false;
            }
        }

        File resultFile = new File(resultPath);

        try {
            FileChannel resultFileChannel = new FileOutputStream(resultFile, true).getChannel();
            for (int i = 0; i < filePathList.size(); i++) {
                FileChannel blk = new FileInputStream(files[i]).getChannel();
                resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
                blk.close();
            }
            resultFileChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Generate a .json format file
     */
    public static boolean createJsonFile(String jsonString, String filePath) {
        boolean flag = true;

        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            // Format json string
            //jsonString = JsonUtil.formatJson(jsonString);

            // Write the formatted string to a file
            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(jsonString);
            write.flush();
            write.close();
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        return flag;
    }

    public static void zipMultiFile(ArrayList<String> srcFilePaths, String zipFilePath) {
        ArrayList<File> srcFiles = new ArrayList<>();
        for (int i = 0; i < srcFilePaths.size(); i++) {
            srcFiles.add(new File(srcFilePaths.get(i)));
        }

        File zipFile = new File(zipFilePath);

        if (!zipFile.exists()) {
            try {
                zipFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream fileOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        FileInputStream fileInputStream = null;

        try {
            fileOutputStream = new FileOutputStream(zipFile);
            zipOutputStream = new ZipOutputStream(fileOutputStream);
            ZipEntry zipEntry = null;
            for (int i = 0; i < srcFiles.size(); i++) {
                fileInputStream = new FileInputStream(srcFiles.get(i));
                zipEntry = new ZipEntry(srcFiles.get(i).getName());
                zipOutputStream.putNextEntry(zipEntry);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = fileInputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, len);
                }

                fileInputStream.close();
            }
            zipOutputStream.closeEntry();
            zipOutputStream.close();

            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the file size in a human-readable string.
     *
     * @param size
     * @return
     * @author paulburke
     */
    public static String getReadableFileSize(long size) {
        final long BYTES_IN_KILOBYTES = 1024l;
        final DecimalFormat dec = new DecimalFormat("###.#");
        final String BYTES = " B";
        final String KILOBYTES = " KB";
        final String MEGABYTES = " MB";
        final String GIGABYTES = " GB";
        double fileSize = size;
        String suffix = BYTES;

        if (size > BYTES_IN_KILOBYTES) {
            suffix = KILOBYTES;
            fileSize = size / BYTES_IN_KILOBYTES;
            if (fileSize > BYTES_IN_KILOBYTES) {
                fileSize = fileSize / BYTES_IN_KILOBYTES;
                if (fileSize > BYTES_IN_KILOBYTES) {
                    fileSize = fileSize / BYTES_IN_KILOBYTES;
                    suffix = GIGABYTES;
                } else {
                    suffix = MEGABYTES;
                }
            }
        }

        return String.valueOf(dec.format(fileSize) + suffix);
    }

    private static final String[][] MIME_MapTable = {
            //{Suffix name,    MIME type}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".JPEG", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            //{".xml",    "text/xml"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };

    public static String getMIMEType(File file) {

        String type = "*/*";
       
        String fName = file.getName();
        //Get the position of the separator "." before the suffix name in fName.
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0)
            return type;
        /* Get the suffix name of the file */
        String fileType = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (fileType == null || "".equals(fileType))
            return type;
        //Find the corresponding MIME type in the match table for MIME and file types.
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (fileType.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }

        return type;
    }
}