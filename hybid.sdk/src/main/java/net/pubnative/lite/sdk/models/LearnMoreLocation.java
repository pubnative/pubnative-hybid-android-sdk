// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//

package net.pubnative.lite.sdk.models;

import java.util.Locale;

public enum LearnMoreLocation {
    DEFAULT("default"),
    BOTTOM_DOWN("bottom_down"),
    BOTTOM_UP("bottom_up");

    final String size;

    LearnMoreLocation(String size) {
        this.size = size;
    }

    public String getLocationName() {
        return size;
    }

    public static LearnMoreLocation fromString(String location) {
        if (location != null) {
            String locationNameLowerCase = location.toLowerCase(Locale.ROOT);
            if (locationNameLowerCase.equals(DEFAULT.size))
                return DEFAULT;
            if (locationNameLowerCase.equals(BOTTOM_DOWN.size))
                return BOTTOM_DOWN;
            if (locationNameLowerCase.equals(BOTTOM_UP.size))
                return BOTTOM_UP;
        }
        return DEFAULT;
    }
}
