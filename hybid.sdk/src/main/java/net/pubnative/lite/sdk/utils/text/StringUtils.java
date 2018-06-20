/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.pubnative.lite.sdk.utils.text;

public class StringUtils {

    public static final String SPACE = " ";

    public static final String EMPTY = "";

    public static final int INDEX_NOT_FOUND = -1;

    private static final int PAD_LIMIT = 8192;

    public StringUtils() {
        super();
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * <p>Checks if a CharSequence is empty (""), null or whitespace only.</p>
     * <p>
     * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     * <p>
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace only
     * @since 2.0
     * @since 3.0 Changed signature from isBlank(String) to isBlank(CharSequence)
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // Left/Right/Mid
    //-----------------------------------------------------------------------

    /**
     * <p>Gets the leftmost {@code len} characters of a String.</p>
     * <p>
     * <p>If {@code len} characters are not available, or the
     * String is {@code null}, the String will be returned without
     * an exception. An empty String is returned if len is negative.</p>
     * <p>
     * <pre>
     * StringUtils.left(null, *)    = null
     * StringUtils.left(*, -ve)     = ""
     * StringUtils.left("", *)      = ""
     * StringUtils.left("abc", 0)   = ""
     * StringUtils.left("abc", 2)   = "ab"
     * StringUtils.left("abc", 4)   = "abc"
     * </pre>
     *
     * @param str the String to get the leftmost characters from, may be null
     * @param len the length of the required String
     * @return the leftmost characters, {@code null} if null String input
     */
    public static String left(final String str, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    /**
     * <p>Gets the rightmost {@code len} characters of a String.</p>
     * <p>
     * <p>If {@code len} characters are not available, or the String
     * is {@code null}, the String will be returned without an
     * an exception. An empty String is returned if len is negative.</p>
     * <p>
     * <pre>
     * StringUtils.right(null, *)    = null
     * StringUtils.right(*, -ve)     = ""
     * StringUtils.right("", *)      = ""
     * StringUtils.right("abc", 0)   = ""
     * StringUtils.right("abc", 2)   = "bc"
     * StringUtils.right("abc", 4)   = "abc"
     * </pre>
     *
     * @param str the String to get the rightmost characters from, may be null
     * @param len the length of the required String
     * @return the rightmost characters, {@code null} if null String input
     */
    public static String right(final String str, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }

    /**
     * <p>Removes all occurrences of a substring from within the source string.</p>
     * <p>
     * <p>A {@code null} source string will return {@code null}.
     * An empty ("") source string will return the empty string.
     * A {@code null} remove string will return the source string.
     * An empty ("") remove string will return the source string.</p>
     * <p>
     * <pre>
     * StringUtils.remove(null, *)        = null
     * StringUtils.remove("", *)          = ""
     * StringUtils.remove(*, null)        = *
     * StringUtils.remove(*, "")          = *
     * StringUtils.remove("queued", "ue") = "qd"
     * StringUtils.remove("queued", "zz") = "queued"
     * </pre>
     *
     * @param str    the source String to search, may be null
     * @param remove the String to search for and remove, may be null
     * @return the substring with the string removed if found,
     * {@code null} if null String input
     * @since 2.1
     */
    public static String remove(final String str, final String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        return replace(str, remove, EMPTY, -1);
    }

    /**
     * <p>Removes all occurrences of a character from within the source string.</p>
     * <p>
     * <p>A {@code null} source string will return {@code null}.
     * An empty ("") source string will return the empty string.</p>
     * <p>
     * <pre>
     * StringUtils.remove(null, *)       = null
     * StringUtils.remove("", *)         = ""
     * StringUtils.remove("queued", 'u') = "qeed"
     * StringUtils.remove("queued", 'z') = "queued"
     * </pre>
     *
     * @param str    the source String to search, may be null
     * @param remove the char to search for and remove, may be null
     * @return the substring with the char removed if found,
     * {@code null} if null String input
     * @since 2.1
     */
    public static String remove(final String str, final char remove) {
        if (isEmpty(str) || str.indexOf(remove) == INDEX_NOT_FOUND) {
            return str;
        }
        final char[] chars = str.toCharArray();
        int pos = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != remove) {
                chars[pos++] = chars[i];
            }
        }
        return new String(chars, 0, pos);
    }

