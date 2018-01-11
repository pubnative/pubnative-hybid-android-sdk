package net.pubnative.tarantula.sdk.interstitial;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.api.InterstitialRequestManager;
import net.pubnative.tarantula.sdk.api.RequestManager;
import net.pubnative.tarantula.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.tarantula.sdk.interstitial.presenter.InterstitialPresenterFactory;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.models.api.PNAPIV3AdModel;
import net.pubnative.tarantula.sdk.utils.CheckUtils;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class Interstitial implements RequestManager.RequestListener, InterstitialPresenter.Listener {
    public interface Listener {
        void onInterstitialLoaded(@NonNull Interstitial interstitial);
        void onInterstitialShown(@NonNull Interstitial interstitial);
        void onInterstitialClicked(@NonNull Interstitial interstitial);
        void onInterstitialDismissed(@NonNull Interstitial interstitial);
        void onInterstitialError(@NonNull Interstitial interstitial);
    }

    @NonNull private final InterstitialPresenterFactory mInterstitialPresenterFactory;
    @NonNull private final RequestManager mRequestManager;
    @Nullable private InterstitialPresenter mInterstitialPresenter;
    @Nullable private Listener mListener;
    private boolean mIsDestroyed;

    public Interstitial(@NonNull Activity activity) {
        this(new InterstitialPresenterFactory(activity), new InterstitialRequestManager());
    }

    @VisibleForTesting
    Interstitial(@NonNull InterstitialPresenterFactory interstitialPresenterFactory,
                 @NonNull RequestManager requestManager) {
        mInterstitialPresenterFactory = interstitialPresenterFactory;
        mRequestManager = requestManager;
        mRequestManager.setRequestListener(this);
    }

    public void setListener(@Nullable Listener listener) {
        mListener = listener;
    }

    public void load(@NonNull String zoneId) {
        if (!CheckUtils.NoThrow.checkArgument(Tarantula.isInitialized(), "Tarantula SDK has not been initialized. " +
                "Please call Tarantula#initialize in your application's onCreate method.")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkNotNull(zoneId, "zone id cannot be null")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "Interstitial has been destroyed")) {
            return;
        }

        mRequestManager.setZoneId(zoneId);
        mRequestManager.requestAd();
    }

    @VisibleForTesting
    void loadInterstitial(@NonNull PNAPIV3AdModel ad) {
        if (mIsDestroyed) {
            return;
        }

        mInterstitialPresenter = mInterstitialPresenterFactory.createInterstitialPresenter(ad, this);
        if (mInterstitialPresenter == null) {
            if (mListener != null) {
                mListener.onInterstitialError(this);
            }
            return;
        }

        mInterstitialPresenter.load();
    }

    public void show() {
        if (mInterstitialPresenter == null) {
            return;
        }

        mInterstitialPresenter.show();
    }

    public void destroy() {
        mRequestManager.destroy();
        if (mInterstitialPresenter != null) {
            mInterstitialPresenter.destroy();
        }
        mInterstitialPresenter = null;
        mListener = null;
        mIsDestroyed = true;
    }

    // RequestManager.RequestListener
    @Override
    public void onRequestSuccess(@NonNull PNAPIV3AdModel ad) {
        if (mIsDestroyed) {
            return;
        }

        loadInterstitial(ad);
    }

    @Override
    public void onRequestFail(@NonNull Throwable throwable) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onInterstitialError(this);
        }
    }

    // Interstitial.Listener
    @Override
    public void onInterstitialLoaded(@NonNull InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onInterstitialLoaded(this);
        }
    }

    @Override
    public void onInterstitialShown(@NonNull InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onInterstitialShown(this);
        }
    }

    @Override
    public void onInterstitialClicked(@NonNull InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onInterstitialClicked(this);
        }
    }

    @Override
    public void onInterstitialDismissed(@NonNull InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mInterstitialPresenter != null) {
            mInterstitialPresenter.destroy();
            mInterstitialPresenter = null;
        }

        if (mListener != null) {
            mListener.onInterstitialDismissed(this);
        }
    }

    @Override
    public void onInterstitialError(@NonNull InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mInterstitialPresenter != null) {
            mInterstitialPresenter.destroy();
            mInterstitialPresenter = null;
        }

        if (mListener != null) {
            mListener.onInterstitialError(this);
        }
    }
}
