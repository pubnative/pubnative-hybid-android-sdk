// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class BidExtensionTest {

    private BidExtension bidExtension;

    @Before
    public void setUp() {
        bidExtension = new BidExtension();
    }

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        // This test covers the no-argument constructor.
        BidExtension ext = new BidExtension();

        assertNotNull(ext);
        assertNull(ext.getCrtype());
        assertNull(ext.getImptrackers());
        assertNull(ext.getSignaldata());
    }

    @Test
    public void testSettersAndGetters() {
        String crtype = "banner";
        List<String> imptrackers = List.of("https://tracker.com/imp");
        String signaldata = "signal_data_string";

        bidExtension.setCrtype(crtype);
        bidExtension.setImptrackers(imptrackers);
        bidExtension.setSignaldata(signaldata);

        assertEquals(crtype, bidExtension.getCrtype());
        assertEquals(imptrackers, bidExtension.getImptrackers());
        assertEquals(signaldata, bidExtension.getSignaldata());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that an object serialized to JSON
        // can be deserialized back into an identical object.

        // 1. Create and populate the original object
        BidExtension originalExtension = new BidExtension();
        originalExtension.setCrtype("video");
        originalExtension.setImptrackers(Arrays.asList("https://tracker.com/1", "https://tracker.com/2"));
        originalExtension.setSignaldata("encrypted_signal_data");

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalExtension.toJson();
        assertNotNull(jsonObject);
        assertEquals("video", jsonObject.getString("crtype"));
        assertEquals("https://tracker.com/2", jsonObject.getJSONArray("imptrackers").get(1));

        // 3. Convert the JSON back into a new object
        BidExtension restoredExtension = new BidExtension(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalExtension.getCrtype(), restoredExtension.getCrtype());
        assertEquals(originalExtension.getImptrackers(), restoredExtension.getImptrackers());
        assertEquals(originalExtension.getSignaldata(), restoredExtension.getSignaldata());
    }
}
