// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
package net.pubnative.lite.sdk.viewability.baseom;

import org.junit.runner.RunWith;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by shubhamkeshri on 22.09.25.
 */

@RunWith(RobolectricTestRunner.class)
public class BaseVerificationScriptResourceTest {

    private String vendorKey;
    private URL resourceUrl;
    private String verificationParameters;

    @Before
    public void setUp() throws MalformedURLException {
        vendorKey = "Test Vendor Key";
        resourceUrl = new URL("https://pubnative.net/");
        verificationParameters = "Test verificationParameters";
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testConstructorAndGetters_whenValuesAreNonNull() {
        BaseVerificationScriptResource scriptResource = BaseVerificationScriptResource
                .createVerificationScriptResourceWithParameters(vendorKey, resourceUrl, verificationParameters);

        // Verify values are correctly set
        assertEquals(vendorKey, scriptResource.getVendorKey());
        assertEquals(resourceUrl, scriptResource.getResourceUrl());
        assertEquals(verificationParameters, scriptResource.getVerificationParameters());
    }

    @Test
    public void testConstructorAndGetters_withNullValues() {
        BaseVerificationScriptResource scriptResource = BaseVerificationScriptResource
                .createVerificationScriptResourceWithParameters(null, null, null);

        // Verify null handling
        assertNull(scriptResource.getVendorKey());
        assertNull(scriptResource.getResourceUrl());
        assertNull(scriptResource.getVerificationParameters());
    }
}
