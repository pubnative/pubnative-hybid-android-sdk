// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.utils;

import android.graphics.Bitmap;
import java.io.File;

/**
 * Defines a contract for decoding a file into a Bitmap.
 * This allows us to replace the real Android implementation with a mock in tests.
 */
public interface BitmapDecoder {
    Bitmap decodeFile(File f, int reqWidth, int reqHeight);
}
