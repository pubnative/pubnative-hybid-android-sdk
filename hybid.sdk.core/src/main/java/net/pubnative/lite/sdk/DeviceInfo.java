// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.WebView;

import net.pubnative.lite.sdk.utils.HyBidAdvertisingId;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNAsyncUtils;
import net.pubnative.lite.sdk.utils.PNCrypto;
import net.pubnative.lite.sdk.utils.ScreenDimensionsUtils;
import net.pubnative.lite.sdk.utils.SoundUtils;

import java.util.Locale;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class DeviceInfo {
    public interface Listener {
        void onInfoLoaded();
    }

    public enum Orientation {
        PORTRAIT("portrait"),
        LANDSCAPE("landscape"),
        NONE("none");

        private final String mOrientation;

        Orientation(String orientation) {
            mOrientation = orientation;
        }

        @Override
        public String toString() {
            return mOrientation;
        }
    }

    public enum Connectivity {
        ETHERNET("ethernet"),
        WIFI("wifi"),
        WWAN("wwan"),
        NONE("none");

        private final String mConnectivity;

        Connectivity(String connectivity) {
            mConnectivity = connectivity;
        }

        @Override
        public String toString() {
            return mConnectivity;
        }
    }

    private static final String TAG = DeviceInfo.class.getSimpleName();
    private final Context mContext;
    private final UserAgentProvider mUserAgentProvider;
    private String mAdvertisingId;
    private String mAdvertisingIdMd5;
    private String mAdvertisingIdSha1;
    private boolean mLimitTracking = false;
    private Listener mListener;
    private String deviceHeight;
    private String deviceWidth;
    private String soundSetting;

    public DeviceInfo(Context context) {
        mContext = context.getApplicationContext();
        mUserAgentProvider = new UserAgentProvider();
        getDeviceScreenDimensions();
    }

    public void initialize(Listener listener) {
        mListener = listener;
        fetchUserAgent();
        fetchAdvertisingId();
    }

    private void fetchAdvertisingId() {
        try {
            PNAsyncUtils.safeExecuteOnExecutor(new HyBidAdvertisingId(mContext, (advertisingId, limitTracking) -> {
                mLimitTracking = limitTracking;
                if (!TextUtils.isEmpty(advertisingId)) {
                    mAdvertisingId = advertisingId;
                    mAdvertisingIdMd5 = PNCrypto.md5(mAdvertisingId);
                    mAdvertisingIdSha1 = PNCrypto.sha1(mAdvertisingId);
                }

                if (mListener != null) {
                    mListener.onInfoLoaded();
                }
            }));
        } catch (Exception exception) {
            Logger.e(TAG, "Error executing HyBidAdvertisingId AsyncTask");
            if (mListener != null) {
                mListener.onInfoLoaded();
            }
        }
    }

    public void fetchUserAgent() {
        mUserAgentProvider.initialise(mContext);
    }

    public void getDeviceScreenDimensions() {
        ScreenDimensionsUtils screenDimensionsUtils = new ScreenDimensionsUtils();
        Point point = screenDimensionsUtils.getScreenDimensionsToPoint(mContext);
        deviceWidth = Integer.toString(point.x);
        deviceHeight = Integer.toString(point.y);
    }

    /**
     * Attempt to use the play services advertising ID, but fall back on the old style Android ID.
     * https://developer.android.com/training/articles/user-data-ids.html
     * https://support.google.com/googleplay/android-developer/answer/6048248?hl=en
     * https://play.google.com/about/monetization-ads/ads/ad-id/
     *
     * @return
     */
    @SuppressLint("HardwareIds")
    public String getAdvertisingId() {
        return mAdvertisingId;
    }

    public String getAdvertisingIdMd5() {
        return mAdvertisingIdMd5;
    }

    public String getAdvertisingIdSha1() {
        return mAdvertisingIdSha1;
    }

    public boolean limitTracking() {
        return mLimitTracking;
    }

    public Locale getLocale() {
        return mContext.getResources().getConfiguration().locale;
    }

    public Orientation getOrientation() {
        switch (mContext.getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT: {
                return Orientation.PORTRAIT;
            }
            case Configuration.ORIENTATION_LANDSCAPE: {
                return Orientation.LANDSCAPE;
            }
            default: {
                return Orientation.NONE;
            }
        }
    }

    public void checkSoundSetting() {
        SoundUtils soundUtils = new SoundUtils();
        boolean muted = soundUtils.isSoundMuted(mContext);

        if (muted) {
            soundSetting = "0";
        } else {
            soundSetting = "1";
        }
    }

    public String getModel() {
        return Build.MODEL;
    }

    public String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public Context getContext() {
        return mContext;
    }

    public String getDeviceHeight() {
        return deviceHeight;
    }

    public String getDeviceWidth() {
        return deviceWidth;
    }

    public String getSoundSetting() {
        return soundSetting;
    }

    public String getUserAgent() {
        return mUserAgentProvider != null ? mUserAgentProvider.getUserAgent() : "";
    }
}