package io.pp.net_disk_demo.util;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.Locale;

public class SystemUtil {

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

}