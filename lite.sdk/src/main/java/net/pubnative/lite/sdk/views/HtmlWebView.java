package net.pubnative.lite.sdk.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class HtmlWebView extends PNWebView {
    public HtmlWebView(Context context) {
        this(context, null);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public HtmlWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Disable scrolling and zoom
        setHorizontalScrollBarEnabled(false);
        setHorizontalScrollbarOverlay(false);
        setVerticalScrollBarEnabled(false);
        setVerticalScrollbarOverlay(false);
        getSettings().setSupportZoom(false);

        getSettings().setJavaScriptEnabled(true);

        enablePlugins(true);
        setBackgroundColor(Color.TRANSPARENT);

        setOnTouchListener(new WebViewClickDetector(this));
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
