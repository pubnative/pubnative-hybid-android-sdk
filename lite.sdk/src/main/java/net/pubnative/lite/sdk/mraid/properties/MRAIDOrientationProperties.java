package net.pubnative.lite.sdk.mraid.properties;

import java.util.Arrays;
import java.util.List;

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public final class MRAIDOrientationProperties {
    public static final int FORCE_ORIENTATION_PORTRAIT = 0;
    public static final int FORCE_ORIENTATION_LANDSCAPE = 1;
    public static final int FORCE_ORIENTATION_NONE = 2;

    // whether or not the ad orientation changes when the device orientation changes
    public boolean allowOrientationChange;

    // what orientation the ad should be displayed in regardless of allowOrientationChange or actual device orientation
    public int forceOrientation;

    public MRAIDOrientationProperties() {
        this(true, FORCE_ORIENTATION_NONE);
    }

    public MRAIDOrientationProperties(boolean allowOrientationChange, int forceOrienation) {
        this.allowOrientationChange = allowOrientationChange;
        this.forceOrientation = forceOrienation;
    }

    static public int forceOrientationFromString(String name) {
        final List<String> names = Arrays.asList("portrait", "landscape", "none");
        int idx = names.indexOf(name);
        if (idx != -1) {
            return idx;
        }
        // Use none for the default value.
        return FORCE_ORIENTATION_NONE;
    }

    public String forceOrientationString() {
        switch (forceOrientation) {
            case FORCE_ORIENTATION_PORTRAIT:
                return "portrait";
            case FORCE_ORIENTATION_LANDSCAPE:
                return "landscape";
            case FORCE_ORIENTATION_NONE:
                return "none";
            default:
                return "error";
        }
    }
}
