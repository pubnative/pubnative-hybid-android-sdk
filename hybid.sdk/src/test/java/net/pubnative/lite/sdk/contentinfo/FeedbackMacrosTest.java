// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.contentinfo;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class FeedbackMacrosTest {

    private FeedbackMacros feedbackMacros;

    @Before
    public void setUp() {
        feedbackMacros = new FeedbackMacros();
    }

    @Test
    public void processUrl_withFullData_replacesAllMacros() {
        String urlTemplate = "https://example.com?token=[APPTOKEN]&zone=[ZONEID]&creative=[CREATIVEID]";
        AdFeedbackData data = new AdFeedbackData.Builder()
                .setAppToken("test_token")
                .setZoneId("test_zone")
                .setCreativeId("test_creative")
                .build();

        String actualUrl = feedbackMacros.processUrl(urlTemplate, data);

        String expectedUrl = "https://example.com?token=test_token&zone=test_zone&creative=test_creative";
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void processUrl_withPartialData_replacesOnlyAvailableMacros() {
        String urlTemplate = "https://example.com?token=[APPTOKEN]&zone=[ZONEID]&creative=[CREATIVEID]";
        AdFeedbackData data = new AdFeedbackData.Builder()
                .setAppToken("test_token")
                .setCreativeId("test_creative")
                // zoneId is not set
                .build();

        String actualUrl = feedbackMacros.processUrl(urlTemplate, data);

        String expectedUrl = "https://example.com?token=test_token&zone=[ZONEID]&creative=test_creative";
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void processUrl_withNullData_returnsOriginalUrl() {
        String urlTemplate = "https://example.com?token=[APPTOKEN]&zone=[ZONEID]";

        String actualUrl = feedbackMacros.processUrl(urlTemplate, null);

        assertEquals(urlTemplate, actualUrl);
    }
}
