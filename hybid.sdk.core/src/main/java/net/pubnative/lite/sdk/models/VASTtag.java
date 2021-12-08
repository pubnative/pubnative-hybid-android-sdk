package net.pubnative.lite.sdk.models;

import android.text.TextUtils;

public class VASTtag {

    private final String vastTagURL;

    final String ad_id;
    final String bundle;
    final String connection;
    final String dnt;
    final String lat;
    final String lon;
    final String user_agent;
    final String width;
    final String height;
    final String gdpr;
    final String gdpr_consent;
    final String us_privacy;

    private String formatted_url;

    private VASTtag(VASTtagBuilder builder) {
        this.vastTagURL = builder.vastTagURL;

        this.ad_id = builder.ad_id;
        this.bundle = builder.bundle;
        this.connection = builder.connection;
        this.dnt = builder.dnt;
        this.lat = builder.lat;
        this.lon = builder.lon;
        this.user_agent = builder.user_agent;
        this.width = builder.width;
        this.height = builder.height;
        this.gdpr = builder.gdpr;
        this.gdpr_consent = builder.gdpr_consent;
        this.us_privacy = builder.us_privacy;
    }

    private void format() {
        formatted_url = vastTagURL;
        if (!TextUtils.isEmpty(ad_id)) {
            formatted_url = formatted_url.replace("{{adid}}", ad_id);
        }

        if (!TextUtils.isEmpty(bundle)) {
            formatted_url = formatted_url.replace("{{bundle}}", bundle);
        }

        if (!TextUtils.isEmpty(connection)) {
            formatted_url = formatted_url.replace("{{connection}}", connection);
        }

        if (!TextUtils.isEmpty(dnt)) {
            formatted_url = formatted_url.replace("{{dnt}}", dnt);
        }

        if (!TextUtils.isEmpty(user_agent)) {
            formatted_url = formatted_url.replace("{{user_agent}}", user_agent);
        }

        if (!TextUtils.isEmpty(width)) {
            formatted_url = formatted_url.replace("{{width}}", width);
        }

        if (!TextUtils.isEmpty(height)) {
            formatted_url = formatted_url.replace("{{height}}", height);
        }

        if (!TextUtils.isEmpty(gdpr)) {
            formatted_url = formatted_url.replace("{{gdpr}}", gdpr);
        }

        if (!TextUtils.isEmpty(gdpr_consent)) {
            formatted_url = formatted_url.replace("{{gdpr_consent}}", gdpr_consent);
        }

        if (!TextUtils.isEmpty(us_privacy)) {
            formatted_url = formatted_url.replace("{{us_privacy}}", us_privacy);
        }

        if (!TextUtils.isEmpty(lat)) {
            formatted_url = formatted_url.replace("{{lat}}", lat);
        }

        if (!TextUtils.isEmpty(lon)) {
            formatted_url = formatted_url.replace("{{lon}}", lon);
        }
    }

    public String getFormattedURL() {
        return formatted_url;
    }

    public static class VASTtagBuilder {

        private final String vastTagURL;
        String ad_id;
        String bundle;
        String connection;
        String dnt;
        String lat;
        String lon;
        String user_agent;
        String width;
        String height;
        String gdpr;
        String gdpr_consent;
        String us_privacy;

        public VASTtagBuilder(String vastTagURL) {
            this.vastTagURL = vastTagURL;
        }

        public VASTtagBuilder adId(String ad_id) {
            this.ad_id = ad_id;
            return this;
        }

        public VASTtagBuilder bundle(String bundle) {
            this.bundle = bundle;
            return this;
        }

        public VASTtagBuilder connection(String connection) {
            this.connection = connection;
            return this;
        }

        public VASTtagBuilder dnt(String dnt) {
            this.dnt = dnt;
            return this;
        }

        public VASTtagBuilder lat(String lat) {
            this.lat = lat;
            return this;
        }

        public VASTtagBuilder lon(String lon) {
            this.lon = lon;
            return this;
        }

        public VASTtagBuilder userAgent(String user_agent) {
            this.user_agent = user_agent;
            return this;
        }

        public VASTtagBuilder width(String width) {
            this.width = width;
            return this;
        }

        public VASTtagBuilder height(String height) {
            this.height = height;
            return this;
        }

        public VASTtagBuilder gdpr(String gdpr) {
            this.gdpr = gdpr;
            return this;
        }

        public VASTtagBuilder gdprConsent(String gdpr_consent) {
            this.gdpr_consent = gdpr_consent;
            return this;
        }

        public VASTtagBuilder usPrivacy(String us_privacy) {
            this.us_privacy = us_privacy;
            return this;
        }

        public VASTtag build() {
            VASTtag tag = new VASTtag(this);
            tag.format();
            return tag;
        }
    }
}