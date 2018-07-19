package net.pubnative.lite.sdk.views;

import android.app.Activity;

import net.pubnative.lite.sdk.api.BannerRequestManager;
import net.pubnative.lite.sdk.api.MRectRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class PNAdViewTest {

    private PNAdView adView;
    private Activity activity;

    @Before
    public void setUp() throws Exception {
        ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
        activity = activityController.get();
    }

    @Test
    public void testCreateBannerView() throws Exception {
        adView = new HyBidBannerAdView(activity);
        Assert.assertThat(adView.getRequestManager(), Matchers.<RequestManager>instanceOf(BannerRequestManager.class));
    }

    @Test
    public void testCreateMRectView() throws Exception {
        adView = new HyBidMRectAdView(activity);
        Assert.assertThat(adView.getRequestManager(), Matchers.<RequestManager>instanceOf(MRectRequestManager.class));
    }

    @After
    public void tearDown() {
        activity = null;
        adView = null;
    }
}
