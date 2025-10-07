// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.browser;

import android.view.View;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 26)
public class DoubleClickPreventionListenerTest {

    @Mock
    private View mockView;

    @Mock
    private DoubleClickPreventionListener.TimeProvider mockTimeProvider;

    /**
     * A concrete implementation of the abstract listener for testing purposes.
     * This allows us to track how many times processClick() has been called.
     */
    private static class TestListener extends DoubleClickPreventionListener {
        int processClickCount = 0;

        // Default constructor for testing without time provider
        public TestListener() {
            super();
        }

        // Constructor with custom time provider for testing
        public TestListener(TimeProvider timeProvider) {
            super(timeProvider);
        }

        @Override
        protected void processClick() {
            processClickCount++;
        }
    }

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void onClick_whenCalledFirstTime_shouldProcessClick() {
        // Test that the first click is processed immediately.
        TestListener listener = new TestListener(mockTimeProvider);

        when(mockTimeProvider.getCurrentTime()).thenReturn(1000L);

        listener.onClick(mockView);

        assertEquals(1, listener.processClickCount);
    }

    @Test
    public void onClick_whenCalledTooSoon_shouldNotProcessClick() {
        // Test that a rapid second click is ignored.
        TestListener listener = new TestListener(mockTimeProvider);

        // First click happens at time 1000ms
        when(mockTimeProvider.getCurrentTime()).thenReturn(1000L);
        listener.onClick(mockView);

        // Second click at 1500ms (500ms later, less than 1000ms interval)
        when(mockTimeProvider.getCurrentTime()).thenReturn(1500L);
        listener.onClick(mockView);

        assertEquals(1, listener.processClickCount);
    }

    @Test
    public void onClick_whenCalledAfterSufficientDelay_shouldProcessClickAgain() {
        TestListener listener = new TestListener(mockTimeProvider);

        // First click at time 1000ms
        when(mockTimeProvider.getCurrentTime()).thenReturn(1000L);
        listener.onClick(mockView);

        // Second click at 2001ms (1001ms later, more than 1000ms interval)
        when(mockTimeProvider.getCurrentTime()).thenReturn(2001L);
        listener.onClick(mockView);

        assertEquals(2, listener.processClickCount);
    }

    @Test
    public void onClick_whenCalledExactlyAtInterval_shouldProcessClickAgain() {
        // Test the boundary condition at exactly 1000ms
        TestListener listener = new TestListener(mockTimeProvider);

        // First click at time 1000ms
        when(mockTimeProvider.getCurrentTime()).thenReturn(1000L);
        listener.onClick(mockView);

        // Second click at 2000ms (exactly 1000ms later)
        when(mockTimeProvider.getCurrentTime()).thenReturn(2000L);
        listener.onClick(mockView);

        assertEquals(2, listener.processClickCount);
    }

    @Test
    public void onClick_multipleRapidClicks_shouldOnlyProcessFirst() {
        // Test multiple rapid clicks within the interval
        TestListener listener = new TestListener(mockTimeProvider);

        // First click at time 1000ms
        when(mockTimeProvider.getCurrentTime()).thenReturn(1000L);
        listener.onClick(mockView);

        // Multiple rapid clicks within the interval
        when(mockTimeProvider.getCurrentTime()).thenReturn(1100L);
        listener.onClick(mockView);

        when(mockTimeProvider.getCurrentTime()).thenReturn(1200L);
        listener.onClick(mockView);

        when(mockTimeProvider.getCurrentTime()).thenReturn(1300L);
        listener.onClick(mockView);

        // Only the first click should be processed
        assertEquals(1, listener.processClickCount);
    }

    @Test
    public void onClick_alternatingValidAndInvalidClicks_shouldProcessOnlyValid() {
        // Test alternating pattern of valid and invalid clicks
        TestListener listener = new TestListener(mockTimeProvider);

        // First click at time 1000ms
        when(mockTimeProvider.getCurrentTime()).thenReturn(1000L);
        listener.onClick(mockView);

        // Rapid click at 1500ms (should be ignored)
        when(mockTimeProvider.getCurrentTime()).thenReturn(1500L);
        listener.onClick(mockView);

        // Valid click at 2001ms (after sufficient delay)
        when(mockTimeProvider.getCurrentTime()).thenReturn(2001L);
        listener.onClick(mockView);

        // Another rapid click at 2200ms (should be ignored)
        when(mockTimeProvider.getCurrentTime()).thenReturn(2200L);
        listener.onClick(mockView);

        // Another valid click at 3002ms
        when(mockTimeProvider.getCurrentTime()).thenReturn(3002L);
        listener.onClick(mockView);

        // Should have processed 3 clicks total
        assertEquals(3, listener.processClickCount);
    }

    @Test
    public void onClick_withNullView_shouldStillProcessClick() {
        // Test that null view parameter doesn't break the functionality
        TestListener listener = new TestListener(mockTimeProvider);

        when(mockTimeProvider.getCurrentTime()).thenReturn(1000L);

        listener.onClick(null);

        assertEquals(1, listener.processClickCount);
    }

    @Test
    public void onClick_boundaryCondition_exactlyAtThreshold_shouldAllowClick() {
        // Test boundary condition where time difference equals MIN_CLICK_INTERVAL_MS
        TestListener listener = new TestListener(mockTimeProvider);

        // First click at time 5000ms
        when(mockTimeProvider.getCurrentTime()).thenReturn(5000L);
        listener.onClick(mockView);

        // Second click at exactly 6000ms (1000ms later - should be allowed)
        when(mockTimeProvider.getCurrentTime()).thenReturn(6000L);
        listener.onClick(mockView);

        assertEquals(2, listener.processClickCount);
    }
}