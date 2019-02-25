package io.pp.net_disk_demo.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Properties;

public class SystemUtil {

    private static final String TAG = "SystemUtil";

    /**
     * Get the current phone system language.
     *
     * @return Returns the current system language. For example: if "Chinese-Chinese" is currently set, then "zh-CN" is returned.
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * Get the list of languages on the current system (Locale list)
     *
     * @return Language list
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * Get the current mobile phone system version number
     *
     * @return System version number
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * Get the phone model
     *
     * @return Phone model
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * Get mobile phone manufacturers
     *
     * @return Mobile phone manufacturer
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * Get the phone IMEI (requires "android.permission.READ_PHONE_STATE" permission)
     *
     * @return phone IMEI
     */
    public static String getIMEI(Context ctx) {
        try {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
            if (tm != null) {
                return tm.getDeviceId();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final String ROM_MIUI = "MIUI";
    public static final String ROM_EMUI = "EMUI";
    public static final String ROM_FLYME = "FLYME";
    public static final String ROM_OPPO = "OPPO";
    public static final String ROM_SMARTISAN = "SMARTISAN";
    public static final String ROM_VIVO = "VIVO";
    public static final String ROM_QIKU = "QIKU";

    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";
    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";
    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";
    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";
    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";

    private static String sName;
    private static String sVersion;

    public static boolean isEmui() {
        return check(ROM_EMUI);
    }

    public static boolean isMiui() {
        return check(ROM_MIUI);
    }

    public static boolean isVivo() {
        return check(ROM_VIVO);
    }

    public static boolean isOppo() {
        return check(ROM_OPPO);
    }

    public static boolean isFlyme() {
        return check(ROM_FLYME);
    }

    public static boolean is360() {
        return check(ROM_QIKU) || check("360");
    }

    public static boolean isSmartisan() {
        return check(ROM_SMARTISAN);
    }

    public static String getName() {
        if (sName == null) {
            check("");
        }
        return sName;
    }

    public static String getVersion() {
        if (sVersion == null) {
            check("");
        }
        return sVersion;
    }

    public static boolean check(String rom) {
        if (sName != null) {
            return sName.equals(rom);
        }

        if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_MIUI))) {
            sName = ROM_MIUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_EMUI))) {
            sName = ROM_EMUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_OPPO))) {
            sName = ROM_OPPO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_VIVO))) {
            sName = ROM_VIVO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_SMARTISAN))) {
            sName = ROM_SMARTISAN;
        } else {
            sVersion = Build.DISPLAY;
            if (sVersion.toUpperCase().contains(ROM_FLYME)) {
                sName = ROM_FLYME;
            } else {
                sVersion = Build.UNKNOWN;
                sName = Build.MANUFACTURER.toUpperCase();
            }
        }
        return sName.equals(rom);
    }

    public static String getProp(String name) {
        String line = null;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read prop " + name, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    public static String getABIS() {
        String aibsStr = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] aibs = Build.SUPPORTED_ABIS;
            if (aibs != null) {
                for (int i = 0; i < aibs.length; i++) {
                    if (i < aibs.length && i > 0) {
                        aibsStr = aibsStr + ", ";
                    }
                    aibsStr = aibsStr + aibs[i];
                }
            }
        } else {
            aibsStr = Build.CPU_ABI + ", " + Build.CPU_ABI2;
        }

        return aibsStr;
    }

    public static String getAppVersion(Context context) {
        try {
            return context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();

            return "?";
        }
    }

    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}