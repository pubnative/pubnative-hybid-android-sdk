// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the SVGParseException class.
 */
public class SVGParseExceptionTest {

    @Test
    public void constructorWithMessage_setsMessageCorrectly() {
        // This test verifies the constructor that takes only a message.
        String expectedMessage = "Invalid <path> element";
        SVGParseException exception = new SVGParseException(expectedMessage);

        assertEquals("The message should be correctly set", expectedMessage, exception.getMessage());
        assertNull("The cause should be null when not provided", exception.getCause());
    }

    @Test
    public void constructorWithMessageAndCause_setsBothCorrectly() {
        // This test verifies the constructor that takes both a message and a causing exception.
        String expectedMessage = "Failed to parse number";
        NumberFormatException cause = new NumberFormatException("For input string: \"xyz\"");

        SVGParseException exception = new SVGParseException(expectedMessage, cause);

        assertEquals("The message should be correctly set", expectedMessage, exception.getMessage());
        assertSame("The cause should be the exact exception instance provided", cause, exception.getCause());
    }
}