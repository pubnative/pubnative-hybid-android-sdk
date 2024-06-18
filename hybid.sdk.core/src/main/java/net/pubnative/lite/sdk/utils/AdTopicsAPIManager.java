package net.pubnative.lite.sdk.utils;

import android.content.Context;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.prefs.HyBidPreferences;

public class AdTopicsAPIManager {

    public static void setTopicsAPIEnabled(Context context, Ad ad) {
        if (context == null || ad == null) return;
        Boolean adIsEnabled = ad.isTopicsAPIEnabled();
        boolean isEnabled = HyBid.isTopicsApiEnabled();
        if (adIsEnabled != null && adIsEnabled != isEnabled) {
            HyBidPreferences preferences = new HyBidPreferences(context);
            preferences.setTopicsAPIEnabled(adIsEnabled);
            HyBid.setTopicsApiEnabled(adIsEnabled);
        }
    }

    public static Boolean isTopicsAPIEnabled(Context context) {
        if (context == null) return null;
        HyBidPreferences preferences = new HyBidPreferences(context);
        return preferences.isTopicsAPIEnabled();
    }
}