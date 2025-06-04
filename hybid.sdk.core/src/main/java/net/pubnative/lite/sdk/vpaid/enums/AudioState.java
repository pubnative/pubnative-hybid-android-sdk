// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.enums;

import java.util.Locale;

public enum AudioState {
    MUTED("muted"),
    ON("on"),
    DEFAULT("default");

    final String stateName;

    AudioState(String stateName) {
        this.stateName = stateName;
    }

    public String getStateName() {
        return stateName;
    }

    public static AudioState fromString(String stateName) {
        String stateNameLowerCase = stateName.toLowerCase(Locale.ROOT);
        if (stateNameLowerCase.equals(MUTED.stateName))
            return MUTED;
        if (stateNameLowerCase.equals(ON.stateName))
            return ON;
        if (stateNameLowerCase.equals(DEFAULT.stateName))
            return DEFAULT;
        return null;
    }
}
