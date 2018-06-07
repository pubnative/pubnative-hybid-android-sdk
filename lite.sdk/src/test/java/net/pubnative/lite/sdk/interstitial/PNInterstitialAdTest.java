package net.pubnative.lite.sdk.interstitial;

import android.app.Activity;

import net.pubnative.lite.sdk.api.InterstitialRequestManager;
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
public class PNInterstitialAdTest {
    private Activity activity;
    private PNInterstitialAd interstitial;

    @Mock
    private PNInterstitialAd.Listener listener;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
        activity = activityController.get();
    }

    @Test
    public void testLoad_emptyZoneId() {
        interstitial = new PNInterstitialAd(activity, "", listener);
        interstitial.load();

        verify(listener).onInterstitialLoadFailed(any(Throwable.class));
    }

    @After
    public void tearDown() {
        activity = null;
        interstitial = null;
    }
}
