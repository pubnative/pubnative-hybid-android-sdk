package net.pubnative.lite.sdk.utils;

import android.text.TextUtils;

import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.ErrorMessages;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.api.PNApiClient;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.SignalData;
import net.pubnative.lite.sdk.vpaid.VideoAdCache;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdProcessor;
import net.pubnative.lite.sdk.vpaid.response.AdParams;

import org.json.JSONObject;

public class SignalDataProcessor {
    private static final String TAG = SignalDataProcessor.class.getSimpleName();

    public interface Listener {
        void onProcessed(Ad ad);

        void onError(Exception error);
    }

    private final PNApiClient mApiClient;
    private final AdCache mAdCache;
    private final VideoAdCache mVideoCache;
    private Listener mListener;
    private boolean mIsDestroyed;

    public SignalDataProcessor() {
        this(HyBid.getApiClient(), HyBid.getAdCache(), HyBid.getVideoAdCache());
    }

    SignalDataProcessor(PNApiClient apiClient, AdCache adCache, VideoAdCache videoAdCache) {
        this.mApiClient = apiClient;
        this.mAdCache = adCache;
        this.mVideoCache = videoAdCache;
    }

    public void processSignalData(String signalDataValue, Listener listener) {
        this.mListener = listener;
        try {
            final SignalData signalData = new SignalData(new JSONObject(signalDataValue));
            if (!TextUtils.isEmpty(signalData.tagid)) {
                if (mApiClient != null) {
                    mApiClient.getAd(signalData.admurl, new PNApiClient.AdRequestListener() {
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
                        mListener.onError(new Exception(ErrorMessages.INTERNAL_ERROR));
                    }
                }
            } else {
                if (mListener != null) {
                    mListener.onError(new Exception(ErrorMessages.INVALID_ZONE_ID));
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
            if (mListener != null) {
                mListener.onError(new Exception(ErrorMessages.INVALID_SIGNAL_DATA));
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
                    public void onCacheSuccess(AdParams adParams, String videoFilePath, String endCardFilePath) {
                        if (mIsDestroyed) {
                            return;
                        }

                        VideoAdCacheItem adCacheItem = new VideoAdCacheItem(adParams, videoFilePath, endCardFilePath);
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
                            mListener.onError(new Exception(error));
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
