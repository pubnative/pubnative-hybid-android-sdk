package net.pubnative.lite.sdk.mraid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by erosgarciaponte on 08.01.18.
 */
@SuppressLint("ViewConstructor")
public class MRAIDBanner extends MRAIDView {
    private static final String TAG = MRAIDBanner.class.getSimpleName();

    public MRAIDBanner(
            Context context,
            String baseUrl,
            String data,
            String[] supportedNativeFeatures,
            MRAIDViewListener viewListener,
            MRAIDNativeFeatureListener nativeFeatureListener,
            ViewGroup contentInfo
    ) {
        super(context, baseUrl, data, supportedNativeFeatures, viewListener, nativeFeatureListener, contentInfo, false);
        webView.setBackgroundColor(Color.TRANSPARENT);
        addView(webView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public boolean onBackPressed() {
        return state != STATE_DEFAULT && super.onBackPressed();
    }

    @Override
    protected void close() {
        if (state == STATE_LOADING || state == STATE_DEFAULT || state == STATE_HIDDEN) {
            return;
        }
        super.close();
    }

    @Override
    protected void expand(String url) {
        // The only time it is valid to call expand on a banner ad is
        // when the ad is currently in either default or resized state.
        if (state != STATE_DEFAULT && state != STATE_RESIZED) {
            return;
        }

        super.expand(url);
    }

    @Override
    protected void expandHelper(WebView webView) {
        state = STATE_EXPANDED;
        super.expandHelper(webView);
        this.fireStateChangeEvent();
    }

    @Override
    protected void onLayoutCompleted() {
        if (state == STATE_LOADING && isPageFinished) {
            state = STATE_DEFAULT;
            fireStateChangeEvent();
            fireReadyEvent();
            if (isViewable) {
                fireViewableChangeEvent();
            }
        }
    }
}
