// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;

public class PNAdView extends HyBidAdView {
    public interface Listener extends HyBidAdView.Listener { }

    public PNAdView(Context context) {
        super(context);
    }

    public PNAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PNAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public PNAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
