// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.hybid.adapters.admob.mediation;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.MediationNativeAdCallback;
import com.google.android.gms.ads.mediation.MediationNativeAdConfiguration;
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper;

import net.pubnative.hybid.adapters.admob.HyBidAdmobUtils;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.NativeAd;
import net.pubnative.lite.sdk.request.HyBidNativeAdRequest;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNBitmapDownloader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HyBidMediationNativeCustomEvent extends HyBidMediationBaseCustomEvent {
    private static final String TAG = HyBidMediationNativeCustomEvent.class.getSimpleName();
    private static final double IMAGE_SCALE = 1.0;

    @Override
    public void loadNativeAd(@NonNull MediationNativeAdConfiguration mediationNativeAdConfiguration, @NonNull MediationAdLoadCallback<UnifiedNativeAdMapper, MediationNativeAdCallback> callback) {
        if (callback == null) {
            Logger.e(TAG, "MediationAdLoadCallback is null");
            return;
        }

        if (mediationNativeAdConfiguration == null || mediationNativeAdConfiguration.getContext() == null) {
            Logger.e(TAG, "Missing context. Dropping call");
            return;
        }

        HyBidNativeCustomEventLoader mAdLoader = new HyBidNativeCustomEventLoader(mediationNativeAdConfiguration, callback);
        mAdLoader.loadAd();
    }

    public class HyBidNativeCustomEventLoader implements HyBidNativeAdRequest.RequestListener {
        private HyBidNativeAdRequest mNativeAdRequest;
        private final MediationNativeAdConfiguration mAdConfiguration;
        private final MediationAdLoadCallback<UnifiedNativeAdMapper, MediationNativeAdCallback> mAdLoadCallback;

        public HyBidNativeCustomEventLoader(MediationNativeAdConfiguration mediationNativeAdConfiguration, MediationAdLoadCallback<UnifiedNativeAdMapper, MediationNativeAdCallback> adLoadCallback) {
            mAdConfiguration = mediationNativeAdConfiguration;
            mAdLoadCallback = adLoadCallback;
        }

        public void loadAd() {
            String zoneId;
            String appToken;

            String serverParameter = mAdConfiguration.getServerParameters().getString(MediationConfiguration.CUSTOM_EVENT_SERVER_PARAMETER_FIELD);
            if (!TextUtils.isEmpty(HyBidAdmobUtils.getAppToken(serverParameter))
                    && !TextUtils.isEmpty(HyBidAdmobUtils.getZoneId(serverParameter))) {
                zoneId = HyBidAdmobUtils.getZoneId(serverParameter);
                appToken = HyBidAdmobUtils.getAppToken(serverParameter);
            } else {
                Logger.e(TAG, "Could not find the required params in MediationNativeAdConfiguration. " +
                        "Required params in MediationNativeAdConfiguration must be provided as a valid JSON Object. " +
                        "Please consult HyBid documentation and update settings in your AdMob publisher dashboard.");
                mAdLoadCallback.onFailure(new AdError(AdRequest.ERROR_CODE_NETWORK_ERROR,
                        "Could not find the required params in MediationNativeAdConfiguration",
                        AdError.UNDEFINED_DOMAIN
                ));
                return;
            }

            if (HyBid.isInitialized()) {
                if (TextUtils.isEmpty(appToken) || !appToken.equals(HyBid.getAppToken())) {
                    Logger.e(TAG, "The provided app token doesn't match the one used to initialise HyBid");
                    mAdLoadCallback.onFailure(new AdError(AdRequest.ERROR_CODE_NETWORK_ERROR,
                            "The provided app token doesn't match the one used to initialise HyBid",
                            AdError.UNDEFINED_DOMAIN
                    ));
                } else {
                    loadNativeAd(zoneId);
                }
            } else {
                HyBid.initialize(appToken, (Application) mAdConfiguration.getContext().getApplicationContext(), b ->
                        loadNativeAd(zoneId));
            }
        }

        private void loadNativeAd(String zoneId) {
            mNativeAdRequest = new HyBidNativeAdRequest();
            mNativeAdRequest.setMediation(true);
            mNativeAdRequest.load(zoneId, this);
        }

        @Override
        public void onRequestSuccess(NativeAd ad) {
            HyBidNativeAdMapper mapper = new HyBidNativeAdMapper(mAdConfiguration.getContext(), ad, mAdLoadCallback);
            mapper.loadAd();
        }

        @Override
        public void onRequestFail(Throwable throwable) {
            Logger.e(TAG, throwable.getMessage());
            if (mAdLoadCallback != null) {
                mAdLoadCallback.onFailure(new AdError(AdRequest.ERROR_CODE_NO_FILL,
                        throwable != null && !TextUtils.isEmpty(throwable.getMessage()) ? throwable.getMessage() : "No fill.",
                        AdError.UNDEFINED_DOMAIN
                ));
            }
        }
    }

    private class HyBidNativeAdMapper extends UnifiedNativeAdMapper implements NativeAd.Listener {
        private final NativeAd mNativeAd;
        private final MediationAdLoadCallback<UnifiedNativeAdMapper, MediationNativeAdCallback> mAdLoadCallback;
        private MediationNativeAdCallback mNativeAdCallback;
        private final Context mContext;

        public HyBidNativeAdMapper(Context context, NativeAd nativeAd, MediationAdLoadCallback<UnifiedNativeAdMapper, MediationNativeAdCallback> adLoadCallback) {
            this.mContext = context;
            this.mNativeAd = nativeAd;
            this.mAdLoadCallback = adLoadCallback;
            setOverrideClickHandling(false);
            setOverrideImpressionRecording(true);
        }

        public void loadAd() {
            if (mNativeAd != null) {
                setHeadline(mNativeAd.getTitle());
                setBody(mNativeAd.getDescription());
                setStarRating((double) mNativeAd.getRating());
                setCallToAction(mNativeAd.getCallToActionText());
                if (mContext != null)
                    setAdChoicesContent(mNativeAd.getContentInfo(mContext));
                if (!TextUtils.isEmpty(mNativeAd.getIconUrl())) {
                    PNBitmapDownloader iconDownloader = new PNBitmapDownloader();
                    iconDownloader.download(mNativeAd.getIconUrl(), mIconDownloadListener);
                } else {
                    if (!TextUtils.isEmpty(mNativeAd.getBannerUrl())) {
                        PNBitmapDownloader bannerDownloader = new PNBitmapDownloader();
                        bannerDownloader.download(mNativeAd.getBannerUrl(), mBannerDownloadListener);
                    } else {
                        reportAdLoaded();
                    }
                }
            }
        }

        private void reportAdLoaded() {
            if (mAdLoadCallback != null) {
                mNativeAdCallback = mAdLoadCallback.onSuccess(this);
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
                    reportAdLoaded();
                }
            }

            @Override
            public void onDownloadFailed(String url, Exception exception) {
                Logger.e(TAG, exception.getMessage());
                HyBid.reportException(exception);
                if (!TextUtils.isEmpty(mNativeAd.getBannerUrl())) {
                    PNBitmapDownloader bannerDownloader = new PNBitmapDownloader();
                    bannerDownloader.download(mNativeAd.getBannerUrl(), mBannerDownloadListener);
                } else {
                    reportAdLoaded();
                }
            }
        };

        private final PNBitmapDownloader.DownloadListener mBannerDownloadListener = new PNBitmapDownloader.DownloadListener() {
            @Override
            public void onDownloadFinish(String url, Bitmap bitmap) {
                List<com.google.android.gms.ads.formats.NativeAd.Image> imageList = new ArrayList<>();
                imageList.add(new HyBidNativeMappedImage(mContext, mNativeAd.getBannerUrl(), IMAGE_SCALE, bitmap));
                setImages(imageList);

                reportAdLoaded();
            }

            @Override
            public void onDownloadFailed(String url, Exception exception) {
                Logger.e(TAG, exception.getMessage());
                HyBid.reportException(exception);
                reportAdLoaded();
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
            if (mNativeAdCallback != null) {
                mNativeAdCallback.reportAdImpression();
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
