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
//            {".3gp", "video/3gpp"},
//            {".apk", "application/vnd.android.package-archive"},
//            {".asf", "video/x-ms-asf"},
//            {".avi", "video/x-msvideo"},
//            {".bin", "application/octet-stream"},
//            {".bmp", "image/bmp"},
//            {".c", "text/plain"},
//            {".class", "application/octet-stream"},
//            {".conf", "text/plain"},
//            {".cpp", "text/plain"},
//            {".doc", "application/msword"},
//            {".docx", "application/msword"},
//            {".exe", "application/octet-stream"},
//            {".gif", "image/gif"},
//            {".gtar", "application/x-gtar"},
//            {".gz", "application/x-gzip"},
//            {".h", "text/plain"},
//            {".htm", "text/html"},
//            {".html", "text/html"},
//            {".jar", "application/java-archive"},
//            {".java", "text/plain"},
//            {".jpeg", "image/jpeg"},
//            {".JPEG", "image/jpeg"},
//            {".jpg", "image/jpeg"},
//            {".js", "application/x-javascript"},
//            {".log", "text/plain"},
//            {".m3u", "audio/x-mpegurl"},
//            {".m4a", "audio/mp4a-latm"},
//            {".m4b", "audio/mp4a-latm"},
//            {".m4p", "audio/mp4a-latm"},
//            {".m4u", "video/vnd.mpegurl"},
//            {".m4v", "video/x-m4v"},
//            {".mov", "video/quicktime"},
//            {".mp2", "audio/x-mpeg"},
//            {".mp3", "audio/x-mpeg"},
//            {".mp4", "video/mp4"},
//            {".mpc", "application/vnd.mpohun.certificate"},
//            {".mpe", "video/mpeg"},
//            {".mpeg", "video/mpeg"},
//            {".mpg", "video/mpeg"},
//            {".mpg4", "video/mp4"},
//            {".mpga", "audio/mpeg"},
//            {".msg", "application/vnd.ms-outlook"},
//            {".ogg", "audio/ogg"},
//            {".pdf", "application/pdf"},
//            {".png", "image/png"},
//            {".pps", "application/vnd.ms-powerpoint"},
//            {".ppt", "application/vnd.ms-powerpoint"},
//            {".pptx", "application/vnd.ms-powerpoint"},
//            {".prop", "text/plain"},
//            {".rar", "application/x-rar-compressed"},
//            {".rc", "text/plain"},
//            {".rmvb", "audio/x-pn-realaudio"},
//            {".rtf", "application/rtf"},
//            {".sh", "text/plain"},
//            {".tar", "application/x-tar"},
//            {".tgz", "application/x-compressed"},
//            {".txt", "text/plain"},
//            {".wav", "audio/x-wav"},
//            {".wma", "audio/x-ms-wma"},
//            {".wmv", "audio/x-ms-wmv"},
//            {".wps", "application/vnd.ms-works"},
//            //{".xml",    "text/xml"},
//            {".xml", "text/plain"},
//            {".z", "application/x-compress"},
//            {".zip", "application/zip"},
//            {"", "*/*"}

