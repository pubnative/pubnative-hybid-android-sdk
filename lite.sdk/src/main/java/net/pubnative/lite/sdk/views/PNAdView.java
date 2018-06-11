package net.pubnative.lite.sdk.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.Logger;

public abstract class PNAdView extends RelativeLayout implements RequestManager.RequestListener {

    public interface Listener {
        void onAdLoaded();
        void onAdLoadFailed(Throwable error);
        void onAdImpression();
        void onAdClick();
    }

    private RequestManager mRequestManager;
    protected Listener mListener;
    protected Ad mAd;

    public PNAdView(Context context) {
        super(context);
        init(getRequestManager());
    }

    public PNAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(getRequestManager());
    }

    public PNAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(getRequestManager());
    }

    @TargetApi(21)
    public PNAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(getRequestManager());
    }

    private void init(RequestManager requestManager) {
        mRequestManager = requestManager;
    }

    public void load(String zoneId, Listener listener) {
        cleanup();
        mListener = listener;
        if (TextUtils.isEmpty(zoneId)) {
            invokeOnLoadFailed(new Exception("Invalid zone id provided"));
        } else {
            mRequestManager.setZoneId(zoneId);
            mRequestManager.setRequestListener(this);
            mRequestManager.requestAd();
        }
    }

    public void destroy() {
        cleanup();
        if (mRequestManager != null) {
            mRequestManager.destroy();
            mRequestManager = null;
        }
    }

    protected void cleanup() {
        stopTracking();
        setBackgroundColor(Color.TRANSPARENT);
        removeAllViews();
        mAd = null;
    }

    protected abstract String getLogTag();

    abstract RequestManager getRequestManager();

    protected abstract void renderAd();

    protected abstract void startTracking();

    protected abstract void stopTracking();

    @Override
    public void onRequestSuccess(Ad ad) {
        if (ad == null) {
            invokeOnLoadFailed(new Exception("Server returned null ad"));
        } else {
            mAd = ad;
            renderAd();
        }
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        invokeOnLoadFailed(new Exception(throwable));
    }

    protected void invokeOnLoadFinished() {
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    protected void invokeOnLoadFailed(Exception exception) {
        Logger.e(getLogTag(), exception.getMessage());
        if (mListener != null) {
            mListener.onAdLoadFailed(exception);
        }
    }

    protected void invokeOnClick() {
        if (mListener != null) {
            mListener.onAdClick();
        }
    }

    protected void invokeOnImpression() {
        if (mListener != null) {
            mListener.onAdImpression();
        }
    }

    protected void setupAdView(View view) {
        LayoutParams adLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        adLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        addView(view, adLayoutParams);

        setBackgroundColor(Color.BLACK);

        View contentInfo = mAd.getContentInfoContainer(getContext());
        if (contentInfo != null) {
            addView(contentInfo);
        }

        invokeOnLoadFinished();
        startTracking();
        invokeOnImpression();
    }
}
