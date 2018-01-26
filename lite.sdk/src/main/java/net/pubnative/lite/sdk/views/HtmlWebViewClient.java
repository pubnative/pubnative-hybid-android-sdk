package net.pubnative.lite.sdk.views;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.pubnative.lite.sdk.utils.UrlHandler;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class HtmlWebViewClient extends WebViewClient{
    private final UrlHandler mUrlHandler;

    public HtmlWebViewClient(Context context) {
        mUrlHandler = new UrlHandler(context);
    }

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
        mUrlHandler.handleUrl(url);
        return true;
    }
}
