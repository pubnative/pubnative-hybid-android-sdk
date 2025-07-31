// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.views.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class ImageHelper {

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int cornerRadius) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, (float) cornerRadius, (float) cornerRadius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int cornerRadius, int width, int height) {

        Bitmap resultBitmap;
        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();

        if(Math.abs(((float) imgWidth / imgHeight) - ((float) width / height)) <= 0.2 ){
            float scaleWidth = ((float) width) / imgWidth;
            float scaleHeight = ((float) height) / imgHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, imgWidth, imgHeight, matrix, false);
            bitmap.recycle();
        } else {
            float scaleHeight = ((float) height) / imgHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleHeight, scaleHeight);
            resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, imgWidth, imgHeight, matrix, false);
            float startX = (float) resultBitmap.getWidth() / 2 - (float) width / 2;
            resultBitmap = Bitmap.createBitmap(resultBitmap, (int) startX, 0, width, height);
            bitmap.recycle();
        }

        return getRoundedCornerBitmap(resultBitmap, cornerRadius);
    }
}
