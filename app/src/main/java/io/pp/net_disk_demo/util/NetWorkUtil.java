package io.pp.net_disk_demo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtil {

    /**
     * This method is used to determine whether the network state is available
     */
    public static boolean isNetConnected(Context context) {
        boolean mIsNetConnected = false;
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo network = conManager.getActiveNetworkInfo();
        if (network != null) {
            mIsNetConnected = network.isConnected();
        }

        return mIsNetConnected;
    }
}