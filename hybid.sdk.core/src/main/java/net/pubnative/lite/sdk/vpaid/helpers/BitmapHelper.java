// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.pubnative.lite.sdk.HyBid;

public class BitmapHelper {

    public static Bitmap toBitmap(Context context, Integer resId, Integer defaultResource) {
        if (context == null || resId == null) {
            return null;
        }

        try {
            return BitmapFactory.decodeResource(context.getResources(), resId);
        } catch (Exception ex) {
            HyBid.reportException(ex);
            return BitmapFactory.decodeResource(context.getResources(), defaultResource);
        }
    }

    public static Bitmap decodeResource(Context context, Integer defaultResource) {
        return BitmapFactory.decodeResource(context.getResources(), defaultResource);
    }
}
