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

    private final Writer out;

    private final List<JsonScope> stack = new ArrayList<>();

    {
        stack.add(JsonScope.EMPTY_DOCUMENT);
    }

    private String indent;

    private String separator = ":";

    private boolean lenient;

    private boolean htmlSafe;

    private String deferredName;

    private boolean serializeNulls = true;

    public JsonWriter(Writer out) {
        if (out == null) {
            throw new NullPointerException("out == null");
        }
        this.out = out;
    }

    public final void setIndent(String indent) {
        if (indent.length() == 0) {
            this.indent = null;
            this.separator = ":";
        } else {
            this.indent = indent;
            this.separator = ": ";
        }
    }

    public final void setSerializeNulls(boolean serializeNulls) {
        this.serializeNulls = serializeNulls;
    }

    public JsonWriter beginArray() throws IOException {
        writeDeferredName();
        return open(JsonScope.EMPTY_ARRAY, "[");
    }

    public JsonWriter endArray() throws IOException {
        return close(JsonScope.EMPTY_ARRAY, JsonScope.NONEMPTY_ARRAY, "]");
    }

    public JsonWriter beginObject() throws IOException {
        writeDeferredName();
        return open(JsonScope.EMPTY_OBJECT, "{");
    }

    public JsonWriter endObject() throws IOException {
        return close(JsonScope.EMPTY_OBJECT, JsonScope.NONEMPTY_OBJECT, "}");
    }

    private JsonWriter open(JsonScope empty, String openBracket) throws IOException {
        beforeValue(true);
        stack.add(empty);
        out.write(openBracket);
        return this;
    }

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

    public void close() throws IOException {
        out.close();

        int size = stack.size();
        if (size > 1 || size == 1 && stack.get(size - 1) != JsonScope.NONEMPTY_DOCUMENT) {
            throw new IOException("Incomplete document");
        }
        stack.clear();
    }

    private JsonScope peek() {
        int size = stack.size();
        if (size == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        }
        return stack.get(size - 1);
    }

    private void replaceTop(JsonScope topOfStack) {
        stack.set(stack.size() - 1, topOfStack);
    }

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

    public JsonWriter value(String value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        writeDeferredName();
        beforeValue(false);
        string(value);
        return this;
    }

    public JsonWriter value(boolean value) throws IOException {
        writeDeferredName();
        beforeValue(false);
        out.write(value ? "true" : "false");
        return this;
    }

    public JsonWriter value(Boolean value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        writeDeferredName();
        beforeValue(false);
        out.write(value ? "true" : "false");
        return this;
    }

    public JsonWriter value(double value) throws IOException {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
        }
        writeDeferredName();
        beforeValue(false);
        out.append(Double.toString(value));
        return this;
    }

    public JsonWriter value(long value) throws IOException {
        writeDeferredName();
        beforeValue(false);
        out.write(Long.toString(value));
        return this;
    }

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
