// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.browser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class BaseWebView extends WebView {

    private static final String DEFAULT_MIME_TYPE = "text/html";
    private static final String DEFAULT_ENCODING = "UTF-8";

    public BaseWebView(Context context) {
        super(context.getApplicationContext());
        init();
    }

    public BaseWebView(Context context, AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);
        init();
    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context.getApplicationContext(), attrs, defStyleAttr);
        init();
    }

    private void init() {
        initWebViewSettings();
    }

    public void loadHtml(String html) {
        if (html != null) {
            loadDataWithBaseURL(null, html, DEFAULT_MIME_TYPE, DEFAULT_ENCODING, null);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSettings() {
        getSettings().setJavaScriptEnabled(true);

        disableContentAccess();
    }

    private void disableContentAccess() {
        getSettings().setAllowContentAccess(false);
        getSettings().setAllowFileAccess(false);
        getSettings().setAllowUniversalAccessFromFileURLs(false);
    }

}

