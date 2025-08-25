// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.bidstream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class GeoLocationTest {

    @Test
    public void constructor_withParameters_assignsFieldsCorrectly() {
        Integer accuracy = 15;
        Integer utcoffset = 3600;

        GeoLocation geoLocation = new GeoLocation(accuracy, utcoffset);

        assertEquals(accuracy, geoLocation.accuracy);
        assertEquals(utcoffset, geoLocation.utcoffset);
    }

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        GeoLocation geoLocation = new GeoLocation();

        assertNull(geoLocation.accuracy);
        assertNull(geoLocation.utcoffset);
    }
}
