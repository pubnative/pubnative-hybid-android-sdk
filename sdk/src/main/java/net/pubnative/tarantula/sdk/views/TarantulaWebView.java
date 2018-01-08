package net.pubnative.tarantula.sdk.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import net.pubnative.tarantula.sdk.utils.ViewUtils;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class TarantulaWebView extends WebView {
    private static boolean sDeadlockCleared = false;

    public TarantulaWebView(Context context) {
        this(context, null);
    }

    public TarantulaWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Don't allow ad creatives to detect or read files on the device's filesystem
        getSettings().setAllowFileAccess(false);
        getSettings().setAllowContentAccess(false);
        getSettings().setAllowFileAccessFromFileURLs(false);
        getSettings().setAllowUniversalAccessFromFileURLs(false);

        enablePlugins(false);
        setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(@NonNull final WebView view, @NonNull final String url, @NonNull final String message,
                                     @NonNull final JsResult result) {
                result.confirm();
                return true;
            }

            @Override
            public boolean onJsConfirm(@NonNull final WebView view, @NonNull final String url, @NonNull final String message,
                                       @NonNull final JsResult result) {
                result.confirm();
                return true;
            }

            @Override
            public boolean onJsPrompt(@NonNull final WebView view, @NonNull final String url, @NonNull final String message,
                                      @NonNull final String defaultValue, @NonNull final JsPromptResult result) {
                result.confirm();
                return true;
            }

            @Override
            public boolean onJsBeforeUnload(@NonNull final WebView view, @NonNull final String url,
                                            @NonNull final String message, @NonNull final JsResult result) {
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

    /**
     * Intended to be used with dummy WebViews to precache WebView javascript and assets.
     */
    @SuppressLint("SetJavaScriptEnabled")
    protected void enableJavascriptCaching() {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDomStorageEnabled(true);
        getSettings().setAppCacheEnabled(true);

        getSettings().setAppCachePath(getContext().getCacheDir().getAbsolutePath());
    }

    /**
     * Fixes issue: https://code.google.com/p/android/issues/detail?id=63754
     *
     * On KitKat, when a WebView with HTML5 video is is destroyed it can deadlock the WebView thread until another
     * hardware accelerated WebView is added to the view hierarchy.
     */
    private void fixWebViewDeadlock(@NonNull Context context) {
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
