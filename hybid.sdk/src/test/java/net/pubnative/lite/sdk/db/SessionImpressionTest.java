// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.db;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

@RunWith(RobolectricTestRunner.class)
public class SessionImpressionTest {

    @Test
    public void testGettersAndSetters() {
        SessionImpression impression = new SessionImpression();

        long timestamp = System.currentTimeMillis();
        long ageOfApp = 123456789L;
        String zoneId = "test-zone-1";
        long sessionDuration = 3000L;
        int count = 5;

        impression.setTimestamp(timestamp);
        impression.setAgeOfApp(ageOfApp);
        impression.setZoneId(zoneId);
        impression.setSessionDuration(sessionDuration);
        impression.setCount(count);

        assertEquals(Long.valueOf(timestamp), impression.getTimestamp());
        assertEquals(Long.valueOf(ageOfApp), impression.getAgeOfApp());
        assertEquals(zoneId, impression.getZoneId());
        assertEquals(Long.valueOf(sessionDuration), impression.getSessionDuration());
        assertEquals(Integer.valueOf(count), impression.getCount());
    }

    @Test
    public void getCount_whenCountIsNull_returnsZero() {
        SessionImpression impression = new SessionImpression();
        impression.setCount(null); // Explicitly set to null

        Integer expected = 0;
        Integer actual = impression.getCount();

        assertEquals(expected, actual);
    }

    @Test
    public void constructor_withValidJson_populatesAllFields() throws JSONException {
        long timestamp = System.currentTimeMillis();
        long ageOfApp = 123456789L;
        String zoneId = "test-zone-1";
        long sessionDuration = 3000L;
        int count = 10;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("timestamp", timestamp);
        jsonObject.put("age_of_app", ageOfApp);
        jsonObject.put("zone_id", zoneId);
        jsonObject.put("session_duration", sessionDuration);
        jsonObject.put("count", count);

        SessionImpression impression = new SessionImpression(jsonObject);

        assertEquals(Long.valueOf(timestamp), impression.getTimestamp());
        assertEquals(Long.valueOf(ageOfApp), impression.getAgeOfApp());
        assertEquals(zoneId, impression.getZoneId());
        assertEquals(Long.valueOf(sessionDuration), impression.getSessionDuration());
        assertEquals(Integer.valueOf(count), impression.getCount());
    }

    @Test
    public void constructor_withInvalidJsonType_throwsRuntimeException() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        // Put a String where a Long is expected
        jsonObject.put("timestamp", "not-a-long");

        // Assert that creating the object throws a RuntimeException, as per the constructor's contract
        assertThrows(RuntimeException.class, () -> new SessionImpression(jsonObject));
    }

    @Test
    public void toJson_withPopulatedObject_createsCorrectJson() throws Exception {
        SessionImpression impression = new SessionImpression();

        long timestamp = 1672531200000L; // Jan 1, 2023
        long ageOfApp = 987654321L;
        String zoneId = "test-zone-json";
        long sessionDuration = 5000L;
        int count = 20;

        impression.setTimestamp(timestamp);
        impression.setAgeOfApp(ageOfApp);
        impression.setZoneId(zoneId);
        impression.setSessionDuration(sessionDuration);
        impression.setCount(count);

        JSONObject jsonResult = impression.toJson();

        assertNotNull(jsonResult);
        assertEquals(timestamp, jsonResult.getLong("timestamp"));
        assertEquals(ageOfApp, jsonResult.getLong("age_of_app"));
        assertEquals(zoneId, jsonResult.getString("zone_id"));
        assertEquals(sessionDuration, jsonResult.getLong("session_duration"));
        assertEquals(count, jsonResult.getInt("count"));
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This "round-trip" test ensures that an object serialized to JSON
        // can be deserialized back into an identical object.

        SessionImpression originalImpression = new SessionImpression();
        originalImpression.setTimestamp(System.currentTimeMillis());
        originalImpression.setAgeOfApp(55555L);
        originalImpression.setZoneId("round-trip-zone");
        originalImpression.setSessionDuration(9999L);
        originalImpression.setCount(42);

        // 1. Convert the original object to JSON
        JSONObject jsonObject = originalImpression.toJson();

        // 2. Convert the JSON back into a new object
        SessionImpression newImpression = new SessionImpression(jsonObject);

        // 3. Assert that the new object is identical to the original
        assertEquals(originalImpression.getTimestamp(), newImpression.getTimestamp());
        assertEquals(originalImpression.getAgeOfApp(), newImpression.getAgeOfApp());
        assertEquals(originalImpression.getZoneId(), newImpression.getZoneId());
        assertEquals(originalImpression.getSessionDuration(), newImpression.getSessionDuration());
        assertEquals(originalImpression.getCount(), newImpression.getCount());
    }
}
