package net.pubnative.lite.sdk.utils;

import android.text.TextUtils;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.Ad;

public class AdCustomCTAManager {

    public static final Integer CUSTOM_CTA_DELAY_DEFAULT = 2;
    public static final Integer CUSTOM_CTA_DELAY_MAX = 10;

    public static Boolean isAbleShow(Ad ad) {
        return isEnabled(ad) && hasIcon(ad);
    }

    public static Integer getCustomCtaDelay(Ad ad){
        Integer delay = ad.getCustomCTADelay() != null && ad.getCustomCTADelay() > 0
                ? ad.getCustomCTADelay() : CUSTOM_CTA_DELAY_DEFAULT;
        return Math.min(delay, CUSTOM_CTA_DELAY_MAX);
    }

    private static Boolean isEnabled(Ad ad){
        return ad.isCustomCTAEnabled() != null && ad.isCustomCTAEnabled();
    }

    private static Boolean hasIcon(Ad ad){
        return ad.hasCustomCTA() &&
                !TextUtils.isEmpty(ad.getAsset(APIAsset.CUSTOM_CTA).getStringField("icon")) &&
                URLValidator.isValidURL(ad.getAsset(APIAsset.CUSTOM_CTA).getStringField("icon"));
    }
}
