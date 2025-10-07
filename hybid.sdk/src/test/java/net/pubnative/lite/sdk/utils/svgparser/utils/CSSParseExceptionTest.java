// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the CSSParseException class.
 */
public class CSSParseExceptionTest {

    @Test
    public void constructor_setsMessageCorrectly() {
        // This test ensures the message passed to the constructor is retrievable.
        String expectedMessage = "Invalid CSS syntax";
        CSSParseException exception = new CSSParseException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void exception_canBeThrownAndCaught() {
        // This test verifies that the exception works as expected in a try-catch block.
        String expectedMessage = "Malformed @media rule";

        try {
            throw new CSSParseException(expectedMessage);
        } catch (CSSParseException e) {
            // Check that the caught exception is the correct type and has the correct message.
            assertNotNull(e);
            assertEquals(expectedMessage, e.getMessage());
        } catch (Exception e) {
            fail("Caught a generic Exception, but CSSParseException was expected.");
        }
    }
}