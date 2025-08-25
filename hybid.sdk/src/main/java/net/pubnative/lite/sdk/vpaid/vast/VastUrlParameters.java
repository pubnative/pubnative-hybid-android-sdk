// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.vast;

public class VastUrlParameters {
    final String advertisingId;
    final String bundleId;
    final String dnt;
    final String latitude;
    final String longitude;
    final String userAgent;
    final String deviceWidth;
    final String deviceHeight;
    final String gdpr;
    final String gdprConsent;
    final String usPrivacy;

    public static class Builder {
        private String advertisingId;
        private String bundleId;
        private String dnt;
        private String latitude;
        private String longitude;
        private String userAgent;
        private String deviceWidth;
        private String deviceHeight;
        private String gdpr;
        private String gdprConsent;
        private String usPrivacy;

        public Builder advertisingId(String val) { advertisingId = val; return this; }
        public Builder bundleId(String val) { bundleId = val; return this; }
        public Builder dnt(String val) { dnt = val; return this; }
        public Builder latitude(String val) { latitude = val; return this; }
        public Builder longitude(String val) { longitude = val; return this; }
        public Builder userAgent(String val) { userAgent = val; return this; }
        public Builder deviceWidth(String val) { deviceWidth = val; return this; }
        public Builder deviceHeight(String val) { deviceHeight = val; return this; }
        public Builder gdpr(String val) { gdpr = val; return this; }
        public Builder gdprConsent(String val) { gdprConsent = val; return this; }
        public Builder usPrivacy(String val) { usPrivacy = val; return this; }

        public VastUrlParameters build() {
            return new VastUrlParameters(this);
        }
    }

    private VastUrlParameters(Builder builder) {
        this.advertisingId = builder.advertisingId;
        this.bundleId = builder.bundleId;
        this.dnt = builder.dnt;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.userAgent = builder.userAgent;
        this.deviceWidth = builder.deviceWidth;
        this.deviceHeight = builder.deviceHeight;
        this.gdpr = builder.gdpr;
        this.gdprConsent = builder.gdprConsent;
        this.usPrivacy = builder.usPrivacy;
    }
}
