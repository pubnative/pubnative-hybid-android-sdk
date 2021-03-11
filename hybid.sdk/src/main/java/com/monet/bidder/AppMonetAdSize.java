package com.monet.bidder;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class AppMonetAdSize {
    public static final String AD_SIZE_KEY = "ad_size";
    final Integer height;
    final Integer width;

    public AppMonetAdSize(Integer width, Integer height) {
        this.height = height;
        this.width = width;
    }

    private static Integer from(Object representation, int defaultValue) {
        if (representation == null) {
            return defaultValue;
        }

        Integer parsed = null;
        if (representation instanceof String) {
            parsed = Integer.parseInt((String) representation, 10);
        } else if (representation instanceof Integer) {
            parsed = (Integer) representation;
        }

        return (parsed != null && parsed > 0) ? parsed : defaultValue;
    }

    int getHeightInPixels(Context context) {
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, displayMetrics);
    }

    int getWidthInPixels(Context context) {
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, displayMetrics);
    }
}
