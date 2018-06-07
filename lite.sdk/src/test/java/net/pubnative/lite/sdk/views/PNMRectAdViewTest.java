package net.pubnative.lite.sdk.views;

import android.app.Activity;

import net.pubnative.lite.sdk.api.MRectRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class PNMRectAdViewTest {
    private PNAdView adView;
    private Activity activity;

    @Mock
    private PNAdView.Listener listener;

    @Mock
    private MRectRequestManager requestManager;
    @Mock
    private RequestManager.RequestListener requestListener;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
        activity = activityController.get();
        requestManager.setRequestListener(requestListener);
        adView = new PNMRectAdView(activity, requestManager);
    }

    @Test
    public void testLoad_emptyZoneId() {
        adView.load("", listener);

        verify(listener).onAdLoadFailed(any(Throwable.class));
    }

    @After
    public void tearDown() {
        activity = null;
        adView = null;
    }
}
