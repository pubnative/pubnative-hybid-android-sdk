// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial;

import android.app.Activity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.utils.sdkmanager.SdkManager;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29)
public class HyBidInterstitialAdTest {
    private Activity activity;
    private HyBidInterstitialAd interstitial;

    @Mock
    private HyBidInterstitialAd.Listener listener;

    private MockedStatic<HyBid> mHyBid;

    @Before
    public void setUp() throws Exception {
        openMocks(this);

        ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
        activity = activityController.get();

        mHyBid = mockStatic(HyBid.class);
        mHyBid.when(HyBid::getSdkManager).thenReturn(mock(SdkManager.class));
    }

    @Test
    public void testLoad_emptyZoneId() {
        interstitial = new HyBidInterstitialAd(activity, "", listener);
        interstitial.load();

        verify(listener).onInterstitialLoadFailed(any(Throwable.class));
    }

    @After
    public void tearDown() {
        mHyBid.close();
        activity = null;
        interstitial = null;
    }
}
