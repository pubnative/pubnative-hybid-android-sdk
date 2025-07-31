// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//

package net.pubnative.lite.sdk.models;

import java.util.Locale;

public enum LearnMoreSize {
    DEFAULT("default"),
    MEDIUM("medium"),
    LARGE("large");

    final String location;

    LearnMoreSize(String location) {
        this.location = location;
    }

    public String getSizeName() {
        return location;
    }

    public static LearnMoreSize fromString(String size) {
        if (size != null) {
            String sizeNameLowerCase = size.toLowerCase(Locale.ROOT);
            if (sizeNameLowerCase.equals(DEFAULT.location))
                return DEFAULT;
            if (sizeNameLowerCase.equals(MEDIUM.location))
                return MEDIUM;
            if (sizeNameLowerCase.equals(LARGE.location))
                return LARGE;
        }
        return DEFAULT;
    }
}
