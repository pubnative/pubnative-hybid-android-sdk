// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import net.pubnative.lite.sdk.models.bidstream.Signal;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AdRequestTest {

    private AdRequest adRequest;

    @Before
    public void setUp() {
        adRequest = new AdRequest();
    }

    @Test
    public void defaultState_initializesFieldsCorrectly() {
        assertNull(adRequest.appToken);
        assertNull(adRequest.zoneId);
        assertFalse(adRequest.isInterstitial);
        assertNull(adRequest.topics);

        // The signals list should be initialized but empty
        assertNotNull(adRequest.getSignals());
        assertTrue(adRequest.getSignals().isEmpty());
    }

    @Test
    public void publicFields_canBeSetAndRead() {
        adRequest.appToken = "test_token";
        adRequest.zoneId = "test_zone";
        adRequest.isInterstitial = true;
        List<Topic> topics = new ArrayList<>();
        topics.add(new Topic(1, 1L, "v1"));
        adRequest.topics = topics;

        assertEquals("test_token", adRequest.appToken);
        assertEquals("test_zone", adRequest.zoneId);
        assertTrue(adRequest.isInterstitial);
        assertEquals(topics, adRequest.topics);
    }

    @Test
    public void addSignal_addsSignalToList() {
        Signal signal1 = new Signal();
        Signal signal2 = new Signal();

        adRequest.addSignal(signal1);

        assertEquals(1, adRequest.getSignals().size());
        assertTrue(adRequest.getSignals().contains(signal1));

        adRequest.addSignal(signal2);

        assertEquals(2, adRequest.getSignals().size());
        assertTrue(adRequest.getSignals().contains(signal2));
    }

    @Test
    public void toJson_returnsEmptyObject() throws Exception {
        // Since AdRequest has no @BindField annotations, toJson should produce an empty object.
        JSONObject jsonObject = adRequest.toJson();

        assertNotNull(jsonObject);
        assertEquals(0, jsonObject.length());
    }
}
