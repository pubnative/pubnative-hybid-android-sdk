package net.pubnative.lite.sdk.visibility;

import android.os.Handler;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class VisibilityTrackerTest {
    @Test
    public void addView_withValidListener_shouldScheduleVisibilityCheck() {

        VisibilityTracker.Listener listener = spy(VisibilityTracker.Listener.class);
        VisibilityTracker visibilityTracker = spy(VisibilityTracker.class);
        visibilityTracker.mHandler = new Handler();
        visibilityTracker.setListener(listener);
        View view = new View(RuntimeEnvironment.application.getApplicationContext());
        visibilityTracker.addView(view, 100);
        verify(visibilityTracker, times(1)).scheduleVisibilityCheck();
    }

    @Test
    public void addView_withNullListener_shouldScheduleVisibilityCheck() {

        VisibilityTracker visibilityTracker = spy(VisibilityTracker.class);
        visibilityTracker.mHandler = new Handler();
        visibilityTracker.setListener(null);
        View view = new View(RuntimeEnvironment.application.getApplicationContext());
        visibilityTracker.addView(view, 100);
        verify(visibilityTracker, times(1)).scheduleVisibilityCheck();
    }
}
