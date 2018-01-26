package net.pubnative.lite.sdk.interstitial;

import android.app.Activity;

import net.pubnative.lite.sdk.api.InterstitialRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenterFactory;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.PNInitializationHelper;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class Interstitial implements RequestManager.RequestListener, InterstitialPresenter.Listener {
    public interface Listener {
        void onInterstitialLoaded(Interstitial interstitial);
        void onInterstitialShown(Interstitial interstitial);
        void onInterstitialClicked(Interstitial interstitial);
        void onInterstitialDismissed(Interstitial interstitial);
        void onInterstitialError(Interstitial interstitial);
    }

    private final InterstitialPresenterFactory mInterstitialPresenterFactory;
    private final RequestManager mRequestManager;
    private InterstitialPresenter mInterstitialPresenter;
    private PNInitializationHelper mInitializationHelper;
    private Listener mListener;
    private boolean mIsDestroyed;

    public Interstitial(Activity activity) {
        this(new InterstitialPresenterFactory(activity), new InterstitialRequestManager(), new PNInitializationHelper());
    }

    Interstitial(InterstitialPresenterFactory interstitialPresenterFactory,
                 RequestManager requestManager, PNInitializationHelper initializationHelper) {
        mInterstitialPresenterFactory = interstitialPresenterFactory;
        mRequestManager = requestManager;
        mRequestManager.setRequestListener(this);
        mInitializationHelper = initializationHelper;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void load(String zoneId) {
        if (!CheckUtils.NoThrow.checkArgument(mInitializationHelper.isInitialized(), "PNLite SDK has not been initialized. " +
                "Please call PNLite#initialize in your application's onCreate method.")) {
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

    void loadInterstitial(Ad ad) {
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
    public void onRequestSuccess(Ad ad) {
        if (mIsDestroyed) {
            return;
        }

        loadInterstitial(ad);
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onInterstitialError(this);
        }
    }

    // Interstitial.Listener
    @Override
    public void onInterstitialLoaded(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onInterstitialLoaded(this);
        }
    }

    @Override
    public void onInterstitialShown(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onInterstitialShown(this);
        }
    }

    @Override
    public void onInterstitialClicked(InterstitialPresenter interstitialPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onInterstitialClicked(this);
        }
    }

    @Override
    public void onInterstitialDismissed(InterstitialPresenter interstitialPresenter) {
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
    public void onInterstitialError(InterstitialPresenter interstitialPresenter) {
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
