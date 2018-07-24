package net.pubnative.lite.sdk.nativead;

import net.pubnative.lite.sdk.api.NativeRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.NativeAd;

public class HyBidNativeAdRequest implements RequestManager.RequestListener {
    private static final String TAG = HyBidNativeAdRequest.class.getSimpleName();

    public interface RequestListener {
        void onRequestSuccess(NativeAd ad);

        void onRequestFail(Throwable throwable);
    }

    private RequestListener mListener;
    private RequestManager mRequestManager;

    public HyBidNativeAdRequest() {
        this.mRequestManager = new NativeRequestManager();
        this.mRequestManager.setRequestListener(this);
    }

    public void load(String zoneId, RequestListener listener) {
        mListener = listener;
        mRequestManager.setZoneId(zoneId);
        mRequestManager.requestAd();
    }

    @Override
    public void onRequestSuccess(Ad ad) {
        createNativeAd(ad);
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        if (mListener != null) {
            mListener.onRequestFail(throwable);
        }
    }

    private void createNativeAd(Ad ad) {
        if (mListener != null) {
            mListener.onRequestSuccess(new NativeAd(ad));
        }
    }
}
