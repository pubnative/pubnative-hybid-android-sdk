// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Helper class for managing watermark views in ad activities and ad views.
 */
public class WatermarkHelper {

    private boolean mWatermarkRegistered = false;

    /**
     * Creates a watermark view from the provided watermark data.
     * Uses default layout parameters (MATCH_PARENT for both width and height).
     *
     * @param context The context to create the view with
     * @param watermarkData The encoded watermark data
     * @return The created watermark view, or null if the data is invalid
     */
    public static View createWatermarkView(Context context, String watermarkData) {
        if (!TextUtils.isEmpty(watermarkData)) {
            Drawable watermarkDrawable = WatermarkDecoder.decodeWatermark(context, watermarkData);
            if (watermarkDrawable != null) {
                ImageView watermarkView = new ImageView(context);
                watermarkView.setClickable(false);
                watermarkView.setFocusable(false);
                watermarkView.setBackground(watermarkDrawable);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                watermarkView.setLayoutParams(layoutParams);
                return watermarkView;
            }
        }
        return null;
    }

    /**
     * Creates a watermark ImageView from a Drawable with custom gravity.
     * Used for banner ads where the watermark needs specific positioning.
     *
     * @param context The context to create the view with
     * @param watermarkDrawable The watermark drawable
     * @return The created watermark ImageView, or null if the drawable is null
     */
    public static ImageView createWatermarkView(Context context, Drawable watermarkDrawable) {
        if (watermarkDrawable != null) {
            ImageView watermarkView = new ImageView(context);
            watermarkView.setClickable(false);
            watermarkView.setFocusable(false);
            watermarkView.setBackground(watermarkDrawable);

            // Set layout params for the watermark
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.TOP | Gravity.START;
            watermarkView.setLayoutParams(layoutParams);
            return watermarkView;
        }
        return null;
    }

    /**
     * Decodes a watermark string into a Drawable.
     *
     * @param context The context to decode with
     * @param watermarkString The encoded watermark string
     * @return The decoded Drawable, or null if decoding fails
     */
    public static Drawable decodeWatermark(Context context, String watermarkString) {
        return WatermarkDecoder.decodeWatermark(context, watermarkString);
    }

    /**
     * Removes a watermark view from its parent and cleans up resources.
     *
     * @param watermarkView The watermark ImageView to remove
     * @return true if the watermark was removed, false otherwise
     */
    public static boolean removeWatermarkView(ImageView watermarkView) {
        if (watermarkView != null) {
            if (watermarkView.getParent() != null && watermarkView.getParent() instanceof ViewGroup) {
                ((ViewGroup) watermarkView.getParent()).removeView(watermarkView);
            }
            watermarkView.setImageDrawable(null);
            return true;
        }
        return false;
    }

    /**
     * Checks if the watermark has been registered.
     *
     * @return true if the watermark is registered, false otherwise
     */
    public boolean isWatermarkRegistered() {
        return mWatermarkRegistered;
    }

    /**
     * Marks the watermark as registered.
     */
    public void setWatermarkRegistered() {
        mWatermarkRegistered = true;
    }

    /**
     * Resets the watermark registration state.
     */
    public void reset() {
        mWatermarkRegistered = false;
    }
}

