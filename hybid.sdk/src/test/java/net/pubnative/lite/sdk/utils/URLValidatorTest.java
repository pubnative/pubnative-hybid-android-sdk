package net.pubnative.lite.sdk.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class URLValidatorTest {

    @Test
    public void isValidURL_withValidUrls_returnsTrue() {
        assertTrue(URLValidator.isValidURL("https://example.com"));
        assertTrue(URLValidator.isValidURL("http://test.co.uk/path?query=1"));
    }

    @Test
    public void isValidURL_withUrlContainingBrackets_returnsTrue() {
        assertTrue(URLValidator.isValidURL("https://example.com/[MACRO]"));
    }

    @Test
    public void isValidURL_withInvalidUrls_returnsFalse() {
        assertFalse(URLValidator.isValidURL("not a valid url"));
        assertFalse(URLValidator.isValidURL("www.missingprotocol.com"));
        assertFalse(URLValidator.isValidURL(""));
        assertFalse(URLValidator.isValidURL("  "));
    }

    @Test
    public void isValidURL_withNullUrl_returnsFalse() {
        assertFalse(URLValidator.isValidURL(null));
    }
}