package net.pubnative.lite.sdk.visibility;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class ImpressionTrackerTest {
    Context applicationContext;
    Activity activity;

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
        activity = Robolectric.buildActivity(Activity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void addView_withValidListener_invokeGetVisibilityTracker() {

        VisibilityTracker.Listener listener = spy(VisibilityTracker.Listener.class);
        ImpressionTracker impressionTracker = spy(ImpressionTracker.class);
        impressionTracker.mHandler = new Handler();
        impressionTracker.mVisibilityListener = listener;
        View view = new View(activity);
        impressionTracker.addView(view);
        verify(impressionTracker, times(1)).getVisibilityTracker();
    }

    @Test
    public void addView_withNullListener_invokeGetVisibilityTracker() {

        ImpressionTracker impressionTracker = spy(ImpressionTracker.class);
        impressionTracker.mHandler = new Handler();
        impressionTracker.mVisibilityListener = null;
        View view = new View(activity);
        impressionTracker.addView(view);
        verify(impressionTracker, times(1)).getVisibilityTracker();
    }
}
