// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.vast;

import android.text.TextUtils;
import java.util.Locale;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.VASTtag;

public class VastUrlUtils {

    public static VastUrlParameters buildParameters() {
        String gdpr = "0";
        boolean isCCPAOptOut = false;
        boolean isConsentDenied = false;

        if (HyBid.getUserDataManager() != null) {
            if (HyBid.getUserDataManager().gdprApplies()) {
                gdpr = "1";
            }
            isCCPAOptOut = HyBid.getUserDataManager().isCCPAOptOut();
            isConsentDenied = HyBid.getUserDataManager().isConsentDenied();
        }

        String dnt = "0";
        if (HyBid.isCoppaEnabled() || isCCPAOptOut || isConsentDenied ||
                HyBid.getDeviceInfo().limitTracking() ||
                TextUtils.isEmpty(HyBid.getDeviceInfo().getAdvertisingId())) {
            dnt = "1";
        }

        String latitude = null;
        String longitude = null;
        if (HyBid.getLocationManager() != null && HyBid.getLocationManager().getUserLocation() != null) {
            if (HyBid.getLocationManager().getUserLocation().getLatitude() != 0.0)
                latitude = String.format(Locale.ENGLISH, "%.2f", HyBid.getLocationManager().getUserLocation().getLatitude());
            if (HyBid.getLocationManager().getUserLocation().getLongitude() != 0.0)
                longitude = String.format(Locale.ENGLISH, "%.2f", HyBid.getLocationManager().getUserLocation().getLongitude());
        }

        return new VastUrlParameters.Builder()
                .advertisingId(HyBid.getDeviceInfo().getAdvertisingId())
                .bundleId(HyBid.getBundleId())
                .dnt(dnt)
                .latitude(latitude)
                .longitude(longitude)
                .userAgent(HyBid.getDeviceInfo().getUserAgent())
                .deviceWidth(HyBid.getDeviceInfo().getDeviceWidth())
                .deviceHeight(HyBid.getDeviceInfo().getDeviceHeight())
                .gdpr(gdpr)
                .gdprConsent(HyBid.getUserDataManager().getIABGDPRConsentString())
                .usPrivacy(HyBid.getUserDataManager().getIABUSPrivacyString())
                .build();
    }

    public static String formatURL(String adValue, VastUrlParameters params) {
        if (params == null) {
            params = new VastUrlParameters.Builder().build();
        }

        VASTtag vasTtag = new VASTtag.VASTtagBuilder(adValue)
                .adId(params.advertisingId)
                .bundle(params.bundleId)
                .dnt(params.dnt)
                .lat(params.latitude)
                .lon(params.longitude)
                .userAgent(params.userAgent)
                .width(params.deviceWidth)
                .height(params.deviceHeight)
                .gdpr(params.gdpr)
                .gdprConsent(params.gdprConsent)
                .usPrivacy(params.usPrivacy)
                .build();

        return vasTtag.getFormattedURL();
    }
}