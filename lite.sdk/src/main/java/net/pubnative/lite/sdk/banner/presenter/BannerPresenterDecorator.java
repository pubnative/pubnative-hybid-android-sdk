package net.pubnative.lite.sdk.banner.presenter;

import android.view.View;
import android.view.ViewGroup;

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

        mBannerPresenter.load();
    }

    @Override
    public void destroy() {
        mBannerPresenter.destroy();
        mIsDestroyed = true;
    }

    @Override
    public void startTracking() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "BannerPresenterDecorator is destroyed")) {
            return;
        }

        mBannerPresenter.startTracking();
    }

    @Override
    public void stopTracking() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "BannerPresenterDecorator is destroyed")) {
            return;
        }

        mBannerPresenter.stopTracking();
    }

    @Override
    public void onBannerLoaded(BannerPresenter bannerPresenter, View banner) {
        if (mIsDestroyed) {
            return;
        }

        mAdTrackingDelegate.trackImpression();
        mListener.onBannerLoaded(bannerPresenter, banner);
    }

    @Override
    public void onBannerClicked(BannerPresenter bannerPresenter) {
        if (mIsDestroyed) {
            return;
        }

        mAdTrackingDelegate.trackClick();
        mListener.onBannerClicked(bannerPresenter);
    }

    @Override
    public void onBannerError(BannerPresenter bannerPresenter) {
        if (mIsDestroyed) {
            return;
        }

        String errorMessage = "Banner error for zone id: ";
        Logger.d(TAG, errorMessage);
        mListener.onBannerError(bannerPresenter);
    }
}
