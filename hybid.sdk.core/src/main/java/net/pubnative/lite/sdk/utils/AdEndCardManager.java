package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;

public class AdEndCardManager {

    private static final Boolean END_CARD_ENABLED = true;

    public static Boolean isEndCardEnabled(Ad ad, Boolean adParams) {

        if (ad == null || !isAbleShowEndCard(ad))
            return false;
        if (ad.isCustomEndCardEnabled() != null && ad.isCustomEndCardEnabled())
            return ad.isCustomEndCardEnabled();
        if (ad.isEndCardEnabled() != null)
            return ad.isEndCardEnabled();
        if (HyBid.isEndCardEnabled() != null)
            return HyBid.isEndCardEnabled();
        if (adParams != null && adParams)
            return adParams;

        return END_CARD_ENABLED;
    }

    public static Boolean isAbleShowEndCard(Ad ad){
        Boolean ableShowEndCard = ((ad.isEndCardEnabled() != null && ad.isEndCardEnabled()) || (ad.isEndCardEnabled() == null && HyBid.isEndCardEnabled() != null && HyBid.isEndCardEnabled())) && ad.hasEndCard();
        Boolean ableSHowCustomEndCard = (ad.isCustomEndCardEnabled() != null && ad.isCustomEndCardEnabled()) && ad.hasCustomEndCard();
        return ableShowEndCard || ableSHowCustomEndCard;
    }

    public static Boolean getDefaultEndCard() {
        return END_CARD_ENABLED;
    }
}
