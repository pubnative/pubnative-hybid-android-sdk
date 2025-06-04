// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.browser;

import android.webkit.CookieManager;
import android.webkit.WebView;

public class BrowserCookieManager {

    private final CookieManager cookieManager;

    public BrowserCookieManager(CookieManager cookieManager) {
        if (cookieManager == null) {
            throw new NullPointerException("CookieManager can not be null");
        }

        this.cookieManager = cookieManager;
    }

    public void setupCookiePolicy(WebView webView) {
        if (webView == null) {
            throw new NullPointerException("WebView can not be null");
        }
        // TODO: Check if we need to validate with cmp for this
        boolean allowThirdPartyCookies = true;
        cookieManager.setAcceptThirdPartyCookies(webView, allowThirdPartyCookies);
    }
}

