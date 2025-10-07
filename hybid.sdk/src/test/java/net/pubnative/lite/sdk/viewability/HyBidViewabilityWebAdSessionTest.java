// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.viewability;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.MockitoAnnotations.openMocks;

import android.webkit.WebView;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by shubhamkeshri on 19.09.25.
 */

@RunWith(RobolectricTestRunner.class)
public class HyBidViewabilityWebAdSessionTest {
    private HyBidViewabilityWebAdSession pubViewabilityWebAdSession;

    @Mock
    private HyBidViewabilityManager mockViewabilityManager;

    @Mock
    private WebView mockWebView;

    @Mock
    private Object mockAdSession;

    @Mock
    private Object mockAdSessionContext;

    @Mock
    private Object mockAdSessionConfiguration;

    @Mock
    private Object mockAdEvents;

    @Before
    public void setUp() {
        openMocks(this);
        pubViewabilityWebAdSession = new HyBidViewabilityWebAdSession(mockViewabilityManager);
    }

    @Test
    public void testInitAdSession_shouldSkipWhenViewabilityDisabled() {
        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(false);

        pubViewabilityWebAdSession.initAdSession(mockWebView, false);

        verify(mockViewabilityManager, never()).createHtmlAdSessionContext(any());
        verify(mockViewabilityManager, never()).createAdSession(any(), any());
    }

    @Test
    public void testInitAdSession_successfulFlow() {
        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(true);
        when(mockViewabilityManager.createHtmlAdSessionContext(mockWebView)).thenReturn(mockAdSessionContext);
        when(mockViewabilityManager.getWebAdSessionConfiguration(anyBoolean(), any())).thenReturn(mockAdSessionConfiguration);
        when(mockViewabilityManager.createAdSession(eq(mockAdSessionConfiguration), eq(mockAdSessionContext))).thenReturn(mockAdSession);
        when(mockViewabilityManager.createAdEvents(mockAdSession)).thenReturn(mockAdEvents);

        pubViewabilityWebAdSession.initAdSession(mockWebView, true);

        verify(mockViewabilityManager).createHtmlAdSessionContext(mockWebView);
        verify(mockViewabilityManager).getWebAdSessionConfiguration(eq(true), any());
        verify(mockViewabilityManager).createAdSession(eq(mockAdSessionConfiguration), eq(mockAdSessionContext));
        verify(mockViewabilityManager).registerAdView(eq(mockAdSession), eq(mockWebView));
        verify(mockViewabilityManager).createAdEvents(eq(mockAdSession));
        verify(mockViewabilityManager).startAdSession(eq(mockAdSession));
    }

    @Test
    public void testInitAdSession_catchesIllegalArgumentException() {
        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(true);
        when(mockViewabilityManager.createHtmlAdSessionContext(mockWebView))
                .thenThrow(new IllegalArgumentException("Invalid args"));

        pubViewabilityWebAdSession.initAdSession(mockWebView, false);

        verify(mockViewabilityManager, never()).createAdSession(any(), any());
    }

    @Test
    public void testInitAdSession_catchesNullPointerException() {
        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(true);
        when(mockViewabilityManager.createHtmlAdSessionContext(mockWebView))
                .thenThrow(new NullPointerException("Null error"));

        pubViewabilityWebAdSession.initAdSession(mockWebView, false);

        verify(mockViewabilityManager, never()).createAdSession(any(), any());
    }

    @Test
    public void testCreateAdEvents_shouldSkipWhenMeasurementDisabled() {
        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(false);

        pubViewabilityWebAdSession.createAdEvents();

        verify(mockViewabilityManager, never()).createAdEvents(any());
    }

    @Test
    public void testCreateAdEvents_shouldSkipWhenAdSessionNull() {
        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(true);

        // mAdSession is null by default
        pubViewabilityWebAdSession.createAdEvents();

        verify(mockViewabilityManager, never()).createAdEvents(any());
    }

    @Test
    public void testCreateAdEvents_successful() {
        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(true);

        //Set mAdSession via init flow
        when(mockViewabilityManager.createHtmlAdSessionContext(mockWebView)).thenReturn(mockAdSessionContext);
        when(mockViewabilityManager.getWebAdSessionConfiguration(anyBoolean(), any())).thenReturn(mockAdSessionConfiguration);
        when(mockViewabilityManager.createAdSession(any(), any())).thenReturn(mockAdSession);
        when(mockViewabilityManager.createAdEvents(mockAdSession)).thenReturn(mockAdEvents);

        pubViewabilityWebAdSession.initAdSession(mockWebView, false);
        pubViewabilityWebAdSession.createAdEvents();

        verify(mockViewabilityManager, times(2)).createAdEvents(eq(mockAdSession));
    }
}
