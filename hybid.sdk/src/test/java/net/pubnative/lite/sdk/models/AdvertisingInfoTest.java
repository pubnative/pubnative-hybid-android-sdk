// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AdvertisingInfoTest {

    @Test
    public void constructor_whenTrackingIsEnabled_setsFieldsCorrectly() {
        String adId = "test-ad-id-123";
        Boolean limitTracking = false;

        AdvertisingInfo adInfo = new AdvertisingInfo(adId, limitTracking);

        assertEquals(adId, adInfo.getAdvertisingId());
        assertFalse(adInfo.isLimitTrackingEnabled());
    }

    @Test
    public void constructor_whenTrackingIsLimited_setsFieldsCorrectly() {
        String adId = "another-ad-id-456";
        Boolean limitTracking = true;

        AdvertisingInfo adInfo = new AdvertisingInfo(adId, limitTracking);

        assertEquals(adId, adInfo.getAdvertisingId());
        assertTrue(adInfo.isLimitTrackingEnabled());
    }
}
