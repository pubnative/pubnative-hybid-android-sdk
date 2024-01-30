package net.pubnative.lite.sdk.utils;

import android.content.Context;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.prefs.HyBidPreferences;

public class AdTopicsAPIManager {

    HyBidPreferences preferences;

    private final Ad ad;

    public AdTopicsAPIManager(Ad ad, Context context) {
        this.ad = ad;
        if (context != null) {
            preferences = new HyBidPreferences(context);
        }
    }

    public Boolean isTopicsAPIEnabled() {
        if (ad == null) return false;
        Boolean isEnabled = ad.isTopicsAPIEnabled();
        if (isEnabled == null) {
            if (preferences != null)
                isEnabled = preferences.isTopicsAPIEnabled();
        } else {
            if (preferences != null)
                preferences.setTopicsAPIEnabled(isEnabled);
        }
        if (isEnabled == null)
            isEnabled = false;
        return isEnabled;
    }
}