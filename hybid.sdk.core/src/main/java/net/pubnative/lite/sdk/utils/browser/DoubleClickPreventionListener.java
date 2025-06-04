// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.browser;

import android.view.View;

public abstract class DoubleClickPreventionListener implements View.OnClickListener {

    private static final long MIN_CLICK_INTERVAL_MS = 1000;

    private long lastClickTimestamp;

    @Override
    public final void onClick(View v) {
        long now = System.currentTimeMillis();
        if (now - lastClickTimestamp < MIN_CLICK_INTERVAL_MS) {
            return;
        }
        lastClickTimestamp = now;
        processClick();
    }

    protected void processClick() {

    }
}
