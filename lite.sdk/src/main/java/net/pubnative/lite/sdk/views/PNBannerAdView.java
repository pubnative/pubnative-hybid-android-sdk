package net.pubnative.lite.sdk.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import net.pubnative.lite.sdk.api.BannerRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.banner.presenter.BannerPresenter;
import net.pubnative.lite.sdk.banner.presenter.BannerPresenterFactory;

public class PNBannerAdView extends PNAdView implements BannerPresenter.Listener {

    private BannerPresenter mPresenter;

    public PNBannerAdView(Context context) {
        super(context);
    }

    public PNBannerAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PNBannerAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public PNBannerAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void cleanup() {
        super.cleanup();
        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
        }
    }

    @Override
    protected String getLogTag() {
        return PNBannerAdView.class.getSimpleName();
    }

    @Override
    protected RequestManager getRequestManager() {
        return new BannerRequestManager();
    }

    @Override
    protected void renderAd() {
        mPresenter = new BannerPresenterFactory(getContext())
                .createBannerPresenter(mAd, this);
        mPresenter.load();
    }

    @Override
    protected void startTracking() {
        if (mPresenter != null) {
            mPresenter.startTracking();
        }
    }

    @Override
    protected void stopTracking() {
        if (mPresenter != null) {
            mPresenter.stopTracking();
        }
    }

    //----------------------------- BannerPresenter Callbacks --------------------------------------
    @Override
    public void onBannerLoaded(BannerPresenter bannerPresenter, View banner) {
        if (banner == null) {
            invokeOnLoadFailed(new Exception("An error has occurred while rendering the ad"));
        } else {
            setupAdView(banner);
        }
    }

    @Override
    public void onBannerError(BannerPresenter bannerPresenter) {
        invokeOnLoadFailed(new Exception("An error has occurred while rendering the ad"));
    }

    @Override
    public void onBannerClicked(BannerPresenter bannerPresenter) {
        invokeOnClick();
    }
}
