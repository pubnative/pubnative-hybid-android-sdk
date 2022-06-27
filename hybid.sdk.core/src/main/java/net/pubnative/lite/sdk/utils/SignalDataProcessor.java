package net.pubnative.lite.sdk.utils;

import android.text.TextUtils;

import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.api.PNApiClient;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.SignalData;
import net.pubnative.lite.sdk.utils.json.JsonOperations;
import net.pubnative.lite.sdk.vpaid.VideoAdCache;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdProcessor;
import net.pubnative.lite.sdk.vpaid.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.response.AdParams;

import org.json.JSONObject;

import java.util.List;

public class SignalDataProcessor {
    private static final String TAG = SignalDataProcessor.class.getSimpleName();

    public interface Listener {
        void onProcessed(Ad ad);

        void onError(Throwable error);
    }

    private final PNApiClient mApiClient;
    private final DeviceInfo mDeviceInfo;
    private final AdCache mAdCache;
    private final VideoAdCache mVideoCache;
    private Listener mListener;
    private boolean mIsDestroyed;

    public SignalDataProcessor() {
        this(HyBid.getApiClient(), HyBid.getDeviceInfo(), HyBid.getAdCache(), HyBid.getVideoAdCache());
    }

    SignalDataProcessor(PNApiClient apiClient, DeviceInfo deviceInfo, AdCache adCache, VideoAdCache videoAdCache) {
        this.mApiClient = apiClient;
        this.mDeviceInfo = deviceInfo;
        this.mAdCache = adCache;
        this.mVideoCache = videoAdCache;
    }

    public void processSignalData(String signalDataValue, Listener listener) {
        this.mListener = listener;
        try {
            final SignalData signalData = new SignalData(new JSONObject(signalDataValue));
            if (!TextUtils.isEmpty(signalData.tagid)) {
                if (!TextUtils.isEmpty(signalData.admurl)) {
                    if (mApiClient != null) {
                        String userAgent = "";
                        if (mDeviceInfo != null) {
                            userAgent = mDeviceInfo.getUserAgent();
                        }
                        mApiClient.getAd(signalData.admurl, userAgent, new PNApiClient.AdRequestListener() {
                            @Override
                            public void onSuccess(Ad ad) {
                                if (mIsDestroyed) {
                                    return;
                                }

                                Logger.d(TAG, "Received ad response for zone id: " + signalData.tagid);
                                processAd(signalData.tagid, ad);
                            }

                            @Override
                            public void onFailure(Throwable exception) {
                                if (mIsDestroyed) {
                                    return;
                                }

                                Logger.w(TAG, exception.getMessage());
                                if (mListener != null) {
                                    mListener.onError(new Exception(exception));
                                }
                            }
                        });
                    } else {
                        if (mListener != null) {
                            mListener.onError(new HyBidError(HyBidErrorCode.INTERNAL_ERROR));
                        }
                    }
                } else if (signalData.adm != null) {
                    if (mApiClient != null) {
                        mApiClient.processStream(signalData.adm, null, new PNApiClient.AdRequestListener() {
                            @Override
                            public void onSuccess(Ad ad) {
                                if (mIsDestroyed) {
                                    return;
                                }

                                Logger.d(TAG, "Received ad response for zone id: " + signalData.tagid);
                                processAd(signalData.tagid, ad);
                            }

                            @Override
                            public void onFailure(Throwable exception) {
                                if (mIsDestroyed) {
                                    return;
                                }

                                Logger.w(TAG, exception.getMessage());
                                if (mListener != null) {
                                    mListener.onError(new Exception(exception));
                                }
                            }
                        });
                    } else {
                        if (mListener != null) {
                            mListener.onError(new HyBidError(HyBidErrorCode.INTERNAL_ERROR));
                        }
                    }
                } else {
                    if (mListener != null) {
                        mListener.onError(new HyBidError(HyBidErrorCode.INTERNAL_ERROR));
                    }
                }
            } else {
                if (mListener != null) {
                    mListener.onError(new HyBidError(HyBidErrorCode.INVALID_ZONE_ID));
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
            if (mListener != null) {
                mListener.onError(new HyBidError(HyBidErrorCode.INVALID_SIGNAL_DATA));
            }
        }
    }

    private void processAd(final String zoneId, final Ad ad) {
        ad.setZoneId(zoneId);
        mAdCache.put(zoneId, ad);

        switch (ad.assetgroupid) {
            case ApiAssetGroupType.VAST_INTERSTITIAL:
            case ApiAssetGroupType.VAST_MRECT: {
                VideoAdProcessor videoAdProcessor = new VideoAdProcessor();
                videoAdProcessor.process(mApiClient.getContext(), ad.getVast(), null, new VideoAdProcessor.Listener() {
                    @Override
                    public void onCacheSuccess(AdParams adParams, String videoFilePath, EndCardData endCardData,
                                               String endCardFilePath, List<String> omidVendors) {
                        if (mIsDestroyed) {
                            return;
                        }

                        boolean hasEndCard = adParams.getEndCardList() != null && !adParams.getEndCardList().isEmpty();
                        ad.setHasEndCard(hasEndCard);

                        VideoAdCacheItem adCacheItem = new VideoAdCacheItem(adParams, videoFilePath, endCardData, endCardFilePath);
                        mVideoCache.put(zoneId, adCacheItem);
                        if (mListener != null) {
                            mListener.onProcessed(ad);
                        }
                    }

                    @Override
                    public void onCacheError(Throwable error) {
                        if (mIsDestroyed) {
                            return;
                        }

                        Logger.w(TAG, error.getMessage());
                        if (mListener != null) {
                            mListener.onError(error);
                        }
                    }
                });
                break;
            }
            default: {
                if (mListener != null) {
                    mListener.onProcessed(ad);
                }
            }
        }
    }

    public void destroy() {
        mIsDestroyed = true;
        mListener = null;
    }
}
