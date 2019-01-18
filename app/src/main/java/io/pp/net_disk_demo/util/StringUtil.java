package io.pp.net_disk_demo.util;

import android.util.Log;

public class StringUtil {

    private static final String TAG = "StringUtil";

    /**
     * Sorts an array of strings
     *
     * @param keys params key
     * @return keys return value keys
     */
    public static String[] sortStrings(String[] keys) {
        for (int i = 0; i < keys.length - 1; i++) {
            for (int j = 0; j < keys.length - i - 1; j++) {
                String pre = keys[j];
                String next = keys[j + 1];
                if (isBiggerThan(pre, next)) {
                    keys[j] = next;
                    keys[j + 1] = pre;
                }
            }
        }
        return keys;
    }

    /**
     * Compares the size of two strings by alphabetic ASCII code
     *
     * @param pre params pre
     * @param next params next
     * @return return boolean value
     */
    public static boolean isBiggerThan(String pre, String next) {
        if (null == pre || null == next || "".equals(pre) || "".equals(next)) {
            Log.e(TAG, "String comparison data cannot be empty!");
            return false;
        }

        char[] c_pre = pre.toCharArray();
        char[] c_next = next.toCharArray();

        int minSize = Math.min(c_pre.length, c_next.length);

        for (int i = 0; i < minSize; i++) {
            if ((int) c_pre[i] > (int) c_next[i]) {
                return true;
            } else if ((int) c_pre[i] < (int) c_next[i]) {
                return false;
            }
        }

        if (c_pre.length > c_next.length) {
            return true;
        }

        return false;
    }

}