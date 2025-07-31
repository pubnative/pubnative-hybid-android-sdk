// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.views;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.MockitoAnnotations.openMocks;

import android.app.Activity;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.utils.sdkmanager.SdkManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class HyBidMRectAdViewTest {
    private HyBidAdView adView;
    private Activity activity;

    @Mock
    private PNAdView.Listener listener;

    private MockedStatic<HyBid> mHyBid;


    @Before
    public void setUp() throws Exception {
        openMocks(this);
        mHyBid = mockStatic(HyBid.class);
        mHyBid.when(HyBid::getSdkManager).thenReturn(mock(SdkManager.class));

        ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
        activity = activityController.get();
        adView = new HyBidMRectAdView(activity);
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
        mHyBid.close();
    }
}