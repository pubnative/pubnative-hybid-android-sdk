// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

import net.pubnative.lite.sdk.models.Ad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

@RunWith(RobolectricTestRunner.class)
public class AdCacheTest {
    private AdCache adCache;

    @Mock
    private Ad mockAd1;

    @Mock
    private Ad mockAd2;

    @Mock
    private Ad mockAd3;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        adCache = new AdCache();
    }

    @After
    public void tearDown() {
        adCache = null;
    }

    @Test
    public void testConstructor() {
        AdCache cache = new AdCache();
        assertNotNull(cache);
    }

    @Test
    public void testPutAndInspect() {
        String zoneId = "zone123";

        adCache.put(zoneId, mockAd1);

        Ad retrievedAd = adCache.inspect(zoneId);
        assertNotNull(retrievedAd);
        assertSame(mockAd1, retrievedAd);
    }

    @Test
    public void testPutMultipleAds() {
        String zoneId1 = "zone1";
        String zoneId2 = "zone2";
        String zoneId3 = "zone3";

        adCache.put(zoneId1, mockAd1);
        adCache.put(zoneId2, mockAd2);
        adCache.put(zoneId3, mockAd3);

        assertSame(mockAd1, adCache.inspect(zoneId1));
        assertSame(mockAd2, adCache.inspect(zoneId2));
        assertSame(mockAd3, adCache.inspect(zoneId3));
    }

    @Test
    public void testPutOverwritesExistingAd() {
        String zoneId = "zone123";

        adCache.put(zoneId, mockAd1);
        adCache.put(zoneId, mockAd2);

        Ad retrievedAd = adCache.inspect(zoneId);
        assertSame(mockAd2, retrievedAd);
    }

    @Test
    public void testInspectNonExistentZone() {
        Ad retrievedAd = adCache.inspect("nonexistent");
        assertNull(retrievedAd);
    }

    @Test
    public void testRemove() {
        String zoneId = "zone123";

        adCache.put(zoneId, mockAd1);
        Ad removedAd = adCache.remove(zoneId);

        assertNotNull(removedAd);
        assertSame(mockAd1, removedAd);

        // Verify it's actually removed
        Ad retrievedAd = adCache.inspect(zoneId);
        assertNull(retrievedAd);
    }

    @Test
    public void testRemoveNonExistentZone() {
        Ad removedAd = adCache.remove("nonexistent");
        assertNull(removedAd);
    }

    @Test
    public void testRemoveDoesNotAffectOtherEntries() {
        String zoneId1 = "zone1";
        String zoneId2 = "zone2";

        adCache.put(zoneId1, mockAd1);
        adCache.put(zoneId2, mockAd2);

        adCache.remove(zoneId1);

        assertNull(adCache.inspect(zoneId1));
        assertSame(mockAd2, adCache.inspect(zoneId2));
    }

    @Test
    public void testPutWithNullZoneId() {
        // Testing edge case - HashMap allows null keys
        adCache.put(null, mockAd1);

        Ad retrievedAd = adCache.inspect(null);
        assertSame(mockAd1, retrievedAd);
    }

    @Test
    public void testPutWithNullAd() {
        // Testing edge case - HashMap allows null values
        String zoneId = "zone123";
        adCache.put(zoneId, null);

        Ad retrievedAd = adCache.inspect(zoneId);
        assertNull(retrievedAd);
    }

    @Test
    public void testRemoveWithNullZoneId() {
        adCache.put(null, mockAd1);

        Ad removedAd = adCache.remove(null);
        assertSame(mockAd1, removedAd);

        assertNull(adCache.inspect(null));
    }

    @Test
    public void testInspectWithNullZoneId() {
        Ad retrievedAd = adCache.inspect(null);
        assertNull(retrievedAd);
    }

    @Test
    public void testPutWithEmptyStringZoneId() {
        String emptyZoneId = "";
        adCache.put(emptyZoneId, mockAd1);

        Ad retrievedAd = adCache.inspect(emptyZoneId);
        assertSame(mockAd1, retrievedAd);
    }

    @Test
    public void testSequentialOperations() {
        String zoneId = "zone123";

        // Put
        adCache.put(zoneId, mockAd1);
        assertSame(mockAd1, adCache.inspect(zoneId));

        // Replace
        adCache.put(zoneId, mockAd2);
        assertSame(mockAd2, adCache.inspect(zoneId));

        // Remove
        Ad removedAd = adCache.remove(zoneId);
        assertSame(mockAd2, removedAd);

        // Verify removed
        assertNull(adCache.inspect(zoneId));

        // Put again
        adCache.put(zoneId, mockAd3);
        assertSame(mockAd3, adCache.inspect(zoneId));
    }
}

