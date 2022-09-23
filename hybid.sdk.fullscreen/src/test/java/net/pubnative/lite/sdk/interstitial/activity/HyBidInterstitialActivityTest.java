package net.pubnative.lite.sdk.interstitial.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29)
public class HyBidInterstitialActivityTest {
    private HyBidInterstitialActivity subject;
    private long broadcastIdentifier;

    // Make a concrete version of the abstract class for testing purposes.
    private static class TestInterstitialActivity extends HyBidInterstitialActivity {
        View view;

        @Override
        public View getAdView() {
            if (view == null) {
                view = new View(this);
            }
            return view;
        }

        @Override
        protected boolean shouldShowContentInfo() {
            return false;
        }

        @Override
        protected void resumeAd() {

        }

        @Override
        protected void pauseAd() {

        }
    }

    @Before
    public void setup() {
        broadcastIdentifier = 2222;
    }

    @Test
    public void onCreate_shouldCreateView() throws Exception {
        Context context = Robolectric.buildActivity(Activity.class).create().get();
        Intent intent = new Intent(context, TestInterstitialActivity.class);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_BROADCAST_ID, broadcastIdentifier);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_ZONE_ID, "2");

        subject = Robolectric.buildActivity(TestInterstitialActivity.class, intent)
                .create().get();
        View adView = getContentView(subject).getChildAt(0);

        Assert.assertNotNull(adView);
    }

    @Test
    public void onDestroy_shouldCleanUpContentView() throws Exception {
        Context context = Robolectric.buildActivity(Activity.class).create().get();
        Intent intent = new Intent(context, TestInterstitialActivity.class);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_BROADCAST_ID, broadcastIdentifier);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_ZONE_ID, "2");

        subject = Robolectric.buildActivity(TestInterstitialActivity.class, intent)
                .create().destroy().get();

        Assert.assertEquals(0, getContentView(subject).getChildCount());
    }

    @Test
    public void getBroadcastIdentifier_shouldReturnBroadcastIdFromIntent() throws Exception {
        Context context = Robolectric.buildActivity(Activity.class).create().get();
        Intent intent = new Intent(context, TestInterstitialActivity.class);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_BROADCAST_ID, broadcastIdentifier);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_ZONE_ID, "2");

        subject = Robolectric.buildActivity(TestInterstitialActivity.class, intent)
                .create().get();
        Assert.assertEquals(2222L, subject.getBroadcastSender().getBroadcastId());
    }

    protected FrameLayout getContentView(HyBidInterstitialActivity subject) {
        return subject.getCloseableContainer();
    }
}
