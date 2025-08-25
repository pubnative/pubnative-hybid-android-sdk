// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class GeoTest {

    private Geo geo;

    @Before
    public void setUp() {
        geo = new Geo();
    }

    @Test
    public void testSettersAndGetters() {
        Float latitude = 52.29f;
        Float longitude = 7.42f;
        String country = "DE";
        String city = "Rheine";

        geo.setLat(latitude);
        geo.setLon(longitude);
        geo.setCountry(country);
        geo.setCity(city);

        assertEquals(latitude, geo.getLat());
        assertEquals(longitude, geo.getLon());
        assertEquals(country, geo.getCountry());
        assertEquals(city, geo.getCity());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that an object serialized to JSON
        // can be deserialized back into an identical object.

        // 1. Create and populate the original object
        Geo originalGeo = new Geo();
        originalGeo.setLat(52.29f);
        originalGeo.setLon(7.42f);
        originalGeo.setType(1);
        originalGeo.setAccuracy(10);
        originalGeo.setCountry("DE");
        originalGeo.setRegion("NW");
        originalGeo.setCity("Rheine");
        originalGeo.setZip("48431");
        originalGeo.setUtcoffset(120);

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalGeo.toJson();
        assertNotNull(jsonObject);
        assertEquals("DE", jsonObject.getString("country"));
        assertEquals(10, jsonObject.getInt("accuracy"));

        // 3. Convert the JSON back into a new object
        Geo restoredGeo = new Geo(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalGeo.getLat(), restoredGeo.getLat());
        assertEquals(originalGeo.getLon(), restoredGeo.getLon());
        assertEquals(originalGeo.getType(), restoredGeo.getType());
        assertEquals(originalGeo.getAccuracy(), restoredGeo.getAccuracy());
        assertEquals(originalGeo.getCountry(), restoredGeo.getCountry());
        assertEquals(originalGeo.getRegion(), restoredGeo.getRegion());
        assertEquals(originalGeo.getCity(), restoredGeo.getCity());
        assertEquals(originalGeo.getZip(), restoredGeo.getZip());
        assertEquals(originalGeo.getUtcoffset(), restoredGeo.getUtcoffset());
    }
}
