// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.browser;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.pubnative.lite.sdk.utils.Logger;

import java.util.Locale;

public class BaseWebViewClient extends WebViewClient {
    private static final String TAG = BaseWebViewClient.class.getName();

    private WebViewClientCallback webViewClientCallback;
    private final BrowserActivity.WebViewCloseListener webViewCloseListener;

    public BaseWebViewClient(BrowserActivity.WebViewCloseListener listener) {
        this.webViewCloseListener = listener;
    }

    public void setWebViewClientCallback(WebViewClientCallback webViewClientCallback) {
        this.webViewClientCallback = webViewClientCallback;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (webViewClientCallback != null) {
            webViewClientCallback.onPageStartedLoading(url);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (webViewClientCallback != null) {
            webViewClientCallback.onPageFinishedLoading(url);
        }
    }

    @Override
    //Will be called on API < 24
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        return shouldOverrideUrlLoadingInternal(url);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.N)
    //Will be called on API >= 24
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
        String url = request.getUrl().toString();
        final Uri uri = Uri.parse(url);
        final String scheme = uri.getScheme();
        final String host = uri.getHost();
        final String uriLower = uri.toString().toLowerCase(Locale.ROOT);

        if ("intent".equalsIgnoreCase(scheme)) {
            return true;
        } else if ("play.google.com".equalsIgnoreCase(host)
                || "market.android.com".equalsIgnoreCase(host)
                || "market".equalsIgnoreCase(scheme)
                || uriLower.startsWith("play.google.com")
                || uriLower.startsWith("market.android.com/")) {

            String packageName = uri.getQueryParameter("id");

            if (packageName != null) {
                Uri marketUri = Uri.parse("market://details?id=" + packageName);

                boolean handled = forceHandleDeepLink(marketUri, webView);
                if (handled && webViewCloseListener != null) {
                    webViewCloseListener.onWebViewCloseRequested();
                }
                return handled;
            } else {
                return forceHandleDeepLink(uri, webView);
            }
        } else if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
            return shouldOverrideUrlLoadingInternal(request.getUrl().toString());
        } else {
            if (forceHandleDeepLink(uri, webView)) {
                if (webViewCloseListener != null) {
                    webViewCloseListener.onWebViewCloseRequested();
                }
            }
            return true;
        }
    }

    private boolean shouldOverrideUrlLoadingInternal(String url) {
        if (webViewClientCallback == null) {
            return false;
        }
        return webViewClientCallback.shouldOverrideUrlLoading(url);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedHttpError(WebView view,
                                    WebResourceRequest request,
                                    WebResourceResponse errorResponse) {
        if (webViewClientCallback != null) {
            webViewClientCallback.onHttpError(request, errorResponse);
        }
    }

    @Override
    public void onReceivedError(WebView view,
                                int errorCode,
                                String description,
                                String failingUrl) {
        if (webViewClientCallback != null) {
            webViewClientCallback.onGeneralError(errorCode, description, failingUrl);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view,
                                WebResourceRequest request,
                                WebResourceError error) {
        /* No need call super here as we map this callback to
         * onReceivedError(WebView view, int errorCode, String description, String failingUrl) ourselves
         */
        if (webViewClientCallback != null) {
            webViewClientCallback.onGeneralError(error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        if (webViewClientCallback != null) {
            webViewClientCallback.onRenderProcessGone();
            return true;
        }
        return false;
    }

    public boolean forceHandleDeepLink(Uri uri, WebView webView) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            webView.getContext().startActivity(intent);
        } catch (RuntimeException e) {
            Logger.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    public interface WebViewClientCallback {

        boolean shouldOverrideUrlLoading(String url);

        void onPageStartedLoading(String url);

        void onPageFinishedLoading(String url);

        @TargetApi(Build.VERSION_CODES.M)
        void onHttpError(WebResourceRequest request, WebResourceResponse errorResponse);

        void onGeneralError(int errorCode, String description, String failingUrl);

        @TargetApi(Build.VERSION_CODES.O)
        void onRenderProcessGone();
    }
}