    /**
     * <p>Replaces a String with another String inside a larger String,
     * for the first {@code max} values of the search String.</p>
     * <p>
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     * <p>
     * <pre>
     * StringUtils.replace(null, *, *, *)         = null
     * StringUtils.replace("", *, *, *)           = ""
     * StringUtils.replace("any", null, *, *)     = "any"
     * StringUtils.replace("any", *, null, *)     = "any"
     * StringUtils.replace("any", "", *, *)       = "any"
     * StringUtils.replace("any", *, *, 0)        = "any"
     * StringUtils.replace("abaa", "a", null, -1) = "abaa"
     * StringUtils.replace("abaa", "a", "", -1)   = "b"
     * StringUtils.replace("abaa", "a", "z", 0)   = "abaa"
     * StringUtils.replace("abaa", "a", "z", 1)   = "zbaa"
     * StringUtils.replace("abaa", "a", "z", 2)   = "zbza"
     * StringUtils.replace("abaa", "a", "z", -1)  = "zbzz"
     * </pre>
     *
     * @param text         text to search and replace in, may be null
     * @param searchString the String to search for, may be null
     * @param replacement  the String to replace it with, may be null
     * @param max          maximum number of values to replace, or {@code -1} if no maximum
     * @return the text with any replacements processed,
     * {@code null} if null String input
     */
    public static String replace(final String text, final String searchString, final String replacement, final int max) {
        return replace(text, searchString, replacement, max, false);
    }

