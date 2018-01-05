package net.pubnative.tarantula.sdk.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public class AdRequest {

    @SerializedName("apptoken")
    @Expose
    @NonNull private final String appToken;

    @SerializedName("os")
    @Expose
    @NonNull private final String os;

    @SerializedName("osver")
    @Expose
    @NonNull private final String osVersion;

    @SerializedName("devicemodel")
    @Expose
    @NonNull private final String deviceModel;

    @SerializedName("dnt")
    @Expose
    @NonNull private final String doNotTrack;

    @SerializedName("al")
    @Expose
    @NonNull private final String adLayoutType;

    @SerializedName("mf")
    @Expose
    @NonNull private final String mf;

    @SerializedName("zoneid")
    @Expose
    @NonNull private final String zoneId;

    @SerializedName("gid")
    @Expose
    @NonNull private final String gid;

    @SerializedName("srvi")
    @Expose
    @NonNull private final boolean srvi;

    @SerializedName("uid")
    @Expose
    @NonNull private final String uid;

    @SerializedName("adcount")
    @Expose
    @NonNull private final String adcount;

    @SerializedName("ua")
    @Expose
    @NonNull private final String ua;

    @SerializedName("ip")
    @Expose
    @NonNull private final String ip;

    @SerializedName("locale")
    @Expose
    @NonNull private final String locale;

    @SerializedName("lat")
    @Expose
    @NonNull private final String latitude;

    @SerializedName("long")
    @Expose
    @NonNull private final String longitude;

    @SerializedName("gender")
    @Expose
    @NonNull private final String gender;

    @SerializedName("age")
    @Expose
    @NonNull private final String age;

    @SerializedName("coppa")
    @Expose
    @NonNull private final String coppa;

    @SerializedName("bundleid")
    @Expose
    @NonNull private final String bundleId;

    @SerializedName("keywords")
    @Expose
    @NonNull private final String keywords;

    @SerializedName("secure")
    @Expose
    @NonNull private final String secure;

    @SerializedName("gidmd5")
    @Expose
    @NonNull private final String gidmd5;

    @SerializedName("gidsha1")
    @Expose
    @NonNull private final String gidsha1;

    private AdRequest(@NonNull String appToken,
                      @NonNull String os,
                      @NonNull String osVersion,
                      @NonNull String deviceModel,
                      @NonNull String doNotTrack,
                      @NonNull String adLayoutType,
                      @NonNull String mf,
                      @NonNull String zoneId,
                      @NonNull String gid,
                      @NonNull boolean srvi,
                      @NonNull String uid,
                      @NonNull String adcount,
                      @NonNull String ua,
                      @NonNull String ip,
                      @NonNull String locale,
                      @NonNull String latitude,
                      @NonNull String longitude,
                      @NonNull String gender,
                      @NonNull String age,
                      @NonNull String coppa,
                      @NonNull String bundleId,
                      @NonNull String keywords,
                      @NonNull String secure,
                      @NonNull String gidmd5,
                      @NonNull String gidsha1) {
        this.appToken = appToken;
        this.os = os;
        this.osVersion = osVersion;
        this.deviceModel = deviceModel;
        this.doNotTrack = doNotTrack;
        this.adLayoutType = adLayoutType;
        this.mf = mf;
        this.zoneId = zoneId;

        this.gid = gid;
        this.srvi = srvi;
        this.uid = uid;
        this.adcount = adcount;
        this.ua = ua;
        this.ip = ip;
        this.locale = locale;
        this.latitude = latitude;
        this.longitude = longitude;
        this.gender = gender;
        this.age = age;
        this.coppa = coppa;
        this.bundleId = bundleId;
        this.keywords = keywords;
        this.secure = secure;
        this.gidmd5 = gidmd5;
        this.gidsha1 = gidsha1;
    }

    public static class Builder {
        @NonNull String appToken;
        @NonNull String os;
        @NonNull String osVersion;
        @NonNull String deviceModel;
        @NonNull String doNotTrack;
        @NonNull String adLayoutType;
        @NonNull String mf;
        @NonNull String zoneId;
        @NonNull String gid;
        @NonNull boolean srvi;
        @NonNull String uid;
        @NonNull String adcount;
        @NonNull String ua;
        @NonNull String ip;
        @NonNull String locale;
        @NonNull String latitude;
        @NonNull String longitude;
        @NonNull String gender;
        @NonNull String age;
        @NonNull String coppa;
        @NonNull String bundleId;
        @NonNull String keywords;
        @NonNull String secure;
        @NonNull String gidmd5;
        @NonNull String gidsha1;

        public Builder(@NonNull String appToken,
                       @NonNull String os,
                       @NonNull String osVersion,
                       @NonNull String deviceModel,
                       @NonNull String doNotTrack,
                       @NonNull String adLayoutType,
                       @NonNull String mf,
                       @NonNull String zoneId) {
            this.appToken = appToken;
            this.os = os;
            this.osVersion = osVersion;
            this.deviceModel = deviceModel;
            this.doNotTrack = doNotTrack;
            this.adLayoutType = adLayoutType;
            this.mf = mf;
            this.zoneId = zoneId;
        }

        public AdRequest.Builder withGid(@Nullable String gid) {
            this.gid = gid;
            return this;
        }

        public AdRequest.Builder withSrvi(@Nullable Boolean srvi) {
            this.srvi = srvi;
            return this;
        }

        public AdRequest.Builder withUid(@Nullable String uid) {
            this.uid = uid;
            return this;
        }

        public AdRequest.Builder withAdcount(@Nullable String adcount) {
            this.adcount = adcount;
            return this;
        }

        public AdRequest.Builder withUa(@Nullable String ua) {
            this.ua = ua;
            return this;
        }

        public AdRequest.Builder withIP(@Nullable String ip) {
            this.ip = ip;
            return this;
        }

        public AdRequest.Builder withLocale(@Nullable String locale) {
            this.locale = locale;
            return this;
        }

        public AdRequest.Builder withLatitude(@Nullable String latitude) {
            this.latitude = latitude;
            return this;
        }

        public AdRequest.Builder withLongitude(@Nullable String longitude) {
            this.longitude = longitude;
            return this;
        }

        public AdRequest.Builder withGender(@Nullable String gender) {
            this.gender = gender;
            return this;
        }

        public AdRequest.Builder withAge(@Nullable String age) {
            this.age = age;
            return this;
        }

        public AdRequest.Builder withCoppa(@Nullable String coppa) {
            this.coppa = coppa;
            return this;
        }

        public AdRequest.Builder withBundleId(@Nullable String bundleId) {
            this.bundleId = bundleId;
            return this;
        }

        public AdRequest.Builder withKeywords(@Nullable String keywords) {
            this.keywords = keywords;
            return this;
        }

        public AdRequest.Builder withSecure(@Nullable String secure) {
            this.secure = secure;
            return this;
        }

        public AdRequest.Builder withGidmd5(@Nullable String gidmd5) {
            this.keywords = gidmd5;
            return this;
        }

        public AdRequest.Builder withGidsha1(@Nullable String gidsha1) {
            this.keywords = gidsha1;
            return this;
        }

        public AdRequest build() {
            return new AdRequest(appToken, os, osVersion, deviceModel, doNotTrack, adLayoutType,
                    mf, zoneId, gid, srvi, uid, adcount, ua, ip, locale, latitude, longitude,
                    gender, age, coppa, bundleId, keywords, secure, gidmd5, gidsha1);
        }
    }
}
