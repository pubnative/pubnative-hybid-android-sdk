// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.viewability;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.view.View;

import net.pubnative.lite.sdk.viewability.baseom.MediaEventType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;

/**
 * Created by shubhamkeshri on 19.09.25.
 */

@RunWith(RobolectricTestRunner.class)
public class HyBidViewabilityNativeVideoAdSessionTest {

    private HyBidViewabilityNativeVideoAdSession adSession;
    private Object mockAdSession;
    private Object mockAdEvents;
    private Object mockMediaEvents;
    private Object mockNativeAdSessionContext;
    private Object mockAdSessionConfiguration;

    @Mock
    HyBidViewabilityManager mockViewabilityManager;

    @Before
    public void setUp() {
        openMocks(this);

        mockAdSession = new Object();
        mockAdEvents = new Object();
        mockMediaEvents = new Object();
        mockNativeAdSessionContext = new Object();
        mockAdSessionConfiguration = new Object();

        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(true);
        when(mockViewabilityManager.createAdSession(any(), any())).thenReturn(mockAdSession);
        when(mockViewabilityManager.createAdEvents(mockAdSession)).thenReturn(mockAdEvents);
        when(mockViewabilityManager.createMediaEvents(mockAdSession)).thenReturn(mockMediaEvents);
        when(mockViewabilityManager.createNativeAdSessionContext(any())).thenReturn(mockNativeAdSessionContext);
        when(mockViewabilityManager.getNativeAdSessionConfiguration()).thenReturn(mockAdSessionConfiguration);

        adSession = new HyBidViewabilityNativeVideoAdSession(mockViewabilityManager, -1);
    }

    @Test
    public void testInitAdSession_createsAdAndMediaEvents() {
        View mockView = mock(View.class);

        adSession.initAdSession(mockView, Collections.emptyList());

        verify(mockViewabilityManager).createNativeAdSessionContext(anyList());
        verify(mockViewabilityManager).getNativeAdSessionConfiguration();
        verify(mockViewabilityManager).createAdSession(any(), any());
        verify(mockViewabilityManager).registerAdView(eq(mockAdSession), eq(mockView));
        verify(mockViewabilityManager).startAdSession(eq(mockAdSession));
        verify(mockViewabilityManager).createAdEvents(mockAdSession);
        verify(mockViewabilityManager).createMediaEvents(mockAdSession);
    }

    @Test
    public void testFireLoaded_shouldFireEventProperties() {
        adSession.initAdSession(mock(View.class), Collections.emptyList());

        Object vastProps = new Object();
        when(mockViewabilityManager.createVastPropertiesForNonSkippableMedia()).thenReturn(vastProps);

        adSession.fireLoaded();

        verify(mockViewabilityManager).fireEventProperties(eq(mockAdEvents), eq(vastProps));
    }

    @Test
    public void testFireStart_shouldFireMediaEvents_onlyOnce() {
        adSession.initAdSession(mock(View.class), Collections.emptyList());

        adSession.fireStart(10f, true);
        adSession.fireStart(10f, true);

        verify(mockViewabilityManager, times(1))
                .fireMediaEventStart(eq(mockMediaEvents), eq(10f), eq(0.0f));
    }

    @Test
    public void testFireFirstQuartile_shouldFireMediaEvents_onlyOnce() {
        adSession.initAdSession(mock(View.class), Collections.emptyList());

        adSession.fireFirstQuartile();
        adSession.fireFirstQuartile();

        verify(mockViewabilityManager, times(1))
                .fireMediaEvents(MediaEventType.FIRST_QUARTILE, mockMediaEvents);
    }

    @Test
    public void testFireMidpoint_shouldFireMediaEvents_onlyOnce() {
        adSession.initAdSession(mock(View.class), Collections.emptyList());

        adSession.fireMidpoint();
        adSession.fireMidpoint();

        verify(mockViewabilityManager, times(1))
                .fireMediaEvents(MediaEventType.MIDPOINT, mockMediaEvents);
    }

    @Test
    public void testFireThirdQuartile_shouldFireMediaEvents_onlyOnce() {
        adSession.initAdSession(mock(View.class), Collections.emptyList());

        adSession.fireThirdQuartile();
        adSession.fireThirdQuartile();

        verify(mockViewabilityManager, times(1))
                .fireMediaEvents(MediaEventType.THIRD_QUARTILE, mockMediaEvents);
    }

