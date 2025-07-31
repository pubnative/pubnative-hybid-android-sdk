// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;

public class PNMRectAdView extends HyBidMRectAdView {
    /*
     *  This class is kept for backwards compatibility.
     */

    public PNMRectAdView(Context context) {
        super(context);
    }

    public PNMRectAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PNMRectAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public PNMRectAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
