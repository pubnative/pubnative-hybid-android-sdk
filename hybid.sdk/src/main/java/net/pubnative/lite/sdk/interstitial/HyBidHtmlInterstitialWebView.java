package net.pubnative.lite.sdk.interstitial;

import android.content.Context;
import android.os.Handler;

import net.pubnative.lite.sdk.views.HyBidHtmlWebView;
import net.pubnative.lite.sdk.views.HyBidHtmlWebViewListener;

public class HyBidHtmlInterstitialWebView extends HyBidHtmlWebView {
    private Handler mHandler;

    public HyBidHtmlInterstitialWebView(Context context) {
        super(context);

        mHandler = new Handler();
    }

    public void init() {
        super.init();

        HyBidHtmlInterstitialWebViewListener htmlInterstitialWebViewListener = new HyBidHtmlInterstitialWebViewListener();
        /*HtmlWebViewClient htmlWebViewClient = new HtmlWebViewClient(htmlInterstitialWebViewListener, this, clickthroughUrl, dspCreativeId);
        setWebViewClient(htmlWebViewClient);*/
    }

    private void postHandlerRunnable(Runnable r) {
        mHandler.post(r);
    }

    static class HyBidHtmlInterstitialWebViewListener implements HyBidHtmlWebViewListener {
        //private final CustomEventInterstitialListener mCustomEventInterstitialListener;

        public HyBidHtmlInterstitialWebViewListener() {
            //mCustomEventInterstitialListener = customEventInterstitialListener;
        }

        @Override
        public void onLoaded(HyBidHtmlWebView htmlWebView) {
            //mCustomEventInterstitialListener.onInterstitialLoaded();
        }

        @Override
        public void onFailed(Throwable error) {
            //mCustomEventInterstitialListener.onInterstitialFailed(errorCode);
        }

        @Override
        public void onClicked() {
            //mCustomEventInterstitialListener.onInterstitialClicked();
        }

        @Override
        public void onCollapsed() {
            // Ignored
        }
    }
}
