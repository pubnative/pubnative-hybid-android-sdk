package net.pubnative.lite.sdk.tracking;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public class JsonWriter implements Closeable {

    enum JsonScope {
        EMPTY_ARRAY,
        NONEMPTY_ARRAY,
        EMPTY_OBJECT,
        DANGLING_NAME,
        NONEMPTY_OBJECT,
        EMPTY_DOCUMENT,
        NONEMPTY_DOCUMENT,
        CLOSED
    }

    /*
     * From RFC 4627, "All Unicode characters may be placed within the
     * quotation marks except for the characters that must be escaped:
     * quotation mark, reverse solidus, and the control characters
     * (U+0000 through U+001F)."
     *
     * We also escape '\u2028' and '\u2029', which JavaScript interprets as
     * newline characters. This prevents eval() from failing with a syntax
     * error. http://code.google.com/p/google-gson/issues/detail?id=341
     */
    private static final String[] REPLACEMENT_CHARS;
    private static final String[] HTML_SAFE_REPLACEMENT_CHARS;

    static {
        REPLACEMENT_CHARS = new String[128];
        for (int i = 0; i <= 0x1f; i++) {
            REPLACEMENT_CHARS[i] = String.format("\\u%04x", (int) i);
        }
        REPLACEMENT_CHARS['"'] = "\\\"";
        REPLACEMENT_CHARS['\\'] = "\\\\";
        REPLACEMENT_CHARS['\t'] = "\\t";
        REPLACEMENT_CHARS['\b'] = "\\b";
        REPLACEMENT_CHARS['\n'] = "\\n";
        REPLACEMENT_CHARS['\r'] = "\\r";
        REPLACEMENT_CHARS['\f'] = "\\f";
        HTML_SAFE_REPLACEMENT_CHARS = REPLACEMENT_CHARS.clone();
        HTML_SAFE_REPLACEMENT_CHARS['<'] = "\\u003c";
        HTML_SAFE_REPLACEMENT_CHARS['>'] = "\\u003e";
        HTML_SAFE_REPLACEMENT_CHARS['&'] = "\\u0026";
        HTML_SAFE_REPLACEMENT_CHARS['='] = "\\u003d";
        HTML_SAFE_REPLACEMENT_CHARS['\''] = "\\u0027";
    }

    /**
     * The output data, containing at most one top-level array or object.
     */
    private final Writer out;

    private final List<JsonScope> stack = new ArrayList<>();

    {
        stack.add(JsonScope.EMPTY_DOCUMENT);
    }

    /**
     * A string containing a full set of spaces for a single level of
     * indentation, or null for no pretty printing.
     */
    private String indent;

    /**
     * The name/value separator; either ":" or ": ".
     */
    private String separator = ":";

    private boolean lenient;

    private boolean htmlSafe;

    private String deferredName;

    private boolean serializeNulls = true;

    /**
     * Creates a new instance that writes a JSON-encoded stream to {@code out}.
     * For best performance, ensure {@link Writer} is buffered; wrapping in
     * {@link java.io.BufferedWriter BufferedWriter} if necessary.
     */
    public JsonWriter(Writer out) {
        if (out == null) {
            throw new NullPointerException("out == null");
        }
        this.out = out;
    }

    /**
     * Sets the indentation string to be repeated for each level of indentation
     * in the encoded document. If {@code indent.isEmpty()} the encoded document
     * will be compact. Otherwise the encoded document will be more
     * human-readable.
     *
     * @param indent a string containing only whitespace.
     */
    public final void setIndent(String indent) {
        if (indent.length() == 0) {
            this.indent = null;
            this.separator = ":";
        } else {
            this.indent = indent;
            this.separator = ": ";
        }
    }

    /**
     * Configure this writer to relax its syntax rules. By default, this writer
     * only emits well-formed JSON as specified by <a
     * href="http://www.ietf.org/rfc/rfc4627.txt">RFC 4627</a>. Setting the writer
     * to lenient permits the following:
     * <ul>
     * <li>Top-level values of any type. With strict writing, the top-level
     * value must be an object or an array.
     * <li>Numbers may be {@link Double#isNaN() NaNs} or {@link
     * Double#isInfinite() infinities}.
     * </ul>
     */
    public final void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    /**
     * Returns true if this writer has relaxed syntax rules.
     */
    public boolean isLenient() {
        return lenient;
    }

    /**
     * Configure this writer to emit JSON that's safe for direct inclusion in HTML
     * and XML documents. This escapes the HTML characters {@code <}, {@code >},
     * {@code &} and {@code =} before writing them to the stream. Without this
     * setting, your XML/HTML encoder should replace these characters with the
     * corresponding escape sequences.
     */
    public final void setHtmlSafe(boolean htmlSafe) {
        this.htmlSafe = htmlSafe;
    }

    /**
     * Returns true if this writer writes JSON that's safe for inclusion in HTML
     * and XML documents.
     */
    public final boolean isHtmlSafe() {
        return htmlSafe;
    }

    /**
     * Sets whether object members are serialized when their value is null.
     * This has no impact on array elements. The default is true.
     */
    public final void setSerializeNulls(boolean serializeNulls) {
        this.serializeNulls = serializeNulls;
    }

    /**
     * Returns true if object members are serialized when their value is null.
     * This has no impact on array elements. The default is true.
     */
    public final boolean getSerializeNulls() {
        return serializeNulls;
    }

    /**
     * Begins encoding a new array. Each call to this method must be paired with
     * a call to {@link #endArray}.
     *
     * @return this writer.
     */
    public JsonWriter beginArray() throws IOException {
        writeDeferredName();
        return open(JsonScope.EMPTY_ARRAY, "[");
    }

    /**
     * Ends encoding the current array.
     *
     * @return this writer.
     */
    public JsonWriter endArray() throws IOException {
        return close(JsonScope.EMPTY_ARRAY, JsonScope.NONEMPTY_ARRAY, "]");
    }

    /**
     * Begins encoding a new object. Each call to this method must be paired
     * with a call to {@link #endObject}.
     *
     * @return this writer.
     */
    public JsonWriter beginObject() throws IOException {
        writeDeferredName();
        return open(JsonScope.EMPTY_OBJECT, "{");
    }

    /**
     * Ends encoding the current object.
     *
     * @return this writer.
     */
    public JsonWriter endObject() throws IOException {
        return close(JsonScope.EMPTY_OBJECT, JsonScope.NONEMPTY_OBJECT, "}");
    }

    /**
     * Enters a new scope by appending any necessary whitespace and the given
     * bracket.
     */
    private JsonWriter open(JsonScope empty, String openBracket) throws IOException {
        beforeValue(true);
        stack.add(empty);
        out.write(openBracket);
        return this;
    }

    /**
     * Closes the current scope by appending any necessary whitespace and the
     * given bracket.
     */
    private JsonWriter close(JsonScope empty, JsonScope nonempty, String closeBracket)
            throws IOException {
        JsonScope context = peek();
        if (context != nonempty && context != empty) {
            throw new IllegalStateException("Nesting problem: " + stack);
        }
        if (deferredName != null) {
            throw new IllegalStateException("Dangling name: " + deferredName);
        }

        stack.remove(stack.size() - 1);
        if (context == nonempty) {
            newline();
        }
        out.write(closeBracket);
        return this;
    }

    /**
     * Flushes and closes this writer and the underlying {@link Writer}.
     *
     * @throws IOException if the JSON document is incomplete.
     */
    public void close() throws IOException {
        out.close();

        int size = stack.size();
        if (size > 1 || size == 1 && stack.get(size - 1) != JsonScope.NONEMPTY_DOCUMENT) {
            throw new IOException("Incomplete document");
        }
        stack.clear();
    }

    /**
     * Returns the value on the top of the stack.
     */
    private JsonScope peek() {
        int size = stack.size();
        if (size == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        }
        return stack.get(size - 1);
    }

    /**
     * Replace the value on the top of the stack with the given value.
     */
    private void replaceTop(JsonScope topOfStack) {
        stack.set(stack.size() - 1, topOfStack);
    }

    /**
     * Encodes the property name.
     *
     * @param name the name of the forthcoming value. May not be null.
     * @return this writer.
     */
    public JsonWriter name(String name) throws IOException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        if (deferredName != null) {
            throw new IllegalStateException();
        }
        if (stack.isEmpty()) {
            throw new IllegalStateException("JsonWriter is closed.");
        }
        deferredName = name;
        return this;
    }

    private void writeDeferredName() throws IOException {
        if (deferredName != null) {
            beforeName();
            string(deferredName);
            deferredName = null;
        }
    }

    /**
     * Encodes {@code null}.
     *
     * @return this writer.
     */
    public JsonWriter nullValue() throws IOException {
        if (deferredName != null) {
            if (serializeNulls) {
                writeDeferredName();
            } else {
                deferredName = null;
                return this; // skip the name and the value
            }
        }
        beforeValue(false);
        out.write("null");
        return this;
    }

    /**
     * Encodes {@code value}.
     *
     * @param value the literal string value, or null to encode a null literal.
     * @return this writer.
     */
    public JsonWriter value(String value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        writeDeferredName();
        beforeValue(false);
        string(value);
        return this;
    }

    /**
     * Encodes {@code value}.
     *
     * @return this writer.
     */
    public JsonWriter value(boolean value) throws IOException {
        writeDeferredName();
        beforeValue(false);
        out.write(value ? "true" : "false");
        return this;
    }

    /**
     * Encodes {@code value}.
     *
     * @return this writer.
     */
    public JsonWriter value(Boolean value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        writeDeferredName();
        beforeValue(false);
        out.write(value ? "true" : "false");
        return this;
    }

    /**
     * Encodes {@code value}.
     *
     * @param value a finite value. May not be {@link Double#isNaN() NaNs} or
     *              {@link Double#isInfinite() infinities}.
     * @return this writer.
     */
    public JsonWriter value(double value) throws IOException {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
        }
        writeDeferredName();
        beforeValue(false);
        out.append(Double.toString(value));
        return this;
    }

    /**
     * Encodes {@code value}.
     *
     * @return this writer.
     */
    public JsonWriter value(long value) throws IOException {
        writeDeferredName();
        beforeValue(false);
        out.write(Long.toString(value));
        return this;
    }

    /**
     * Encodes {@code value}.
     *
     * @param value a finite value. May not be {@link Double#isNaN() NaNs} or
     *              {@link Double#isInfinite() infinities}.
     * @return this writer.
     */
    public JsonWriter value(Number value) throws IOException {
        if (value == null) {
            return nullValue();
        }

        writeDeferredName();
        String string = value.toString();
        if (!lenient
                && (string.equals("-Infinity") || string.equals("Infinity") || string.equals("NaN"))) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
        }
        beforeValue(false);
        out.append(string);
        return this;
    }

    /**
     * Ensures all buffered data is written to the underlying {@link Writer}
     * and flushes that writer.
     */
    public void flush() throws IOException {
        if (stack.isEmpty()) {
            throw new IllegalStateException("JsonWriter is closed.");
        }
        out.flush();
    }

    private void string(String value) throws IOException {
        String[] replacements = htmlSafe ? HTML_SAFE_REPLACEMENT_CHARS : REPLACEMENT_CHARS;
        out.write("\"");
        int last = 0;
        int length = value.length();
        for (int i = 0; i < length; i++) {
            char charAt = value.charAt(i);
            String replacement;
            if (charAt < 128) {
                replacement = replacements[charAt];
                if (replacement == null) {
                    continue;
                }
            } else if (charAt == '\u2028') {
                replacement = "\\u2028";
            } else if (charAt == '\u2029') {
                replacement = "\\u2029";
            } else {
                continue;
            }
            if (last < i) {
                out.write(value, last, i - last);
            }
            out.write(replacement);
            last = i + 1;
        }
        if (last < length) {
            out.write(value, last, length - last);
        }
        out.write("\"");
    }

    private void newline() throws IOException {
        if (indent == null) {
            return;
        }

        out.write("\n");
        for (int i = 1; i < stack.size(); i++) {
            out.write(indent);
        }
    }

    /**
     * Inserts any necessary separators and whitespace before a name. Also
     * adjusts the stack to expect the name's value.
     */
    private void beforeName() throws IOException {
        JsonScope context = peek();
        if (context == JsonScope.NONEMPTY_OBJECT) { // first in object
            out.write(',');
        } else if (context != JsonScope.EMPTY_OBJECT) { // not in an object!
            throw new IllegalStateException("Nesting problem: " + stack);
        }
        newline();
        replaceTop(JsonScope.DANGLING_NAME);
    }

    /**
     * Inserts any necessary separators and whitespace before a literal value,
     * inline array, or inline object. Also adjusts the stack to expect either a
     * closing bracket or another element.
     *
     * @param root true if the value is a new array or object, the two values
     *             permitted as top-level elements.
     */
    @SuppressWarnings("fallthrough")
    void beforeValue(boolean root) throws IOException {
        switch (peek()) {
            case NONEMPTY_DOCUMENT:
                if (!lenient) {
                    throw new IllegalStateException(
                            "JSON must have only one top-level value.");
                }
                // fall-through
            case EMPTY_DOCUMENT: // first in document
                if (!lenient && !root) {
                    throw new IllegalStateException(
                            "JSON must start with an array or an object.");
                }
                replaceTop(JsonScope.NONEMPTY_DOCUMENT);
                break;

            case EMPTY_ARRAY: // first in array
                replaceTop(JsonScope.NONEMPTY_ARRAY);
                newline();
                break;

            case NONEMPTY_ARRAY: // another in array
                out.append(',');
                newline();
                break;

            case DANGLING_NAME: // value for name
                out.append(separator);
                replaceTop(JsonScope.NONEMPTY_OBJECT);
                break;

            default:
                throw new IllegalStateException("Nesting problem: " + stack);
        }
    }
}
