package net.pubnative.lite.sdk.views;

import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class WebViewClickDetector implements View.OnTouchListener {
    private final WebView mWebView;

    public WebViewClickDetector(WebView webView) {
        mWebView = webView;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP: {
                mWebView.performClick();
                break;
            }
        }
        return false;
    }
}
