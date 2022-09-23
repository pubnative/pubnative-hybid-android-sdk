package net.pubnative.lite.sdk.contentinfo;

public class AdFeedbackData {
    private final String appToken;
    private final String zoneId;
    private final String audioState;
    private final String appVersion;
    private final String deviceInfo;
    private final String creativeId;
    private final String impressionBeacon;
    private final String sdkVersion;
    private final String integrationType;
    private final String adFormat;
    private final String hasEndCard;
    private final String creative;

    private AdFeedbackData(Builder builder) {
        this.appToken = builder.appToken;
        this.zoneId = builder.zoneId;
        this.audioState = builder.audioState;
        this.appVersion = builder.appVersion;
        this.deviceInfo = builder.deviceInfo;
        this.creativeId = builder.creativeId;
        this.impressionBeacon = builder.impressionBeacon;
        this.sdkVersion = builder.sdkVersion;
        this.integrationType = builder.integrationType;
        this.adFormat = builder.adFormat;
        this.hasEndCard = builder.hasEndCard;
        this.creative = builder.creative;
    }

    public String getAppToken() {
        return appToken;
    }

    public String getZoneId() {
        return zoneId;
    }

    public String getAudioState() {
        return audioState;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public String getCreativeId() {
        return creativeId;
    }

    public String getImpressionBeacon() {
        return impressionBeacon;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public String getIntegrationType() {
        return integrationType;
    }

    public String getAdFormat() {
        return adFormat;
    }

    public String getHasEndCard() {
        return hasEndCard;
    }

    public String getCreative() {
        return creative;
    }

    public static class Builder {
        private String appToken;
        private String zoneId;
        private String audioState;
        private String appVersion;
        private String deviceInfo;
        private String creativeId;
        private String impressionBeacon;
        private String sdkVersion;
        private String integrationType;
        private String adFormat;
        private String hasEndCard;
        private String creative;

        public Builder setAppToken(final String appToken) {
            this.appToken = appToken;
            return this;
        }

        public Builder setZoneId(String zoneId) {
            this.zoneId = zoneId;
            return this;
        }

        public Builder setAudioState(String audioState) {
            this.audioState = audioState;
            return this;
        }

        public Builder setAppVersion(String appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        public Builder setDeviceInfo(String deviceInfo) {
            this.deviceInfo = deviceInfo;
            return this;
        }

        public Builder setCreativeId(String creativeId) {
            this.creativeId = creativeId;
            return this;
        }

        public Builder setImpressionBeacon(String impressionBeacon) {
            this.impressionBeacon = impressionBeacon;
            return this;
        }

        public Builder setSdkVersion(String sdkVersion) {
            this.sdkVersion = sdkVersion;
            return this;
        }

        public Builder setIntegrationType(String integrationType) {
            this.integrationType = integrationType;
            return this;
        }

        public Builder setAdFormat(String adFormat) {
            this.adFormat = adFormat;
            return this;
        }

        public Builder setHasEndCard(String hasEndCard) {
            this.hasEndCard = hasEndCard;
            return this;
        }

        public Builder setCreative(String creative) {
            this.creative = creative;
            return this;
        }

        public AdFeedbackData build() {
            return new AdFeedbackData(this);
        }
    }
}
