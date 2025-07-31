// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.api.PNApiClient;
import net.pubnative.lite.sdk.testing.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class AdTrackerTest {
    @Mock
    private PNApiClient mMockApiClient;
    @Mock
    private DeviceInfo mMockDeviceInfo;
    @Mock
    private PNApiClient.TrackUrlListener mListener;

    private AdTracker mSubject;

    @Before
    public void setup() {
        initMocks(this);
        mSubject = new AdTracker(mMockApiClient, mMockDeviceInfo,
                TestUtil.createMockImpressionBeacons(),
                TestUtil.createMockClickBeacons(),
                TestUtil.createMockLoadEventBeacons(),
                TestUtil.createMockCompanionAdEventsBeacons(),
                TestUtil.createMockCustomEndcardBeacons());
        mSubject.setTrackUrlListener(mListener);
    }

    @Test
    public void trackClick() {
        mSubject.trackClick();
        mSubject.trackClick();

        verify(mMockApiClient, times(1)).trackUrl("https://got.pubnative.net/click/rtb?aid=1036637", null, "CLICK", mListener);
    }

    @Test
    public void trackImpression() {
        mSubject.trackImpression();
        mSubject.trackImpression();

        verify(mMockApiClient, times(1)).trackUrl("https://mock-dsp.pubnative.net/tracker/nurl?app_id=1036637&p=0.01", null, "IMPRESSION", mListener);
    }
}
