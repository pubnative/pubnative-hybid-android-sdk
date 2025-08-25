// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//


package net.pubnative.lite.sdk.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import net.pubnative.lite.sdk.utils.DrawableResources;

@RunWith(AndroidJUnit4ClassRunner.class)
public class DrawableResourcesTest {

    private Context context;
    private DrawableResources drawableResources;

    @Test
    public void testCreateDrawable_normalState_base64Fallback() {
        context = ApplicationProvider.getApplicationContext();
        drawableResources = new DrawableResources();

        Drawable drawable = drawableResources.createDrawable(context, -1, DrawableResources.DrawableState.NORMAL);

        assertNotNull(drawable);
        assertTrue(drawable instanceof BitmapDrawable);

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        assertNotNull(bitmap);
        assertTrue(bitmap.getWidth() > 0 && bitmap.getHeight() > 0);
    }

    @Test
    public void testCreateDrawable_pressedState_base64Fallback() {
        context = ApplicationProvider.getApplicationContext();
        drawableResources = new DrawableResources();

        Drawable drawable = drawableResources.createDrawable(context, -1, DrawableResources.DrawableState.PRESSED);

        assertNotNull(drawable);
        assertTrue(drawable instanceof BitmapDrawable);

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        assertNotNull(bitmap);
        assertTrue(bitmap.getWidth() > 0 && bitmap.getHeight() > 0);
    }

    @Test
    public void testGetBitmap_withNullResource_returnsBase64Image() {
        context = ApplicationProvider.getApplicationContext();
        drawableResources = new DrawableResources();

        Bitmap bitmap = drawableResources.getBitmap(context, null, DrawableResources.DrawableState.NORMAL);
        assertNotNull(bitmap);
        assertTrue(bitmap.getWidth() > 0 && bitmap.getHeight() > 0);
    }
}