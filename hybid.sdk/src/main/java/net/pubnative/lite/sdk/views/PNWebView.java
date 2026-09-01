// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.views;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import net.pubnative.lite.sdk.utils.ViewUtils;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class PNWebView extends WebView {
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
    }

    @Override
    public void destroy() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ViewUtils.removeFromParent(this);
            removeAllViews();
            super.destroy();
        } else {
            new Handler(Looper.getMainLooper()).post(this::destroy);
        }
    }

    public void enableWebDebugging() {
        if (0 != (getContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
            setWebContentsDebuggingEnabled(true);
        }
    }
}
