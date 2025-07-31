// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SoundEffectConstants;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.R;
import net.pubnative.lite.sdk.utils.ViewUtils;
import net.pubnative.lite.sdk.vpaid.helpers.BitmapHelper;

import java.util.Random;

public class CloseableContainer extends FrameLayout {
    public interface OnCloseListener {
        void onClose();
    }

    public enum ClosePosition {
        TOP_LEFT(Gravity.TOP | Gravity.START),
        TOP_CENTER(Gravity.TOP | Gravity.CENTER_HORIZONTAL),
        TOP_RIGHT(Gravity.TOP | Gravity.END),
        CENTER(Gravity.CENTER),
        BOTTOM_LEFT(Gravity.BOTTOM | Gravity.START),
        BOTTOM_CENTER(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL),
        BOTTOM_RIGHT(Gravity.BOTTOM | Gravity.END),
        RANDOM(0);

        private final int mGravity;

        ClosePosition(final int mGravity) {
            this.mGravity = mGravity;
        }

        int getGravity() {
            return mGravity;
        }

        public static ClosePosition getRandomPosition() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    private static final float CLOSE_REGION_SIZE_DP = 30.0f;
    static final float CLOSE_BUTTON_PADDING_DP = 0.0f;
    static final float CLOSE_BUTTON_PADDING_BORDER_DP = 0.0f;
    private Integer mCustomCloseSize = null;

    private OnCloseListener mOnCloseListener;
    private final ImageButton mCloseButton;
    private ClosePosition mClosePosition;

    public CloseableContainer(Context context) {
        this(context, null, 0);
    }

    public CloseableContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CloseableContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mClosePosition = ClosePosition.TOP_LEFT;

        int paddingPixels = (int) ViewUtils.convertDpToPixel(CLOSE_BUTTON_PADDING_DP, context);
        int paddingBorderPixels = (int) ViewUtils.convertDpToPixel(CLOSE_BUTTON_PADDING_BORDER_DP, context);

        mCloseButton = new ImageButton(context);
        Bitmap closeBitmap = BitmapHelper.toBitmap(context, HyBid.getNormalCloseXmlResource(), R.mipmap.close);
        if (closeBitmap != null) mCloseButton.setImageBitmap(closeBitmap);
        else
            mCloseButton.setImageBitmap(BitmapHelper.decodeResource(mCloseButton.getContext(), R.mipmap.close));
        mCloseButton.setId(R.id.button_fullscreen_close);
        mCloseButton.setBackgroundColor(Color.TRANSPARENT);
        mCloseButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mCloseButton.setPadding(paddingPixels, paddingBorderPixels, paddingBorderPixels, paddingPixels);
        mCloseButton.setOnClickListener(v -> {
            playSoundEffect(SoundEffectConstants.CLICK);
            if (mOnCloseListener != null) {
                mOnCloseListener.onClose();
            }
        });

        //positionButton();
    }

    private void positionButton() {
        LayoutParams layoutParams;
        if (mCustomCloseSize != null) {
            layoutParams = new LayoutParams(mCustomCloseSize, mCustomCloseSize);
            mCloseButton.setId(R.id.button_fullscreen_close_small);
            int margins = (int) ViewUtils.convertDpToPixel(8f, getContext());
            layoutParams.setMargins(margins, margins, margins, margins);
        } else {
            int buttonSize = (int) ViewUtils.convertDpToPixel(CLOSE_REGION_SIZE_DP, getContext());
            layoutParams = new LayoutParams(buttonSize, buttonSize);
        }
        layoutParams.gravity = mClosePosition.getGravity();
        removeView(mCloseButton);
        addView(mCloseButton, layoutParams);
    }

    public void setOnCloseListener(OnCloseListener onCloseListener) {
        mOnCloseListener = onCloseListener;
    }

    public void setClosePosition(ClosePosition closePosition) {
        if (closePosition != null) {
            if (closePosition == ClosePosition.RANDOM) {
                mClosePosition = ClosePosition.getRandomPosition();
            } else if (closePosition == ClosePosition.TOP_LEFT) {
                int paddingPixels = (int) ViewUtils.convertDpToPixel(CLOSE_BUTTON_PADDING_DP, getContext());
                int paddingBorderPixels = (int) ViewUtils.convertDpToPixel(CLOSE_BUTTON_PADDING_BORDER_DP, getContext());
                mClosePosition = closePosition;
                mCloseButton.setPadding(paddingBorderPixels, paddingBorderPixels, paddingPixels, paddingPixels);
            } else {
                mClosePosition = closePosition;
            }
        }
    }

    public void setCloseVisible(boolean visible) {
        if (mCloseButton != null) {
            mCloseButton.setVisibility(visible ? VISIBLE : GONE);
            if (visible) {
                positionButton();
            }
        }
    }

    public void setCloseSize(Integer size) {
        mCustomCloseSize = (int) ViewUtils.convertDpToPixel(size.floatValue(), getContext());
        LayoutParams layoutParams = new LayoutParams(mCustomCloseSize, mCustomCloseSize);
        layoutParams.gravity = mClosePosition.getGravity();
        removeView(mCloseButton);
        addView(mCloseButton, layoutParams);
    }
}
