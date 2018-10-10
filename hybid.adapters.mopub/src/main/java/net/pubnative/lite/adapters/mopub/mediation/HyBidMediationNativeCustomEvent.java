package net.pubnative.lite.adapters.mopub.mediation;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;

import com.mopub.nativeads.CustomEventNative;
import com.mopub.nativeads.ImpressionTracker;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.StaticNativeAd;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.NativeAd;
import net.pubnative.lite.sdk.nativead.HyBidNativeAdRequest;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.Map;

public class HyBidMediationNativeCustomEvent extends CustomEventNative implements HyBidNativeAdRequest.RequestListener {
    private static final String TAG = HyBidMediationNativeCustomEvent.class.getSimpleName();

    private static final String APP_TOKEN_KEY = "pn_app_token";
    private static final String ZONE_ID_KEY = "pn_zone_id";

    private CustomEventNativeListener mListener;
    private Context mContext;
    private HyBidNativeAdRequest mAdRequest;

    @Override
    protected void loadNativeAd(@NonNull Context context,
                                @NonNull CustomEventNativeListener customEventNativeListener,
                                @NonNull Map<String, Object> localExtras,
                                @NonNull Map<String, String> serverExtras) {
        mContext = context;
        mListener = customEventNativeListener;

        String zoneId;
        String appToken;
        if (serverExtras.containsKey(ZONE_ID_KEY) && serverExtras.containsKey(APP_TOKEN_KEY)) {
            zoneId = serverExtras.get(ZONE_ID_KEY);
            appToken = serverExtras.get(APP_TOKEN_KEY);
        } else {
            Logger.e(TAG, "Could not find the required params in CustomEventNative serverExtras");
            mListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (appToken == null || !appToken.equals(HyBid.getAppToken())) {
            Logger.e(TAG, "The provided app token doesn't match the one used to initialise HyBid");
            mListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mAdRequest = new HyBidNativeAdRequest();
        mAdRequest.load(zoneId, this);
    }

    @Override
    public void onRequestSuccess(NativeAd ad) {
        new HyBidStaticNativeAd(ad, new ImpressionTracker(mContext), mListener);
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        if (mListener != null) {
            mListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
        }
    }

    private class HyBidStaticNativeAd extends StaticNativeAd implements NativeAd.Listener {
        private final CustomEventNativeListener mListener;
        private final NativeAd mNativeAd;
        private final ImpressionTracker mImpressionTracker;

        public HyBidStaticNativeAd(NativeAd nativeAd,
                                   ImpressionTracker impressionTracker,
                                   CustomEventNativeListener listener) {
            this.mNativeAd = nativeAd;
            this.mImpressionTracker = impressionTracker;
            this.mListener = listener;
            fillData();
            if (mListener != null) {
                mListener.onNativeAdLoaded(this);
            }
        }

        private void fillData() {
            setTitle(mNativeAd.getTitle());
            setText(mNativeAd.getDescription());
            setIconImageUrl(mNativeAd.getIconUrl());
            setMainImageUrl(mNativeAd.getBannerUrl());
            setCallToAction(mNativeAd.getCallToActionText());
            setStarRating((double) mNativeAd.getRating());
            setPrivacyInformationIconImageUrl(mNativeAd.getContentInfoIconUrl());
            setPrivacyInformationIconClickThroughUrl(mNativeAd.getContentInfoClickUrl());
        }

        @Override
        public void prepare(@NonNull View view) {
            mImpressionTracker.addView(view, this);
            mNativeAd.startTracking(view, this);
        }

        @Override
        public void clear(@NonNull View view) {
            mImpressionTracker.removeView(view);
            mNativeAd.stopTracking();
        }

        @Override
        public void onAdImpression(NativeAd ad, View view) {
            notifyAdImpressed();
        }

        @Override
        public void onAdClick(NativeAd ad, View view) {
            notifyAdClicked();
        }
    }
}
