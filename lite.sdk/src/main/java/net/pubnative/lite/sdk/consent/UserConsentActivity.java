package net.pubnative.lite.sdk.consent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.utils.Logger;

public class UserConsentActivity extends Activity {
    private static final String TAG = UserConsentActivity.class.getSimpleName();

    private static final String REDIRECT_ACCEPT = "https://pubnative.net/personalize-experience-yes/";
    private static final String REDIRECT_REJECT = "https://pubnative.net/personalize-experience-no/";
    private static final String REDIRECT_CLOSE = "https://pubnative.net/";

    public static final int RESULT_CONSENT_ACCEPTED = 200;
    public static final int RESULT_CONSENT_REJECTED = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);
        setupWebView(webView);

        loadConsentPage(webView);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView(WebView webView) {
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(this.getCacheDir().getAbsolutePath());
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);

        settings.setLoadWithOverviewMode(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(false);
        }

        webView.setWebViewClient(webViewClient);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        setContentView(webView, layoutParams);
    }

    private final WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            showLoadProgress();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            hideLoadProgress();
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            switch (url) {
                case REDIRECT_ACCEPT:
                    PNLite.getUserDataManager().grantConsent();
                    setResult(RESULT_CONSENT_ACCEPTED);
                    return false;
                case REDIRECT_REJECT:
                    PNLite.getUserDataManager().denyConsent();
                    setResult(RESULT_CONSENT_REJECTED);
                    return false;
                case REDIRECT_CLOSE:
                    finish();
                    return false;
                default:
                    return super.shouldOverrideUrlLoading(view, url);
            }
        }
    };

    private void loadConsentPage(WebView webView) {
        String url = PNLite.getUserDataManager().getConsentPageLink();

        if (TextUtils.isEmpty(url)) {
            Logger.e(TAG, "Invalid consent page URL");
        } else {
            webView.loadUrl(url);
        }
    }

    private void showLoadProgress() {

    }

    private void hideLoadProgress() {

    }

    @Override
    public void onBackPressed() {
        // User should use decide if he accepts or rejects and then exit through the close button.
    }
}
