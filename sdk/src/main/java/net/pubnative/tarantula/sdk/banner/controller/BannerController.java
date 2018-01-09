package net.pubnative.tarantula.sdk.banner.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.api.RequestManager;
import net.pubnative.tarantula.sdk.banner.presenter.BannerPresenter;
import net.pubnative.tarantula.sdk.banner.presenter.BannerPresenterFactory;
import net.pubnative.tarantula.sdk.banner.view.BannerView;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.utils.CheckUtils;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class BannerController implements RequestManager.RequestListener, BannerPresenter.Listener {
    @NonNull private final BannerPresenterFactory mBannerPresenterFactory;
    @NonNull private final RequestManager mRequestManager;

    @Nullable private BannerView mBannerAdView;
    @Nullable private BannerPresenter mCurrentBannerPresenter;
    @Nullable private BannerPresenter mNextBannerPresenter;
    @Nullable private BannerView.Listener mListener;
    private boolean mIsDestroyed;

    public BannerController(@NonNull Context context) {
        this(new BannerPresenterFactory(context), new RequestManager());
    }

    @VisibleForTesting
    BannerController(@NonNull BannerPresenterFactory bannerPresenterFactory,
                     @NonNull RequestManager requestManager) {
        mBannerPresenterFactory = bannerPresenterFactory;
        mRequestManager = requestManager;
        mRequestManager.setRequestListener(this);
    }

    public void setListener(@Nullable BannerView.Listener listener) {
        mListener = listener;
    }

    public void load(@NonNull String adUnitId, @NonNull BannerView bannerAdView) {
        if (!CheckUtils.NoThrow.checkArgument(Tarantula.isInitialized(), "MaxAds SDK has not been initialized. " +
                "Please call MaxAds#initialize in your application's onCreate method.")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkNotNull(adUnitId, "adUnitId cannot be null")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkNotNull(bannerAdView, "bannerAdView cannot be null")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "BannerController is destroyed")) {
            return;
        }

        mBannerAdView = bannerAdView;
        mRequestManager.setAdUnitId(adUnitId);
        mRequestManager.requestAd();
        mRequestManager.stopRefreshTimer();
    }

    @VisibleForTesting
    void showAd(@NonNull Ad ad) {
        if (mIsDestroyed) {
            return;
        }

        // TODO (steffan): there is a low probability bug here if ads are requested rapidly that the mNextBannerPresenter
        // will continue to change before it can be loaded into the view. This means that there will be BannerPresenters
        // without a strong reference to them attempting to be loaded. It's possible for them to be garbage collected before
        // being displayed
        mNextBannerPresenter = mBannerPresenterFactory.createBannerPresenter(ad, this);
        if (mNextBannerPresenter == null) {
            mRequestManager.startRefreshTimer(ad.getRefreshTimeSeconds());

            if (mListener != null && mBannerAdView != null) {
                mListener.onBannerError(mBannerAdView);
            }
            return;
        }
        mNextBannerPresenter.load();
    }

    public void destroy() {
        mRequestManager.destroy();
        mBannerAdView = null;
        destroyBannerPresenter(mCurrentBannerPresenter);
        mCurrentBannerPresenter = null;
        destroyBannerPresenter(mNextBannerPresenter);
        mNextBannerPresenter = null;
        mListener = null;
        mIsDestroyed = true;
    }

    private void destroyBannerPresenter(@Nullable BannerPresenter bannerPresenter) {
        if (bannerPresenter != null) {
            bannerPresenter.destroy();
        }
    }

    // RequestManager.RequestListener
    @Override
    public void onRequestSuccess(@NonNull Ad ad) {
        if (mIsDestroyed) {
            return;
        }

        showAd(ad);
    }

    @Override
    public void onRequestFail(@NonNull Throwable throwable) {
        if (mIsDestroyed) {
            return;
        }

        mRequestManager.startRefreshTimer(RequestManager.DEFAULT_REFRESH_TIME_SECONDS);

        if (mListener != null && mBannerAdView != null) {
            mListener.onBannerError(mBannerAdView);
        }
    }

    // BannerPresenter.Listener
    @Override
    public void onBannerLoaded(@NonNull BannerPresenter bannerPresenter, @NonNull View banner) {
        if (mIsDestroyed) {
            return;
        }

        destroyBannerPresenter(mCurrentBannerPresenter);
        mCurrentBannerPresenter = mNextBannerPresenter;
        mNextBannerPresenter = null;

        mRequestManager.startRefreshTimer(bannerPresenter.getAd().getRefreshTimeSeconds());

        if (mBannerAdView != null) {
            mBannerAdView.removeAllViews();
            banner.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            mBannerAdView.addView(banner);

            if (mListener != null) {
                mListener.onBannerLoaded(mBannerAdView);
            }
        }
    }

    @Override
    public void onBannerClicked(@NonNull BannerPresenter bannerPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null && mBannerAdView != null) {
            mListener.onBannerClicked(mBannerAdView);
        }
    }

    @Override
    public void onBannerError(@NonNull BannerPresenter bannerPresenter) {
        if (mIsDestroyed) {
            return;
        }

        mRequestManager.startRefreshTimer(bannerPresenter.getAd().getRefreshTimeSeconds());

        if (mListener != null && mBannerAdView != null) {
            mListener.onBannerError(mBannerAdView);
        }
    }
}
