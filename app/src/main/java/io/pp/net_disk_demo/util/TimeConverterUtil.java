package io.pp.net_disk_demo.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeConverterUtil {

    private static String TAG = "TimeConverterUtil";

    public static String GTMToLocal(String GTMDate) {
        int tIndex = GTMDate.indexOf("T");
        String dateTemp = GTMDate.substring(0, tIndex);
        String timeTemp = GTMDate.substring(tIndex + 1, 20);
        String convertString = dateTemp + " " + timeTemp;

        SimpleDateFormat format;
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date result_date;
        long result_time;

        if (null == GTMDate) {
            return GTMDate;
        } else {
            try {
                format.setTimeZone(TimeZone.getTimeZone("GMT00:00"));
                result_date = format.parse(convertString);
                result_time = result_date.getTime();
                format.setTimeZone(TimeZone.getDefault());

                return format.format(result_time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return GTMDate;
    }

    public static String UTCToCST(String UTCStr) throws ParseException {
        final String format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(UTCStr);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8);
        //calendar.getTime() return Date type，you can also use calendar.getTimeInMillis() to get time stamp

        return calendar.getTime().toString();
    }

    /**
     * Method Description: Convert UTC time to local time format
     *
     * @param utcTime         UTC time
     * @param utcTimePatten   UTC time format
     * @param localTimePatten locale time format
     * @return Time displayed in local time format
     * eg:utc2Local(utcTime: "2017-06-14 09:37:50.788+08:00",
     * utcTimePatten: "yyyy-MM-dd HH:mm:ss.SSSXXX",
     * localTimePatten: "yyyy-MM-dd HH:mm:ss.SSS")
     */
    public static String utc2Local(String utcTime, String utcTimePatten, String localTimePatten) {
        SimpleDateFormat utcFormat = new SimpleDateFormat(utcTimePatten);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));//define Time zone
        Date gpsUTCDate;
        try {
            gpsUTCDate = utcFormat.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return utcTime;
        }

        SimpleDateFormat localFormat = new SimpleDateFormat(localTimePatten);
        localFormat.setTimeZone(TimeZone.getDefault());

        return localFormat.format(gpsUTCDate.getTime());
    }

    /**
     * Function description: Convert UTC time to local time format
     *
     * @param utcTime          UTC time
     * @param localTimePattern The local time format (that will be converted into)
     * @return Local time format time
     */
    public static String utc2Local(String utcTime, String localTimePattern) {
        String utcTimePattern = "yyyy-MM-dd";
        //The UTC time format begins with yyyy-mm-dd, intercepting the first 10 bits of the UTC time,
        // followed by data containing information in the multi-time zone time format
        String subTime = utcTime.substring(10);

        //When the suffix is :+8:00, it is converted to :+08:00 , or convert -8:00 to -08:00
        if (subTime.contains("+")) {
            subTime = changeUtcSuffix(subTime, "\\+");
        }
        if (subTime.contains("-")) {
            subTime = changeUtcSuffix(subTime, "\\-");
        }

        utcTime = utcTime.substring(0, 10) + subTime;

        //When the suffix is :+8:00, it is converted to :+08:00, or convert -8:00 to -08:00
        //Step 1: process T
        if(utcTime.contains("T")) {
            utcTimePattern = utcTimePattern + "'T'";
        }

        //Step 2: process the ms SSS
        if (utcTime.contains(".")) {
            utcTimePattern = utcTimePattern + " HH:mm:ss.SSS";
        } else {
            utcTimePattern = utcTimePattern + " HH:mm:ss";
        }

        //Step 3: deal with the time zone problem
        if (subTime.contains("+") || subTime.contains("-")) {
            utcTimePattern = utcTimePattern + "XXX";
        } else if (subTime.contains("Z")) {
            utcTimePattern = utcTimePattern + "'Z'";
        }

        if ("yyyy-MM-dd HH:mm:ss".equals(utcTimePattern) || "yyyy-MM-dd HH:mm:ss.SSS".equals(utcTimePattern)) {
            return utcTime;
        }

        SimpleDateFormat utcFormat = new SimpleDateFormat(utcTimePattern);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUtcDate;
        try {
            gpsUtcDate = utcFormat.parse(utcTime);
        } catch (Exception e) {
            Log.e(TAG, "utcTime converter localTime failed!!!", e);
            return utcTime;
        }

        SimpleDateFormat localFormat = new SimpleDateFormat(localTimePattern);
        localFormat.setTimeZone(TimeZone.getDefault());

        return localFormat.format(gpsUtcDate.getTime());
    }

    /**
     * Function description: modify the time format suffix
     * Function description: When the suffix is :+8:00, it is converted to :+08:00 or -8:00 to -08:00
     *
     * @param subTime params subTime
     * @param sign    params sign
     * @return subTime return value
     */
    private static String changeUtcSuffix(String subTime, String sign) {
        String timeSuffix;
        String[] splitTimeArrayOne = subTime.split(sign);
        String[] splitTimeArrayTwo = splitTimeArrayOne[1].split(":");
        if (splitTimeArrayTwo[0].length() < 2) {
            timeSuffix = "+" + "0" + splitTimeArrayTwo[0] + ":" + splitTimeArrayTwo[1];
            subTime = splitTimeArrayOne[0] + timeSuffix;
            return subTime;
        }
        return subTime;
    }

    /**
     * Function description: get a representation of the local time zone (for example: zone 8 -->+08:00)
     *
     * @return timeZoneByNumExpressStr return value
     */
    public static String getTimeZoneByNumExpress() {
        Calendar cal = Calendar.getInstance();
        TimeZone timeZone = cal.getTimeZone();
        int rawOffset = timeZone.getRawOffset();
        int timeZoneByNumExpress = rawOffset / 3600 / 1000;
        String timeZoneByNumExpressStr;
        if (timeZoneByNumExpress > 0 && timeZoneByNumExpress < 10) {
            timeZoneByNumExpressStr = "+" + "0" + timeZoneByNumExpress + ":" + "00";
        } else if (timeZoneByNumExpress >= 10) {
            timeZoneByNumExpressStr = "+" + timeZoneByNumExpress + ":" + "00";
        } else if (timeZoneByNumExpress > -10 && timeZoneByNumExpress < 0) {
            timeZoneByNumExpress = Math.abs(timeZoneByNumExpress);
            timeZoneByNumExpressStr = "-" + "0" + timeZoneByNumExpress + ":" + "00";
        } else if (timeZoneByNumExpress <= -10) {
            timeZoneByNumExpress = Math.abs(timeZoneByNumExpress);
            timeZoneByNumExpressStr = "-" + timeZoneByNumExpress + ":" + "00";
        } else {
            timeZoneByNumExpressStr = "Z";
        }
        return timeZoneByNumExpressStr;
    }
}