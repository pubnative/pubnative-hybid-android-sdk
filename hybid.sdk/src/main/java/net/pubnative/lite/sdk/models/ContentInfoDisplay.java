// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import android.text.TextUtils;

import java.util.Locale;

public enum ContentInfoDisplay {
    IN_APP("inapp"),
    SYSTEM_BROWSER("system");

    ContentInfoDisplay(String display) {
        this.display = display;
    }

    public final String display;

    public static ContentInfoDisplay fromString(String name) {
        if (TextUtils.isEmpty(name)) return SYSTEM_BROWSER;

        String lowercaseName = name.toLowerCase(Locale.ENGLISH);

        if (lowercaseName.equalsIgnoreCase(IN_APP.display))
            return IN_APP;
        else if (lowercaseName.equalsIgnoreCase(SYSTEM_BROWSER.display))
            return SYSTEM_BROWSER;

        return SYSTEM_BROWSER;
    }
}
