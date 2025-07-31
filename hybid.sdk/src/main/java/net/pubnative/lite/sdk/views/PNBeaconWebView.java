// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.views;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class PNBeaconWebView extends WebView {
    public PNBeaconWebView(Context context) {
        super(context);
        init();
    }

    public PNBeaconWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PNBeaconWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        getSettings().setJavaScriptEnabled(true);
    }

    public void loadBeacon(final String jsBeacon) {
        loadUrl("javascript:" + jsBeacon);
    }
}
