// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.presenter;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.view.View;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.CheckUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AdPresenterDecoratorTest {

    @Mock
    private AdPresenter mockAdPresenter;
    @Mock
    private AdTracker mockAdTracker;
    @Mock
    private ReportingController mockReportingController;
    @Mock
    private AdPresenter.Listener mockListener;
    @Mock
    private AdPresenter.ImpressionListener mockImpressionListener;
    @Mock
    private Ad mockAd;
    @Captor
    private ArgumentCaptor<ReportingEvent> eventCaptor;

    private AdPresenterDecorator subject;
    private AutoCloseable closeable;
    private View mockView;

    @Before
    public void setUp() {
        closeable = openMocks(this);
        mockView = Mockito.mock(View.class);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    // --- Delegation Tests ---
    @Test
    public void load_whenNotDestroyed_delegatesToWrappedPresenter() {
        try (MockedStatic<CheckUtils.NoThrow> mockedCheck = mockStatic(CheckUtils.NoThrow.class)) {
            mockedCheck.when(() -> CheckUtils.NoThrow.checkArgument(true, "AdPresenterDecorator is destroyed")).thenReturn(true);
            subject = new AdPresenterDecorator(mockAdPresenter, mockAdTracker, mockReportingController, mockListener, mockImpressionListener, IntegrationType.STANDALONE);
            subject.load();
            verify(mockAdPresenter).load();
        }
    }

    @Test
    public void load_whenDestroyed_doesNotDelegate() {
        try (MockedStatic<CheckUtils.NoThrow> mockedCheck = mockStatic(CheckUtils.NoThrow.class)) {
            mockedCheck.when(() -> CheckUtils.NoThrow.checkArgument(false, "AdPresenterDecorator is destroyed")).thenReturn(false);
            subject = new AdPresenterDecorator(mockAdPresenter, mockAdTracker, mockReportingController, mockListener, mockImpressionListener, IntegrationType.STANDALONE);
            subject.destroy();
            subject.load();
            verify(mockAdPresenter, never()).load();
        }
    }

    @Test
    public void getAd_delegatesToWrappedPresenter() {
        when(mockAdPresenter.getAd()).thenReturn(mockAd);
        subject = new AdPresenterDecorator(mockAdPresenter, mockAdTracker, mockReportingController, mockListener, mockImpressionListener, IntegrationType.STANDALONE);
        Ad result = subject.getAd();
        assertEquals(mockAd, result);
        verify(mockAdPresenter).getAd();
    }

    // --- Callback Decoration Tests ---
    @Test
    public void onAdClicked_whenFirstTime_tracksReportsAndDelegates() {
        try (MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::isReportingEnabled).thenReturn(true);
            when(mockAdPresenter.getAd()).thenReturn(mockAd);
            subject = new AdPresenterDecorator(mockAdPresenter, mockAdTracker, mockReportingController, mockListener, mockImpressionListener, IntegrationType.STANDALONE);

            subject.onAdClicked(mockAdPresenter);

            // Verify decoration logic
            verify(mockAdTracker).trackClick();
            verify(mockReportingController).reportEvent(eventCaptor.capture());
            assertEquals(Reporting.EventType.CLICK, eventCaptor.getValue().getEventType());

            // Verify delegation
            verify(mockListener).onAdClicked(mockAdPresenter);
        }
    }

    @Test
    public void onAdClicked_whenAlreadyTracked_onlyDelegatesOnce() {
        try (MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::isReportingEnabled).thenReturn(true);
            subject = new AdPresenterDecorator(mockAdPresenter, mockAdTracker, mockReportingController, mockListener, mockImpressionListener, IntegrationType.STANDALONE);

            subject.onAdClicked(mockAdPresenter);
            subject.onAdClicked(mockAdPresenter);

            // Verify decoration logic only ran once
            verify(mockAdTracker, times(1)).trackClick();
            verify(mockReportingController, times(1)).reportEvent(any(ReportingEvent.class));

            verify(mockListener, times(1)).onAdClicked(mockAdPresenter);
        }
    }

    @Test
    public void onImpression_whenFirstTime_tracksReportsAndDelegates() {
        try (MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::isReportingEnabled).thenReturn(true);
            when(mockAdPresenter.getAd()).thenReturn(mockAd);
            subject = new AdPresenterDecorator(mockAdPresenter, mockAdTracker, mockReportingController, mockListener, mockImpressionListener, IntegrationType.STANDALONE);

            subject.onImpression();

            verify(mockAdTracker).trackImpression();
            verify(mockReportingController).reportEvent(eventCaptor.capture());
            assertEquals(Reporting.EventType.IMPRESSION, eventCaptor.getValue().getEventType());

            verify(mockImpressionListener).onImpression();
        }
    }

    @Test
    public void onImpression_whenAlreadyTracked_doesNothing() {
        try (MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::isReportingEnabled).thenReturn(true);
            subject = new AdPresenterDecorator(mockAdPresenter, mockAdTracker, mockReportingController, mockListener, mockImpressionListener, IntegrationType.STANDALONE);

            subject.onImpression();
            subject.onImpression();

            // Verify all logic was only called once
            verify(mockAdTracker, times(1)).trackImpression();
            verify(mockReportingController, times(1)).reportEvent(any(ReportingEvent.class));
            verify(mockImpressionListener, times(1)).onImpression();
        }
    }

    @Test
    public void onAdError_whenNotDestroyed_reportsAndDelegates() {
        when(mockAdPresenter.getAd()).thenReturn(mockAd);
        subject = new AdPresenterDecorator(mockAdPresenter, mockAdTracker, mockReportingController, mockListener, mockImpressionListener, IntegrationType.STANDALONE);

        subject.onAdError(mockAdPresenter);

        verify(mockReportingController).reportEvent(eventCaptor.capture());
        assertEquals(Reporting.EventType.ERROR, eventCaptor.getValue().getEventType());
        verify(mockListener).onAdError(mockAdPresenter);
    }

    @Test
    public void onAdError_whenDestroyed_doesNothing() {
        subject = new AdPresenterDecorator(mockAdPresenter, mockAdTracker, mockReportingController, mockListener, mockImpressionListener, IntegrationType.STANDALONE);
        subject.destroy(); // Destroy the decorator
        subject.onAdError(mockAdPresenter);

        verify(mockReportingController, never()).reportEvent(any(ReportingEvent.class));
        verify(mockListener, never()).onAdError(any(AdPresenter.class));
    }

    @Test
    public void addFriendlyObstruction_withValidView_delegatesToPresenter() {
        subject = new AdPresenterDecorator(mockAdPresenter, mockAdTracker, mockReportingController, mockListener, mockImpressionListener, IntegrationType.STANDALONE);
        subject.addFriendlyObstruction(mockView);
        verify(mockAdPresenter).addFriendlyObstruction(mockView);
    }

    @Test
    public void addFriendlyObstruction_withNullView_delegatesToPresenter() {
        subject = new AdPresenterDecorator(mockAdPresenter, mockAdTracker, mockReportingController, mockListener, mockImpressionListener, IntegrationType.STANDALONE);
        subject.addFriendlyObstruction(null);
        verify(mockAdPresenter).addFriendlyObstruction(null);
    }

    @Test
    public void addFriendlyObstruction_withNullPresenter_handlesGracefully() {
        subject = new AdPresenterDecorator(null, mockAdTracker, mockReportingController, mockListener, mockImpressionListener, IntegrationType.STANDALONE);
        subject.addFriendlyObstruction(mockView);
    }
}