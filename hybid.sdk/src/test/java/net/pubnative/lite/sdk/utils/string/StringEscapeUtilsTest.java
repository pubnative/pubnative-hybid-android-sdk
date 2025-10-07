// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.string;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class StringEscapeUtilsTest {

    //----------------------------------------------------------------------------------------------
    // Constructor and Class Structure Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void constructor_shouldBePublicAndInstantiable() throws Exception {
        // Test that constructor is public (for JavaBean compatibility)
        Constructor<StringEscapeUtils> constructor = StringEscapeUtils.class.getDeclaredConstructor();
        assertTrue("Constructor should be public", Modifier.isPublic(constructor.getModifiers()));

        StringEscapeUtils instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    public void staticFields_shouldBeProperlyInitialized() throws Exception {
        // Test that all static translator fields are properly initialized
        assertNotNull("ESCAPE_JAVA should not be null", StringEscapeUtils.ESCAPE_JAVA);
        assertNotNull("ESCAPE_XSI should not be null", StringEscapeUtils.ESCAPE_XSI);
        assertNotNull("UNESCAPE_JAVA should not be null", StringEscapeUtils.UNESCAPE_JAVA);

        // Test EMPTY constant
        assertEquals("", StringEscapeUtils.EMPTY);

        // Verify static fields are final
        Field escapeJavaField = StringEscapeUtils.class.getDeclaredField("ESCAPE_JAVA");
        assertTrue("ESCAPE_JAVA should be final", Modifier.isFinal(escapeJavaField.getModifiers()));
        assertTrue("ESCAPE_JAVA should be static", Modifier.isStatic(escapeJavaField.getModifiers()));
    }

    //----------------------------------------------------------------------------------------------
    // Builder Pattern Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void builder_shouldSupportFluentChaining() {
        // Test Builder pattern with fluent interface
        StringEscapeUtils.Builder builder = StringEscapeUtils.builder(StringEscapeUtils.ESCAPE_JAVA);
        assertNotNull(builder);

        String result = builder
                .append("<p>")
                .escape("Hello \"World\" & others")
                .append("</p>")
                .toString();

        assertTrue(result.contains("<p>"));
        assertTrue(result.contains("</p>"));
        assertTrue(result.contains("\\\""));  // Escaped quotes
        assertFalse(result.contains("\"Hello")); // Original quotes should be escaped
    }

    @Test
    public void builder_shouldHandleVariousInputCombinations() {
        // Test Builder with different input scenarios
        StringEscapeUtils.Builder builder = StringEscapeUtils.builder(StringEscapeUtils.ESCAPE_JAVA);

        // Empty inputs
        String emptyResult = builder.append("").escape("").toString();
        assertEquals("", emptyResult);

        // Mixed append and escape
        builder = StringEscapeUtils.builder(StringEscapeUtils.ESCAPE_JAVA);
        String mixedResult = builder
                .append("Literal: ")
                .escape("He said \"Hi!\"")
                .append(" End.")
                .toString();

        assertTrue(mixedResult.startsWith("Literal: "));
        assertTrue(mixedResult.endsWith(" End."));
        assertTrue(mixedResult.contains("\\\""));

        // Multiple operations
        builder = StringEscapeUtils.builder(StringEscapeUtils.ESCAPE_JAVA);
        String multiResult = builder
                .escape("First")
                .append("-")
                .escape("Second")
                .toString();

        assertEquals("First-Second", multiResult);
    }

    //----------------------------------------------------------------------------------------------
    // Java Escaping Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void escapeJava_shouldHandleVariousCharacterTypes() {
        // Test Java string escaping functionality

        // Basic quote escaping
        assertEquals("He said \\\"Hello\\\"", StringEscapeUtils.escapeJava("He said \"Hello\""));

        // Backslash escaping
        assertEquals("Path: C:\\\\folder", StringEscapeUtils.escapeJava("Path: C:\\folder"));

        // Control characters
        assertEquals("Line1\\nLine2", StringEscapeUtils.escapeJava("Line1\nLine2"));
        assertEquals("Tab\\there", StringEscapeUtils.escapeJava("Tab\there"));
        assertEquals("Return\\rhere", StringEscapeUtils.escapeJava("Return\rhere"));

        // Mixed scenarios
        assertEquals("He said: \\\"Hello\\nWorld\\\"",
                StringEscapeUtils.escapeJava("He said: \"Hello\nWorld\""));
    }

    @Test
    public void escapeJava_shouldHandleEdgeCases() {
        // Test edge cases for Java escaping

        // Null input
        assertNull(StringEscapeUtils.escapeJava(null));

        // Empty string
        assertEquals("", StringEscapeUtils.escapeJava(""));

        // String with no escapable characters
        assertEquals("Hello World", StringEscapeUtils.escapeJava("Hello World"));

        // Unicode characters (outside ASCII range)
        String unicodeInput = "Hello 世界";
        String unicodeResult = StringEscapeUtils.escapeJava(unicodeInput);
        assertTrue(unicodeResult.contains("Hello"));
        // Should escape non-ASCII characters with Unicode escapes
        assertTrue(unicodeResult.contains("\\u"));
    }

    //----------------------------------------------------------------------------------------------
    // Java Unescaping Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void unescapeJava_shouldHandleVariousEscapeSequences() {
        // Test Java string unescaping functionality

        // Basic quote unescaping
        assertEquals("He said \"Hello\"", StringEscapeUtils.unescapeJava("He said \\\"Hello\\\""));

        // Backslash unescaping
        assertEquals("Path: C:\\folder", StringEscapeUtils.unescapeJava("Path: C:\\\\folder"));

        // Control characters
        assertEquals("Line1\nLine2", StringEscapeUtils.unescapeJava("Line1\\nLine2"));
        assertEquals("Tab\there", StringEscapeUtils.unescapeJava("Tab\\there"));
        assertEquals("Return\rhere", StringEscapeUtils.unescapeJava("Return\\rhere"));

        // Mixed scenarios
        assertEquals("He said: \"Hello\nWorld\"",
                StringEscapeUtils.unescapeJava("He said: \\\"Hello\\nWorld\\\""));
    }

    @Test
    public void unescapeJava_shouldHandleComplexEscapeSequences() {
        // Test complex unescaping scenarios

        // Octal sequences
        assertEquals("ABC", StringEscapeUtils.unescapeJava("\\101\\102\\103"));

        // Unicode sequences
        String unicodeInput = "Hello \\u4e16\\u754c"; // 世界 in Unicode
        String unicodeResult = StringEscapeUtils.unescapeJava(unicodeInput);
        assertEquals("Hello 世界", unicodeResult);

        // Mixed escape types
        String mixedInput = "\\\"Hello\\u0020World\\n\\\"";
        String mixedResult = StringEscapeUtils.unescapeJava(mixedInput);
        assertEquals("\"Hello World\n\"", mixedResult);
    }

    @Test
    public void unescapeJava_shouldHandleEdgeCases() {
        // Test edge cases for Java unescaping

        // Null input
        assertNull(StringEscapeUtils.unescapeJava(null));

        // Empty string
        assertEquals("", StringEscapeUtils.unescapeJava(""));

        // String with no escape sequences
        assertEquals("Hello World", StringEscapeUtils.unescapeJava("Hello World"));

        // Invalid escape sequences (should be left as-is or handled gracefully)
        String invalidResult = StringEscapeUtils.unescapeJava("\\invalid");
        assertNotNull(invalidResult);
    }

    //----------------------------------------------------------------------------------------------
    // Round-trip Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void javaEscapeUnescape_shouldBeReversible() {
        // Test that escape and unescape are inverse operations
        String[] testInputs = {
                "Hello World",
                "He said \"Hello\"",
                "Path: C:\\folder\\file.txt",
                "Line1\nLine2\tTabbed",
                "Mixed: \"quotes\" and \\ backslashes",
                "Control chars: \b\f\r\n",
                ""
        };

        for (String input : testInputs) {
            String escaped = StringEscapeUtils.escapeJava(input);
            String unescaped = StringEscapeUtils.unescapeJava(escaped);
            assertEquals("Round-trip failed for: " + input, input, unescaped);
        }
    }

    //----------------------------------------------------------------------------------------------
    // Static Translator Behavior Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void staticTranslators_shouldWorkCorrectly() {
        // Test that static translator instances work as expected

        // ESCAPE_JAVA
        String javaEscaped = StringEscapeUtils.ESCAPE_JAVA.translate("Hello \"World\"");
        assertEquals("Hello \\\"World\\\"", javaEscaped);

        // UNESCAPE_JAVA
        String javaUnescaped = StringEscapeUtils.UNESCAPE_JAVA.translate("Hello \\\"World\\\"");
        assertEquals("Hello \"World\"", javaUnescaped);

        // ESCAPE_XSI (Shell escaping)
        String shellEscaped = StringEscapeUtils.ESCAPE_XSI.translate("echo \"hello world\"");
        assertTrue(shellEscaped.contains("\\\""));
        assertNotEquals("echo \"hello world\"", shellEscaped);
    }

    @Test
    public void escapeXSI_shouldHandleShellSpecialCharacters() {
        // Test XSI shell escaping functionality

        // Test various shell special characters
        assertEquals("\\|", StringEscapeUtils.ESCAPE_XSI.translate("|"));
        assertEquals("\\&", StringEscapeUtils.ESCAPE_XSI.translate("&"));
        assertEquals("\\;", StringEscapeUtils.ESCAPE_XSI.translate(";"));
        assertEquals("\\<", StringEscapeUtils.ESCAPE_XSI.translate("<"));
        assertEquals("\\>", StringEscapeUtils.ESCAPE_XSI.translate(">"));
        assertEquals("\\$", StringEscapeUtils.ESCAPE_XSI.translate("$"));
        assertEquals("\\`", StringEscapeUtils.ESCAPE_XSI.translate("`"));
        assertEquals("\\ ", StringEscapeUtils.ESCAPE_XSI.translate(" "));

        // Test line endings (should be removed)
        assertEquals("", StringEscapeUtils.ESCAPE_XSI.translate("\n"));
        assertEquals("", StringEscapeUtils.ESCAPE_XSI.translate("\r\n"));

        // Complex shell command
        String shellCommand = "echo \"hello world\" | grep pattern";
        String escapedCommand = StringEscapeUtils.ESCAPE_XSI.translate(shellCommand);
        assertTrue(escapedCommand.contains("\\\""));
        assertTrue(escapedCommand.contains("\\|"));
        assertTrue(escapedCommand.contains("\\ "));
    }

    //----------------------------------------------------------------------------------------------
    // Integration and Performance Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void integration_shouldWorkWithComplexScenarios() {
        // Test integration scenarios with complex inputs

        // Large string with mixed content
        StringBuilder largeInput = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            largeInput.append("Line ").append(i).append(": \"Hello\\World\"\n");
        }

        String escaped = StringEscapeUtils.escapeJava(largeInput.toString());
        String unescaped = StringEscapeUtils.unescapeJava(escaped);
        assertEquals(largeInput.toString(), unescaped);

        // Builder with complex operations
        StringEscapeUtils.Builder complexBuilder = StringEscapeUtils.builder(StringEscapeUtils.ESCAPE_JAVA);
        String complexResult = complexBuilder
                .append("Start: ")
                .escape("He said \"Hello\nWorld\"")
                .append(" Middle: ")
                .escape("Path: C:\\folder")
                .append(" End.")
                .toString();

        assertTrue(complexResult.contains("Start: "));
        assertTrue(complexResult.contains("\\\""));
        assertTrue(complexResult.contains("\\n"));
        assertTrue(complexResult.contains("\\\\"));
        assertTrue(complexResult.contains(" End."));
    }

    @Test
    public void utilities_shouldHandleNullAndEmptyInputsConsistently() {
        // Test consistent null and empty handling across all utilities

        // Null inputs
        assertNull(StringEscapeUtils.escapeJava(null));
        assertNull(StringEscapeUtils.unescapeJava(null));
        assertNull(StringEscapeUtils.ESCAPE_XSI.translate(null));

        // Empty inputs
        assertEquals("", StringEscapeUtils.escapeJava(""));
        assertEquals("", StringEscapeUtils.unescapeJava(""));
        assertEquals("", StringEscapeUtils.ESCAPE_XSI.translate(""));

        // Builder with null translator should handle gracefully
        try {
            StringEscapeUtils.Builder nullBuilder = StringEscapeUtils.builder(null);
            assertNotNull(nullBuilder); // Constructor should work

            // Operations might fail, but shouldn't crash immediately
            String result = nullBuilder.append("test").toString();
            assertEquals("test", result); // Append should still work
        } catch (Exception e) {
            // Some failure is acceptable when translator is null
            assertTrue("Exception should be related to null translator",
                    e.getMessage() == null || e.getMessage().contains("null"));
        }
    }
}