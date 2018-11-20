package net.pubnative.lite.sdk.views;

import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.ViewGestureDetector;

public class HyBidHtmlWebView extends HyBidWebView implements ViewGestureDetector.UserClickListener {
    private static final String TAG = HyBidHtmlWebView.class.getSimpleName();

    private final ViewGestureDetector mViewGestureDetector;
    private boolean mClicked;

    public HyBidHtmlWebView(Context context) {
        super(context);

        disableScrollingAndZoom();
        getSettings().setJavaScriptEnabled(true);

        mViewGestureDetector = new ViewGestureDetector(context, this, new GestureDetector.SimpleOnGestureListener());
        mViewGestureDetector.setUserClickListener(this);

        enablePlugins(true);
        setBackgroundColor(Color.TRANSPARENT);
    }

    public void init() {
        initializeOnTouchListener();
    }

    @Override
    public void loadUrl(final String url) {
        if (url == null) {
            return;
        }

        if (url.startsWith("javascript:")) {
            super.loadUrl(url);
            return;
        }

        Logger.d(TAG, "Loading url: " + url);
    }

    @Override
    public void stopLoading() {
        if (mIsDestroyed) {
            Logger.w(TAG, "#stopLoading() called after destroy()");
            return;
        }

        final WebSettings webSettings = getSettings();
        if (webSettings == null) {
            Logger.w(TAG, "#getSettings() returned null");
            return;
        }

        webSettings.setJavaScriptEnabled(false);
        super.stopLoading();
        webSettings.setJavaScriptEnabled(true);
    }

    private void disableScrollingAndZoom() {
        setHorizontalScrollBarEnabled(false);
        setHorizontalScrollbarOverlay(false);
        setVerticalScrollBarEnabled(false);
        setVerticalScrollbarOverlay(false);
        getSettings().setSupportZoom(false);
    }

    void loadHtmlResponse(String htmlResponse) {
        /*loadDataWithBaseURL(Networking.getBaseUrlScheme() + "://" + Constants.HOST + "/", htmlResponse,
                "text/html", "utf-8", null);*/
    }

    void initializeOnTouchListener() {
        setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mViewGestureDetector.sendTouchEvent(event);

                // We're not handling events if the current action is ACTION_MOVE
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });
    }

    @Override
    public void onUserClick() {
        mClicked = true;
    }

    @Override
    public void onResetUserClick() {
        mClicked = false;
    }

    @Override
    public boolean wasClicked() {
        return mClicked;
    }
}
