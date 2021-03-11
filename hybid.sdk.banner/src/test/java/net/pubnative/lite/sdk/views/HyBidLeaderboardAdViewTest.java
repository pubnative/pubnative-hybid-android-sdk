package net.pubnative.lite.sdk.views;

import android.app.Activity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;


@RunWith(RobolectricTestRunner.class)
public class HyBidLeaderboardAdViewTest {
    private HyBidAdView adView;
    private Activity activity;

    @Mock
    private PNAdView.Listener listener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
        activity = activityController.get();
        adView = new HyBidLeaderboardAdView(activity);
    }

    @Test
    public void testLoad_emptyZoneId() {
        adView.load("", listener);

        Mockito.verify(listener).onAdLoadFailed(ArgumentMatchers.any(Throwable.class));
    }

    @After
    public void tearDown() {
        activity = null;
        adView = null;
    }
}
