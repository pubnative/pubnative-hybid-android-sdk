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
public class FormatTest {

    private Format format;

    @Before
    public void setUp() {
        format = new Format();
    }

    @Test
    public void testSettersAndGetters() {
        Integer width = 320;
        Integer height = 50;
        Integer widthRatio = 16;
        Integer heightRatio = 9;
        Integer widthMin = 300;

        format.setWidth(width);
        format.setHeight(height);
        format.setWidthRatio(widthRatio);
        format.setHeightRatio(heightRatio);
        format.setWidthMin(widthMin);

        assertEquals(width, format.getWidth());
        assertEquals(height, format.getHeight());
        assertEquals(widthRatio, format.getWidthRatio());
        assertEquals(heightRatio, format.getHeightRatio());
        assertEquals(widthMin, format.getWidthMin());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that an object serialized to JSON
        // can be deserialized back into an identical object.

        // 1. Create and populate the original object
        Format originalFormat = new Format();
        originalFormat.setWidth(728);
        originalFormat.setHeight(90);
        originalFormat.setWidthRatio(8);
        originalFormat.setHeightRatio(1);
        originalFormat.setWidthMin(600);

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalFormat.toJson();
        assertNotNull(jsonObject);
        assertEquals(728, jsonObject.getInt("w"));
        assertEquals(90, jsonObject.getInt("h"));

        // 3. Convert the JSON back into a new object
        Format restoredFormat = new Format(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalFormat.getWidth(), restoredFormat.getWidth());
        assertEquals(originalFormat.getHeight(), restoredFormat.getHeight());
        assertEquals(originalFormat.getWidthRatio(), restoredFormat.getWidthRatio());
        assertEquals(originalFormat.getHeightRatio(), restoredFormat.getHeightRatio());
        assertEquals(originalFormat.getWidthMin(), restoredFormat.getWidthMin());
    }
}
