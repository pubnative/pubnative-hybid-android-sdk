package net.pubnative.lite.sdk.utils;

import android.content.Context;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.prefs.HyBidPreferences;

public class AtomManager {

    static Boolean mIsAtomConfigEnabled = null;

    public static void setAtomEnabled(Context context, Ad ad) {
        if (context == null || ad == null) return;
        Boolean adIsEnabled = ad.isAtomEnabled();
        Boolean atomValue;
        if (adIsEnabled != null) {
            atomValue = adIsEnabled;
        } else if (mIsAtomConfigEnabled != null) {
            atomValue = mIsAtomConfigEnabled;
        } else {
            atomValue = HyBid.isAtomEnabled();
        }
        HyBidPreferences preferences = new HyBidPreferences(context);
        preferences.setAtomEnabled(atomValue);
//        HyBid.setAtomEnabled(atomValue);
    }

    public static Boolean isAtomEnabled(Context context) {
//        if (context == null) return null;
//        HyBidPreferences preferences = new HyBidPreferences(context);
//        return preferences.isAtomEnabled();
        return HyBid.isAtomEnabled();
    }

    public static void setAtomSDKConfig(Boolean isAtomEnabled) {
        mIsAtomConfigEnabled = isAtomEnabled;
    }
}