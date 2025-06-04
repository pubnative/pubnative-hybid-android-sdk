// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
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

    private Boolean isCreatedByFeedbackForm = false;

    public MRAIDInterstitial(Context context, String baseUrl, String data, Boolean showTimerBeforeEndCard, Boolean isExpandEnabled, String[] supportedNativeFeatures, MRAIDViewListener viewListener, MRAIDNativeFeatureListener nativeFeatureListener, ViewGroup contentInfo) {
        super(context, baseUrl, data, showTimerBeforeEndCard, supportedNativeFeatures, viewListener, nativeFeatureListener, contentInfo, true, isExpandEnabled);
        webView.setBackgroundColor(Color.BLACK);
        addView(webView);
    }

    public void hide() {
        close();
    }

    @Deprecated
    @Override
    protected void expand(String url, Boolean isCreatedByFeedbackForm, OnExpandCreativeFailListener listener) {
        // only expand interstitials from loading state
        if (state != STATE_LOADING) {
            return;
        }

        super.expand(url, isCreatedByFeedbackForm, listener);
    }

    @Override
    protected void expandHelper(WebView webView) {
        super.expandHelper(webView);
        isLaidOut = true;
        state = STATE_DEFAULT;
        this.fireStateChangeEvent();
    }

    @Override
    public void closeFromExpanded() {
        if (state == STATE_DEFAULT) {
            state = STATE_HIDDEN;
            clearView();
            handler.post(() -> {
                fireStateChangeEvent();
                if (listener != null) {
                    listener.mraidViewClose(MRAIDInterstitial.this);
                }
            });
        }

        super.closeFromExpanded();
    }

    public void show(Activity activity, OnExpandCreativeFailListener listener) {
        this.showAsInterstitial(activity, isCreatedByFeedbackForm, listener);
    }

    public void show(Activity activity, OnExpandCreativeFailListener listener, String url) {
        this.showAsInterstitial(activity, isCreatedByFeedbackForm, listener, url);
    }

    public void showDefaultContentInfoURL(String url) {
        expandContentInfo(url);
    }

    public void markCreativeAdComingFromFeedbackForm() {
        this.isCreatedByFeedbackForm = true;
    }
}
