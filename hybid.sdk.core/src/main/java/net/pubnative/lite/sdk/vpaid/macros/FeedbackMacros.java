package net.pubnative.lite.sdk.vpaid.macros;

import android.text.TextUtils;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.vpaid.enums.AdFormat;

import java.util.Locale;

public class FeedbackMacros {
    private static final String MACRO_APP_TOKEN = "[APPTOKEN]";
    private static final String MACRO_ZONE_ID = "[ZONEID]";
    private static final String MACRO_AUDIO_STATE = "[AUDIOSTATE]";
    private static final String MACRO_APP_VERSION = "[APPVERSION]";
    private static final String MACRO_DEVICE_INFO = "[DEVICEINFO]";
    private static final String MACRO_CREATIVE_ID = "[CREATIVEID]";
    private static final String MACRO_IMPRESSION_BEACON = "[IMPRESSIONBEACON]";
    private static final String MACRO_SDK_VERSION = "[SDKVERSION]";
    private static final String MACRO_INTEGRATION_TYPE = "[INTEGRATIONTYPE]";
    private static final String MACRO_AD_FORMAT = "[ADFORMAT]";
    private static final String MACRO_HAS_END_CARD = "[HASENDCARD]";

    private final DeviceInfo mDeviceInfo;

    public FeedbackMacros() {
        this(HyBid.getDeviceInfo());
    }

    public FeedbackMacros(DeviceInfo deviceInfo) {
        mDeviceInfo = deviceInfo;
    }

    public String processUrl(String url, Ad ad, String adFormat, IntegrationType integrationType) {
        if (HyBid.isInitialized() && !TextUtils.isEmpty(HyBid.getAppToken())) {
            url = url.replace(MACRO_APP_TOKEN, HyBid.getAppToken());
        }

        if (!TextUtils.isEmpty(HyBid.getSDKVersionInfo())) {
            url = url.replace(MACRO_SDK_VERSION, HyBid.getSDKVersionInfo());
        }

        if (!TextUtils.isEmpty(HyBid.getAppVersion())) {
            url = url.replace(MACRO_APP_VERSION, HyBid.getAppVersion());
        }

        if (!TextUtils.isEmpty(adFormat)) {
            url = url.replace(MACRO_AD_FORMAT, adFormat);
        }

        if (integrationType != null) {
            url = url.replace(MACRO_INTEGRATION_TYPE, integrationType.getCode());
        }

        if (!TextUtils.isEmpty(ad.getZoneId())) {
            url = url.replace(MACRO_ZONE_ID, ad.getZoneId());
        }

        url = url.replace(MACRO_AUDIO_STATE, HyBid.getVideoAudioStatus().getStateName());

        if (mDeviceInfo != null) {
            if (!TextUtils.isEmpty(mDeviceInfo.getModel()) && !TextUtils.isEmpty(mDeviceInfo.getOSVersion())) {
                url = url.replace(MACRO_DEVICE_INFO, String.format(Locale.ENGLISH, "%s Android %s",
                        mDeviceInfo.getModel(), mDeviceInfo.getOSVersion()));
            }
        }

        if (ad != null) {
            if (!TextUtils.isEmpty(ad.getCreativeId())) {
                url = url.replace(MACRO_CREATIVE_ID, ad.getCreativeId());
            }
            if (!TextUtils.isEmpty(ad.getImpressionId())) {
                url = url.replace(MACRO_IMPRESSION_BEACON, ad.getImpressionId());
            }

            url = url.replace(MACRO_HAS_END_CARD, ad.hasEndCard() ? "true" : "false");
        }
        return url;
    }
}
