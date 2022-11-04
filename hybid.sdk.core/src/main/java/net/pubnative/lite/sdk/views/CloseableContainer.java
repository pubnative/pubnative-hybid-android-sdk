package net.pubnative.lite.sdk.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SoundEffectConstants;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.core.R;
import net.pubnative.lite.sdk.utils.DrawableResources;
import net.pubnative.lite.sdk.utils.ViewUtils;

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

    private static final float CLOSE_REGION_SIZE_DP = 50.0f;
    static final float CLOSE_BUTTON_PADDING_DP = 8.0f;

    private OnCloseListener mOnCloseListener;
    private final StateListDrawable mCloseDrawable;
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

        mCloseDrawable = new StateListDrawable();
        mClosePosition = ClosePosition.TOP_RIGHT;
        DrawableResources drawableResources = new DrawableResources();
        mCloseDrawable.addState(SELECTED_STATE_SET,
                drawableResources.createDrawable(context, HyBid.getPressedCloseXmlResource(),
                        DrawableResources.DrawableState.PRESSED));
        mCloseDrawable.addState(EMPTY_STATE_SET,
                drawableResources.createDrawable(context, HyBid.getNormalCloseXmlResource(),
                        DrawableResources.DrawableState.NORMAL));
        mCloseDrawable.setState(EMPTY_STATE_SET);
        mCloseDrawable.setCallback(this);

        int paddingPixels = (int) ViewUtils.convertDpToPixel(CLOSE_BUTTON_PADDING_DP, context);

        mCloseButton = new ImageButton(context);
        mCloseButton.setImageDrawable(mCloseDrawable);
        mCloseButton.setId(R.id.button_fullscreen_close);
        mCloseButton.setBackgroundColor(Color.TRANSPARENT);
        mCloseButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mCloseButton.setPadding(paddingPixels, paddingPixels, paddingPixels, paddingPixels);
        mCloseButton.setOnClickListener(v -> {
            playSoundEffect(SoundEffectConstants.CLICK);
            if (mOnCloseListener != null) {
                mOnCloseListener.onClose();
            }
        });

        //positionButton();
    }

    private void positionButton() {
        int buttonSize = (int) ViewUtils.convertDpToPixel(CLOSE_REGION_SIZE_DP, getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(buttonSize, buttonSize);
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
}
