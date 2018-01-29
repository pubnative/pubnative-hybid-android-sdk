package net.pubnative.lite.adapters.mopub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.mrect.presenter.MRectPresenter;
import net.pubnative.lite.sdk.mrect.presenter.MRectPresenterFactory;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.Map;

/**
 * Created by erosgarciaponte on 12.01.18.
 */

public class PNLiteMoPubMRectCustomEvent extends CustomEventBanner implements MRectPresenter.Listener {
    @NonNull
    private static final String TAG = PNLiteMoPubMRectCustomEvent.class.getSimpleName();

    @NonNull
    private static final String ZONE_ID_KEY = "pn_zone_id";
    @Nullable
    private CustomEventBannerListener mBannerListener;
    @Nullable
    private MRectPresenter mMRectPresenter;

    @Override
    protected void loadBanner(Context context,
                              CustomEventBannerListener customEventBannerListener,
                              Map<String, Object> localExtras,
                              Map<String, String> serverExtras) {

        if (customEventBannerListener == null) {
            Logger.e(TAG, "customEventBannerListener is null");
            return;
        }
        mBannerListener = customEventBannerListener;

        String zoneIdKey;
        if (localExtras.containsKey(ZONE_ID_KEY)) {
            zoneIdKey = (String) localExtras.get(ZONE_ID_KEY);
        } else if (serverExtras.containsKey(ZONE_ID_KEY)) {
            zoneIdKey = serverExtras.get(ZONE_ID_KEY);
        } else {
            Logger.e(TAG, "Could not find zone id value in CustomEventBanner localExtras or serverExtras");
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        final Ad ad = PNLite.getAdCache().remove(zoneIdKey);
        if (ad == null) {
            Logger.e(TAG, "Could not find an ad in the cache for zone id with key: " + zoneIdKey);
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mMRectPresenter = new MRectPresenterFactory(context).createMRectPresenter(ad, this);
        if (mMRectPresenter == null) {
            Logger.e(TAG, "Could not create valid MRect presenter");
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mMRectPresenter.load();
    }

    @Override
    protected void onInvalidate() {
        if (mMRectPresenter != null) {
            mMRectPresenter.destroy();
            mMRectPresenter = null;
        }
    }

    @Override
    public void onMRectLoaded(@NonNull MRectPresenter mRectPresenter, @NonNull View mRect) {
        if (mBannerListener != null) {
            mBannerListener.onBannerLoaded(mRect);
        }
    }

    @Override
    public void onMRectClicked(@NonNull MRectPresenter mRectPresenter) {
        if (mBannerListener != null) {
            mBannerListener.onBannerClicked();
        }
    }

    @Override
    public void onMRectError(@NonNull MRectPresenter mRectPresenter) {
        if (mBannerListener != null) {
            mBannerListener.onBannerFailed(MoPubErrorCode.INTERNAL_ERROR);
        }
    }
}