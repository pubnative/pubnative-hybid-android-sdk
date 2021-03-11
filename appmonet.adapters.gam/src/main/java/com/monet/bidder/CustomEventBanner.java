package com.monet.bidder;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;
import com.monet.bidder.mapper.AdRequestInfo;
import com.monet.bidder.mapper.PlacementMappingManager;

import net.pubnative.hybid.adapters.admob.HyBidAdmobUtils;
import net.pubnative.hybid.adapters.admob.mediation.HyBidMediationBannerCustomEvent;
import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.views.HyBidAdView;

public class CustomEventBanner extends HyBidMediationBannerCustomEvent {
    private static final String TAG = CustomEventBanner.class.getSimpleName();

    @Override
    public void requestBannerAd(Context context, CustomEventBannerListener customEventBannerListener, String serverParameter, com.google.android.gms.ads.AdSize adSize, MediationAdRequest mediationAdRequest, Bundle bundle) {
        if (customEventBannerListener == null) {
            Logger.e(TAG, "customEventBannerListener is null");
            return;
        }

        mBannerListener = customEventBannerListener;

        String ecpm;
        String zoneId = null;
        String appToken = null;

        net.pubnative.lite.sdk.models.AdSize hyBidAdSize = getAdSize(adSize);

        if (adSize.getWidth() < hyBidAdSize.getWidth() || adSize.getHeight() < hyBidAdSize.getHeight()) {
            Logger.e(TAG, "The requested ad size is smaller than " + hyBidAdSize.toString());
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        ecpm = HyBidAdmobUtils.getEcpm(serverParameter);
        if (!TextUtils.isEmpty(ecpm)) {
            try {
                Double ecpmNumber = Double.parseDouble(ecpm);
                AdRequestInfo adRequestInfo = PlacementMappingManager.getInstance(context).getEcpmMapping(hyBidAdSize, ecpmNumber);
                if (adRequestInfo != null
                        && !TextUtils.isEmpty(adRequestInfo.getAppToken())
                        && !TextUtils.isEmpty(adRequestInfo.getZoneId())) {
                    zoneId = adRequestInfo.getZoneId();
                    appToken = adRequestInfo.getAppToken();
                }
            } catch (NumberFormatException formatException) {
                Logger.e(TAG, formatException.getMessage());
            }
        }

        if (TextUtils.isEmpty(zoneId) || TextUtils.isEmpty(appToken)) {
            String zoneIdParam = HyBidAdmobUtils.getZoneId(serverParameter);
            String appTokenParam = HyBidAdmobUtils.getAppToken(serverParameter);
            if (!TextUtils.isEmpty(zoneIdParam) && (!TextUtils.isEmpty(appTokenParam))) {
                zoneId = zoneIdParam;
                appToken = appTokenParam;
            } else {
                Logger.e(TAG, "Could not find the required params in CustomEventBanner serverExtras");
                mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
                return;
            }
        }

        if (appToken == null || !appToken.equals(HyBid.getAppToken())) {
            Logger.e(TAG, "The provided app token doesn't match the one used to initialise HyBid");
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }

        mBannerView = new HyBidAdView(context);
        mBannerView.setAdSize(hyBidAdSize);

        AdCache adCache = HyBid.getAdCache();
        if (adCache != null && adCache.inspect(zoneId) != null) {
            Ad cachedBid = adCache.remove(zoneId);
            mBannerView.renderAd(cachedBid, this);
        } else {
            mBannerView.setMediation(true);
            mBannerView.load(zoneId, this);
        }
    }

    @Override
    protected AdSize getAdSize(com.google.android.gms.ads.AdSize adSize) {
        int width = adSize.getWidth();
        int height = adSize.getHeight();
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
        return super.getAdSize(adSize);
    }
}
