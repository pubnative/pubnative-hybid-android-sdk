// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.string;

import java.io.IOException;
import java.io.Writer;

/**
 * Helper subclass to CharSequenceTranslator to allow for translations that
 * will replace up to one character at a time.
 *
 * @since 1.0
 */
public abstract class CodePointTranslator extends CharSequenceTranslator {

    /**
     * Implementation of translate that maps onto the abstract translate(int, Writer) method.
     * {@inheritDoc}
     */
    @Override
    public final int translate(final CharSequence input, final int index, final Writer writer) throws IOException {
        final int codePoint = Character.codePointAt(input, index);
        final boolean consumed = translate(codePoint, writer);
        return consumed ? 1 : 0;
    }

    /**
     * Translates the specified code point into another.
     *
     * @param codePoint int character input to translate
     * @param writer    Writer to optionally push the translated output to
     * @return boolean as to whether translation occurred or not
     * @throws IOException if and only if the Writer produces an IOException
     */
    public abstract boolean translate(int codePoint, Writer writer) throws IOException;

}
