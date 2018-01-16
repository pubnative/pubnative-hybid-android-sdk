package net.pubnative.tarantula.sdk.mrect.controller;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.api.MRectRequestManager;
import net.pubnative.tarantula.sdk.api.RequestManager;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.mrect.presenter.MRectPresenter;
import net.pubnative.tarantula.sdk.mrect.presenter.MRectPresenterFactory;
import net.pubnative.tarantula.sdk.mrect.view.MRectView;
import net.pubnative.tarantula.sdk.utils.CheckUtils;

/**
 * Created by erosgarciaponte on 12.01.18.
 */

public class MRectController implements RequestManager.RequestListener, MRectPresenter.Listener {
    private static final int REFRESH_TIME_SECONDS = 60;

    private final MRectPresenterFactory mMRectPresenterFactory;
    private final RequestManager mRequestManager;

    private MRectView mMRectAdView;
    private MRectPresenter mCurrentMRectPresenter;
    private MRectPresenter mNextMRectPresenter;
    private MRectView.Listener mListener;
    private boolean mIsDestroyed;

    public MRectController(Context context) {
        this(new MRectPresenterFactory(context), new MRectRequestManager());
    }

    MRectController(MRectPresenterFactory mRectPresenterFactory,
                    RequestManager requestManager) {
        mMRectPresenterFactory = mRectPresenterFactory;
        mRequestManager = requestManager;
        mRequestManager.setRequestListener(this);
    }

    public void setListener(MRectView.Listener listener) {
        mListener = listener;
    }

    public void load(String zoneId, MRectView mRectAdView) {
        if (!CheckUtils.NoThrow.checkArgument(Tarantula.isInitialized(), "Tarantula SDK has not been initialized. " +
                "Please call Tarantula#initialize in your application's onCreate method.")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkNotNull(zoneId, "zone id cannot be null")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkNotNull(mRectAdView, "mRectAdView cannot be null")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MRectController is destroyed")) {
            return;
        }

        mMRectAdView = mRectAdView;
        mRequestManager.setZoneId(zoneId);
        mRequestManager.requestAd();
    }

    void showAd(Ad ad) {
        if (mIsDestroyed) {
            return;
        }

        // TODO there is a low probability bug here if ads are requested rapidly that the mNextMRectPresenter
        // will continue to change before it can be loaded into the view. This means that there will be MRectPresenters
        // without a strong reference to them attempting to be loaded. It's possible for them to be garbage collected before
        // being displayed
        mNextMRectPresenter = mMRectPresenterFactory.createMRectPresenter(ad, this);
        if (mNextMRectPresenter == null) {
            if (mListener != null && mMRectAdView != null) {
                mListener.onMRectError(mMRectAdView);
            }
            return;
        }
        mNextMRectPresenter.load();
    }

    public void destroy() {
        mRequestManager.destroy();
        mMRectAdView = null;
        destroyMRectPresenter(mCurrentMRectPresenter);
        mCurrentMRectPresenter = null;
        destroyMRectPresenter(mNextMRectPresenter);
        mNextMRectPresenter = null;
        mListener = null;
        mIsDestroyed = true;
    }

    private void destroyMRectPresenter(MRectPresenter mRectPresenter) {
        if (mRectPresenter != null) {
            mRectPresenter.destroy();
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

        if (mListener != null && mMRectAdView != null) {
            mListener.onMRectError(mMRectAdView);
        }
    }

    // MRectPresenter.Listener
    @Override
    public void onMRectLoaded(MRectPresenter mRectPresenter, View mRect) {
        if (mIsDestroyed) {
            return;
        }

        destroyMRectPresenter(mCurrentMRectPresenter);
        mCurrentMRectPresenter = mNextMRectPresenter;
        mNextMRectPresenter = null;

        if (mMRectAdView != null) {
            mMRectAdView.removeAllViews();
            mRect.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            mMRectAdView.addView(mRect);

            if (mListener != null) {
                mListener.onMRectLoaded(mMRectAdView);
            }
        }
    }

    @Override
    public void onMRectClicked(MRectPresenter mRectPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null && mMRectAdView != null) {
            mListener.onMRectClicked(mMRectAdView);
        }
    }

    @Override
    public void onMRectError(MRectPresenter mRectPresenter) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null && mMRectAdView != null) {
            mListener.onMRectError(mMRectAdView);
        }
    }
}
