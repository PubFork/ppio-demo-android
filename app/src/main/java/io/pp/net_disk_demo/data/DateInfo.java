package io.pp.net_disk_demo.data;

public class DateInfo {
    private int mYear;
    private int mMonthOfYear;
    private int mDayOfMonth;

    public DateInfo() {

    }

    public DateInfo(int year, int monthOfYear, int dayOfMonth) {
        mYear = year;
        mMonthOfYear = monthOfYear;
        mDayOfMonth = dayOfMonth;
    }

    public String getDate() {
        String monthStr = "";
        String dayStr = "";

        int month = mMonthOfYear + 1;
        if (month < 10) {
            monthStr = monthStr + 0 + month;
        } else {
            monthStr = "" + month;
        }

        if (mDayOfMonth < 10) {
            dayStr = "0" + mDayOfMonth;
        } else {
            dayStr = "" + mDayOfMonth;
        }

        return mYear + "-" + monthStr + "-" + dayStr;
    }

    public int getYear() {
        return mYear;
    }

    public int getMonthOfYear() {
        return mMonthOfYear;
    }

    public int getDayOfMonth() {
        return mDayOfMonth;
    }
}