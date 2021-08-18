package net.pubnative.lite.sdk.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import net.pubnative.lite.sdk.utils.DrawableResources;
import net.pubnative.lite.sdk.utils.ViewUtils;

import java.util.Random;

public class CloseableContainer extends FrameLayout {
    public interface OnCloseListener {
        void onClose();
    }

    static final float CLOSE_BUTTON_SIZE_DP = 30.0f;
    static final float CLOSE_REGION_SIZE_DP = 50.0f;

    static final float CLOSE_BUTTON_PADDING_DP = 8.0f;

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

    // Used in onTouchEvent to be lenient about moving outside the close button bounds. This is the
    // same pattern used in the Android framework to handle click events.
    private final int mTouchSlop;

    private OnCloseListener mOnCloseListener;

    private final StateListDrawable mCloseDrawable;
    private ClosePosition mClosePosition;
    private final int mCloseRegionSize;  // Size of the touchable close region.
    private final int mCloseButtonSize;  // Size of the drawn close button.
    private final int mCloseButtonPadding;

    // Whether we need to recalculate the close bounds on the next draw pass
    private boolean mCloseBoundChanged;

    // Hang on to our bounds Rects so we don't allocate memory in the draw() method.
    private final Rect mClosableLayoutRect = new Rect();
    private final Rect mCloseRegionBounds = new Rect();
    private final Rect mCloseButtonBounds = new Rect();
    private final Rect mInsetCloseRegionBounds = new Rect();

    private boolean mCloseAlwaysInteractable;

    private UnsetPressedState mUnsetPressedState;

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

        mCloseDrawable.addState(SELECTED_STATE_SET,
                DrawableResources.INTERSTITIAL_CLOSE_BUTTON_PRESSED.createDrawable(context));
        mCloseDrawable.addState(EMPTY_STATE_SET,
                DrawableResources.INTERSTITIAL_CLOSE_BUTTON_NORMAL.createDrawable(context));

        mCloseDrawable.setState(EMPTY_STATE_SET);
        mCloseDrawable.setCallback(this);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mCloseRegionSize = ViewUtils.asIntPixels(CLOSE_REGION_SIZE_DP, context);
        mCloseButtonSize = ViewUtils.asIntPixels(CLOSE_BUTTON_SIZE_DP, context);
        mCloseButtonPadding = ViewUtils.asIntPixels(CLOSE_BUTTON_PADDING_DP, context);

        setWillNotDraw(false);
        mCloseAlwaysInteractable = true;
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
            mCloseBoundChanged = true;
            invalidate();
        }
    }

    public void setCloseVisible(boolean visible) {
        if (mCloseDrawable.setVisible(visible, false)) {
            invalidate(mCloseRegionBounds);
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        mCloseBoundChanged = true;
    }

    @Override
    public void draw(final Canvas canvas) {
        super.draw(canvas);

        // Only recalculate the close bounds if they are dirty
        if (mCloseBoundChanged) {
            mCloseBoundChanged = false;

            mClosableLayoutRect.set(0, 0, getWidth(), getHeight());
            // Create the bounds for our close regions.
            applyCloseRegionBounds(mClosePosition, mClosableLayoutRect, mCloseRegionBounds);

            // The inset rect applies padding around the visible closeButton.
            mInsetCloseRegionBounds.set(mCloseRegionBounds);
            mInsetCloseRegionBounds.inset(mCloseButtonPadding, mCloseButtonPadding);
            // The close button sits inside the close region with padding and gravity
            // in the same way the close region sits inside the whole ClosableLayout
            applyCloseButtonBounds(mClosePosition, mInsetCloseRegionBounds, mCloseButtonBounds);
            mCloseDrawable.setBounds(mCloseButtonBounds);
        }

        // Draw last so that this gets drawn as the top layer. This is also why we override
        // draw instead of onDraw.
        if (mCloseDrawable.isVisible()) {
            mCloseDrawable.draw(canvas);
        }
    }

    public void applyCloseRegionBounds(ClosePosition closePosition, Rect bounds, Rect closeBounds) {
        applyCloseBoundsWithSize(closePosition, mCloseRegionSize, bounds, closeBounds);
    }

    private void applyCloseButtonBounds(ClosePosition closePosition, Rect bounds, Rect outBounds) {
        applyCloseBoundsWithSize(closePosition, mCloseButtonSize, bounds, outBounds);
    }

    private void applyCloseBoundsWithSize(ClosePosition closePosition, final int size, Rect bounds, Rect outBounds) {
        Gravity.apply(closePosition.getGravity(), size, size, bounds, outBounds);
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent event) {
        // Start intercepting touch events only when we see a down event
        if (event.getAction() != MotionEvent.ACTION_DOWN || !mCloseDrawable.isVisible()) {
            return false;
        }

        // Start intercepting if the down event is in the close bounds. Returning true
        // here causes onTouchEvent to get called for all events up until ACTION_CANCEL gets called.
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        return pointInCloseBounds(x, y, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Stop receiving touch events if we aren't within the bounds (including some slop)
        // or if we aren't allowing a touch event due to an invisible button.
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        if (!pointInCloseBounds(x, y, mTouchSlop) || !shouldAllowPress()) {
            setClosePressed(false);
            super.onTouchEvent(event);
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setClosePressed(true);
                break;
            case MotionEvent.ACTION_CANCEL:
                // Cancelled by a parent
                setClosePressed(false);
                break;
            case MotionEvent.ACTION_UP:
                if (isClosePressed()) {
                    // Delay setting the unpressed state so that the button remains pressed
                    // at least long enough to respond to the close event.
                    if (mUnsetPressedState == null) {
                        mUnsetPressedState = new UnsetPressedState();
                    }
                    postDelayed(mUnsetPressedState, ViewConfiguration.getPressedStateDuration());
                    performClose();
                }
                break;
        }
        return true;
    }

    public void setCloseAlwaysInteractable(boolean closeAlwaysInteractable) {
        mCloseAlwaysInteractable = closeAlwaysInteractable;
    }

    boolean shouldAllowPress() {
        return mCloseAlwaysInteractable || mCloseDrawable.isVisible();
    }

    private void setClosePressed(boolean pressed) {
        if (pressed == isClosePressed()) {
            return;
        }

        mCloseDrawable.setState(pressed ? SELECTED_STATE_SET : EMPTY_STATE_SET);
        invalidate(mCloseRegionBounds);
    }

    boolean isClosePressed() {
        return mCloseDrawable.getState() == SELECTED_STATE_SET;
    }

    boolean pointInCloseBounds(int x, int y, int slop) {
        return x >= mCloseRegionBounds.left - slop
                && y >= mCloseRegionBounds.top - slop
                && x < mCloseRegionBounds.right + slop
                && y < mCloseRegionBounds.bottom + slop;
    }

    private void performClose() {
        playSoundEffect(SoundEffectConstants.CLICK);
        if (mOnCloseListener != null) {
            mOnCloseListener.onClose();
        }
    }

    /**
     * This is a copy of the UnsetPressedState pattern from Android's View.java, which is used
     * to unset the pressed state of a button after a delay.
     */
    private final class UnsetPressedState implements Runnable {
        public void run() {
            setClosePressed(false);
        }

    }

    void setCloseBounds(Rect closeBounds) {
        mCloseRegionBounds.set(closeBounds);
    }

    Rect getCloseBounds() {
        return mCloseRegionBounds;
    }

    void setCloseBoundChanged(boolean changed) {
        mCloseBoundChanged = changed;
    }

    public boolean isCloseVisible() {
        return mCloseDrawable.isVisible();
    }
}
