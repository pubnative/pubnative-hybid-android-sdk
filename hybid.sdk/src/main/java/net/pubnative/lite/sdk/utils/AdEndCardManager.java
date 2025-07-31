// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.models.Ad;

public class AdEndCardManager {

    private static final Boolean END_CARD_ENABLED = true;
    private static final Boolean CUSTOM_END_CARD_ENABLED = false;

    public static Boolean isEndCardEnabled(Ad ad) {
        if (ad == null) return false;

        boolean shouldShow = shouldShowEndcard(ad);

        if (!shouldShow) {
            shouldShow = shouldShowCustomEndcard(ad);
        }

        return shouldShow;
    }

    public static boolean shouldShowEndcard(Ad ad) {
        if (ad.hasEndCard()) {
            if (hasEndcardRemoteConfig(ad)) {
                return ad.isEndCardEnabled();
            } else {
                return END_CARD_ENABLED;
            }
        }
        return false;
    }

    public static boolean shouldShowCustomEndcard(Ad ad) {
        if (ad.hasCustomEndCard()) {
            if (ad.isCustomEndCardEnabled() != null) {
                return ad.isCustomEndCardEnabled();
            } else {
                return CUSTOM_END_CARD_ENABLED;
            }
        }
        return false;
    }

    private static Boolean hasEndcardRemoteConfig(Ad ad) {
        return ad.isEndCardEnabled() != null;
    }

    public static Boolean getDefaultEndCard() {
        return END_CARD_ENABLED;
    }
}
