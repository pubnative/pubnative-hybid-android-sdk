package net.pubnative.lite.sdk.vpaid.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import net.pubnative.lite.sdk.vpaid.enums.ConnectionType;

public class RequestParametersProvider {

    private static final String LOG_TAG = RequestParametersProvider.class.getSimpleName();

    private static RequestParametersProvider sProvider;

    private String mAdvertisingId;
    private String mId;
    private String mAppKey;

    private RequestParametersProvider() {
    }

    public static RequestParametersProvider getInstance() {
        if (sProvider == null) {
            sProvider = new RequestParametersProvider();
        }
        return sProvider;
    }

    public void init(String appKey) {
        mAppKey = appKey;
    }

    public String getAppKey() {
        if (mAppKey == null) {
            return "unknown";
        } else {
            return mAppKey;
        }
    }

    int getConnectionType(Context context) {
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

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                return ConnectionType.UNKNOWN;
            }

            int networkType = telephonyManager.getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return ConnectionType.MOBILE_2G;

                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return ConnectionType.MOBILE_3G;

                case TelephonyManager.NETWORK_TYPE_LTE:
                    return ConnectionType.MOBILE_4G;

                default:
                    return ConnectionType.MOBILE_UNKNOWN_GENERATION;
            }
        } else {
            return ConnectionType.UNKNOWN;
        }
    }
}
