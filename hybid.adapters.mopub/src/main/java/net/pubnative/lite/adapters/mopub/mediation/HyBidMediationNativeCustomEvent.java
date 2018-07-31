package net.pubnative.lite.adapters.mopub.mediation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.mopub.nativeads.CustomEventNative;
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
    private HyBidNativeAdRequest mAdRequest;

    @Override
    protected void loadNativeAd(@NonNull Context context,
                                @NonNull CustomEventNativeListener customEventNativeListener,
                                @NonNull Map<String, Object> localExtras,
                                @NonNull Map<String, String> serverExtras) {
        if (customEventNativeListener == null) {
            Logger.e(TAG, "customEventBannerListener is null");
            return;
        }

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
        if (mListener != null) {
            mListener.onNativeAdLoaded(null);
        }
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        if (mListener != null) {
            mListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
        }
    }

    public class HyBidStaticNativeAd extends StaticNativeAd {
        private final Context mContext;
        private final CustomEventNativeListener mListener;
        private final NativeAd mNativeAd;
        private final NativeAd.Listener mNativeAdListener = new NativeAd.Listener() {
            @Override
            public void onAdImpression(NativeAd PNAPIAdModel, View view) {
                notifyAdImpressed();
            }

            @Override
            public void onAdClick(NativeAd PNAPIAdModel, View view) {
                notifyAdClicked();
            }

            @Override
            public void onAdOpenOffer(NativeAd PNAPIAdModel) {

            }
        };

        HyBidStaticNativeAd(Context context, NativeAd nativeAd, CustomEventNativeListener listener) {
            mContext = context;
            mNativeAd = nativeAd;
            mListener = listener;
        }

        @Override
        public void prepare(@NonNull View view) {
            mNativeAd.startTracking(view, mNativeAdListener);
        }

        @Override
        public void clear(@NonNull View view) {
            mNativeAd.stopTracking();
        }

        @Override
        public void destroy() {

        }
    }
}
