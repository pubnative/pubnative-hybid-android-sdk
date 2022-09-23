package net.pubnative.lite.sdk.contentinfo;

import android.text.TextUtils;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDInterstitial;

import java.util.Locale;

public class AdFeedbackDataCollector {
    private static final String TAG = AdFeedbackDataCollector.class.getSimpleName();

    private final DeviceInfo mDeviceInfo;

    public AdFeedbackDataCollector() {
        this(HyBid.getDeviceInfo());
    }

    public AdFeedbackDataCollector(DeviceInfo deviceInfo) {
        mDeviceInfo = deviceInfo;
    }

    public AdFeedbackData collectData(Ad ad, String adFormat, IntegrationType integrationType) {
        AdFeedbackData.Builder builder = new AdFeedbackData.Builder();

        if (HyBid.isInitialized() && !TextUtils.isEmpty(HyBid.getAppToken())) {
            builder.setAppToken(HyBid.getAppToken());
        }

        if (!TextUtils.isEmpty(HyBid.getSDKVersionInfo())) {
            builder.setSdkVersion(HyBid.getSDKVersionInfo());
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

        builder.setAudioState(HyBid.getVideoAudioStatus().getStateName());

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
