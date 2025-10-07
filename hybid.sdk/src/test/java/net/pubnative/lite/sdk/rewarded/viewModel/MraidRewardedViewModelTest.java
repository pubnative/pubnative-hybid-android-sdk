// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.viewModel;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastReceiver;
import net.pubnative.lite.sdk.rewarded.RewardedActivityInteractor;
import net.pubnative.lite.sdk.utils.AdTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
public class MraidRewardedViewModelTest {

    @Mock
    private Context mockContext;
    @Mock
    private RewardedActivityInteractor mockListener;
    @Mock
    private Ad mockAd;
    @Mock
    private ReportingController mockReportingController;

    @Mock
    private AdTracker mockAdTracker;
    @Mock
    private AdTracker mockAdEventTracker;
    @Mock
    private AdTracker mockCustomEndcardTracker;

    private MockedStatic<HyBid> mockedHyBid;
    private MraidRewardedViewModel viewModel;
    private MraidRewardedViewModel viewModelSpy;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockContext = RuntimeEnvironment.getApplication();
        mockedHyBid = mockStatic(HyBid.class);

        when(HyBid.getReportingController()).thenReturn(mockReportingController);
        when(HyBid.isReportingEnabled()).thenReturn(true);
        when(mockAd.getAssetUrl(anyString())).thenReturn("https://example.com/ad.html");

        viewModel = new MraidRewardedViewModel(mockContext, "test_zone_id", "STANDALONE", 10, 12345L, mockListener);
        viewModelSpy = spy(viewModel);
        viewModelSpy.mAd = mockAd;

        setPrivateField(viewModelSpy, "mAdTracker", mockAdTracker);
        setPrivateField(viewModelSpy, "mAdEventTracker", mockAdEventTracker);
        setPrivateField(viewModelSpy, "mCustomEndcardTracker", mockCustomEndcardTracker);

        Mockito.reset(mockListener);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getSuperclass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @After
    public void tearDown() {
        mockedHyBid.close();
    }

    @Test
    public void skipButtonClicked_sendsBroadcastAndDelegates() {
        try (MockedConstruction<MRAIDBanner> mockedBanner = mockConstruction(MRAIDBanner.class)) {
            viewModelSpy.getAdView();
            MRAIDBanner mockMraidBanner = mockedBanner.constructed().get(0);
            viewModelSpy.skipButtonClicked();
            verify(viewModelSpy).sendBroadcast(HyBidRewardedBroadcastReceiver.Action.PLAYABLE_SKIP_CLICK);
            verify(mockMraidBanner).skipButtonClicked();
        }
    }

    @Test
    public void destroyAd_destroysMraidView() {
        try (MockedConstruction<MRAIDBanner> mockedBanner = mockConstruction(MRAIDBanner.class)) {
            viewModelSpy.getAdView();
            MRAIDBanner mockMraidBanner = mockedBanner.constructed().get(0);
            viewModelSpy.destroyAd();
            verify(mockMraidBanner).stopAdSession();
            verify(mockMraidBanner).destroy();
        }
    }

    @Test
    public void onCustomEndCardClicked_whenFirstTime_tracksAndReports() {
        viewModelSpy.onCustomEndCardClicked();

        verify(mockReportingController).reportEvent(any(ReportingEvent.class));
        // Verify each mock was called exactly once
        verify(mockAdTracker).trackClick();
        verify(mockCustomEndcardTracker).trackClick();
        verify(mockAdEventTracker).trackCustomEndcardEvent(any(), any());

        // Call a second time to ensure it doesn't track again
        viewModelSpy.onCustomEndCardClicked();
        // Verify the total invocation count is still 1 for each mock
        verify(mockAdTracker, times(1)).trackClick();
        verify(mockCustomEndcardTracker, times(1)).trackClick();
    }
}