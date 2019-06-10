package io.pp.net_disk_demo.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * 根据格式获取当前格式化时间
     *
     * @param format 格式化方式，基础格式为yyyy-MM-dd HH:mm:ss
     * @return 当前时间
     */
    public static String getCurrentTimeByFormat(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 格式化时间
     *
     * @param format      格式化格式，基础格式为yyyy-MM-dd HH:mm:ss
     * @param currentTime
     * @return
     */
    public static String formatTime(String format, long currentTime) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date(currentTime));
    }

    public static String getCurrentTimeStr() {
        Calendar calendar = Calendar.getInstance();

        return "" + calendar.get(Calendar.YEAR) + "-" +
                calendar.get(Calendar.MONTH) + "-" +
                calendar.get(Calendar.DAY_OF_MONTH) + "-" +
                calendar.get(Calendar.HOUR_OF_DAY) + "-" +
                calendar.get(Calendar.MINUTE) + "-" +
                calendar.get(Calendar.SECOND) + "-" +
                calendar.get(Calendar.MILLISECOND) + "-";
    }
}
