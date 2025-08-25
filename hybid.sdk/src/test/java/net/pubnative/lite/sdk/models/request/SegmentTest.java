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
public class SegmentTest {

    private Segment segment;

    @Before
    public void setUp() {
        segment = new Segment();
    }

    @Test
    public void testSettersAndGetters() {
        String id = "seg-123";
        String name = "Test Segment";
        String value = "segment_value";
        String signal = "segment_signal";

        segment.setId(id);
        segment.setName(name);
        segment.setValue(value);
        segment.setSignal(signal);

        assertEquals(id, segment.getId());
        assertEquals(name, segment.getName());
        assertEquals(value, segment.getValue());
        assertEquals(signal, segment.getSignal());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that an object serialized to JSON
        // can be deserialized back into an identical object.

        // 1. Create and populate the original object
        Segment originalSegment = new Segment();
        originalSegment.setId("seg-abc");
        originalSegment.setName("Original Segment");
        originalSegment.setValue("original_value");
        originalSegment.setSignal("original_signal");

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalSegment.toJson();
        assertNotNull(jsonObject);
        assertEquals("seg-abc", jsonObject.getString("id"));
        assertEquals("original_value", jsonObject.getString("value"));

        // 3. Convert the JSON back into a new object
        Segment restoredSegment = new Segment(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalSegment.getId(), restoredSegment.getId());
        assertEquals(originalSegment.getName(), restoredSegment.getName());
        assertEquals(originalSegment.getValue(), restoredSegment.getValue());
        assertEquals(originalSegment.getSignal(), restoredSegment.getSignal());
    }
}
