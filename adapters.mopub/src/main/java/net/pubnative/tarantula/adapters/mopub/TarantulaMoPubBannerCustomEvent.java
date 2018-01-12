package net.pubnative.tarantula.adapters.mopub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.banner.presenter.BannerPresenter;
import net.pubnative.tarantula.sdk.banner.presenter.BannerPresenterFactory;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.utils.Logger;

import java.util.Map;

/**
 * Created by erosgarciaponte on 12.01.18.
 */

public class TarantulaMoPubBannerCustomEvent extends CustomEventBanner implements BannerPresenter.Listener {
    @NonNull
    private static final String TAG = TarantulaMoPubBannerCustomEvent.class.getSimpleName();

    @NonNull
    private static final String ZONE_ID_KEY = "zone_id";
    @Nullable
    private CustomEventBannerListener mBannerListener;
    @Nullable
    private BannerPresenter mBannerPresenter;

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

        final Ad ad = Tarantula.getAdCache().remove(zoneIdKey);
        if (ad == null) {
            Logger.e(TAG, "Could not find an ad in the cache for zone id with key: " + zoneIdKey);
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mBannerPresenter = new BannerPresenterFactory(context).createBannerPresenter(ad, this);
        if (mBannerPresenter == null) {
            Logger.e(TAG, "Could not create valid banner presenter");
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mBannerPresenter.load();
    }

    @Override
    protected void onInvalidate() {
        if (mBannerPresenter != null) {
            mBannerPresenter.destroy();
            mBannerPresenter = null;
        }
    }

    @Override
    public void onBannerLoaded(@NonNull BannerPresenter bannerPresenter, @NonNull View banner) {
        if (mBannerListener != null) {
            mBannerListener.onBannerLoaded(banner);
        }
    }

    @Override
    public void onBannerClicked(@NonNull BannerPresenter bannerPresenter) {
        if (mBannerListener != null) {
            mBannerListener.onBannerClicked();
        }
    }

    @Override
    public void onBannerError(@NonNull BannerPresenter bannerPresenter) {
        if (mBannerListener != null) {
            mBannerListener.onBannerFailed(MoPubErrorCode.INTERNAL_ERROR);
        }
    }
}
