package net.pubnative.tarantula.sdk.banner.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.utils.AdTracker;
import net.pubnative.tarantula.sdk.utils.CheckUtils;
import net.pubnative.tarantula.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class BannerPresenterDecorator implements BannerPresenter, BannerPresenter.Listener {
    @NonNull
    private static final String TAG = BannerPresenterDecorator.class.getSimpleName();
    @NonNull
    private final BannerPresenter mBannerPresenter;
    @NonNull
    private final AdTracker mAdTrackingDelegate;
    @NonNull
    private final BannerPresenter.Listener mListener;
    private boolean mIsDestroyed;

    public BannerPresenterDecorator(@NonNull BannerPresenter bannerPresenter,
                                    @NonNull AdTracker adTrackingDelegate,
                                    @NonNull BannerPresenter.Listener listener) {
        mBannerPresenter = bannerPresenter;
        mAdTrackingDelegate = adTrackingDelegate;
        mListener = listener;
    }

    @Override
    public void setListener(@Nullable Listener listener) {
        // We set the listener in the constructor instead
    }

    @NonNull
    @Override
    public Ad getAd() {
        return mBannerPresenter.getAd();
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "BannerPresenterDecorator is destroyed")) {
            return;
        }

        //Logger.d(TAG, "Loading banner presenter for zone id: " + getAd().getAdUnitId());
        mBannerPresenter.load();
    }

    @Override
    public void destroy() {
        //Logger.d(TAG, "Destroying banner presenter for ad unit id: " + getAd().getAdUnitId());
        mBannerPresenter.destroy();
        mIsDestroyed = true;
    }

    @Override
    public void onBannerLoaded(@NonNull BannerPresenter bannerPresenter, @NonNull View banner) {
        if (mIsDestroyed) {
            return;
        }

        //Logger.d(TAG, "Banner loaded for ad unit id: " + getAd().getAdUnitId());
        mAdTrackingDelegate.trackImpression();
        mListener.onBannerLoaded(bannerPresenter, banner);
    }

    @Override
    public void onBannerClicked(@NonNull BannerPresenter bannerPresenter) {
        if (mIsDestroyed) {
            return;
        }

        //Logger.d(TAG, "Banner clicked for ad unit id: " + getAd().getAdUnitId());
        mAdTrackingDelegate.trackClick();
        mListener.onBannerClicked(bannerPresenter);
    }

    @Override
    public void onBannerError(@NonNull BannerPresenter bannerPresenter) {
        if (mIsDestroyed) {
            return;
        }

        //String errorMessage = "Banner error for zone id: " + getAd().getAdUnitId();
        String errorMessage = "Banner error for zone id: ";
        Logger.d(TAG, errorMessage);
        //mAdTrackingDelegate.trackError(errorMessage);
        mListener.onBannerError(bannerPresenter);
    }
}