    /**
     * <p>Replaces a String with another String inside a larger String,
     * for the first {@code max} values of the search String,
     * case sensitively/insensisitively based on {@code ignoreCase} value.</p>
     * <p>
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     * <p>
     * <pre>
     * StringUtils.replace(null, *, *, *, false)         = null
     * StringUtils.replace("", *, *, *, false)           = ""
     * StringUtils.replace("any", null, *, *, false)     = "any"
     * StringUtils.replace("any", *, null, *, false)     = "any"
     * StringUtils.replace("any", "", *, *, false)       = "any"
     * StringUtils.replace("any", *, *, 0, false)        = "any"
     * StringUtils.replace("abaa", "a", null, -1, false) = "abaa"
     * StringUtils.replace("abaa", "a", "", -1, false)   = "b"
     * StringUtils.replace("abaa", "a", "z", 0, false)   = "abaa"
     * StringUtils.replace("abaa", "A", "z", 1, false)   = "abaa"
     * StringUtils.replace("abaa", "A", "z", 1, true)   = "zbaa"
     * StringUtils.replace("abAa", "a", "z", 2, true)   = "zbza"
     * StringUtils.replace("abAa", "a", "z", -1, true)  = "zbzz"
     * </pre>
     *
     * @param text         text to search and replace in, may be null
     * @param searchString the String to search for (case insensitive), may be null
     * @param replacement  the String to replace it with, may be null
     * @param max          maximum number of values to replace, or {@code -1} if no maximum
     * @param ignoreCase   if true replace is case insensitive, otherwise case sensitive
     * @return the text with any replacements processed,
     * {@code null} if null String input
     */
    private static String replace(final String text, String searchString, final String replacement, int max, final boolean ignoreCase) {
        if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
            return text;
        }
        String searchText = text;
        if (ignoreCase) {
            searchText = text.toLowerCase();
            searchString = searchString.toLowerCase();
        }
        int start = 0;
        int end = searchText.indexOf(searchString, start);
        if (end == INDEX_NOT_FOUND) {
            return text;
        }
        final int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = increase < 0 ? 0 : increase;
        increase *= max < 0 ? 16 : max > 64 ? 64 : max;
        final StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != INDEX_NOT_FOUND) {
            buf.append(text, start, end).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = searchText.indexOf(searchString, start);
        }
        buf.append(text, start, text.length());
        return buf.toString();
    }

    public static String repeat(final char ch, final int repeat) {
        if (repeat <= 0) {
            return EMPTY;
        }
        final char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }

    /**
     * <p>Right pad a String with a specified character.</p>
     * <p>
     * <p>The String is padded to the size of {@code size}.</p>
     * <p>
     * <pre>
     * StringUtils.rightPad(null, *, *)     = null
     * StringUtils.rightPad("", 3, 'z')     = "zzz"
     * StringUtils.rightPad("bat", 3, 'z')  = "bat"
     * StringUtils.rightPad("bat", 5, 'z')  = "batzz"
     * StringUtils.rightPad("bat", 1, 'z')  = "bat"
     * StringUtils.rightPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str     the String to pad out, may be null
     * @param size    the size to pad to
     * @param padChar the character to pad with
     * @return right padded String or original String if no padding is necessary,
     * {@code null} if null String input
     * @since 2.0
     */
    public static String rightPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(repeat(padChar, pads));
    }

    /**
     * <p>Right pad a String with a specified String.</p>
     * <p>
     * <p>The String is padded to the size of {@code size}.</p>
     * <p>
     * <pre>
     * StringUtils.rightPad(null, *, *)      = null
     * StringUtils.rightPad("", 3, "z")      = "zzz"
     * StringUtils.rightPad("bat", 3, "yz")  = "bat"
     * StringUtils.rightPad("bat", 5, "yz")  = "batyz"
     * StringUtils.rightPad("bat", 8, "yz")  = "batyzyzy"
     * StringUtils.rightPad("bat", 1, "yz")  = "bat"
     * StringUtils.rightPad("bat", -1, "yz") = "bat"
     * StringUtils.rightPad("bat", 5, null)  = "bat  "
     * StringUtils.rightPad("bat", 5, "")    = "bat  "
     * </pre>
     *
     * @param str    the String to pad out, may be null
     * @param size   the size to pad to
     * @param padStr the String to pad with, null or empty treated as single space
     * @return right padded String or original String if no padding is necessary,
     * {@code null} if null String input
     */
    public static String rightPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return rightPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }

    /**
     * <p>Left pad a String with a specified character.</p>
     * <p>
     * <p>Pad to a size of {@code size}.</p>
     * <p>
     * <pre>
     * StringUtils.leftPad(null, *, *)     = null
     * StringUtils.leftPad("", 3, 'z')     = "zzz"
     * StringUtils.leftPad("bat", 3, 'z')  = "bat"
     * StringUtils.leftPad("bat", 5, 'z')  = "zzbat"
     * StringUtils.leftPad("bat", 1, 'z')  = "bat"
     * StringUtils.leftPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str     the String to pad out, may be null
     * @param size    the size to pad to
     * @param padChar the character to pad with
     * @return left padded String or original String if no padding is necessary,
     * {@code null} if null String input
     * @since 2.0
     */
    public static String leftPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return repeat(padChar, pads).concat(str);
    }

    /**
     * <p>Left pad a String with a specified String.</p>
     * <p>
     * <p>Pad to a size of {@code size}.</p>
     * <p>
     * <pre>
     * StringUtils.leftPad(null, *, *)      = null
     * StringUtils.leftPad("", 3, "z")      = "zzz"
     * StringUtils.leftPad("bat", 3, "yz")  = "bat"
     * StringUtils.leftPad("bat", 5, "yz")  = "yzbat"
     * StringUtils.leftPad("bat", 8, "yz")  = "yzyzybat"
     * StringUtils.leftPad("bat", 1, "yz")  = "bat"
     * StringUtils.leftPad("bat", -1, "yz") = "bat"
     * StringUtils.leftPad("bat", 5, null)  = "  bat"
     * StringUtils.leftPad("bat", 5, "")    = "  bat"
     * </pre>
     *
     * @param str    the String to pad out, may be null
     * @param size   the size to pad to
     * @param padStr the String to pad with, null or empty treated as single space
     * @return left padded String or original String if no padding is necessary,
     * {@code null} if null String input
     */
    public static String leftPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return leftPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    /**
     * Gets a CharSequence length or {@code 0} if the CharSequence is
     * {@code null}.
     *
     * @param cs a CharSequence or {@code null}
     * @return CharSequence length or {@code 0} if the CharSequence is
     * {@code null}.
     * @since 2.4
     * @since 3.0 Changed signature from length(String) to length(CharSequence)
     */
    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    // Centering
    //-----------------------------------------------------------------------

    /**
     * <p>Centers a String in a larger String of size {@code size}
     * using the space character (' ').</p>
     * <p>
     * <p>If the size is less than the String length, the String is returned.
     * A {@code null} String returns {@code null}.
     * A negative size is treated as zero.</p>
     * <p>
     * <p>Equivalent to {@code center(str, size, " ")}.</p>
     * <p>
     * <pre>
     * StringUtils.center(null, *)   = null
     * StringUtils.center("", 4)     = "    "
     * StringUtils.center("ab", -1)  = "ab"
     * StringUtils.center("ab", 4)   = " ab "
     * StringUtils.center("abcd", 2) = "abcd"
     * StringUtils.center("a", 4)    = " a  "
     * </pre>
     *
     * @param str  the String to center, may be null
     * @param size the int size of new String, negative treated as zero
     * @return centered String, {@code null} if null String input
     */
    public static String center(final String str, final int size) {
        return center(str, size, ' ');
    }

    /**
     * <p>Centers a String in a larger String of size {@code size}.
     * Uses a supplied character as the value to pad the String with.</p>
     * <p>
     * <p>If the size is less than the String length, the String is returned.
     * A {@code null} String returns {@code null}.
     * A negative size is treated as zero.</p>
     * <p>
     * <pre>
     * StringUtils.center(null, *, *)     = null
     * StringUtils.center("", 4, ' ')     = "    "
     * StringUtils.center("ab", -1, ' ')  = "ab"
     * StringUtils.center("ab", 4, ' ')   = " ab "
     * StringUtils.center("abcd", 2, ' ') = "abcd"
     * StringUtils.center("a", 4, ' ')    = " a  "
     * StringUtils.center("a", 4, 'y')    = "yayy"
     * </pre>
     *
     * @param str     the String to center, may be null
     * @param size    the int size of new String, negative treated as zero
     * @param padChar the character to pad the new String with
     * @return centered String, {@code null} if null String input
     * @since 2.0
     */
    public static String center(String str, final int size, final char padChar) {
        if (str == null || size <= 0) {
            return str;
        }
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padChar);
        str = rightPad(str, size, padChar);
        return str;
    }

    /**
     * <p>Centers a String in a larger String of size {@code size}.
     * Uses a supplied String as the value to pad the String with.</p>
     * <p>
     * <p>If the size is less than the String length, the String is returned.
     * A {@code null} String returns {@code null}.
     * A negative size is treated as zero.</p>
     * <p>
     * <pre>
     * StringUtils.center(null, *, *)     = null
     * StringUtils.center("", 4, " ")     = "    "
     * StringUtils.center("ab", -1, " ")  = "ab"
     * StringUtils.center("ab", 4, " ")   = " ab "
     * StringUtils.center("abcd", 2, " ") = "abcd"
     * StringUtils.center("a", 4, " ")    = " a  "
     * StringUtils.center("a", 4, "yz")   = "yayz"
     * StringUtils.center("abc", 7, null) = "  abc  "
     * StringUtils.center("abc", 7, "")   = "  abc  "
     * </pre>
     *
     * @param str    the String to center, may be null
     * @param size   the int size of new String, negative treated as zero
     * @param padStr the String to pad the new String with, must not be null or empty
     * @return centered String, {@code null} if null String input
     * @throws IllegalArgumentException if padStr is {@code null} or empty
     */
    public static String center(String str, final int size, String padStr) {
        if (str == null || size <= 0) {
            return str;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padStr);
        str = rightPad(str, size, padStr);
        return str;
    }

    /**
     * <p>Abbreviates a String using another given String as replacement marker. This will turn
     * "Now is the time for all good men" into "Now is the time for..." if "..." was defined
     * as the replacement marker.</p>
     * <p>
     * <p>Specifically:</p>
     * <ul>
     * <li>If the number of characters in {@code str} is less than or equal to
     * {@code maxWidth}, return {@code str}.</li>
     * <li>Else abbreviate it to {@code (substring(str, 0, max-abbrevMarker.length) + abbrevMarker)}.</li>
     * <li>If {@code maxWidth} is less than {@code abbrevMarker.length + 1}, throw an
     * {@code IllegalArgumentException}.</li>
     * <li>In no case will it return a String of length greater than
     * {@code maxWidth}.</li>
     * </ul>
     * <p>
     * <pre>
     * StringUtils.abbreviate(null, "...", *)      = null
     * StringUtils.abbreviate("abcdefg", null, *)  = "abcdefg"
     * StringUtils.abbreviate("", "...", 4)        = ""
     * StringUtils.abbreviate("abcdefg", ".", 5)   = "abcd."
     * StringUtils.abbreviate("abcdefg", ".", 7)   = "abcdefg"
     * StringUtils.abbreviate("abcdefg", ".", 8)   = "abcdefg"
     * StringUtils.abbreviate("abcdefg", "..", 4)  = "ab.."
     * StringUtils.abbreviate("abcdefg", "..", 3)  = "a.."
     * StringUtils.abbreviate("abcdefg", "..", 2)  = IllegalArgumentException
     * StringUtils.abbreviate("abcdefg", "...", 3) = IllegalArgumentException
     * </pre>
     *
     * @param str          the String to check, may be null
     * @param abbrevMarker the String used as replacement marker
     * @param maxWidth     maximum length of result String, must be at least {@code abbrevMarker.length + 1}
     * @return abbreviated String, {@code null} if null String input
     * @throws IllegalArgumentException if the width is too small
     * @since 3.6
     */
    public static String abbreviate(final String str, final String abbrevMarker, final int maxWidth) {
        return abbreviate(str, abbrevMarker, 0, maxWidth);
    }

    /**
     * <p>Abbreviates a String using a given replacement marker. This will turn
     * "Now is the time for all good men" into "...is the time for..." if "..." was defined
     * as the replacement marker.</p>
     * <p>
     * <p>Works like {@code abbreviate(String, String, int)}, but allows you to specify
     * a "left edge" offset.  Note that this left edge is not necessarily going to
     * be the leftmost character in the result, or the first character following the
     * replacement marker, but it will appear somewhere in the result.
     * <p>
     * <p>In no case will it return a String of length greater than {@code maxWidth}.</p>
     * <p>
     * <pre>
     * StringUtils.abbreviate(null, null, *, *)                 = null
     * StringUtils.abbreviate("abcdefghijklmno", null, *, *)    = "abcdefghijklmno"
     * StringUtils.abbreviate("", "...", 0, 4)                  = ""
     * StringUtils.abbreviate("abcdefghijklmno", "---", -1, 10) = "abcdefg---"
     * StringUtils.abbreviate("abcdefghijklmno", ",", 0, 10)    = "abcdefghi,"
     * StringUtils.abbreviate("abcdefghijklmno", ",", 1, 10)    = "abcdefghi,"
     * StringUtils.abbreviate("abcdefghijklmno", ",", 2, 10)    = "abcdefghi,"
     * StringUtils.abbreviate("abcdefghijklmno", "::", 4, 10)   = "::efghij::"
     * StringUtils.abbreviate("abcdefghijklmno", "...", 6, 10)  = "...ghij..."
     * StringUtils.abbreviate("abcdefghijklmno", "*", 9, 10)    = "*ghijklmno"
     * StringUtils.abbreviate("abcdefghijklmno", "'", 10, 10)   = "'ghijklmno"
     * StringUtils.abbreviate("abcdefghijklmno", "!", 12, 10)   = "!ghijklmno"
     * StringUtils.abbreviate("abcdefghij", "abra", 0, 4)       = IllegalArgumentException
     * StringUtils.abbreviate("abcdefghij", "...", 5, 6)        = IllegalArgumentException
     * </pre>
     *
     * @param str          the String to check, may be null
     * @param abbrevMarker the String used as replacement marker
     * @param offset       left edge of source String
     * @param maxWidth     maximum length of result String, must be at least 4
     * @return abbreviated String, {@code null} if null String input
     * @throws IllegalArgumentException if the width is too small
     * @since 3.6
     */
    public static String abbreviate(final String str, final String abbrevMarker, int offset, final int maxWidth) {
        if (isEmpty(str) || isEmpty(abbrevMarker)) {
            return str;
        }

        final int abbrevMarkerLength = abbrevMarker.length();
        final int minAbbrevWidth = abbrevMarkerLength + 1;
        final int minAbbrevWidthOffset = abbrevMarkerLength + abbrevMarkerLength + 1;

        if (maxWidth < minAbbrevWidth) {
            throw new IllegalArgumentException(String.format("Minimum abbreviation width is %d", minAbbrevWidth));
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        if (offset > str.length()) {
            offset = str.length();
        }
        if (str.length() - offset < maxWidth - abbrevMarkerLength) {
            offset = str.length() - (maxWidth - abbrevMarkerLength);
        }
        if (offset <= abbrevMarkerLength + 1) {
            return str.substring(0, maxWidth - abbrevMarkerLength) + abbrevMarker;
        }
        if (maxWidth < minAbbrevWidthOffset) {
            throw new IllegalArgumentException(String.format("Minimum abbreviation width with offset is %d", minAbbrevWidthOffset));
        }
        if (offset + maxWidth - abbrevMarkerLength < str.length()) {
            return abbrevMarker + abbreviate(str.substring(offset), abbrevMarker, maxWidth - abbrevMarkerLength);
        }
        return abbrevMarker + str.substring(str.length() - (maxWidth - abbrevMarkerLength));
    }

    // Misc
    //-----------------------------------------------------------------------

    /**
     * <p>
     * Wraps a string with a char.
     * </p>
     * <p>
     * <pre>
     * StringUtils.wrap(null, *)        = null
     * StringUtils.wrap("", *)          = ""
     * StringUtils.wrap("ab", '\0')     = "ab"
     * StringUtils.wrap("ab", 'x')      = "xabx"
     * StringUtils.wrap("ab", '\'')     = "'ab'"
     * StringUtils.wrap("\"ab\"", '\"') = "\"\"ab\"\""
     * </pre>
     *
     * @param str      the string to be wrapped, may be {@code null}
     * @param wrapWith the char that will wrap {@code str}
     * @return the wrapped string, or {@code null} if {@code str==null}
     * @since 3.4
     */
    public static String wrap(final String str, final char wrapWith) {

        if (isEmpty(str) || wrapWith == CharUtils.NUL) {
            return str;
        }

        return wrapWith + str + wrapWith;
    }

    /**
     * <p>
     * Wraps a String with another String.
     * </p>
     * <p>
     * <p>
     * A {@code null} input String returns {@code null}.
     * </p>
     * <p>
     * <pre>
     * StringUtils.wrap(null, *)         = null
     * StringUtils.wrap("", *)           = ""
     * StringUtils.wrap("ab", null)      = "ab"
     * StringUtils.wrap("ab", "x")       = "xabx"
     * StringUtils.wrap("ab", "\"")      = "\"ab\""
     * StringUtils.wrap("\"ab\"", "\"")  = "\"\"ab\"\""
     * StringUtils.wrap("ab", "'")       = "'ab'"
     * StringUtils.wrap("'abcd'", "'")   = "''abcd''"
     * StringUtils.wrap("\"abcd\"", "'") = "'\"abcd\"'"
     * StringUtils.wrap("'abcd'", "\"")  = "\"'abcd'\""
     * </pre>
     *
     * @param str      the String to be wrapper, may be null
     * @param wrapWith the String that will wrap str
     * @return wrapped String, {@code null} if null String input
     * @since 3.4
     */
    public static String wrap(final String str, final String wrapWith) {

        if (isEmpty(str) || isEmpty(wrapWith)) {
            return str;
        }

        return wrapWith.concat(str).concat(wrapWith);
    }
}
