// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.browser;

import android.os.SystemClock;
import android.view.View;

public abstract class DoubleClickPreventionListener implements View.OnClickListener {

    private static final long MIN_CLICK_INTERVAL_MS = 1000;

    private long lastClickTimestamp;

    // Package-private for testing
    TimeProvider timeProvider;

    // Default constructor - uses SystemClock (existing behavior)
    public DoubleClickPreventionListener() {
        this(SystemClock::elapsedRealtime);
    }

    // Constructor for testing - allows injection of custom TimeProvider
    public DoubleClickPreventionListener(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public final void onClick(View v) {
        long now = timeProvider.getCurrentTime();
        if (now - lastClickTimestamp < MIN_CLICK_INTERVAL_MS) {
            return;
        }
        lastClickTimestamp = now;
        processClick();
    }

    protected abstract void processClick();

    // Interface for dependency injection in tests
    interface TimeProvider {
        long getCurrentTime();
    }
}
