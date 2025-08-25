package net.pubnative.lite.sdk.analytics.tracker;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ReportingTrackerTest {

    @Test
    public void testUrlConstructor_setsFieldsCorrectly() {
        String testType = "url_tracker";
        String testUrl = "https://example.com/tracker";
        int testCode = 200;

        ReportingTracker tracker = new ReportingTracker(testType, testUrl, testCode);

        assertEquals(testType, tracker.getType());
        assertEquals(testUrl, tracker.getUrl());
        assertEquals(testCode, tracker.getResponseCode());
        assertEquals("", tracker.getJs()); // Verify default value
    }

    @Test
    public void testJsConstructor_setsFieldsCorrectly() {
        String testType = "js_tracker";
        String testJs = "<script>track()</script>";

        ReportingTracker tracker = new ReportingTracker(testType, testJs);

        assertEquals(testType, tracker.getType());
        assertEquals(testJs, tracker.getJs());
        assertEquals("", tracker.getUrl()); // Verify default value
        assertEquals(0, tracker.getResponseCode()); // Verify default value
    }
}
