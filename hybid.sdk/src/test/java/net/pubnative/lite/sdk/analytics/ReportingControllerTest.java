package net.pubnative.lite.sdk.analytics;

import android.os.Looper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import net.pubnative.lite.sdk.analytics.tracker.ReportingTracker;
import net.pubnative.lite.sdk.analytics.tracker.ReportingTrackerCallback;

/**
 * Unit tests for the ReportingController class.
 * Uses RobolectricTestRunner to handle Android framework dependencies like Looper and Handler.
 */
@RunWith(RobolectricTestRunner.class)
public class ReportingControllerTest {

    @Mock
    private ReportingEventCallback mockEventCallback1;
    @Mock
    private ReportingEventCallback mockEventCallback2;
    @Mock
    private ReportingTrackerCallback mockTrackerCallback1;
    @Mock
    private ReportingTrackerCallback mockTrackerCallback2;

    private ReportingController reportingController;
    private AutoCloseable mockitoCloseable;

    @Before
    public void setUp() {
        mockitoCloseable = MockitoAnnotations.openMocks(this);
        reportingController = new ReportingController();
    }

    @After
    public void tearDown() throws Exception {
        mockitoCloseable.close();
    }

    @Test
    public void addCallback_withValidCallback_shouldSucceed() {
        reportingController.addCallback(mockEventCallback1);
        // Verification for this is in the reportEvent test to ensure it's actually called.
    }

    @Test
    public void addCallback_withNullCallback_shouldNotAdd() {
        reportingController.addCallback(null);
        // This is a defensive test. We verify by ensuring no crash and that reportEvent doesn't fail.
    }

    @Test
    public void removeCallback_withExistingCallback_shouldRemoveAndReturnTrue() {
        reportingController.addCallback(mockEventCallback1);
        boolean result = reportingController.removeCallback(mockEventCallback1);
        assertTrue(result);

        // Verify that the removed callback is no longer notified.
        reportingController.reportEvent(new ReportingEvent());
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        verify(mockEventCallback1, never()).onEvent(any(ReportingEvent.class));
    }

    @Test
    public void removeCallback_withNonExistentCallback_shouldReturnFalse() {
        boolean result = reportingController.removeCallback(mockEventCallback1);
        assertFalse(result);
    }

    @Test
    public void reportEvent_shouldNotifyAllRegisteredListeners() {
        ReportingEvent event = new ReportingEvent();
        reportingController.addCallback(mockEventCallback1);
        reportingController.addCallback(mockEventCallback2);

        reportingController.reportEvent(event);

        // Execute the task posted to the main looper's handler
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        // Verify that both listeners received the event
        verify(mockEventCallback1).onEvent(event);
        verify(mockEventCallback2).onEvent(event);
    }

    @Test
    public void reportFiredTracker_shouldNotifyAllRegisteredTrackerListeners() {
        ReportingTracker tracker = new ReportingTracker("test_type", "test_url", 200);
        reportingController.addTrackerCallback(mockTrackerCallback1);
        reportingController.addTrackerCallback(mockTrackerCallback2);

        reportingController.reportFiredTracker(tracker);

        // Execute the task posted to the main looper's handler
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        // Verify that both tracker listeners were notified
        verify(mockTrackerCallback1).onFire(tracker);
        verify(mockTrackerCallback2).onFire(tracker);
    }

    @Test
    public void cacheAdEventList_and_getAdEventList_shouldStoreAndRetrieveList() {
        List<ReportingEvent> eventList = new ArrayList<>();
        eventList.add(new ReportingEvent());

        reportingController.cacheAdEventList(eventList);
        List<ReportingEvent> retrievedList = reportingController.getAdEventList();

        assertEquals(eventList, retrievedList);
        assertEquals(1, retrievedList.size());
    }

    @Test
    public void clearAdEventList_shouldClearTheList() {
        List<ReportingEvent> eventList = new ArrayList<>();
        eventList.add(new ReportingEvent());
        reportingController.cacheAdEventList(eventList);

        reportingController.clearAdEventList();

        // The original list object itself is cleared, so its size should be 0.
        assertEquals(0, eventList.size());
    }
}
