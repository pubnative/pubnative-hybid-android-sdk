package net.pubnative.lite.sdk.banner.presenter;

import android.view.View;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class BannerPresenterDecorator implements BannerPresenter, BannerPresenter.Listener {
    private static final String TAG = BannerPresenterDecorator.class.getSimpleName();
    private final BannerPresenter mBannerPresenter;
    private final AdTracker mAdTrackingDelegate;
    private final BannerPresenter.Listener mListener;
    private boolean mIsDestroyed;

    public BannerPresenterDecorator(BannerPresenter bannerPresenter,
                                    AdTracker adTrackingDelegate,
                                    BannerPresenter.Listener listener) {
        mBannerPresenter = bannerPresenter;
        mAdTrackingDelegate = adTrackingDelegate;
        mListener = listener;
    }

    @Override
    public void setListener(Listener listener) {
        // We set the listener in the constructor instead
    }

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
    public void onBannerLoaded(BannerPresenter bannerPresenter, View banner) {
        if (mIsDestroyed) {
            return;
        }

        //Logger.d(TAG, "Banner loaded for ad unit id: " + getAd().getAdUnitId());
        mAdTrackingDelegate.trackImpression();
        mListener.onBannerLoaded(bannerPresenter, banner);
    }

    @Override
    public void onBannerClicked(BannerPresenter bannerPresenter) {
        if (mIsDestroyed) {
            return;
        }

        //Logger.d(TAG, "Banner clicked for ad unit id: " + getAd().getAdUnitId());
        mAdTrackingDelegate.trackClick();
        mListener.onBannerClicked(bannerPresenter);
    }

    @Override
    public void onBannerError(BannerPresenter bannerPresenter) {
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
