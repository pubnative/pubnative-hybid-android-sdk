package net.pubnative.lite.sdk.utils.browser;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import net.pubnative.lite.sdk.utils.Logger;


/*package*/ class BrowserModel {
    private static final String TAG = BrowserModel.class.getSimpleName();

    private final BaseWebViewClient webViewClient;
    private final BaseWebChromeClient webChromeClient;
    private final BrowserCookieManager cookieManager;

    private WebView webView;

    private Callback browserModelCallback;

    private final BaseWebChromeClient.WebChromeClientCallback webChromeClientCallback = new BaseWebChromeClient.WebChromeClientCallback() {
        @Override
        public void onProgressChanged(int newProgress) {
            if (browserModelCallback != null) {
                browserModelCallback.onProgressChanged(newProgress);

                if (webView != null) {
                    browserModelCallback.onPageNavigationStackChanged(
                            webView.canGoBack(),
                            webView.canGoForward()
                    );
                }
            }
        }
    };

    private String lastKnownUrl;

    private final BaseWebViewClient.WebViewClientCallback webViewClientCallback = new WebViewClientCallbackAdapter() {

        @Override
        public boolean shouldOverrideUrlLoading(String url) {
            if (browserModelCallback != null) {
                return browserModelCallback.shouldOverrideUrlLoading(url);
            }

            return false;
        }

        @Override
        public void onPageStartedLoading(String url) {
            lastKnownUrl = url;
            if (browserModelCallback != null) {
                browserModelCallback.onUrlLoadingStarted(url);
            }
        }

        @Override
        public void onHttpError(WebResourceRequest request, WebResourceResponse errorResponse) {
            Logger.e(TAG, "onHttpError: " + errorResponse.toString());
            if (browserModelCallback != null) {
                browserModelCallback.onHttpError(request, errorResponse);
            }
        }

        @Override
        public void onGeneralError(int errorCode, String description, String failingUrl) {
            Logger.e(TAG, "onGeneralError. Errorcode " + errorCode + " Description: " + description);
            if (browserModelCallback != null) {
                browserModelCallback.onGeneralError(errorCode, description, failingUrl);
            }
        }

        @Override
        public void onRenderProcessGone() {
            Logger.e(TAG, "WebView's render process has exited");
            if (browserModelCallback != null) {
                browserModelCallback.onRenderProcessGone();
            }
        }
    };

    /*package*/ BrowserModel(BaseWebViewClient webViewClient,
                             BaseWebChromeClient webChromeClient,
                             BrowserCookieManager cookieManager) {

        if (webViewClient != null) {
            this.webViewClient = webViewClient;
        } else
            throw new NullPointerException("BrowserModel: webViewClient can not be null");

        if (webChromeClient != null) {
            this.webChromeClient = webChromeClient;
        } else
            throw new NullPointerException("BrowserModel: webChromeClient can not be null");

        if (cookieManager != null) {
            this.cookieManager = cookieManager;
        } else
            throw new NullPointerException("BrowserModel: cookieManager can not be null");

        webViewClient.setWebViewClientCallback(webViewClientCallback);
        webChromeClient.setWebChromeClientCallback(webChromeClientCallback);
    }

    public void setWebView(WebView webView) {
        if (webView != null) {
            this.webView = webView;
        } else throw new NullPointerException("BrowserModel: Parameter webView cannot be null");

        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);

        cookieManager.setupCookiePolicy(webView);
    }

    public void setBrowserModelCallback(Callback browserModelCallback) {
        this.browserModelCallback = browserModelCallback;
    }

    public void load(String url) {
        if (url != null) {
            lastKnownUrl = url;
            if (webView != null) {
                webView.loadUrl(url);
            }
        }
        lastKnownUrl = url;
    }

    public void reload() {
        if (webView != null) {
            webView.reload();
        }
    }

    public void goBack() {
        if (webView != null) {
            webView.goBack();
        }
    }

    public void goForward() {
        if (webView != null) {
            webView.goForward();
        }
    }

    public String getCurrentUrl() {
        if (lastKnownUrl == null) {
            Logger.e(TAG, "Internal error: loadUrl() was not called");
        }
        return lastKnownUrl;
    }

    public void resume() {
        if (webView != null) {
            webView.onResume();
        }
    }

    public void pause() {
        if (webView != null) {
            webView.onPause();
        }
    }

    public interface Callback {

        boolean shouldOverrideUrlLoading(String url);

        void onUrlLoadingStarted(String url);

        void onPageNavigationStackChanged(boolean backNavigationEnabled, boolean forwardNavigationEnabled);

        void onProgressChanged(int newProgress);

        @TargetApi(Build.VERSION_CODES.M)
        void onHttpError(WebResourceRequest request, WebResourceResponse errorResponse);

        void onGeneralError(int errorCode, String description, String failingUrl);

        @TargetApi(Build.VERSION_CODES.O)
        void onRenderProcessGone();
    }
}

