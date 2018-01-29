package net.pubnative.lite.sdk.banner.controller;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.pubnative.lite.sdk.api.BannerRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.banner.presenter.BannerPresenter;
import net.pubnative.lite.sdk.banner.presenter.BannerPresenterFactory;
import net.pubnative.lite.sdk.banner.view.BannerView;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.PNInitializationHelper;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class BannerController implements RequestManager.RequestListener, BannerPresenter.Listener {
    private final BannerPresenterFactory mBannerPresenterFactory;
    private final RequestManager mRequestManager;
    private final PNInitializationHelper mInitializationHelper;

    private BannerView mBannerAdView;
    private BannerPresenter mCurrentBannerPresenter;
    private BannerPresenter mNextBannerPresenter;
    private BannerView.Listener mListener;
    private boolean mIsDestroyed;

    public BannerController(Context context) {
        this(new BannerPresenterFactory(context), new BannerRequestManager(), new PNInitializationHelper());
    }

    BannerController(BannerPresenterFactory bannerPresenterFactory,
                     RequestManager requestManager, PNInitializationHelper initializationHelper) {
        mBannerPresenterFactory = bannerPresenterFactory;
        mRequestManager = requestManager;
        mRequestManager.setRequestListener(this);
        mInitializationHelper = initializationHelper;
    }

    public void setListener(BannerView.Listener listener) {
        mListener = listener;
    }

    public void load(String zoneId, BannerView bannerAdView) {
        if (!CheckUtils.NoThrow.checkArgument(mInitializationHelper.isInitialized(), "PNLite SDK has not been initialized. " +
                "Please call PNLite#initialize in your application's onCreate method.")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkNotNull(zoneId, "zone id cannot be null")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkNotNull(bannerAdView, "bannerAdView cannot be null")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "BannerController is destroyed")) {
            return;
        }

        mBannerAdView = bannerAdView;
        mRequestManager.setZoneId(zoneId);
        mRequestManager.requestAd();
    }

    void showAd(Ad ad) {
        if (mIsDestroyed) {
            return;
        }

        // TODO there is a low probability bug here if ads are requested rapidly that the mNextBannerPresenter
        // will continue to change before it can be loaded into the view. This means that there will be BannerPresenters
        // without a strong reference to them attempting to be loaded. It's possible for them to be garbage collected before
        // being displayed
        mNextBannerPresenter = mBannerPresenterFactory.createBannerPresenter(ad, this);
        if (mNextBannerPresenter == null) {
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

    private void destroyBannerPresenter(BannerPresenter bannerPresenter) {
        if (bannerPresenter != null) {
            bannerPresenter.destroy();
        }
    }

    // RequestManager.RequestListener
    @Override
    public void onRequestSuccess(Ad ad) {
        if (mIsDestroyed) {
            return;
        }

        showAd(ad);
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null && mBannerAdView != null) {
            mListener.onBannerError(mBannerAdView);
        }
    }

    // BannerPresenter.Listener
    @Override
    public void onBannerLoaded(BannerPresenter bannerPresenter, View banner) {
        if (mIsDestroyed) {
            return;
        }

        destroyBannerPresenter(mCurrentBannerPresenter);
        mCurrentBannerPresenter = mNextBannerPresenter;
        mNextBannerPresenter = null;

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
    public void onBannerClicked(BannerPresenter bannerPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null && mBannerAdView != null) {
            mListener.onBannerClicked(mBannerAdView);
        }
    }

    @Override
    public void onBannerError(BannerPresenter bannerPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null && mBannerAdView != null) {
            mListener.onBannerError(mBannerAdView);
        }
    }
}