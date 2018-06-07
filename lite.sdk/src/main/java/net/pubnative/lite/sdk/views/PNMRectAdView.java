package net.pubnative.lite.sdk.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import net.pubnative.lite.sdk.api.MRectRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.mrect.presenter.MRectPresenter;
import net.pubnative.lite.sdk.mrect.presenter.MRectPresenterFactory;

public class PNMRectAdView extends PNAdView implements MRectPresenter.Listener {

    private MRectPresenter mPresenter;

    public PNMRectAdView(Context context) {
        super(context);
    }

    public PNMRectAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PNMRectAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public PNMRectAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        return PNMRectAdView.class.getSimpleName();
    }

    @Override
    protected RequestManager getRequestManager() {
        return new MRectRequestManager();
    }

    @Override
    protected void renderAd() {
        mPresenter = new MRectPresenterFactory(getContext())
                .createMRectPresenter(mAd, this);
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

    //------------------------------ MRectPresenter Callbacks --------------------------------------
    @Override
    public void onMRectLoaded(MRectPresenter mRectPresenter, View mRect) {
        if (mRect == null) {
            invokeOnLoadFailed(new Exception("An error has occurred while rendering the ad"));
        } else {
            setupAdView(mRect);
        }
    }

    @Override
    public void onMRectError(MRectPresenter mRectPresenter) {
        invokeOnLoadFailed(new Exception("An error has occurred while rendering the ad"));
    }

    @Override
    public void onMRectClicked(MRectPresenter mRectPresenter) {
        invokeOnClick();
    }
}
