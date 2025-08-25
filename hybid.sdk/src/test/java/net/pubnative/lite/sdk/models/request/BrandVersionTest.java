// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class BrandVersionTest {

    private BrandVersion brandVersion;

    @Before
    public void setUp() {
        brandVersion = new BrandVersion();
    }

    @Test
    public void testSettersAndGetters() {
        String brand = "TestBrand";
        List<String> versions = Arrays.asList("1.0", "2.0");

        brandVersion.setBrand(brand);
        brandVersion.setVersion(versions);

        assertEquals(brand, brandVersion.getBrand());
        assertEquals(versions, brandVersion.getVersion());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that an object serialized to JSON
        // can be deserialized back into an identical object.

        // 1. Create and populate the original object
        String brand = "AnotherBrand";
        List<String> versions = Arrays.asList("alpha-1", "beta-2");
        BrandVersion originalBrandVersion = new BrandVersion();
        originalBrandVersion.setBrand(brand);
        originalBrandVersion.setVersion(versions);

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalBrandVersion.toJson();
        assertNotNull(jsonObject);
        assertEquals(brand, jsonObject.getString("brand"));
        assertEquals("beta-2", jsonObject.getJSONArray("version").get(1));

        // 3. Convert the JSON back into a new object
        BrandVersion restoredBrandVersion = new BrandVersion(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalBrandVersion.getBrand(), restoredBrandVersion.getBrand());
        assertEquals(originalBrandVersion.getVersion(), restoredBrandVersion.getVersion());
    }
}