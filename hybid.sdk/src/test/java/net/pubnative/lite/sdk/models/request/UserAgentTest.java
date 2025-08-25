// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

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
public class UserAgentTest {

    private UserAgent userAgent;

    @Before
    public void setUp() {
        userAgent = new UserAgent();
    }

    @Test
    public void testDefaultConstructor_initializesFieldToDefaultValue() {
        assertEquals(Integer.valueOf(0), userAgent.getSource());
        assertNull(userAgent.getPlatform());
        assertNull(userAgent.getBrowsers());
    }

    @Test
    public void testSettersAndGetters() {
        BrandVersion platform = new BrandVersion();
        platform.setBrand("Android");
        List<BrandVersion> browsers = Arrays.asList(new BrandVersion());
        String model = "Pixel Test";

        userAgent.setPlatform(platform);
        userAgent.setBrowsers(browsers);
        userAgent.setModel(model);

        assertEquals(platform, userAgent.getPlatform());
        assertEquals("Android", userAgent.getPlatform().getBrand());
        assertEquals(browsers, userAgent.getBrowsers());
        assertEquals(model, userAgent.getModel());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the original object with nested data
        BrandVersion platform = new BrandVersion();
        platform.setBrand("Android");
        platform.setVersion(Arrays.asList("12"));

        BrandVersion browser = new BrandVersion();
        browser.setBrand("Chrome");
        browser.setVersion(Arrays.asList("108", "0", "0", "0"));

        UserAgent originalUserAgent = new UserAgent();
        originalUserAgent.setPlatform(platform);
        originalUserAgent.setBrowsers(Arrays.asList(browser));
        originalUserAgent.setModel("Pixel");
        originalUserAgent.setMobile(1);
        originalUserAgent.setSource(2); // Override default

        // 2. Convert to JSON
        JSONObject jsonObject = originalUserAgent.toJson();
        assertNotNull(jsonObject);
        assertEquals("Pixel", jsonObject.getString("model"));
        assertEquals("Android", jsonObject.getJSONObject("platform").getString("brand"));
        assertEquals("Chrome", jsonObject.getJSONArray("browsers").getJSONObject(0).getString("brand"));

        // 3. Convert back to an object
        UserAgent restoredUserAgent = new UserAgent(jsonObject);

        // 4. Assert that the objects are identical
        assertEquals(originalUserAgent.getModel(), restoredUserAgent.getModel());
        assertEquals(originalUserAgent.getMobile(), restoredUserAgent.getMobile());
        assertEquals(originalUserAgent.getSource(), restoredUserAgent.getSource());

        // Assert nested platform
        assertNotNull(restoredUserAgent.getPlatform());
        assertEquals(originalUserAgent.getPlatform().getBrand(), restoredUserAgent.getPlatform().getBrand());
        assertEquals(originalUserAgent.getPlatform().getVersion(), restoredUserAgent.getPlatform().getVersion());

        // Assert nested browsers list
        assertNotNull(restoredUserAgent.getBrowsers());
        assertEquals(1, restoredUserAgent.getBrowsers().size());
        assertEquals(originalUserAgent.getBrowsers().get(0).getBrand(), restoredUserAgent.getBrowsers().get(0).getBrand());
        assertEquals(originalUserAgent.getBrowsers().get(0).getVersion(), restoredUserAgent.getBrowsers().get(0).getVersion());
    }
}