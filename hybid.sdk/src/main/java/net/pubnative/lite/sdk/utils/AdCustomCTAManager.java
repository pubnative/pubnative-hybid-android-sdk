// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.text.TextUtils;

import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdData;

public class AdCustomCTAManager {

    public static final Integer CUSTOM_CTA_DELAY_DEFAULT = 2;
    public static final Integer CUSTOM_CTA_DELAY_MAX = 10;

    public static Boolean isAbleShow(Ad ad) {
        return isEnabled(ad) && hasIcon(ad);
    }

    public static Integer getCustomCtaDelay(Ad ad){
        Integer delay = ad != null && ad.getCustomCTADelay() != null && ad.getCustomCTADelay() >= 0
                ? ad.getCustomCTADelay() : CUSTOM_CTA_DELAY_DEFAULT;
        return Math.min(delay, CUSTOM_CTA_DELAY_MAX);
    }

    public static CtaType getCustomCtaType(Ad ad) {
        String customCtaType = ad.getCustomCTAType();

        if (customCtaType != null && customCtaType.equals(CtaType.EXTENDED.toString())) {
            return CtaType.EXTENDED;
        } else {
            return CtaType.DEFAULT;
        }
    }

    public static Boolean isEnabled(Ad ad){
        return ad != null && ad.isCustomCTAEnabled() != null && ad.isCustomCTAEnabled();
    }

    private static Boolean hasIcon(Ad ad) {
        if (ad == null || !ad.hasCustomCTA()) return false;
        AdData asset = ad.getAsset(APIAsset.CUSTOM_CTA);
        String icon = (asset != null) ? asset.getStringField("icon") : null;
        return !TextUtils.isEmpty(icon) && URLValidator.isValidURL(icon);
    }

    public enum CtaType {
        DEFAULT("default"),
        EXTENDED("extended");

        private final String mCtaType;

        CtaType(String connectivity) {
            mCtaType = connectivity;
        }

        @Override
        public String toString() {
            return mCtaType;
        }
    }
}
