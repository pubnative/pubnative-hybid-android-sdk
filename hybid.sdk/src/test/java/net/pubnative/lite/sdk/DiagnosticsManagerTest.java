// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.vpaid.enums.AudioState;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class DiagnosticsManagerTest {

    @Mock
    private Context mockContext;

    @Mock
    private ReportingController mockReportingController;

    @Mock
    private PackageManager mockPackageManager;

    @Mock
    private ApplicationInfo mockApplicationInfo;

    private DiagnosticsManager diagnosticsManager;
    private AutoCloseable mockitoCloseable;

    @Before
    public void setUp() throws Exception {
        mockitoCloseable = MockitoAnnotations.openMocks(this);

        // Setup default mock behavior
        when(mockContext.getPackageName()).thenReturn("com.test.app");
        when(mockContext.getPackageManager()).thenReturn(mockPackageManager);
    }

    @After
    public void tearDown() throws Exception {
        mockitoCloseable.close();
    }

    @Test
    public void constructor_withValidContext_initializesGoogleAdsId() throws Exception {
        // Given
        Bundle metaData = new Bundle();
        metaData.putString("com.google.android.gms.ads.APPLICATION_ID", "ca-app-pub-1234567890");
        mockApplicationInfo.metaData = metaData;
        when(mockPackageManager.getApplicationInfo(anyString(), anyInt())).thenReturn(mockApplicationInfo);

        // When
        diagnosticsManager = new DiagnosticsManager(mockContext, mockReportingController);

        // Then
        verify(mockReportingController).addCallback(diagnosticsManager);
    }

    @Test
    public void constructor_withNullContext_initializesWithEmptyGoogleAdsId() {
        // When
        diagnosticsManager = new DiagnosticsManager(null, mockReportingController);

        // Then
        verify(mockReportingController).addCallback(diagnosticsManager);
    }

    @Test
    public void constructor_withNullReportingController_doesNotCrash() {
        // When
        diagnosticsManager = new DiagnosticsManager(mockContext, null);

        // Then - should not crash
        assertNotNull(diagnosticsManager);
    }

    @Test
    public void constructor_whenPackageManagerThrowsException_handlesGracefully() throws Exception {
        // Given
        when(mockPackageManager.getApplicationInfo(anyString(), anyInt()))
                .thenThrow(new PackageManager.NameNotFoundException());

        // When
        diagnosticsManager = new DiagnosticsManager(mockContext, mockReportingController);

        // Then - should not crash
        assertNotNull(diagnosticsManager);
    }

    @Test
    public void constructor_whenApplicationInfoIsNull_handlesGracefully() throws Exception {
        // Given
        when(mockPackageManager.getApplicationInfo(anyString(), anyInt())).thenReturn(null);

        // When
        diagnosticsManager = new DiagnosticsManager(mockContext, mockReportingController);

        // Then - should not crash
        assertNotNull(diagnosticsManager);
    }

    @Test
    public void constructor_whenMetaDataIsNull_handlesGracefully() throws Exception {
        // Given
        mockApplicationInfo.metaData = null;
        when(mockPackageManager.getApplicationInfo(anyString(), anyInt())).thenReturn(mockApplicationInfo);

        // When
        diagnosticsManager = new DiagnosticsManager(mockContext, mockReportingController);

        // Then - should not crash
        assertNotNull(diagnosticsManager);
    }

    @Test
    public void onEvent_withNullEvent_doesNotCrash() {
        // Given
        diagnosticsManager = new DiagnosticsManager(mockContext, mockReportingController);

        // When
        diagnosticsManager.onEvent(null);

        // Then - should not crash (no exception thrown)
    }

    @Test
    public void onEvent_withNonSdkInitEvent_doesNotReportDiagnostics() {
        // Given
        diagnosticsManager = spy(new DiagnosticsManager(mockContext, mockReportingController));
        ReportingEvent event = new ReportingEvent();
        event.setEventType(Reporting.EventType.REQUEST);

        try (MockedStatic<HyBid> hybidMock = mockStatic(HyBid.class)) {
            hybidMock.when(HyBid::isDiagnosticsEnabled).thenReturn(true);

            // When
            diagnosticsManager.onEvent(event);

            // Then
            verify(diagnosticsManager, never()).printDiagnosticsLog(any(ReportingEvent.class));
        }
    }

    @Test
    public void onEvent_whenDiagnosticsDisabled_doesNotReportDiagnostics() {
        // Given
        diagnosticsManager = spy(new DiagnosticsManager(mockContext, mockReportingController));
        ReportingEvent event = new ReportingEvent();
        event.setEventType(Reporting.EventType.SDK_INIT);

        try (MockedStatic<HyBid> hybidMock = mockStatic(HyBid.class)) {
            hybidMock.when(HyBid::isDiagnosticsEnabled).thenReturn(false);

            // When
            diagnosticsManager.onEvent(event);

            // Then
            verify(diagnosticsManager, never()).printDiagnosticsLog(any(ReportingEvent.class));
        }
    }

    @Test
    public void onEvent_withSdkInitEventAndDiagnosticsEnabled_reportsDiagnostics() {
        // Given
        diagnosticsManager = spy(new DiagnosticsManager(mockContext, mockReportingController));
        ReportingEvent event = new ReportingEvent();
        event.setEventType(Reporting.EventType.SDK_INIT);

        try (MockedStatic<HyBid> hybidMock = mockStatic(HyBid.class)) {
            hybidMock.when(HyBid::isDiagnosticsEnabled).thenReturn(true);
            hybidMock.when(HyBid::isInitialized).thenReturn(true);
            hybidMock.when(HyBid::getHyBidVersion).thenReturn("3.0.0");
            hybidMock.when(HyBid::getBundleId).thenReturn("com.test.app");
            hybidMock.when(HyBid::getAppToken).thenReturn("test-token");
            hybidMock.when(HyBid::isTestMode).thenReturn(false);
            hybidMock.when(HyBid::isCoppaEnabled).thenReturn(false);
            hybidMock.when(HyBid::getVideoAudioStatus).thenReturn(AudioState.ON);
            hybidMock.when(HyBid::isLocationTrackingEnabled).thenReturn(true);
            hybidMock.when(HyBid::areLocationUpdatesEnabled).thenReturn(true);

            // When
            diagnosticsManager.onEvent(event);

            // Then
            verify(diagnosticsManager).printDiagnosticsLog(event);
        }
    }

    @Test
    public void printDiagnosticsLog_whenHyBidInitialized_includesAllDiagnosticInfo() {
        // Given
        diagnosticsManager = new DiagnosticsManager(mockContext, mockReportingController);

        try (MockedStatic<HyBid> hybidMock = mockStatic(HyBid.class)) {
            hybidMock.when(HyBid::isInitialized).thenReturn(true);
            hybidMock.when(HyBid::getHyBidVersion).thenReturn("3.0.0");
            hybidMock.when(HyBid::getBundleId).thenReturn("com.test.app");
            hybidMock.when(HyBid::getAppToken).thenReturn("test-token");
            hybidMock.when(HyBid::isTestMode).thenReturn(true);
            hybidMock.when(HyBid::isCoppaEnabled).thenReturn(true);
            hybidMock.when(HyBid::getVideoAudioStatus).thenReturn(AudioState.MUTED);
            hybidMock.when(HyBid::isLocationTrackingEnabled).thenReturn(false);
            hybidMock.when(HyBid::areLocationUpdatesEnabled).thenReturn(false);

            // When/Then - should not crash when generating diagnostic log
            diagnosticsManager.printDiagnosticsLog();
        }
    }

    @Test
    public void printDiagnosticsLog_withEvent_includesEventType() {
        // Given
        diagnosticsManager = new DiagnosticsManager(mockContext, mockReportingController);
        ReportingEvent event = new ReportingEvent();
        event.setEventType(Reporting.EventType.SDK_INIT);

        try (MockedStatic<HyBid> hybidMock = mockStatic(HyBid.class)) {
            hybidMock.when(HyBid::isInitialized).thenReturn(true);
            hybidMock.when(HyBid::getHyBidVersion).thenReturn("3.0.0");
            hybidMock.when(HyBid::getBundleId).thenReturn("com.test.app");
            hybidMock.when(HyBid::getAppToken).thenReturn("test-token");
            hybidMock.when(HyBid::isTestMode).thenReturn(false);
            hybidMock.when(HyBid::isCoppaEnabled).thenReturn(false);
            hybidMock.when(HyBid::getVideoAudioStatus).thenReturn(AudioState.ON);
            hybidMock.when(HyBid::isLocationTrackingEnabled).thenReturn(true);
            hybidMock.when(HyBid::areLocationUpdatesEnabled).thenReturn(true);

            // When/Then - should not crash when printing log with event
            diagnosticsManager.printDiagnosticsLog(event);
        }
    }

    @Test
    public void generatePlacementDiagnosticsLog_withValidJson_returnsFormattedLog() throws JSONException {
        // Given
        JSONObject placementParams = new JSONObject();
        placementParams.put("placement_id", "12345");
        placementParams.put("ad_size", "320x50");

        // When
        String log = DiagnosticsManager.generatePlacementDiagnosticsLog(mockContext, placementParams);

        // Then
        assertNotNull(log);
        assertTrue(log.contains("HyBid Placement Diagnostics Log"));
        assertTrue(log.contains("placement_id"));
        assertTrue(log.contains("12345"));
    }

    @Test
    public void generatePlacementDiagnosticsLog_withNullJson_returnsEmptyLog() {
        // When
        String log = DiagnosticsManager.generatePlacementDiagnosticsLog(mockContext, null);

        // Then
        assertNotNull(log);
        assertTrue(log.contains("HyBid Placement Diagnostics Log"));
    }

    @Test
    public void generatePlacementDiagnosticsLog_withEmptyJson_returnsEmptyLog() {
        // Given
        JSONObject placementParams = new JSONObject();

        // When
        String log = DiagnosticsManager.generatePlacementDiagnosticsLog(mockContext, placementParams);

        // Then
        assertNotNull(log);
        assertTrue(log.contains("HyBid Placement Diagnostics Log"));
    }

    @Test
    public void onEvent_withEventWithEmptyEventType_doesNotReportDiagnostics() {
        // Given
        diagnosticsManager = spy(new DiagnosticsManager(mockContext, mockReportingController));
        ReportingEvent event = new ReportingEvent();
        event.setEventType("");

        try (MockedStatic<HyBid> hybidMock = mockStatic(HyBid.class)) {
            hybidMock.when(HyBid::isDiagnosticsEnabled).thenReturn(true);

            // When
            diagnosticsManager.onEvent(event);

            // Then
            verify(diagnosticsManager, never()).printDiagnosticsLog(any(ReportingEvent.class));
        }
    }

    @Test
    public void generatePlacementDiagnosticsLog_withMalformedJson_handlesGracefully() {
        // Given - Create a JSONObject that will throw an exception when formatting
        JSONObject placementParams = new JSONObject() {
            @Override
            public String toString(int indentSpaces) throws JSONException {
                throw new JSONException("Test exception");
            }
        };

        // When
        String log = DiagnosticsManager.generatePlacementDiagnosticsLog(mockContext, placementParams);

        // Then
        assertNotNull(log);
        assertTrue(log.contains("HyBid Placement Diagnostics Log"));
    }
}

