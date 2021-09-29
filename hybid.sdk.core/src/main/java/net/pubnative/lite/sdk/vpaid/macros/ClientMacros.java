package net.pubnative.lite.sdk.vpaid.macros;

import android.location.Location;
import android.text.TextUtils;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.core.BuildConfig;
import net.pubnative.lite.sdk.location.HyBidLocationManager;
import net.pubnative.lite.sdk.utils.EncodingUtils;

import java.util.Locale;

public class ClientMacros {
    private static final String MACRO_IFA = "[IFA]";
    private static final String MACRO_IFA_TYPE = "[IFATYPE]";
    private static final String MACRO_CLIENT_UA = "[CLIENTUA]";
    private static final String MACRO_SERVER_UA = "[SERVERUA]";
    private static final String MACRO_DEVICE_UA = "[DEVICEUA]";
    private static final String MACRO_SERVER_SIDE = "[SERVERSIDE]";
    private static final String MACRO_DEVICE_IP = "[DEVICEIP]";
    private static final String MACRO_LAT_LONG = "[LATLONG]";

    private final DeviceInfo mDeviceInfo;
    private final HyBidLocationManager mLocationManager;
    private final String mClientUserAgent;
    private final String mDeviceUserAgent;

    public ClientMacros() {
        this(HyBid.getDeviceInfo(), HyBid.getLocationManager());
    }

    ClientMacros(DeviceInfo deviceInfo, HyBidLocationManager locationManager) {
        mDeviceInfo = deviceInfo;
        mLocationManager = locationManager;
        String playerName = "HyBid";
        String playerVersion = BuildConfig.SDK_VERSION;
        String pluginName = "HyBid VAST Player";
        mClientUserAgent = EncodingUtils.urlEncode(String.format(Locale.ENGLISH, "%s/%s %s/%s", playerName, playerVersion, pluginName, playerVersion));
        //TODO get device user agent
        mDeviceUserAgent = EncodingUtils.urlEncode("");
    }

    public String processUrl(String url) {
        return url
                .replace(MACRO_IFA, getIfa())
                .replace(MACRO_IFA_TYPE, getIfaType())
                .replace(MACRO_CLIENT_UA, getClientUA())
                .replace(MACRO_DEVICE_UA, getDeviceUA())
                .replace(MACRO_SERVER_SIDE, getServerSide())
                .replace(MACRO_LAT_LONG, getLocation());
    }

    private String getIfa() {
        if (mDeviceInfo != null) {
            if (mDeviceInfo.limitTracking()) {
                return String.valueOf(MacroDefaultValues.VALUE_BLOCKED);
            } else {
                if (TextUtils.isEmpty(mDeviceInfo.getAdvertisingId()))
                    return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
                return mDeviceInfo.getAdvertisingId();
            }
        } else {
            return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
        }
    }

    private String getIfaType() {
        if (mDeviceInfo != null) {
            if (mDeviceInfo.limitTracking()) {
                return String.valueOf(MacroDefaultValues.VALUE_BLOCKED);
            } else {
                return "aaid";
            }
        } else {
            return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
        }
    }

    private String getClientUA() {
        return mClientUserAgent;
    }

    private String getServerUA() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getDeviceUA() {
        return mDeviceUserAgent;
    }

    private String getServerSide() {
        return "0";
    }

    private String getDeviceIp() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getLocation() {
        if (mLocationManager != null) {
            Location location = mLocationManager.getUserLocation();
            if (location != null) {
                return String.format(Locale.ENGLISH, "%f,%f", location.getLatitude(), location.getLongitude());
            } else {
                return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
            }
        } else {
            return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
        }
    }
}
