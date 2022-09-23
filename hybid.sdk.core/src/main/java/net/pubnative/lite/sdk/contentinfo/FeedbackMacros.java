package net.pubnative.lite.sdk.contentinfo;

import android.text.TextUtils;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;

import java.util.Locale;

public class FeedbackMacros {
    public static final String MACRO_APP_TOKEN = "[APPTOKEN]";
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

    public String processUrl(String url, AdFeedbackData data) {
        if (data != null) {
            if (!TextUtils.isEmpty(data.getAppToken())) {
                url = url.replace(MACRO_APP_TOKEN, data.getAppToken());
            }

            if (!TextUtils.isEmpty(data.getSdkVersion())) {
                url = url.replace(MACRO_SDK_VERSION, data.getSdkVersion());
            }

            if (!TextUtils.isEmpty(data.getAppVersion())) {
                url = url.replace(MACRO_APP_VERSION, data.getAppVersion());
            }

            if (!TextUtils.isEmpty(data.getAdFormat())) {
                url = url.replace(MACRO_AD_FORMAT, data.getAdFormat());
            }

            if (!TextUtils.isEmpty(data.getIntegrationType())) {
                url = url.replace(MACRO_INTEGRATION_TYPE, data.getIntegrationType());
            }

            if (!TextUtils.isEmpty(data.getZoneId())) {
                url = url.replace(MACRO_ZONE_ID, data.getZoneId());
            }

            if (!TextUtils.isEmpty(data.getAudioState())) {
                url = url.replace(MACRO_AUDIO_STATE, data.getAudioState());
            }

            if (!TextUtils.isEmpty(data.getDeviceInfo())) {
                url = url.replace(MACRO_DEVICE_INFO, data.getDeviceInfo());
            }

            if (!TextUtils.isEmpty(data.getCreativeId())) {
                url = url.replace(MACRO_CREATIVE_ID, data.getCreativeId());
            }
            if (!TextUtils.isEmpty(data.getImpressionBeacon())) {
                url = url.replace(MACRO_IMPRESSION_BEACON, data.getImpressionBeacon());
            }

            if (!TextUtils.isEmpty(data.getHasEndCard())) {
                url = url.replace(MACRO_HAS_END_CARD, data.getHasEndCard());
            }
        }
        return url;
    }
}
