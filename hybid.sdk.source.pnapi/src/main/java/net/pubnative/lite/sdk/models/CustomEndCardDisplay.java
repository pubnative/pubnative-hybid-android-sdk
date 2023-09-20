package net.pubnative.lite.sdk.models;

import android.text.TextUtils;

import java.util.Locale;

public enum CustomEndCardDisplay {

    EXTENSION("extension"),
    FALLBACK("fallback");

    CustomEndCardDisplay(String display) {
        this.display = display;
    }

    public final String display;

    public static CustomEndCardDisplay fromString(String name) {
        if (TextUtils.isEmpty(name)) return FALLBACK;

        String lowercaseName = name.toLowerCase(Locale.ENGLISH);

        if (lowercaseName.equals(EXTENSION.display))
            return EXTENSION;
        else if (lowercaseName.equals(FALLBACK.display))
            return FALLBACK;

        return FALLBACK;
    }
}
