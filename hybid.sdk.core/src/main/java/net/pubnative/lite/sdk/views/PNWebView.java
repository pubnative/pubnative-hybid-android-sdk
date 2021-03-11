// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk.views;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import net.pubnative.lite.sdk.utils.ViewUtils;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class PNWebView extends WebView {
    private static boolean sDeadlockCleared = false;

    public PNWebView(Context context) {
        this(context.getApplicationContext(), null);
    }

    public PNWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Don't allow ad creatives to detect or read files on the device's filesystem
        getSettings().setAllowFileAccess(false);
        getSettings().setAllowContentAccess(false);
        getSettings().setAllowFileAccessFromFileURLs(false);
        getSettings().setAllowUniversalAccessFromFileURLs(false);

        enableWebDebugging();

        enablePlugins(false);
        setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(final WebView view, final String url, final String message,
                                     final JsResult result) {
                result.confirm();
                return true;
            }

            @Override
            public boolean onJsConfirm(final WebView view, final String url, final String message,
                                       final JsResult result) {
                result.confirm();
                return true;
            }

            @Override
            public boolean onJsPrompt(final WebView view, final String url, final String message,
                                      final String defaultValue, final JsPromptResult result) {
                result.confirm();
                return true;
            }

            @Override
            public boolean onJsBeforeUnload(final WebView view, final String url,
                                            final String message, final JsResult result) {
                result.confirm();
                return true;
            }
        });

        if (!sDeadlockCleared) {
            fixWebViewDeadlock(getContext());
            sDeadlockCleared = true;
        }
    }

    @Override
    public void destroy() {
        ViewUtils.removeFromParent(this);
        removeAllViews();
        super.destroy();
    }

    public void enablePlugins(boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return;
        }

        getSettings().setPluginState(enabled ? WebSettings.PluginState.ON : WebSettings.PluginState.OFF);
    }

    public void enableWebDebugging() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                setWebContentsDebuggingEnabled(true);
            }
        }
    }

    /**
     * Fixes issue: https://code.google.com/p/android/issues/detail?id=63754
     * <p>
     * On KitKat, when a WebView with HTML5 video is is destroyed it can deadlock the WebView thread until another
     * hardware accelerated WebView is added to the view hierarchy.
     */
    private void fixWebViewDeadlock(Context context) {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.KITKAT) {
            return;
        }

        // Create an invisible WebView
        final WebView webView = new WebView(context.getApplicationContext());
        webView.setBackgroundColor(Color.TRANSPARENT);

        // Clear the deadlock by loading content and add to the view hierarchy using a system window
        webView.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = 1;
        params.height = 1;

        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        params.format = PixelFormat.TRANSPARENT;
        params.gravity = Gravity.START | Gravity.TOP;

        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.addView(webView, params);
        }
    }
}
