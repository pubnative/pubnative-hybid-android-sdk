package net.pubnative.lite.sdk.models;

import android.text.TextUtils;

import java.util.Locale;

public enum ContentInfoIconYPosition {
    TOP("top"),
    BOTTOM("bottom");

    ContentInfoIconYPosition(String position) {
        this.verticalPosition = position;
    }

    public final String verticalPosition;

    public static ContentInfoIconYPosition fromString(String name) {
        if (TextUtils.isEmpty(name)) return TOP;

        String lowercaseName = name.toLowerCase(Locale.ENGLISH);

        if (lowercaseName.equals(TOP.verticalPosition))
            return TOP;
        else if (lowercaseName.equals(BOTTOM.verticalPosition))
            return BOTTOM;

        return TOP;
    }

    public static ContentInfoIconYPosition getDefaultYPosition() {
        return BOTTOM;
    }
}

