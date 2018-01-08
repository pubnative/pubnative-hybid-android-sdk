package net.pubnative.tarantula.sdk.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.pubnative.tarantula.sdk.utils.UrlHandler;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class HtmlWebViewClient extends WebViewClient{
    @NonNull private final UrlHandler mUrlHandler;

    public HtmlWebViewClient(@NonNull Context context) {
        mUrlHandler = new UrlHandler(context);
    }

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
        mUrlHandler.handleUrl(url);
        return true;
    }
}
