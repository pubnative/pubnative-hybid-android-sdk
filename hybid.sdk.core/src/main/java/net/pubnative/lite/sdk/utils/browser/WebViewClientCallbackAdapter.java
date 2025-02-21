package net.pubnative.lite.sdk.utils.browser;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

public class WebViewClientCallbackAdapter implements BaseWebViewClient.WebViewClientCallback {

    @Override
    public boolean shouldOverrideUrlLoading(String url) {
        return false;
    }

    @Override
    public void onPageStartedLoading(String url) {
    }

    @Override
    public void onPageFinishedLoading(String url) {
    }

    @Override
    public void onHttpError(WebResourceRequest request, WebResourceResponse errorResponse) {
    }

    @Override
    public void onGeneralError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onRenderProcessGone() {
    }
}
