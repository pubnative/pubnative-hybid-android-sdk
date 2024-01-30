package net.pubnative.lite.sdk.models;

import android.text.TextUtils;

import java.util.Locale;

public enum ImpressionTrackingMethod {

    AD_RENDERED("rendered"),
    AD_VIEWABLE("viewable");

    ImpressionTrackingMethod(String methodName) {
        this.methodName = methodName;
    }

    public final String methodName;

    public static ImpressionTrackingMethod fromString(String name) {
        if (TextUtils.isEmpty(name)) return AD_VIEWABLE;

        String lowercaseName = name.toLowerCase(Locale.ENGLISH);

        if (lowercaseName.equals(AD_RENDERED.methodName))
            return AD_RENDERED;
        else if (lowercaseName.equals(AD_VIEWABLE.methodName))
            return AD_VIEWABLE;

        return AD_VIEWABLE;
    }
}
