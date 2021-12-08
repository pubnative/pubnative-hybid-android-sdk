package net.pubnative.lite.sdk.vpaid.vast;

import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.VASTtag;

public class VastUrlUtils {

    public static String formatURL(String adValue) {
        String gdpr = "0";
        boolean isCCPAOptOut = false;
        boolean isConsentDenied = false;

        if (HyBid.getUserDataManager() != null) {
            boolean isApplied = HyBid.getUserDataManager().gdprApplies();
            if (isApplied)
                gdpr = "1";
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
                latitude = String.valueOf(HyBid.getLocationManager().getUserLocation().getLatitude());
            if (HyBid.getLocationManager().getUserLocation().getLongitude() != 0.0)
                longitude = String.valueOf(HyBid.getLocationManager().getUserLocation().getLongitude());
        }

        return getVastURL(adValue,
                HyBid.getDeviceInfo().getAdvertisingId(),
                HyBid.getBundleId(),
                dnt,
                latitude,
                longitude,
                HyBid.getDeviceInfo().getUserAgent(),
                HyBid.getDeviceInfo().getDeviceWidth(),
                HyBid.getDeviceInfo().getDeviceHeight(),
                gdpr,
                HyBid.getUserDataManager().getIABGDPRConsentString(),
                HyBid.getUserDataManager().getIABUSPrivacyString());
    }

    private static String getVastURL(String url,
                                     String ad_id,
                                     String bundle,
                                     String dnt,
                                     String lat,
                                     String lon,
                                     String user_agent,
                                     String width,
                                     String height,
                                     String gdpr,
                                     String gdpr_consent,
                                     String us_privacy) {

        VASTtag vasTtag =
                new VASTtag.VASTtagBuilder(url)
                        .adId(ad_id)
                        .bundle(bundle)
                        .connection("wifi")
                        .dnt(dnt)
                        .gdpr(gdpr)
                        .gdprConsent(gdpr_consent)
                        .width(width)
                        .height(height)
                        .lat(lat)
                        .lon(lon)
                        .userAgent(user_agent)
                        .usPrivacy(us_privacy)
                        .build();

        return vasTtag.getFormattedURL();
    }
}
