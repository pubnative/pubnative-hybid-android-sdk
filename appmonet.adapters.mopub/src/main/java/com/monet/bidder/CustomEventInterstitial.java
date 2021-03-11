package com.monet.bidder;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.monet.bidder.mapper.AdRequestInfo;
import com.monet.bidder.mapper.PlacementMappingManager;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.AdData;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.lite.adapters.mopub.mediation.HyBidMediationInterstitialCustomEvent;
import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.utils.Logger;

public class CustomEventInterstitial extends HyBidMediationInterstitialCustomEvent {
    private static final String TAG = CustomEventInterstitial.class.getSimpleName();
    protected static final String ECPM_KEY = "cpm";

    @Override
    protected void load(@NonNull Context context, @NonNull AdData adData) throws Exception {
        String ecpm;
        String appToken = null;
        if (adData.getExtras().containsKey(ECPM_KEY)) {
            ecpm = adData.getExtras().get(ECPM_KEY);
            if (!TextUtils.isEmpty(ecpm)) {
                try {
                    Double ecpmNumber = Double.parseDouble(ecpm);
                    AdRequestInfo adRequestInfo = PlacementMappingManager.getInstance(context).getEcpmMapping(AdSize.SIZE_INTERSTITIAL, ecpmNumber);
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
        mInterstitialAd = new HyBidInterstitialAd(context, mZoneID, this);

        AdCache adCache = HyBid.getAdCache();
        if (adCache != null && adCache.inspect(mZoneID) != null) {
            Ad cachedBid = adCache.inspect(mZoneID);
            mInterstitialAd.prepareAd(cachedBid);
        } else {
            mInterstitialAd.setMediation(true);
            mInterstitialAd.load();
        }

        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED, TAG);
    }
}
