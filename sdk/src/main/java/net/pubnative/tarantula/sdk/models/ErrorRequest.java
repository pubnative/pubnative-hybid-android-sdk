package net.pubnative.tarantula.sdk.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class ErrorRequest {
    @SerializedName("message")
    @Expose
    @NonNull private final String mMessage;

    @SerializedName("v")
    @Expose
    @NonNull private final String mVersion;

    @SerializedName("sdk_v")
    @Expose
    @NonNull private final String mSdkVersion;

    @SerializedName("ifa")
    @Expose
    @NonNull private final String mIFA;

    @SerializedName("lmt")
    @Expose
    @NonNull private final Boolean mLMT;

    @SerializedName("vendor_id")
    @Expose
    @NonNull private final String mVendorId;

    @SerializedName("tz")
    @Expose
    @NonNull private final String mTimeZone;

    @SerializedName("locale")
    @Expose
    @NonNull private final String mLocale;

    @SerializedName("orientation")
    @Expose
    @NonNull private final String mOrientation;

    @SerializedName("w")
    @Expose
    @NonNull private final Integer mWidthPx;

    @SerializedName("h")
    @Expose
    @NonNull private final Integer mHeightPx;

    @SerializedName("browser_agent")
    @Expose
    @NonNull private final String mBrowserAgent;

    @SerializedName("model")
    @Expose
    @NonNull private final String mModel;

    @SerializedName("connectivity")
    @Expose
    @NonNull private final String mConnectivity;

    @SerializedName("carrier")
    @Expose
    @NonNull private final String mCarrier;

    @SerializedName("session_depth")
    @Expose
    @Nullable private final Integer mSessionDepth;

    @SerializedName("latitude")
    @Expose
    @Nullable private final Integer mLatitude;

    @SerializedName("longitude")
    @Expose
    @Nullable private final Integer mLongitude;

    @SerializedName("test")
    @Expose
    @Nullable private final Boolean mTest;

    private ErrorRequest(@NonNull String message, @NonNull String version, @NonNull String sdkVersion, @NonNull String ifa,
                         @NonNull Boolean lmt, @NonNull String vendorId, @NonNull String timeZone, @NonNull String locale,
                         @NonNull String orientation, @NonNull Integer widthPx, @NonNull Integer heightPx,
                         @NonNull String browserAgent, @NonNull String model, @NonNull String connectivity,
                         @NonNull String carrier, @Nullable Integer sessionDepth, @Nullable Integer latitude,
                         @Nullable Integer longitude, @Nullable Boolean test) {
        mMessage = message;
        mVersion = version;
        mSdkVersion = sdkVersion;
        mIFA = ifa;
        mLMT = lmt;
        mVendorId = vendorId;
        mTimeZone = timeZone;
        mLocale = locale;
        mOrientation = orientation;
        mWidthPx = widthPx;
        mHeightPx = heightPx;
        mBrowserAgent = browserAgent;
        mModel = model;
        mConnectivity = connectivity;
        mCarrier = carrier;
        mSessionDepth = sessionDepth;
        mLatitude = latitude;
        mLongitude = longitude;
        mTest = test;
    }

    public static class Builder {
        @NonNull private final String mMessage;
        @NonNull private final String mVersion;
        @NonNull private final String mSdkVersion;
        @NonNull private final String mIFA;
        @NonNull private final Boolean mLMT;
        @NonNull private final String mVendorId;
        @NonNull private final String mTimeZone;
        @NonNull private final String mLocale;
        @NonNull private final String mOrientation;
        @NonNull private final Integer mWidthPx;
        @NonNull private final Integer mHeightPx;
        @NonNull private final String mBrowserAgent;
        @NonNull private final String mModel;
        @NonNull private final String mConnectivity;
        @NonNull private final String mCarrier;
        @NonNull private Integer mSessionDepth;
        @Nullable private Integer mLatitude;
        @Nullable private Integer mLongitude;
        @Nullable private Boolean mTest;

        public Builder(@NonNull String message, @NonNull String version, @NonNull String sdkVersion, @NonNull String ifa,
                       @NonNull Boolean lmt, @NonNull String vendorId, @NonNull String timeZone, @NonNull String locale,
                       @NonNull String orientation, @NonNull Integer widthPx, @NonNull Integer heightPx,
                       @NonNull String browserAgent, @NonNull String model, @NonNull String connectivity,
                       @NonNull String carrier, @NonNull Integer sessionDepth) {
            mMessage = message;
            mVersion = version;
            mSdkVersion = sdkVersion;
            mIFA = ifa;
            mLMT = lmt;
            mVendorId = vendorId;
            mTimeZone = timeZone;
            mLocale = locale;
            mOrientation = orientation;
            mWidthPx = widthPx;
            mHeightPx = heightPx;
            mBrowserAgent = browserAgent;
            mModel = model;
            mConnectivity = connectivity;
            mCarrier = carrier;
            mSessionDepth = sessionDepth;
        }

        public ErrorRequest.Builder withLatitude(@Nullable Integer latitude) {
            mLatitude = latitude;
            return this;
        }

        public ErrorRequest.Builder withLongitude(@Nullable Integer longitude) {
            mLongitude = longitude;
            return this;
        }

        public ErrorRequest.Builder withTest(@Nullable Boolean test) {
            mTest = test;
            return this;
        }

        public ErrorRequest build() {
            return new ErrorRequest(mMessage, mVersion, mSdkVersion, mIFA, mLMT, mVendorId, mTimeZone, mLocale, mOrientation,
                    mWidthPx, mHeightPx, mBrowserAgent, mModel, mConnectivity, mCarrier, mSessionDepth, mLatitude, mLongitude,
                    mTest);
        }
    }
}
