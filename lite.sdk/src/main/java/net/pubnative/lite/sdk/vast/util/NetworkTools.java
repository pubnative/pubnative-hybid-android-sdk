//
//  NetworkTools.java
//
//  Copyright (c) 2014 Nexage. All rights reserved.
//

package net.pubnative.lite.sdk.vast.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import net.pubnative.lite.sdk.utils.Logger;

public class NetworkTools {

    private static final String TAG = NetworkTools.class.getName();

    // This method return true if it's connected to Internet
    public static boolean isConnectedToInternet(Context context) {

        Logger.d(TAG, "Testing connectivity:");

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiNetwork != null && wifiNetwork.isConnected()) {

            Logger.d(TAG, "Connected to Internet");
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (mobileNetwork != null && mobileNetwork.isConnected()) {

            Logger.d(TAG, "Connected to Internet");
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected()) {

            Logger.d(TAG, "Connected to Internet");
            return true;
        }

        Logger.d(TAG, "No Internet connection");
        return false;
    }
}
