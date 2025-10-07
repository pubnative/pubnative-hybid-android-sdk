// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.browser;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.net.URISyntaxException;
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
        return handleUrlLoading(webView, url);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.N)
    //Will be called on API >= 24
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
        return handleUrlLoading(webView, request.getUrl().toString());
    }

    private boolean handleUrlLoading(WebView webView, String url) {
        final Uri uri = Uri.parse(url);
        final String scheme = uri.getScheme();
        final String host = uri.getHost();
        final String uriLower = uri.toString().toLowerCase(Locale.ROOT);

        if ("intent".equalsIgnoreCase(scheme)) {
            return handleIntentUrl(url, webView);
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
            return shouldOverrideUrlLoadingInternal(url);
        } else {
            // Handle custom schemes (deep links)
            boolean handled = forceHandleDeepLink(uri, webView);
            if (handled && webViewCloseListener != null) {
                webViewCloseListener.onWebViewCloseRequested();
            }
            return handled;
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

    private boolean handleIntentUrl(String intentUrl, WebView webView) {
        try {
            Intent intent = Intent.parseUri(intentUrl, Intent.URI_INTENT_SCHEME);

            // Check if the app is installed and can handle the intent
            PackageManager packageManager = webView.getContext().getPackageManager();
            ComponentName componentName = intent.resolveActivity(packageManager);

            if (componentName != null) {
                // App is installed, launch it
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                webView.getContext().startActivity(intent);

                // Close the browser since we're launching the app
                if (webViewCloseListener != null) {
                    webViewCloseListener.onWebViewCloseRequested();
                }
                return true;
            } else {
                // App is not installed, try to get fallback URL
                String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                if (fallbackUrl != null && !fallbackUrl.isEmpty()) {
                    // Load the fallback URL in the WebView
                    webView.loadUrl(fallbackUrl);
                    return true;
                } else {
                    // Check if there's a market URL for the app
                    String packageName = intent.getPackage();
                    if (packageName != null && !packageName.isEmpty()) {
                        Uri marketUri = Uri.parse("market://details?id=" + packageName);
                        boolean handled = forceHandleDeepLink(marketUri, webView);
                        if (handled && webViewCloseListener != null) {
                            webViewCloseListener.onWebViewCloseRequested();
                        }
                        return handled;
                    }
                }
            }
        } catch (ActivityNotFoundException e) {
            Logger.e(TAG, "Activity not found for intent URL: " + e.getMessage());
        } catch (URISyntaxException e) {
            Logger.e(TAG, "URI syntax error: " + e.getMessage());
        } catch (Exception e) {
            Logger.e(TAG, "Error handling intent URL: " + e.getMessage());
        }

        // If all else fails, don't override - let WebView handle it
        return false;
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
