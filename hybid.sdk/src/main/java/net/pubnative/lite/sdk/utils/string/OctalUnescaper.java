// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.string;

import java.io.IOException;
import java.io.Writer;

/**
 * Translate escaped octal Strings back to their octal values.
 * <p>
 * For example, "\45" should go back to being the specific value (a %).
 * <p>
 * Note that this currently only supports the viable range of octal for Java; namely
 * 1 to 377. This is because parsing Java is the main use case.
 *
 * @since 1.0
 */
public class OctalUnescaper extends CharSequenceTranslator {

    /**
     * Tests if the given char is an octal digit. Octal digits are the character representations of the digits 0 to 7.
     *
     * @param ch the char to check
     * @return true if the given char is the character representation of one of the digits from 0 to 7
     */
    private boolean isOctalDigit(final char ch) {
        return ch >= '0' && ch <= '7';
    }

    /**
     * Tests if the given char is the character representation of one of the digit from 0 to 3.
     *
     * @param ch the char to check
     * @return true if the given char is the character representation of one of the digits from 0 to 3
     */
    private boolean isZeroToThree(final char ch) {
        return ch >= '0' && ch <= '3';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int translate(final CharSequence input, final int index, final Writer writer) throws IOException {
        final int remaining = input.length() - index - 1; // how many characters left, ignoring the first \
        final StringBuilder builder = new StringBuilder();
        if (input.charAt(index) == '\\' && remaining > 0 && isOctalDigit(input.charAt(index + 1))) {
            final int next = index + 1;
            final int next2 = index + 2;
            final int next3 = index + 3;

            // we know this is good as we checked it in the if block above
            builder.append(input.charAt(next));

            if (remaining > 1 && isOctalDigit(input.charAt(next2))) {
                builder.append(input.charAt(next2));
                if (remaining > 2 && isZeroToThree(input.charAt(next)) && isOctalDigit(input.charAt(next3))) {
                    builder.append(input.charAt(next3));
                }
            }

            writer.write(Integer.parseInt(builder.toString(), 8));
            return 1 + builder.length();
        }
        return 0;
    }
}