    @Test
    public void testFireComplete_shouldFireMediaEvents_onlyOnce() {
        adSession.initAdSession(mock(View.class), Collections.emptyList());

        adSession.fireComplete();
        adSession.fireComplete();

        verify(mockViewabilityManager, times(1))
                .fireMediaEvents(MediaEventType.COMPLETE, mockMediaEvents);
    }

    @Test
    public void testFirePauseResume_shouldFireMediaEvents() {
        adSession.initAdSession(mock(View.class), Collections.emptyList());

        adSession.firePause();
        adSession.fireResume();

        verify(mockViewabilityManager).fireMediaEvents(MediaEventType.PAUSE, mockMediaEvents);
        verify(mockViewabilityManager).fireMediaEvents(MediaEventType.RESUME, mockMediaEvents);
    }

    @Test
    public void testFireBufferingEvents_shouldFireMediaEvents() {
        adSession.initAdSession(mock(View.class), Collections.emptyList());

        adSession.fireBufferStart();
        adSession.fireBufferFinish();

        verify(mockViewabilityManager).fireMediaEvents(MediaEventType.BUFFER_START, mockMediaEvents);
        verify(mockViewabilityManager).fireMediaEvents(MediaEventType.BUFFER_FINISH, mockMediaEvents);
    }

    @Test
    public void testFireVolumeChange_shouldFireMediaEventVolumeChange_onlyIfChanged() {
        adSession.initAdSession(mock(View.class), Collections.emptyList());

        adSession.fireVolumeChange(false); // change from default muted=true
        adSession.fireVolumeChange(false); // no change, should not fire again

        verify(mockViewabilityManager, times(1))
                .fireMediaEventVolumeChange(eq(mockMediaEvents), eq(1.0f));

        adSession.fireVolumeChange(true); // status Changed, should fire again
        verify(mockViewabilityManager, times(1))
                .fireMediaEventVolumeChange(eq(mockMediaEvents), eq(1.0f));
    }

    @Test
    public void testFireSkippedAndClick_shouldFireMediaEvents() {
        adSession.initAdSession(mock(View.class), Collections.emptyList());

        adSession.fireSkipped();
        adSession.fireClick();

        verify(mockViewabilityManager).fireMediaEvents(MediaEventType.SKIPPED, mockMediaEvents);
        verify(mockViewabilityManager).fireMediaEvents(MediaEventType.CLICK, mockMediaEvents);
    }

    @Test
    public void testMethods_shouldSkipExecution_whenViewabilityMeasurementDisabled() {
        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(false);

        //calling HyBidViewabilityNativeVideoAdSession's method to interact with ViewabilityManager.
        adSession.initAdSession(mock(View.class), Collections.emptyList());
        adSession.createAdEvents();
        adSession.createMediaEvents();
        adSession.fireLoaded();
        adSession.fireStart(10f, true);
        adSession.fireFirstQuartile();
        adSession.fireMidpoint();
        adSession.fireThirdQuartile();
        adSession.fireComplete();
        adSession.firePause();
        adSession.fireResume();
        adSession.fireBufferStart();
        adSession.fireBufferFinish();
        adSession.fireVolumeChange(true);
        adSession.fireSkipped();
        adSession.fireClick();

        // Only `isViewabilityMeasurementEnabled` is called, nothing else
        verify(mockViewabilityManager, atLeastOnce()).isViewabilityMeasurementEnabled();

        // No Event calls possible since ViewabilityMeasurement is Disabled
        verify(mockViewabilityManager, never()).createAdSession(any(), any());
        verify(mockViewabilityManager, never()).registerAdView(any(), any());
        verify(mockViewabilityManager, never()).startAdSession(any());
        verify(mockViewabilityManager, never()).createAdEvents(any());
        verify(mockViewabilityManager, never()).createMediaEvents(any());
        verify(mockViewabilityManager, never()).fireMediaEvents(any(), any());
        verify(mockViewabilityManager, never()).fireEventProperties(any(), any());

        verifyNoMoreInteractions(mockViewabilityManager);
    }

    @Test
    public void testMethods_shouldSkipExecution_whenViewabilityManagerIsNull() {
        HyBidViewabilityNativeVideoAdSession sessionWithNullManager =
                new HyBidViewabilityNativeVideoAdSession(null, -1);

        sessionWithNullManager.fireStart(10f, true);
        sessionWithNullManager.fireClick();
        sessionWithNullManager.fireComplete();

        // No Event calls possible since manager is null
        verifyNoMoreInteractions(mockViewabilityManager);
    }
}