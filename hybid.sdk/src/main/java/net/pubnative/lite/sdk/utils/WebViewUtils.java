package net.pubnative.lite.sdk.utils;

import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import net.pubnative.lite.sdk.HyBid;

public class WebViewUtils {
    private static final String TAG = WebViewUtils.class.getSimpleName();

    public static void manageThirdPartyCookies(final WebView webView) {
        if (webView != null) {
            CookieManager cookieManager = CookieManager.getInstance();
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.setAcceptThirdPartyCookies(webView, HyBid.getUserDataManager().canCollectData());
            }
        }
    }

    public static void setDisableJSChromeClient(final WebView webView) {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(final WebView view, final String url,
                                     final String message, final JsResult result) {
                Logger.d(TAG, message);
                result.confirm();
                return true;
            }

            @Override
            public boolean onJsConfirm(final WebView view, final String url,
                                       final String message, final JsResult result) {
                Logger.d(TAG, message);
                result.confirm();
                return true;
            }

            @Override
            public boolean onJsPrompt(final WebView view, final String url,
                                      final String message, final String defaultValue,
                                      final JsPromptResult result) {
                Logger.d(TAG, message);
                result.confirm();
                return true;
            }

            @Override
            public boolean onJsBeforeUnload(final WebView view, final String url,
                                            final String message, final JsResult result) {
                Logger.d(TAG, message);
                result.confirm();
                return true;
            }
        });
    }
}