//
            {".3gp", "video/3gpp"},
            {".3gpp", "video/3gpp"},
            {".aac", "audio/x-mpeg"},
            {".amr", "audio/x-mpeg"},
            {".apk", "application/vnd.android.package-archive"},
            {".avi", "video/x-msvideo"},
            {".aab", "application/x-authoware-bin"},
            {".aam", "application/x-authoware-map"},
            {".aas", "application/x-authoware-seg"},
            {".ai", "application/postscript"},
            {".aif", "audio/x-aiff"},
            {".aifc", "audio/x-aiff"},
            {".aiff", "audio/x-aiff"},
            {".als", "audio/x-alpha5"},
            {".amc", "application/x-mpeg"},
            {".ani", "application/octet-stream"},
            {".asc", "text/plain"},
            {".asd", "application/astound"},
            {".asf", "video/x-ms-asf"},
            {".asn", "application/astound"},
            {".asp", "application/x-asap"},
            {".asx", " video/x-ms-asf"},
            {".au", "audio/basic"},
            {".avb", "application/octet-stream"},
            {".awb", "audio/amr-wb"},
            {".bcpio", "application/x-bcpio"},
            {".bld", "application/bld"},
            {".bld2", "application/bld2"},
            {".bpk", "application/octet-stream"},
            {".bz2", "application/x-bzip2"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".cal", "image/x-cals"},
            {".ccn", "application/x-cnc"},
            {".cco", "application/x-cocoa"},
            {".cdf", "application/x-netcdf"},
            {".cgi", "magnus-internal/cgi"},
            {".chat", "application/x-chat"},
            {".clp", "application/x-msclip"},
            {".cmx", "application/x-cmx"},
            {".co", "application/x-cult3d-object"},
            {".cod", "image/cis-cod"},
            {".cpio", "application/x-cpio"},
            {".cpt", "application/mac-compactpro"},
            {".crd", "application/x-mscardfile"},
            {".csh", "application/x-csh"},
            {".csm", "chemical/x-csml"},
            {".csml", "chemical/x-csml"},
            {".css", "text/css"},
            {".cur", "application/octet-stream"},
            {".doc", "application/msword"},
            {".dcm", "x-lml/x-evm"},
            {".dcr", "application/x-director"},
            {".dcx", "image/x-dcx"},
            {".dhtml", "text/html"},
            {".dir", "application/x-director"},
            {".dll", "application/octet-stream"},
            {".dmg", "application/octet-stream"},
            {".dms", "application/octet-stream"},
            {".dot", "application/x-dot"},
            {".dvi", "application/x-dvi"},
            {".dwf", "drawing/x-dwf"},
            {".dwg", "application/x-autocad"},
            {".dxf", "application/x-autocad"},
            {".dxr", "application/x-director"},
            {".ebk", "application/x-expandedbook"},
            {".emb", "chemical/x-embl-dl-nucleotide"},
            {".embl", "chemical/x-embl-dl-nucleotide"},
            {".eps", "application/postscript"},
            {".epub", "application/epub+zip"},
            {".eri", "image/x-eri"},
            {".es", "audio/echospeech"},
            {".esl", "audio/echospeech"},
            {".etc", "application/x-earthtime"},
            {".etx", "text/x-setext"},
            {".evm", "x-lml/x-evm"},
            {".evy", "application/x-envoy"},
            {".exe", "application/octet-stream"},
            {".fh4", "image/x-freehand"},
            {".fh5", "image/x-freehand"},
            {".fhc", "image/x-freehand"},
            {".fif", "image/fif"},
            {".fm", "application/x-maker"},
            {".fpx", "image/x-fpx"},
            {".fvi", "video/isivideo"},
            {".flv", "video/x-msvideo"},
            {".gau", "chemical/x-gaussian-input"},
            {".gca", "application/x-gca-compressed"},
            {".gdb", "x-lml/x-gdb"},
            {".gif", "image/gif"},
            {".gps", "application/x-gps"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".hdf", "application/x-hdf"},
            {".hdm", "text/x-hdml"},
            {".hdml", "text/x-hdml"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".hlp", "application/winhlp"},
            {".hqx", "application/mac-binhex40"},
            {".hts", "text/html"},
            {".ice", "x-conference/x-cooltalk"},
            {".ico", "application/octet-stream"},
            {".ief", "image/ief"},
            {".ifm", "image/gif"},
            {".ifs", "image/ifs"},
            {".imy", "audio/melody"},
            {".ins", "application/x-net-install"},
            {".ips", "application/x-ipscript"},
            {".ipx", "application/x-ipix"},
            {".it", "audio/x-mod"},
            {".itz", "audio/x-mod"},
            {".ivr", "i-world/i-vrml"},
            {".j2k", "image/j2k"},
            {".jad", "text/vnd.sun.j2me.app-descriptor"},
            {".jam", "application/x-jam"},
            {".jnlp", "application/x-java-jnlp-file"},
            {".jpe", "image/jpeg"},
            {".jpz", "image/jpeg"},
            {".jwc", "application/jwc"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".kjx", "application/x-kjx"},
            {".lak", "x-lml/x-lak"},
            {".latex", "application/x-latex"},
            {".lcc", "application/fastman"},
            {".lcl", "application/x-digitalloca"},
            {".lcr", "application/x-digitalloca"},
            {".lgh", "application/lgh"},
            {".lha", "application/octet-stream"},
            {".lml", "x-lml/x-lml"},
            {".lmlpack", "x-lml/x-lmlpack"},
            {".log", "text/plain"},
            {".lsf", "video/x-ms-asf"},
            {".lsx", "video/x-ms-asf"},
            {".lzh", "application/x-lzh "},
            {".m13", "application/x-msmediaview"},
            {".m14", "application/x-msmediaview"},
            {".m15", "audio/x-mod"},
            {".m3u", "audio/x-mpegurl"},
            {".m3url", "audio/x-mpegurl"},
            {".ma1", "audio/ma1"},
            {".ma2", "audio/ma2"},
            {".ma3", "audio/ma3"},
            {".ma5", "audio/ma5"},
            {".man", "application/x-troff-man"},
            {".map", "magnus-internal/imagemap"},
            {".mbd", "application/mbedlet"},
            {".mct", "application/x-mascot"},
            {".mdb", "application/x-msaccess"},
            {".mdz", "audio/x-mod"},
            {".me", "application/x-troff-me"},
            {".mel", "text/x-vmel"},
            {".mi", "application/x-mif"},
            {".mid", "audio/midi"},
            {".midi", "audio/midi"},
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
            {".mif", "application/x-mif"},
            {".mil", "image/x-cals"},
            {".mio", "audio/x-mio"},
            {".mmf", "application/x-skt-lbs"},
            {".mng", "video/x-mng"},
            {".mny", "application/x-msmoney"},
            {".moc", "application/x-mocha"},
            {".mocha", "application/x-mocha"},
            {".mod", "audio/x-mod"},
            {".mof", "application/x-yumekara"},
            {".mol", "chemical/x-mdl-molfile"},
            {".mop", "chemical/x-mopac-input"},
            {".movie", "video/x-sgi-movie"},
            {".mpn", "application/vnd.mophun.application"},
            {".mpp", "application/vnd.ms-project"},
            {".mps", "application/x-mapserver"},
            {".mrl", "text/x-mrml"},
            {".mrm", "application/x-mrm"},
            {".ms", "application/x-troff-ms"},
            {".mts", "application/metastream"},
            {".mtx", "application/metastream"},
            {".mtz", "application/metastream"},
            {".mzv", "application/metastream"},
            {".nar", "application/zip"},
            {".nbmp", "image/nbmp"},
            {".nc", "application/x-netcdf"},
            {".ndb", "x-lml/x-ndb"},
            {".ndwn", "application/ndwn"},
            {".nif", "application/x-nif"},
            {".nmz", "application/x-scream"},
            {".nokia-op-logo", "image/vnd.nok-oplogo-color"},
            {".npx", "application/x-netfpx"},
            {".nsnd", "audio/nsnd"},
            {".nva", "application/x-neva1"},
            {".oda", "application/oda"},
            {".oom", "application/x-atlasMate-plugin"},
            {".ogg", "audio/ogg"},
            {".pac", "audio/x-pac"},
            {".pae", "audio/x-epac"},
            {".pan", "application/x-pan"},
            {".pbm", "image/x-portable-bitmap"},
            {".pcx", "image/x-pcx"},
            {".pda", "image/x-pda"},
            {".pdb", "chemical/x-pdb"},
            {".pdf", "application/pdf"},
            {".pfr", "application/font-tdpfr"},
            {".pgm", "image/x-portable-graymap"},
            {".pict", "image/x-pict"},
            {".pm", "application/x-perl"},
            {".pmd", "application/x-pmd"},
            {".png", "image/png"},
            {".pnm", "image/x-portable-anymap"},
            {".pnz", "image/png"},
            {".pot", "application/vnd.ms-powerpoint"},
            {".ppm", "image/x-portable-pixmap"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
			{".pptx", "application/vnd.ms-powerpoint"},
            {".pqf", "application/x-cprplayer"},
            {".pqi", "application/cprplayer"},
            {".prc", "application/x-prc"},
            {".proxy", "application/x-ns-proxy-autoconfig"},
            {".prop", "text/plain"},
            {".ps", "application/postscript"},
            {".ptlk", "application/listenup"},
            {".pub", "application/x-mspublisher"},
            {".pvx", "video/x-pv-pvx"},
            {".qcp", "audio/vnd.qcelp"},
            {".qt", "video/quicktime"},
            {".qti", "image/x-quicktime"},
            {".qtif", "image/x-quicktime"},
            {".r3t", "text/vnd.rn-realtext3d"},
            {".ra", "audio/x-pn-realaudio"},
            {".ram", "audio/x-pn-realaudio"},
            {".ras", "image/x-cmu-raster"},
            {".rdf", "application/rdf+xml"},
            {".rf", "image/vnd.rn-realflash"},
            {".rgb", "image/x-rgb"},
            {".rlf", "application/x-richlink"},
            {".rm", "audio/x-pn-realaudio"},
            {".rmf", "audio/x-rmf"},
            {".rmm", "audio/x-pn-realaudio"},
            {".rnx", "application/vnd.rn-realplayer"},
            {".roff", "application/x-troff"},
            {".rp", "image/vnd.rn-realpix"},
            {".rpm", "audio/x-pn-realaudio-plugin"},
            {".rt", "text/vnd.rn-realtext"},
            {".rte", "x-lml/x-gps"},
            {".rtf", "application/rtf"},
            {".rtg", "application/metastream"},
            {".rtx", "text/richtext"},
            {".rv", "video/vnd.rn-realvideo"},
            {".rwc", "application/x-rogerwilco"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".s3m", "audio/x-mod"},
            {".s3z", "audio/x-mod"},
            {".sca", "application/x-supercard"},
            {".scd", "application/x-msschedule"},
            {".sdf", "application/e-score"},
            {".sea", "application/x-stuffit"},
            {".sgm", "text/x-sgml"},
            {".sgml", "text/x-sgml"},
			{".sh", "text/plain"},
            {".shar", "application/x-shar"},
            {".shtml", "magnus-internal/parsed-html"},
            {".shw", "application/presentations"},
            {".si6", "image/si6"},
            {".si7", "image/vnd.stiwap.sis"},
            {".si9", "image/vnd.lgtwap.sis"},
            {".sis", "application/vnd.symbian.install"},
            {".sit", "application/x-stuffit"},
            {".skd", "application/x-koan"},
            {".skm", "application/x-koan"},
            {".skp", "application/x-koan"},
            {".skt", "application/x-koan"},
            {".slc", "application/x-salsa"},
            {".smd", "audio/x-smd"},
            {".smi", "application/smil"},
            {".smil", "application/smil"},
            {".smp", "application/studiom"},
            {".smz", "audio/x-smd"},
            {".sh", "application/x-sh"},
            {".snd", "audio/basic"},
            {".spc", "text/x-speech"},
            {".spl", "application/futuresplash"},
            {".spr", "application/x-sprite"},
            {".sprite", "application/x-sprite"},
            {".sdp", "application/sdp"},
            {".spt", "application/x-spt"},
            {".src", "application/x-wais-source"},
            {".stk", "application/hyperstudio"},
            {".stm", "audio/x-mod"},
            {".sv4cpio", "application/x-sv4cpio"},
            {".sv4crc", "application/x-sv4crc"},
            {".svf", "image/vnd"},
            {".svg", "image/svg-xml"},
            {".svh", "image/svh"},
            {".svr", "x-world/x-svr"},
            {".swf", "application/x-shockwave-flash"},
            {".swfl", "application/x-shockwave-flash"},
            {".t", "application/x-troff"},
            {".tad", "application/octet-stream"},
            {".talk", "text/x-speech"},
            {".tar", "application/x-tar"},
            {".taz", "application/x-tar"},
            {".tbp", "application/x-timbuktu"},
            {".tbt", "application/x-timbuktu"},
            {".tcl", "application/x-tcl"},
            {".tex", "application/x-tex"},
            {".texi", "application/x-texinfo"},
            {".texinfo", "application/x-texinfo"},
            {".tgz", "application/x-tar"},
            {".thm", "application/vnd.eri.thm"},
            {".tif", "image/tiff"},
            {".tiff", "image/tiff"},
            {".tki", "application/x-tkined"},
            {".tkined", "application/x-tkined"},
            {".toc", "application/toc"},
            {".toy", "image/toy"},
            {".tr", "application/x-troff"},
            {".trk", "x-lml/x-gps"},
            {".trm", "application/x-msterminal"},
            {".tsi", "audio/tsplayer"},
            {".tsp", "application/dsptype"},
            {".tsv", "text/tab-separated-values"},
            {".ttf", "application/octet-stream"},
            {".ttz", "application/t-time"},
            {".txt", "text/plain"},
            {".ult", "audio/x-mod"},
            {".ustar", "application/x-ustar"},
            {".uu", "application/x-uuencode"},
            {".uue", "application/x-uuencode"},
            {".vcd", "application/x-cdlink"},
            {".vcf", "text/x-vcard"},
            {".vdo", "video/vdo"},
            {".vib", "audio/vib"},
            {".viv", "video/vivo"},
            {".vivo", "video/vivo"},
            {".vmd", "application/vocaltec-media-desc"},
            {".vmf", "application/vocaltec-media-file"},
            {".vmi", "application/x-dreamcast-vms-info"},
            {".vms", "application/x-dreamcast-vms"},
            {".vox", "audio/voxware"},
            {".vqe", "audio/x-twinvq-plugin"},
            {".vqf", "audio/x-twinvq"},
            {".vql", "audio/x-twinvq"},
            {".vre", "x-world/x-vream"},
            {".vrml", "x-world/x-vrml"},
            {".vrt", "x-world/x-vrt"},
            {".vrw", "x-world/x-vream"},
            {".vts", "workbook/formulaone"},
            {".wax", "audio/x-ms-wax"},
            {".wbmp", "image/vnd.wap.wbmp"},
            {".web", "application/vnd.xara"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wi", "image/wavelet"},
            {".wis", "application/x-InstallShield"},
            {".wm", "video/x-ms-wm"},
            {".wmd", "application/x-ms-wmd"},
            {".wmf", "application/x-msmetafile"},
            {".wml", "text/vnd.wap.wml"},
            {".wmlc", "application/vnd.wap.wmlc"},
            {".wmls", "text/vnd.wap.wmlscript"},
            {".wmlsc", "application/vnd.wap.wmlscriptc"},
            {".wmlscript", "text/vnd.wap.wmlscript"},
            {".wmv", "video/x-ms-wmv"},
            {".wmx", "video/x-ms-wmx"},
            {".wmz", "application/x-ms-wmz"},
            {".wpng", "image/x-up-wpng"},
            {".wps", "application/vnd.ms-works"},
            {".wpt", "x-lml/x-gps"},
            {".wri", "application/x-mswrite"},
            {".wrl", "x-world/x-vrml"},
            {".wrz", "x-world/x-vrml"},
            {".ws", "text/vnd.wap.wmlscript"},
            {".wsc", "application/vnd.wap.wmlscriptc"},
            {".wv", "video/wavelet"},
            {".wvx", "video/x-ms-wvx"},
            {".wxl", "application/x-wxl"},
            {".x-gzip", "application/x-gzip"},
            {".xar", "application/vnd.xara"},
            {".xbm", "image/x-xbitmap"},
            {".xdm", "application/x-xdma"},
            {".xdma", "application/x-xdma"},
            {".xdw", "application/vnd.fujixerox.docuworks"},
            {".xht", "application/xhtml+xml"},
            {".xhtm", "application/xhtml+xml"},
            {".xhtml", "application/xhtml+xml"},
            {".xla", "application/vnd.ms-excel"},
            {".xlc", "application/vnd.ms-excel"},
            {".xll", "application/x-excel"},
            {".xlm", "application/vnd.ms-excel"},
            {".xls", "application/vnd.ms-excel"},
            {".xlt", "application/vnd.ms-excel"},
            {".xlw", "application/vnd.ms-excel"},
            {".xm", "audio/x-mod"},
            {".xml", "text/xml"},
            {".xmz", "audio/x-mod"},
            {".xpi", "application/x-xpinstall"},
            {".xpm", "image/x-xpixmap"},
            {".xsit", "text/xml"},
            {".xsl", "text/xml"},
            {".xul", "text/xul"},
            {".xwd", "image/x-xwindowdump"},
            {".xyz", "chemical/x-pdb"},
            {".yz1", "application/x-yz1"},
            {".z", "application/x-compress"},
            {".zac", "application/x-zaurus-zac"},
            {".zip", "application/zip"},
            {"", "*/*"}
            //
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