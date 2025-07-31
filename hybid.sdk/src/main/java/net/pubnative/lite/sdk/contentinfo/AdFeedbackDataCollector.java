// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.contentinfo;

import android.text.TextUtils;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;

import java.util.Locale;

public class AdFeedbackDataCollector {
    private static final String TAG = AdFeedbackDataCollector.class.getSimpleName();

    private final DeviceInfo mDeviceInfo;
    private final IntegrationType mIntegrationType;

    public AdFeedbackDataCollector(IntegrationType integrationType) {
        this(HyBid.getDeviceInfo(), integrationType);
    }

    public AdFeedbackDataCollector(DeviceInfo deviceInfo, IntegrationType integrationType) {
        mDeviceInfo = deviceInfo;
        mIntegrationType = integrationType;
    }

    public AdFeedbackData collectData(Ad ad, String adFormat, IntegrationType integrationType) {
        AdFeedbackData.Builder builder = new AdFeedbackData.Builder();

        if (HyBid.isInitialized() && !TextUtils.isEmpty(HyBid.getAppToken())) {
            builder.setAppToken(HyBid.getAppToken());
        }

        if (!TextUtils.isEmpty(HyBid.getSDKVersionInfo(mIntegrationType))) {
            builder.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
        }

        if (!TextUtils.isEmpty(HyBid.getAppVersion())) {
            builder.setAppVersion(HyBid.getAppVersion());
        }

        if (!TextUtils.isEmpty(adFormat)) {
            builder.setAdFormat(adFormat);
        }

        if (integrationType != null) {
            builder.setIntegrationType(integrationType.getCode());
        }

        String audioState;
        String stateFromRemoteConfig = ad.getAudioState();
        if (stateFromRemoteConfig != null) {
            audioState = stateFromRemoteConfig;
        } else {
            audioState = HyBid.getVideoAudioStatus().getStateName();
        }

        builder.setAudioState(audioState);

        if (mDeviceInfo != null && !TextUtils.isEmpty(mDeviceInfo.getModel()) && !TextUtils.isEmpty(mDeviceInfo.getOSVersion())) {
            builder.setDeviceInfo(String.format(Locale.ENGLISH, "%s Android %s",
                    mDeviceInfo.getModel(), mDeviceInfo.getOSVersion()));
        }

        if (ad != null) {
            if (!TextUtils.isEmpty(ad.getZoneId())) {
                builder.setZoneId(ad.getZoneId());
            }

            if (!TextUtils.isEmpty(ad.getCreativeId())) {
                builder.setCreativeId(ad.getCreativeId());
            }
            if (!TextUtils.isEmpty(ad.getImpressionId())) {
                builder.setImpressionBeacon(ad.getImpressionId());
            }

            builder.setHasEndCard(ad.hasEndCard() ? "true" : "false");

            if (!TextUtils.isEmpty(ad.getVast())) {
                builder.setCreative(ad.getVast());
            } else if (!TextUtils.isEmpty(ad.getAssetUrl(APIAsset.HTML_BANNER))) {
                builder.setCreative(ad.getAssetUrl(APIAsset.HTML_BANNER));
            } else if (!TextUtils.isEmpty(ad.getAssetHtml(APIAsset.HTML_BANNER))) {
                builder.setCreative(ad.getAssetHtml(APIAsset.HTML_BANNER));
            }
        }

        return builder.build();
    }
}
