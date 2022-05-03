package net.pubnative.lite.sdk.models;

public interface RemoteConfigFeature {
    interface AdFormat {
        String NATIVE = "native";
        String BANNER = "banner";
        String INTERSTITIAL = "interstitial";
        String REWARDED = "rewarded";
    }

    interface Rendering {
        String MRAID = "mraid";
        String VAST = "vast";
    }

    interface Reporting {
        String AD_EVENTS = "ad_events";
        String AD_ERRORS = "ad_errors";
        String DIAGNOSTIC_INIT = "diagnostic_init";
        String DIAGNOSTIC_PLACEMENT = "diagnostic_placement";
        String DIAGNOSTIC_REPORT = "diagnostic_report";
    }

    interface UserConsent {
        String CCPA = "ccpa";
        String GDPR = "gdpr";
    }
}
