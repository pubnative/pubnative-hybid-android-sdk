package com.monet.bidder;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.monet.bidder.mapper.AdRequestInfo;
import com.monet.bidder.mapper.PlacementMappingManager;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.AdData;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.lite.adapters.mopub.mediation.HyBidMediationBannerCustomEvent;
import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.views.HyBidAdView;

public class CustomEventBanner extends HyBidMediationBannerCustomEvent {
    private static final String TAG = CustomEventBanner.class.getSimpleName();
    protected static final String ECPM_KEY = "cpm";

    @Override
    protected void load(@NonNull Context context, @NonNull AdData adData) throws Exception {
        String ecpm;
        String appToken = null;
        AdSize adSize = getAdSize(adData);
        if (adData.getExtras().containsKey(ECPM_KEY)) {
            ecpm = adData.getExtras().get(ECPM_KEY);
            if (!TextUtils.isEmpty(ecpm)) {
                try {
                    Double ecpmNumber = Double.parseDouble(ecpm);
                    AdRequestInfo adRequestInfo = PlacementMappingManager.getInstance(context).getEcpmMapping(adSize, ecpmNumber);
                    if (adRequestInfo != null
                            && !TextUtils.isEmpty(adRequestInfo.getAppToken())
                            && !TextUtils.isEmpty(adRequestInfo.getZoneId())) {
                        mZoneID = adRequestInfo.getZoneId();
                        appToken = adRequestInfo.getAppToken();
                    }
                } catch (NumberFormatException formatException) {
                    Logger.e(TAG, formatException.getMessage());
                }
            }
        }

        if (TextUtils.isEmpty(mZoneID) || TextUtils.isEmpty(appToken)) {
            if (adData.getExtras().containsKey(ZONE_ID_KEY) && adData.getExtras().containsKey(APP_TOKEN_KEY)) {
                mZoneID = adData.getExtras().get(ZONE_ID_KEY);
                appToken = adData.getExtras().get(APP_TOKEN_KEY);
            } else {
                Logger.e(TAG, "Could not find the required params in CustomEventBanner serverExtras");
                mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
                return;
            }
        }

        if (appToken == null || !appToken.equals(HyBid.getAppToken())) {
            Logger.e(TAG, "The provided app token doesn't match the one used to initialise HyBid");
            mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        setAutomaticImpressionAndClickTracking(false);
        mBannerView = new HyBidAdView(context);
        mBannerView.setAdSize(adSize);

        AdCache adCache = HyBid.getAdCache();
        if (adCache != null && adCache.inspect(mZoneID) != null) {
            Ad cachedBid = adCache.remove(mZoneID);
            mBannerView.renderAd(cachedBid, this);
        } else {
            mBannerView.setMediation(true);
            mBannerView.load(mZoneID, this);
        }

        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED, TAG);
    }

    @Override
    protected AdSize getAdSize(AdData adData) {
        int width = adData.getAdWidth() != null ? adData.getAdWidth() : 0;
        int height = adData.getAdHeight() != null ? adData.getAdHeight() : 0;
        if (width != 0 && height != 0) {
            if (height >= 1024) {
                if (width >= AdSize.SIZE_768x1024.getWidth()) {
                    return AdSize.SIZE_768x1024;
                }
            } else if (height >= 768) {
                if (width >= AdSize.SIZE_1024x768.getWidth()) {
                    return AdSize.SIZE_1024x768;
                }
            } else if (height >= 600) {
                if (width >= AdSize.SIZE_300x600.getWidth()) {
                    return AdSize.SIZE_300x600;
                } else if (width >= AdSize.SIZE_160x600.getWidth()) {
                    return AdSize.SIZE_160x600;
                }
            } else if (height >= 480) {
                if (width >= AdSize.SIZE_320x480.getWidth()) {
                    return AdSize.SIZE_320x480;
                }
            } else if (height >= 320) {
                if (width >= AdSize.SIZE_480x320.getWidth()) {
                    return AdSize.SIZE_480x320;
                }
            } else if (height >= 250) {
                if (width >= AdSize.SIZE_300x250.getWidth()) {
                    return AdSize.SIZE_300x250;
                } else if (width >= AdSize.SIZE_250x250.getWidth()) {
                    return AdSize.SIZE_250x250;
                }
            } else if (height >= 100) {
                if (width >= AdSize.SIZE_320x100.getWidth()) {
                    return AdSize.SIZE_320x100;
                }
            } else if (height >= 90) {
                if (width >= AdSize.SIZE_728x90.getWidth()) {
                    return AdSize.SIZE_728x90;
                }
            } else if (height >= 50) {
                if (width >= AdSize.SIZE_320x50.getWidth()) {
                    return AdSize.SIZE_320x50;
                } else if (width >= AdSize.SIZE_300x50.getWidth()) {
                    return AdSize.SIZE_300x50;
                }
            } else {
                return AdSize.SIZE_320x50;
            }
        }
        return super.getAdSize(adData);
    }
}
