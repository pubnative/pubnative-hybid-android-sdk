package net.pubnative.lite.sdk.models;

import android.text.TextUtils;

import java.util.Locale;

public enum ContentInfoIconXPosition {
    LEFT("left"),
    RIGHT("right");

    ContentInfoIconXPosition(String position) {
        this.horizontalPosition = position;
    }
    public final String horizontalPosition;

    public static ContentInfoIconXPosition fromString(String name) {
        if (TextUtils.isEmpty(name)) return null;

        String lowercaseName = name.toLowerCase(Locale.ENGLISH);

        if (lowercaseName.equals(LEFT.horizontalPosition))
            return LEFT;
        else if (lowercaseName.equals(RIGHT.horizontalPosition))
            return RIGHT;

        return LEFT;
    }
}
