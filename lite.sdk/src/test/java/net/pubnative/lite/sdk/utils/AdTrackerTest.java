package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.api.PNApiClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by erosgarciaponte on 24.01.18.
 */
@RunWith(RobolectricTestRunner.class)
public class AdTrackerTest {
    @Mock
    private PNApiClient mMockApiClient;
    @Mock
    private PNApiClient.TrackUrlListener mListener;

    private AdTracker mSubject;

    @Before
    public void setup() {
        initMocks(this);
        mSubject = new AdTracker(mMockApiClient, TestUtil.createMockImpressionBeacons(), TestUtil.createMockClickBeacons());
        mSubject.setTrackUrlListener(mListener);
    }

    @Test
    public void trackClick() {
        mSubject.trackClick();
        mSubject.trackClick();

        verify(mMockApiClient, times(1)).trackUrl("https://got.pubnative.net/click/rtb?aid=1036637", mListener);
    }

    @Test
    public void trackImpression() {
        mSubject.trackImpression();
        mSubject.trackImpression();

        verify(mMockApiClient, times(1)).trackUrl("https://mock-dsp.pubnative.net/tracker/nurl?app_id=1036637&p=0.01", mListener);
    }
}
