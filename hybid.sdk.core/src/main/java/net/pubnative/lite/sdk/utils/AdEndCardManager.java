package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.models.Ad;

public class AdEndCardManager {

    private static final Boolean END_CARD_ENABLED = false;

    public static Boolean isEndCardEnabled(Ad ad, Boolean remoteConfig, Boolean rendering, Boolean adParams) {
        if (ad == null || !ad.hasEndCard())
            return END_CARD_ENABLED;
        if (remoteConfig != null)
            return remoteConfig;
        if (rendering != null)
            return rendering;
        if (adParams != null)
            return adParams;

        return END_CARD_ENABLED;
    }

    public static Boolean getDefaultEndCard() {
        return END_CARD_ENABLED;
    }
}
