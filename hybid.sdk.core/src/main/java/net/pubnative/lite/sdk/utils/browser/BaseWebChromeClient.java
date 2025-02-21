package net.pubnative.lite.sdk.utils.browser;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class BaseWebChromeClient extends WebChromeClient {


    private WebChromeClientCallback webChromeClientCallback;


    public void setWebChromeClientCallback(WebChromeClientCallback webChromeClientCallback) {
        this.webChromeClientCallback = webChromeClientCallback;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (webChromeClientCallback != null) {
            webChromeClientCallback.onProgressChanged(newProgress);
        }
    }

    public interface WebChromeClientCallback {
        void onProgressChanged(int newProgress);
    }

}
