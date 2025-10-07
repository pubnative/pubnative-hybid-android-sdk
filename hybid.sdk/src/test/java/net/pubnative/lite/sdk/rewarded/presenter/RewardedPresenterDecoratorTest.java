// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.presenter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.SdkEventType;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.CheckUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RewardedPresenterDecoratorTest {

    @Mock
    private RewardedPresenter mockRewardedPresenter;
    @Mock
    private AdTracker mockAdTrackingDelegate;
    @Mock
    private AdTracker mockCustomEndCardTrackingDelegate;
    @Mock
    private ReportingController mockReportingController;
    @Mock
    private RewardedPresenter.Listener mockListener;
    @Mock
    private Ad mockAd;

    private MockedStatic<CheckUtils.NoThrow> mockedCheckUtils;
    private MockedStatic<HyBid> mockedHyBid;
    private MockedStatic<TextUtils> mockedTextUtils;

    private RewardedPresenterDecorator rewardedPresenterDecorator;

    @Before
    public void setUp() {
        mockedCheckUtils = Mockito.mockStatic(CheckUtils.NoThrow.class);
        mockedHyBid = Mockito.mockStatic(HyBid.class);
        mockedTextUtils = Mockito.mockStatic(TextUtils.class);

        when(CheckUtils.NoThrow.checkArgument(true, "RewardedPresenterDecorator is destroyed")).thenReturn(true);
        when(HyBid.isReportingEnabled()).thenReturn(true);
        when(TextUtils.isEmpty(any())).thenAnswer(invocation -> {
            CharSequence s = invocation.getArgument(0);
            return s == null || s.length() == 0;
        });

        rewardedPresenterDecorator = new RewardedPresenterDecorator(mockRewardedPresenter, mockAdTrackingDelegate,
                mockCustomEndCardTrackingDelegate, mockReportingController, mockListener, IntegrationType.STANDALONE);
    }

    @After
    public void tearDown() {
        mockedCheckUtils.close();
        mockedHyBid.close();
        mockedTextUtils.close();
    }

    @Test
    public void onRewardedOpened_whenFirstTime_tracksImpressionAndNotifiesListener() {
        // Intercept the creation of ReportingEvent to prevent it from calling Android APIs
        try (MockedConstruction<ReportingEvent> mockedEvent = mockConstruction(ReportingEvent.class)) {
            when(mockRewardedPresenter.getAd()).thenReturn(mockAd);
            rewardedPresenterDecorator.onRewardedOpened(mockRewardedPresenter);

            // Verify decorator actions
            verify(mockReportingController).reportEvent(any(ReportingEvent.class));
            verify(mockAdTrackingDelegate).trackImpression();
            verify(mockAdTrackingDelegate).trackSdkEvent(SdkEventType.SHOW, null);

            // Verify delegation to the final listener
            verify(mockListener).onRewardedOpened(mockRewardedPresenter);
        }
    }

    @Test
    public void onRewardedClicked_whenFirstTime_tracksClickAndNotifiesListener() {
        try (MockedConstruction<ReportingEvent> mockedEvent = mockConstruction(ReportingEvent.class)) {
            when(mockRewardedPresenter.getAd()).thenReturn(mockAd);
            rewardedPresenterDecorator.onRewardedClicked(mockRewardedPresenter);

            verify(mockReportingController).reportEvent(any(ReportingEvent.class));
            verify(mockAdTrackingDelegate).trackClick();
            verify(mockListener).onRewardedClicked(mockRewardedPresenter);
        }
    }

    @Test
    public void onRewardedError_tracksErrorAndNotifiesListener() {
        try (MockedConstruction<ReportingEvent> mockedEvent = mockConstruction(ReportingEvent.class)) {
            when(mockRewardedPresenter.getAd()).thenReturn(mockAd);
            when(mockAd.getZoneId()).thenReturn("test_zone_id");

            rewardedPresenterDecorator.onRewardedError(mockRewardedPresenter);

            verify(mockReportingController).reportEvent(any(ReportingEvent.class));
            verify(mockAdTrackingDelegate).trackSdkEvent(SdkEventType.LOAD, HyBidErrorCode.UNKNOWN_ERROR.getCode());
            verify(mockListener).onRewardedError(mockRewardedPresenter);
        }
    }

    @Test
    public void onCustomEndCardShow_whenFirstTime_tracksImpression() {
        try (MockedConstruction<ReportingEvent> mockedEvent = mockConstruction(ReportingEvent.class)) {
            rewardedPresenterDecorator.onCustomEndCardShow();

            verify(mockReportingController).reportEvent(any(ReportingEvent.class));
            verify(mockCustomEndCardTrackingDelegate).trackImpression();
        }
    }

    // --- Other tests that do not create ReportingEvents ---

    @Test
    public void load_whenNotDestroyed_delegatesToPresenter() {
        rewardedPresenterDecorator.load();
        verify(mockRewardedPresenter).load();
    }

    @Test
    public void onRewardedLoaded_tracksLoadAndNotifiesListener() {
        rewardedPresenterDecorator.onRewardedLoaded(mockRewardedPresenter);
        verify(mockAdTrackingDelegate).trackSdkEvent(SdkEventType.LOAD, null);
        verify(mockListener).onRewardedLoaded(mockRewardedPresenter);
    }

    @Test
    public void onRewardedOpened_whenSecondTime_doesNothing() {
        // Use try-with-resources here as the first call creates a reporting event
        try (MockedConstruction<ReportingEvent> mockedEvent = mockConstruction(ReportingEvent.class)) {
            rewardedPresenterDecorator.onRewardedOpened(mockRewardedPresenter);
        }
        // Second call
        rewardedPresenterDecorator.onRewardedOpened(mockRewardedPresenter);

        // Verify that all actions were only called once from the first invocation.
        verify(mockAdTrackingDelegate, times(1)).trackImpression();
        verify(mockListener, times(1)).onRewardedOpened(mockRewardedPresenter);
    }
}