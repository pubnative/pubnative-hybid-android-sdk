package net.pubnative.lite.sdk.views;

import android.app.Activity;

import net.pubnative.lite.sdk.api.BannerRequestManager;
import net.pubnative.lite.sdk.api.LeaderboardRequestManager;
import net.pubnative.lite.sdk.api.MRectRequestManager;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class HyBidAdViewTest {

    private HyBidAdView adView;
    private Activity activity;

    @Before
    public void setUp() throws Exception {
        ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
        activity = activityController.get();
    }

    @Test
    public void testCreateBannerView() {
        adView = new HyBidBannerAdView(activity);
        MatcherAssert.assertThat(adView.getRequestManager(), Matchers.instanceOf(BannerRequestManager.class));
    }

    @Test
    public void testCreateMRectView() {
        adView = new HyBidMRectAdView(activity);
        MatcherAssert.assertThat(adView.getRequestManager(), Matchers.instanceOf(MRectRequestManager.class));
    }

    @Test
    public void testCreateLeaderboardView() {
        adView = new HyBidLeaderboardAdView(activity);
        MatcherAssert.assertThat(adView.getRequestManager(), Matchers.instanceOf(LeaderboardRequestManager.class));
    }

    @After
    public void tearDown() {
        activity = null;
        adView = null;
    }
}
