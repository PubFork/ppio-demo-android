package io.pp.net_disk_demo;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

public class MyApplication extends MultiDexApplication {

    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        Context context = getApplicationContext();
        //String packageName = context.getPackageName();
        //String processName = getProcessName();

        //if set it as report prohress
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        //strategy.setUploadProcess(processName == null || processName.equals(packageName));
        strategy.setUploadProcess(true);

        // init Bugly
        //CrashReport.initCrashReport(context, "9527", true, strategy);

        // if init the app's information by AndroidManifest.xml, use this method
        CrashReport.initCrashReport(context, strategy);
        strategy.setAppReportDelay(1000);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}