package net.pubnative.lite.sdk.contentinfo;

import android.text.TextUtils;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.mraid.MRAIDInterstitial;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.utils.string.StringEscapeUtils;

import java.util.Locale;

public class FeedbackJSInterface {
    private static final String TAG = FeedbackJSInterface.class.getSimpleName();

    private static final String JS_VAR = "hybidFeedback";
    private static final String JS_PARAM_APP_TOKEN = "appToken";
    private static final String JS_PARAM_ZONE_ID = "zoneId";
    private static final String JS_PARAM_AUDIO_STATE = "audioState";
    private static final String JS_PARAM_APP_VERSION = "appVersion";
    private static final String JS_PARAM_DEVICE_INFO = "deviceInfo";
    private static final String JS_PARAM_CREATIVE_ID = "creativeId";
    private static final String JS_PARAM_IMPRESSION_BEACON = "impressionBeacon";
    private static final String JS_PARAM_SDK_VERSION = "sdkVersion";
    private static final String JS_PARAM_INTEGRATION_TYPE = "integrationType";
    private static final String JS_PARAM_AD_FORMAT = "adFormat";
    private static final String JS_PARAM_HAS_END_CARD = "hasEndCard";
    private static final String JS_PARAM_CREATIVE = "creative";

    private String buildJS(AdFeedbackData data) {
        StringBuilder stringBuilder = new StringBuilder();

        if (data != null) {
            if (!TextUtils.isEmpty(data.getAppToken())) {
                stringBuilder.append(getJSFunction(JS_PARAM_APP_TOKEN, data.getAppToken()));
            }

            if (!TextUtils.isEmpty(data.getZoneId())) {
                stringBuilder.append(getJSFunction(JS_PARAM_ZONE_ID, data.getZoneId()));
            }

            if (!TextUtils.isEmpty(data.getAudioState())) {
                stringBuilder.append(getJSFunction(JS_PARAM_AUDIO_STATE, data.getAudioState()));
            }

            if (!TextUtils.isEmpty(data.getAppVersion())) {
                stringBuilder.append(getJSFunction(JS_PARAM_APP_VERSION, data.getAppVersion()));
            }

            if (!TextUtils.isEmpty(data.getDeviceInfo())) {
                stringBuilder.append(getJSFunction(JS_PARAM_DEVICE_INFO, data.getDeviceInfo()));
            }

            if (!TextUtils.isEmpty(data.getCreativeId())) {
                stringBuilder.append(getJSFunction(JS_PARAM_CREATIVE_ID, data.getCreativeId()));
            }

            if (!TextUtils.isEmpty(data.getImpressionBeacon())) {
                stringBuilder.append(getJSFunction(JS_PARAM_IMPRESSION_BEACON, data.getImpressionBeacon()));
            }

            if (!TextUtils.isEmpty(data.getSdkVersion())) {
                stringBuilder.append(getJSFunction(JS_PARAM_SDK_VERSION, data.getSdkVersion()));
            }

            if (!TextUtils.isEmpty(data.getIntegrationType())) {
                stringBuilder.append(getJSFunction(JS_PARAM_INTEGRATION_TYPE, data.getIntegrationType()));
            }

            if (!TextUtils.isEmpty(data.getAdFormat())) {
                stringBuilder.append(getJSFunction(JS_PARAM_AD_FORMAT, data.getAdFormat()));
            }

            if (!TextUtils.isEmpty(data.getHasEndCard())) {
                stringBuilder.append(getJSFunction(JS_PARAM_HAS_END_CARD, data.getHasEndCard()));
            }

            if (!TextUtils.isEmpty(data.getCreative())) {

                stringBuilder.append(getJSFunction(JS_PARAM_CREATIVE, StringEscapeUtils.escapeJava(data.getCreative())));
            }
        }

        return stringBuilder.toString();
    }

    public void submitData(AdFeedbackData data, MRAIDView mraidView) {
        if (data != null) {
            String js = buildJS(data);

            if (mraidView != null && !TextUtils.isEmpty(js)) {

                mraidView.injectJavaScript(js);
            }
        }
    }

    private String getJSFunction(String functionName, String param) {
        return String.format(Locale.ENGLISH, "%s.%s = \"%s\";", JS_VAR, functionName, param);
    }
}
