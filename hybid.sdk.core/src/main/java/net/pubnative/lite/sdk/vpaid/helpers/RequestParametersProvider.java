package net.pubnative.lite.sdk.vpaid.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import net.pubnative.lite.sdk.vpaid.enums.ConnectionType;

public class RequestParametersProvider {
    @SuppressLint("MissingPermission")
    static int getConnectionType(Context context) {
        if (context == null) {
            return ConnectionType.UNKNOWN;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return ConnectionType.UNKNOWN;
        }

        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return ConnectionType.UNKNOWN;
        }

        int type = ni.getType();

        if (type == ConnectivityManager.TYPE_WIFI) {
            return ConnectionType.WIFI;
        } else if (type == ConnectivityManager.TYPE_ETHERNET) {
            return ConnectionType.ETHERNET;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            return ConnectionType.MOBILE;
        } else {
            return ConnectionType.UNKNOWN;
        }
    }
}
