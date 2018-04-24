package net.pubnative.lite.sdk.utils.text;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StringEscapeUtils {

    public static final CharSequenceTranslator ESCAPE_JAVA;
    static {
        final Map<CharSequence, CharSequence> escapeJavaMap = new HashMap<>();
        escapeJavaMap.put("\"", "\\\"");
        escapeJavaMap.put("\\", "\\\\");
        ESCAPE_JAVA = new AggregateTranslator(
                new LookupTranslator(Collections.unmodifiableMap(escapeJavaMap)),
                new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE),
                JavaUnicodeEscaper.outsideOf(32, 0x7f)
        );
    }

    /**
     * Translator object for unescaping escaped Java.
     *
     * While {@link #unescapeJava(String)} is the expected method of use, this
     * object allows the Java unescaping functionality to be used
     * as the foundation for a custom translator.
     */
    public static final CharSequenceTranslator UNESCAPE_JAVA;
    static {
        final Map<CharSequence, CharSequence> unescapeJavaMap = new HashMap<>();
        unescapeJavaMap.put("\\\\", "\\");
        unescapeJavaMap.put("\\\"", "\"");
        unescapeJavaMap.put("\\'", "'");
        unescapeJavaMap.put("\\", "");
        UNESCAPE_JAVA = new AggregateTranslator(
                new OctalUnescaper(),     // .between('\1', '\377'),
                new UnicodeUnescaper(),
                new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_UNESCAPE),
                new LookupTranslator(Collections.unmodifiableMap(unescapeJavaMap))
        );
    }

    /* Helper functions */

    /**
     * <p>{@code StringEscapeUtils} instances should NOT be constructed in
     * standard programming.</p>
     *
     * <p>Instead, the class should be used as:</p>
     * <pre>StringEscapeUtils.escapeJava("foo");</pre>
     *
     * <p>This constructor is public to permit tools that require a JavaBean
     * instance to operate.</p>
     */
    public StringEscapeUtils() {
        super();
    }

    /**
     * <p>Convenience wrapper for {@link java.lang.StringBuilder} providing escape methods.</p>
     *
     * <p>Example:</p>
     * <pre>
     * new Builder(ESCAPE_HTML4)
     *      .append("&lt;p&gt;")
     *      .escape("This is paragraph 1 and special chars like &amp; get escaped.")
     *      .append("&lt;/p&gt;&lt;p&gt;")
     *      .escape("This is paragraph 2 &amp; more...")
     *      .append("&lt;/p&gt;")
     *      .toString()
     * </pre>
     *
     */
    public static final class Builder {

        /**
         * StringBuilder to be used in the Builder class.
         */
        private final StringBuilder sb;

        /**
         * CharSequenceTranslator to be used in the Builder class.
         */
        private final CharSequenceTranslator translator;

        /**
         * Builder constructor.
         *
         * @param translator a CharSequenceTranslator.
         */
        private Builder(final CharSequenceTranslator translator) {
            this.sb = new StringBuilder();
            this.translator = translator;
        }

        /**
         * <p>Escape {@code input} according to the given {@link CharSequenceTranslator}.</p>
         *
         * @param input the String to escape
         * @return {@code this}, to enable chaining
         */
        public Builder escape(final String input) {
            sb.append(translator.translate(input));
            return this;
        }

        /**
         * Literal append, no escaping being done.
         *
         * @param input the String to append
         * @return {@code this}, to enable chaining
         */
        public Builder append(final String input) {
            sb.append(input);
            return this;
        }

        /**
         * <p>Return the escaped string.</p>
         *
         * @return the escaped string
         */
        @Override
        public String toString() {
            return sb.toString();
        }
    }

    /**
     * Get a {@link Builder}.
     * @param translator the text translator
     * @return {@link Builder}
     */
    public static StringEscapeUtils.Builder builder(final CharSequenceTranslator translator) {
        return new Builder(translator);
    }

    // Java and JavaScript
    //--------------------------------------------------------------------------
    /**
     * <p>Escapes the characters in a {@code String} using Java String rules.</p>
     *
     * <p>Deals correctly with quotes and control-chars (tab, backslash, cr, ff, etc.) </p>
     *
     * <p>So a tab becomes the characters {@code '\\'} and
     * {@code 't'}.</p>
     *
     * <p>The only difference between Java strings and JavaScript strings
     * is that in JavaScript, a single quote and forward-slash (/) are escaped.</p>
     *
     * <p>Example:</p>
     * <pre>
     * input string: He didn't say, "Stop!"
     * output string: He didn't say, \"Stop!\"
     * </pre>
     *
     * @param input  String to escape values in, may be null
     * @return String with escaped values, {@code null} if null string input
     */
    public static final String escapeJava(final String input) {
        return ESCAPE_JAVA.translate(input);
    }

    /**
     * <p>Unescapes any Java literals found in the {@code String}.
     * For example, it will turn a sequence of {@code '\'} and
     * {@code 'n'} into a newline character, unless the {@code '\'}
     * is preceded by another {@code '\'}.</p>
     *
     * @param input  the {@code String} to unescape, may be null
     * @return a new unescaped {@code String}, {@code null} if null string input
     */
    public static final String unescapeJava(final String input) {
        return UNESCAPE_JAVA.translate(input);
    }
}
