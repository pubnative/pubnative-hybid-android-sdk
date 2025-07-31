// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.mraid.properties;

import java.util.Arrays;
import java.util.List;

public final class MRAIDResizeProperties {
    public static final int CUSTOM_CLOSE_POSITION_TOP_LEFT = 0;
    public static final int CUSTOM_CLOSE_POSITION_TOP_CENTER = 1;
    public static final int CUSTOM_CLOSE_POSITION_TOP_RIGHT = 2;
    public static final int CUSTOM_CLOSE_POSITION_CENTER = 3;
    public static final int CUSTOM_CLOSE_POSITION_BOTTOM_LEFT = 4;
    public static final int CUSTOM_CLOSE_POSITION_BOTTOM_CENTER = 5;
    public static final int CUSTOM_CLOSE_POSITION_BOTTOM_RIGHT = 6;

    public int width;
    public int height;
    public int offsetX;
    public int offsetY;
    public int customClosePosition;
    public boolean allowOffscreen;

    public MRAIDResizeProperties() {
        this(0, 0, 0, 0, CUSTOM_CLOSE_POSITION_TOP_RIGHT, true);
    }

    public MRAIDResizeProperties(
            int width,
            int height,
            int offsetX,
            int offsetY,
            int customClosePosition,
            boolean allowOffscreen) {
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.customClosePosition = customClosePosition;
        this.allowOffscreen = allowOffscreen;
    }

    public static int customClosePositionFromString(String name) {
        final List<String> names = Arrays.asList(
                "top-left",
                "top-center",
                "top-right",
                "center",
                "bottom-left",
                "bottom-center",
                "bottom-right"
        );
        int idx = names.indexOf(name);
        if (idx != -1) {
            return idx;
        }
        // Use top-right for the default value.
        return CUSTOM_CLOSE_POSITION_TOP_RIGHT;
    }
}
