package net.pubnative.lite.sdk.mraid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by erosgarciaponte on 08.01.18.
 */
@SuppressLint("ViewConstructor")
public class MRAIDInterstitial extends MRAIDView {
    private final static String TAG = "MRAIDInterstitial";

    public MRAIDInterstitial(
            Context context,
            String baseUrl,
            String data,
            String[] supportedNativeFeatures,
            MRAIDViewListener viewListener,
            MRAIDNativeFeatureListener nativeFeatureListener,
            ViewGroup contentInfo
    ) {
        super(context, baseUrl, data, supportedNativeFeatures, viewListener, nativeFeatureListener, contentInfo, true);
        webView.setBackgroundColor(Color.BLACK);
        addView(webView);
    }

    public void hide() {
        close();
    }

    @Override
    protected void close() {
        super.close();
    }

    @Override
    protected void expand(String url) {
        // only expand interstitials from loading state
        if (state != STATE_LOADING) {
            return;
        }

        super.expand(url);
    }

    @Override
    protected void expandHelper(WebView webView) {
        super.expandHelper(webView);
        isLaidOut = true;
        state = STATE_DEFAULT;
        this.fireStateChangeEvent();
    }

    @Override
    protected void closeFromExpanded() {
        if (state == STATE_DEFAULT) {
            state = STATE_HIDDEN;
            clearView();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    fireStateChangeEvent();
                    if (listener != null) {
                        listener.mraidViewClose(MRAIDInterstitial.this);
                    }
                }
            });
        }

        super.closeFromExpanded();
    }

    public void show(Activity activity) {
        this.showAsInterstitial(activity);
    }
}
