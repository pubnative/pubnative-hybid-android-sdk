// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.contentinfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AdFeedbackDataTest {

    @Test
    public void build_withAllFieldsSet_createsObjectWithCorrectData() {
        String appToken = "test_app_token";
        String zoneId = "test_zone_id";
        String audioState = "on";
        String appVersion = "1.0.0";
        String deviceInfo = "Pixel 5";
        String creativeId = "test_creative_id";
        String impressionBeacon = "https://impression.beacon";
        String sdkVersion = "2.14.0";
        String integrationType = "standalone";
        String adFormat = "banner";
        String hasEndCard = "true";
        String creative = "<html/>";

        AdFeedbackData feedbackData = new AdFeedbackData.Builder()
                .setAppToken(appToken)
                .setZoneId(zoneId)
                .setAudioState(audioState)
                .setAppVersion(appVersion)
                .setDeviceInfo(deviceInfo)
                .setCreativeId(creativeId)
                .setImpressionBeacon(impressionBeacon)
                .setSdkVersion(sdkVersion)
                .setIntegrationType(integrationType)
                .setAdFormat(adFormat)
                .setHasEndCard(hasEndCard)
                .setCreative(creative)
                .build();

        assertEquals(appToken, feedbackData.getAppToken());
        assertEquals(zoneId, feedbackData.getZoneId());
        assertEquals(audioState, feedbackData.getAudioState());
        assertEquals(appVersion, feedbackData.getAppVersion());
        assertEquals(deviceInfo, feedbackData.getDeviceInfo());
        assertEquals(creativeId, feedbackData.getCreativeId());
        assertEquals(impressionBeacon, feedbackData.getImpressionBeacon());
        assertEquals(sdkVersion, feedbackData.getSdkVersion());
        assertEquals(integrationType, feedbackData.getIntegrationType());
        assertEquals(adFormat, feedbackData.getAdFormat());
        assertEquals(hasEndCard, feedbackData.getHasEndCard());
        assertEquals(creative, feedbackData.getCreative());
    }

    @Test
    public void build_withPartialFieldsSet_setsNullForUnsetFields() {
        String appToken = "test_app_token";
        String zoneId = "test_zone_id";

        AdFeedbackData feedbackData = new AdFeedbackData.Builder()
                .setAppToken(appToken)
                .setZoneId(zoneId)
                .build();

        assertEquals(appToken, feedbackData.getAppToken());
        assertEquals(zoneId, feedbackData.getZoneId());
        assertNull(feedbackData.getAudioState());
        assertNull(feedbackData.getAppVersion());
        assertNull(feedbackData.getDeviceInfo());
        assertNull(feedbackData.getCreativeId());
        assertNull(feedbackData.getImpressionBeacon());
        assertNull(feedbackData.getSdkVersion());
        assertNull(feedbackData.getIntegrationType());
        assertNull(feedbackData.getAdFormat());
        assertNull(feedbackData.getHasEndCard());
        assertNull(feedbackData.getCreative());
    }
}