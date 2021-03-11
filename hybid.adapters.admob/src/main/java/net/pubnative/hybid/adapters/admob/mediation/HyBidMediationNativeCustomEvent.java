package net.pubnative.hybid.adapters.admob.mediation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.NativeMediationAdRequest;
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper;
import com.google.android.gms.ads.mediation.customevent.CustomEventNative;
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener;

import net.pubnative.hybid.adapters.admob.HyBidAdmobUtils;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.NativeAd;
import net.pubnative.lite.sdk.request.HyBidNativeAdRequest;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNBitmapDownloader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HyBidMediationNativeCustomEvent implements CustomEventNative, HyBidNativeAdRequest.RequestListener {
    private static final String TAG = HyBidMediationNativeCustomEvent.class.getSimpleName();
    private static final double IMAGE_SCALE = 1.0;

    private CustomEventNativeListener mNativeListener;
    private Context mContext;
    private HyBidNativeAdRequest mAdRequest;

    @Override
    public void requestNativeAd(Context context,
                                CustomEventNativeListener listener,
                                String serverParameter,
                                NativeMediationAdRequest mediationAdRequest,
                                Bundle customEventExtras) {
        if (listener == null) {
            Logger.e(TAG, "customEventBannerListener is null");
            return;
        }

        mNativeListener = listener;
        mContext = context;

        String zoneId;
        String appToken;
        if (!TextUtils.isEmpty(HyBidAdmobUtils.getAppToken(serverParameter))
                && !TextUtils.isEmpty(HyBidAdmobUtils.getZoneId(serverParameter))) {
            zoneId = HyBidAdmobUtils.getZoneId(serverParameter);
            appToken = HyBidAdmobUtils.getAppToken(serverParameter);
        } else {
            Logger.e(TAG, "Could not find the required params in CustomEventBanner serverExtras");
            mNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        if (appToken == null || !appToken.equals(HyBid.getAppToken())) {
            Logger.e(TAG, "The provided app token doesn't match the one used to initialise HyBid");
            mNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }

        mAdRequest = new HyBidNativeAdRequest();
        mAdRequest.setMediation(true);
        mAdRequest.load(zoneId, this);
    }

    //------------------------------ HyBidNativeAdRequest Callbacks --------------------------------
    @Override
    public void onRequestSuccess(NativeAd ad) {
        HyBidNativeAdMapper mapper = new HyBidNativeAdMapper(ad, new HyBidNativeAdMapperCallback() {
            @Override
            public void onAdLoaded(HyBidNativeAdMapper mappedAd) {
                if (mNativeListener != null) {
                    mNativeListener.onAdLoaded(mappedAd);
                }
            }
        });
        mapper.loadAd();
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        if (mNativeListener != null) {
            mNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    private class HyBidNativeAdMapper extends UnifiedNativeAdMapper implements NativeAd.Listener {
        private final NativeAd mNativeAd;
        private final HyBidNativeAdMapperCallback mMapperListener;

        public HyBidNativeAdMapper(NativeAd nativeAd, HyBidNativeAdMapperCallback listener) {
            this.mNativeAd = nativeAd;
            this.mMapperListener = listener;
            setOverrideClickHandling(false);
            setOverrideImpressionRecording(true);
        }

        public void loadAd() {
            setHeadline(mNativeAd.getTitle());
            setBody(mNativeAd.getDescription());
            setStarRating((double) mNativeAd.getRating());
            setCallToAction(mNativeAd.getCallToActionText());
            setAdChoicesContent(mNativeAd.getContentInfo(mContext));

            if (!TextUtils.isEmpty(mNativeAd.getIconUrl())) {
                PNBitmapDownloader iconDownloader = new PNBitmapDownloader();
                iconDownloader.download(mNativeAd.getIconUrl(), mIconDownloadListener);
            } else {
                if (!TextUtils.isEmpty(mNativeAd.getBannerUrl())) {
                    PNBitmapDownloader bannerDownloader = new PNBitmapDownloader();
                    bannerDownloader.download(mNativeAd.getBannerUrl(), mBannerDownloadListener);
                } else {
                    if (mMapperListener != null) {
                        mMapperListener.onAdLoaded(this);
                    }
                }
            }
        }

        private final PNBitmapDownloader.DownloadListener mIconDownloadListener = new PNBitmapDownloader.DownloadListener() {
            @Override
            public void onDownloadFinish(String url, Bitmap bitmap) {
                HyBidNativeMappedImage iconImage = new HyBidNativeMappedImage(mContext, mNativeAd.getIconUrl(), IMAGE_SCALE, bitmap);
                setIcon(iconImage);
                if (!TextUtils.isEmpty(mNativeAd.getBannerUrl())) {
                    PNBitmapDownloader bannerDownloader = new PNBitmapDownloader();
                    bannerDownloader.download(mNativeAd.getBannerUrl(), mBannerDownloadListener);
                } else {
                    if (mMapperListener != null) {
                        mMapperListener.onAdLoaded(HyBidNativeAdMapper.this);
                    }
                }
            }

            @Override
            public void onDownloadFailed(String url, Exception exception) {
                Logger.e(TAG, exception.getMessage());
                if (!TextUtils.isEmpty(mNativeAd.getBannerUrl())) {
                    PNBitmapDownloader bannerDownloader = new PNBitmapDownloader();
                    bannerDownloader.download(mNativeAd.getBannerUrl(), mBannerDownloadListener);
                } else {
                    if (mMapperListener != null) {
                        mMapperListener.onAdLoaded(HyBidNativeAdMapper.this);
                    }
                }
            }
        };

        private final PNBitmapDownloader.DownloadListener mBannerDownloadListener = new PNBitmapDownloader.DownloadListener() {
            @Override
            public void onDownloadFinish(String url, Bitmap bitmap) {
                List<com.google.android.gms.ads.formats.NativeAd.Image> imageList = new ArrayList<>();
                imageList.add(new HyBidNativeMappedImage(mContext, mNativeAd.getBannerUrl(), IMAGE_SCALE, bitmap));
                setImages(imageList);

                if (mMapperListener != null) {
                    mMapperListener.onAdLoaded(HyBidNativeAdMapper.this);
                }
            }

            @Override
            public void onDownloadFailed(String url, Exception exception) {
                Logger.e(TAG, exception.getMessage());
                if (mMapperListener != null) {
                    mMapperListener.onAdLoaded(HyBidNativeAdMapper.this);
                }
            }
        };

        @Override
        public void trackViews(View containerView,
                               Map<String, View> clickableAssetViews,
                               Map<String, View> nonClickableAssetViews) {
            mNativeAd.startTracking(containerView, null, this);
        }

        @Override
        public void untrackView(View view) {
            mNativeAd.stopTracking();
        }

        @Override
        public void handleClick(View view) {
            if (mNativeAd != null) {
                mNativeAd.onNativeClick();
            }
        }

        @Override
        public void onAdImpression(NativeAd ad, View view) {
            if (mNativeListener != null) {
                mNativeListener.onAdImpression();
            }
        }

        @Override
        public void onAdClick(NativeAd ad, View view) {
            //This is not used because we're using the click tracking from Admob
        }
    }

    private interface HyBidNativeAdMapperCallback {
        void onAdLoaded(HyBidNativeAdMapper mappedAd);
    }

    private static class HyBidNativeMappedImage extends com.google.android.gms.ads.formats.NativeAd.Image {
        private final Uri mImageUri;
        private final double mScale;
        private final Drawable mDrawable;

        public HyBidNativeMappedImage(Context context, String imageUrl, double scale, Bitmap bitmap) {
            if (TextUtils.isEmpty(imageUrl)) {
                mImageUri = null;
            } else {
                mImageUri = Uri.parse(imageUrl);
            }
            mScale = scale;

            if (bitmap != null) {
                mDrawable = new BitmapDrawable(context.getResources(), bitmap);
            } else {
                mDrawable = null;
            }
        }

        @Override
        public Drawable getDrawable() {
            return mDrawable;
        }

        @Override
        public Uri getUri() {
            return mImageUri;
        }

        @Override
        public double getScale() {
            return mScale;
        }
    }
}
