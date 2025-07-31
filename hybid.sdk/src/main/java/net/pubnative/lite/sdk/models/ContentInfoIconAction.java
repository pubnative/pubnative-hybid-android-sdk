// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import android.text.TextUtils;

import java.util.Locale;

public enum ContentInfoIconAction {
    EXPAND("expand"),
    OPEN("open");

    ContentInfoIconAction(String action) {
        this.action = action;
    }

    public final String action;

    public static ContentInfoIconAction fromString(String name) {
        if (TextUtils.isEmpty(name)) return EXPAND;

        String lowercaseName = name.toLowerCase(Locale.ENGLISH);

        if (lowercaseName.equals(EXPAND.action))
            return EXPAND;
        else if (lowercaseName.equals(OPEN.action))
            return OPEN;

        return EXPAND;
    }
}
