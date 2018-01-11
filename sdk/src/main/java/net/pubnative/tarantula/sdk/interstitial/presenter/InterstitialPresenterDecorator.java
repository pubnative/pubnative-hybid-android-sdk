package net.pubnative.tarantula.sdk.interstitial.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.models.api.PNAPIV3AdModel;
import net.pubnative.tarantula.sdk.utils.AdTracker;
import net.pubnative.tarantula.sdk.utils.CheckUtils;
import net.pubnative.tarantula.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class InterstitialPresenterDecorator implements InterstitialPresenter, InterstitialPresenter.Listener {
    @NonNull private static final String TAG = InterstitialPresenterDecorator.class.getSimpleName();
    @NonNull private final InterstitialPresenter mInterstitialPresenter;
    @NonNull private final AdTracker mAdTrackingDelegate;
    @NonNull private final InterstitialPresenter.Listener mListener;
    private boolean mIsDestroyed;

    public InterstitialPresenterDecorator(@NonNull InterstitialPresenter interstitialPresenter,
                                          @NonNull AdTracker adTrackingDelegate,
                                          @NonNull InterstitialPresenter.Listener listener) {
        mInterstitialPresenter = interstitialPresenter;
        mAdTrackingDelegate = adTrackingDelegate;
        mListener = listener;
    }

    @Override
    public void setListener(@Nullable InterstitialPresenter.Listener listener) {
        // We set the listener in the constructor instead
    }

    @NonNull
    @Override
    public PNAPIV3AdModel getAd() {
        return mInterstitialPresenter.getAd();
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "InterstitialPresenterDecorator is destroyed")) {
            return;
        }

        //Logger.d(TAG, "Loading interstitial presenter for ad unit id: " + getAd().getAdUnitId());
        mInterstitialPresenter.load();
    }

    @Override
    public void show() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "InterstitialPresenterDecorator is destroyed")) {
            return;
        }

        //Logger.d(TAG, "Showing interstitial presenter for ad unit id: " + getAd().getAdUnitId());
        mInterstitialPresenter.show();
    }

    @Override
    public void destroy() {
        //Logger.d(TAG, "Destroying interstitial presenter for ad unit id: " + getAd().getAdUnitId());
        mInterstitialPresenter.destroy();
        mIsDestroyed = true;
    }

    @Override
    public void onInterstitialLoaded(@NonNull InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        //ogger.d(TAG, "Interstitial loaded for ad unit id: " + getAd().getAdUnitId());
        mListener.onInterstitialLoaded(interstitialPresenter);
    }

    @Override
    public void onInterstitialShown(@NonNull InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        //Logger.d(TAG, "Interstitial shown for ad unit id: " + getAd().getAdUnitId());
        mAdTrackingDelegate.trackImpression();
        mListener.onInterstitialShown(interstitialPresenter);
    }

    @Override
    public void onInterstitialClicked(@NonNull InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        //Logger.d(TAG, "Interstitial clicked for ad unit id: " + getAd().getAdUnitId());
        mAdTrackingDelegate.trackClick();
        mListener.onInterstitialClicked(interstitialPresenter);
    }

    @Override
    public void onInterstitialDismissed(@NonNull InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        //Logger.d(TAG, "Interstitial dismissed for ad unit id: " + getAd().getAdUnitId());
        mListener.onInterstitialDismissed(interstitialPresenter);
    }

    @Override
    public void onInterstitialError(@NonNull InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        //String errorMessage = "Interstitial error for ad unit id: " + getAd().getAdUnitId();
        String errorMessage = "Interstitial error for zone id: ";
        Logger.d(TAG, errorMessage);
        mAdTrackingDelegate.trackError(errorMessage);
        mListener.onInterstitialError(interstitialPresenter);
    }
}
