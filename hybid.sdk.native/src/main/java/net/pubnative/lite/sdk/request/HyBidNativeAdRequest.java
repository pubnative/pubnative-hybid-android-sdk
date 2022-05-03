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
package net.pubnative.lite.sdk.request;

import android.graphics.Bitmap;
import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.NativeAd;
import net.pubnative.lite.sdk.models.RemoteConfigFeature;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNBitmapDownloader;
import net.pubnative.lite.sdk.utils.SignalDataProcessor;

public class HyBidNativeAdRequest implements RequestManager.RequestListener {
    private static final String TAG = HyBidNativeAdRequest.class.getSimpleName();

    private String mScreenIabCategory;
    private String mScreenKeywords;
    private String mUserIntent;

    public interface RequestListener {
        void onRequestSuccess(NativeAd ad);

        void onRequestFail(Throwable throwable);
    }

    private RequestListener mListener;
    private final RequestManager mRequestManager;
    private SignalDataProcessor mSignalDataProcessor;
    private PNBitmapDownloader mBitmapDownloader;
    private boolean mPreLoadMediaAssets;

    public HyBidNativeAdRequest() {
        this.mRequestManager = new NativeRequestManager();
        this.mRequestManager.setIntegrationType(IntegrationType.STANDALONE);
        this.mRequestManager.setRequestListener(this);
        this.mBitmapDownloader = new PNBitmapDownloader();
        this.mPreLoadMediaAssets = false;
    }

    public void load(String zoneId, RequestListener listener) {
        load(null, zoneId, listener);
    }

    public void load(String appToken, String zoneId, RequestListener listener) {
        if (HyBid.getConfigManager() != null
                && !HyBid.getConfigManager().getFeatureResolver().isAdFormatEnabled(RemoteConfigFeature.AdFormat.NATIVE)) {
            if (listener != null) {
                listener.onRequestFail(new HyBidError(HyBidErrorCode.DISABLED_FORMAT));
            }
        } else {
            mListener = listener;
            if (!TextUtils.isEmpty(appToken)) {
                mRequestManager.setAppToken(appToken);
            }
            mRequestManager.setZoneId(zoneId);
            mRequestManager.requestAd();
        }
    }

    public void prepareAd(String adValue, RequestListener listener) {
        if (!TextUtils.isEmpty(adValue)) {
            mListener = listener;

            mSignalDataProcessor = new SignalDataProcessor();
            mSignalDataProcessor.processSignalData(adValue, new SignalDataProcessor.Listener() {
                @Override
                public void onProcessed(Ad ad) {
                    if (ad != null) {
                        createNativeAd(ad);
                    } else {
                        if (mListener != null) {
                            mListener.onRequestFail(new HyBidError(HyBidErrorCode.NULL_AD));
                        }
                    }
                }

                @Override
                public void onError(Throwable error) {
                    if (mListener != null) {
                        mListener.onRequestFail(error);
                    }
                }
            });

        } else {
            if (listener != null) {
                listener.onRequestFail(new HyBidError(HyBidErrorCode.INVALID_SIGNAL_DATA));
            }
        }
    }

    @Override
    public void onRequestSuccess(Ad ad) {
        createNativeAd(ad);
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        if (throwable instanceof HyBidError) {
            HyBidError hyBidError = (HyBidError) throwable;
            if (hyBidError.getErrorCode() == HyBidErrorCode.NO_FILL) {
                Logger.w(TAG, throwable.getMessage());
            } else {
                Logger.e(TAG, throwable.getMessage());
            }
        }
        if (mListener != null) {
            mListener.onRequestFail(throwable);
        }
    }

    private void createNativeAd(Ad ad) {
        final NativeAd nativeAd = new NativeAd(ad);
        if (mPreLoadMediaAssets) {
            fetchBanner(nativeAd);
        } else {
            if (mListener != null) {
                mListener.onRequestSuccess(nativeAd);
            }
        }
    }

    private void fetchBanner(final NativeAd nativeAd) {
        if (TextUtils.isEmpty(nativeAd.getBannerUrl())) {
            fetchIcon(nativeAd);
        } else {
            mBitmapDownloader.download(nativeAd.getBannerUrl(), new PNBitmapDownloader.DownloadListener() {
                @Override
                public void onDownloadFinish(String url, Bitmap bitmap) {
                    if (bitmap != null) {
                        nativeAd.setBannerBitmap(bitmap);
                    }
                    fetchIcon(nativeAd);
                }

                @Override
                public void onDownloadFailed(String url, Exception exception) {
                    fetchIcon(nativeAd);
                }
            });
        }
    }

    private void fetchIcon(final NativeAd nativeAd) {
        if (TextUtils.isEmpty(nativeAd.getIconUrl())) {
            if (mListener != null) {
                mListener.onRequestSuccess(nativeAd);
            }
        } else {
            mBitmapDownloader.download(nativeAd.getIconUrl(), new PNBitmapDownloader.DownloadListener() {
                @Override
                public void onDownloadFinish(String url, Bitmap bitmap) {
                    if (bitmap != null) {
                        nativeAd.setIconBitmap(bitmap);
                    }

                    if (mListener != null) {
                        mListener.onRequestSuccess(nativeAd);
                    }
                }

                @Override
                public void onDownloadFailed(String url, Exception exception) {
                    if (mListener != null) {
                        mListener.onRequestSuccess(nativeAd);
                    }
                }
            });
        }
    }

    public void setMediationVendor(String mediationVendor) {
        if (mRequestManager != null) {
            mRequestManager.setMediationVendor(mediationVendor);
        }
    }

    public void setMediation(boolean isMediation) {
        if (mRequestManager != null) {
            mRequestManager.setIntegrationType(isMediation ? IntegrationType.MEDIATION : IntegrationType.STANDALONE);
        }
    }

    public void setPreLoadMediaAssets(boolean preLoadMediaAssets) {
        this.mPreLoadMediaAssets = preLoadMediaAssets;
    }

    public void setScreenIabCategory(String screenIabCategory) {
        mScreenIabCategory = screenIabCategory;
    }

    public void setScreenKeywords(String screenKeywords) {
        mScreenKeywords = screenKeywords;
    }

    public void setUserIntent(String userIntent) {
        mUserIntent = userIntent;
    }
}
