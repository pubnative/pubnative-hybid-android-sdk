package net.pubnative.lite.sdk.views;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class CloseableContainerTest {
    private CloseableContainer subject;

    @Mock
    private CloseableContainer.OnCloseListener mockCloseListener;

    private MotionEvent closeRegionDown;
    private MotionEvent closeRegionUp;
    private MotionEvent closeRegionCancel;
    private MotionEvent contentRegionDown;
    private MotionEvent contentRegionUp;
    private MotionEvent contentRegionCancel;

    @Before
    public void setup() {
        initMocks(this);
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        subject = new CloseableContainer(activity);
        subject.setClosePosition(CloseableContainer.ClosePosition.TOP_RIGHT);

        // Fake the close bounds, which allows us to set up close regions
        subject.setCloseBounds(new Rect(100, 10, 110, 20));
        closeRegionDown = MotionEvent.obtain(
                100, 200, MotionEvent.ACTION_DOWN, 100, 10, 0);
        closeRegionUp = MotionEvent.obtain(
                100, 200, MotionEvent.ACTION_UP, 100, 10, 0);
        closeRegionCancel = MotionEvent.obtain(
                100, 200, MotionEvent.ACTION_CANCEL, 100, 10, 0);
        contentRegionDown = MotionEvent.obtain(
                100, 200, MotionEvent.ACTION_DOWN, 0, 0, 0);
        contentRegionUp = MotionEvent.obtain(
                100, 200, MotionEvent.ACTION_UP, 0, 0, 0);
        contentRegionCancel = MotionEvent.obtain(
                100, 200, MotionEvent.ACTION_CANCEL, 0, 0, 0);
    }

    @Test
    public void setOnCloseListener_thenTouchCloseRegion_shouldCallOnClick() {
        subject.setOnCloseListener(mockCloseListener);
        subject.onTouchEvent(closeRegionDown);
        subject.onTouchEvent(closeRegionUp);

        verify(mockCloseListener).onClose();
    }

    @Test
    public void setOnCloseListener_thenTouchContentRegion_shouldNotCallCloseListener() {
        subject.setOnCloseListener(mockCloseListener);
        subject.onTouchEvent(contentRegionDown);
        subject.onTouchEvent(contentRegionUp);

        verify(mockCloseListener, never()).onClose();
    }

    @Test
    public void setCloseVisible_shouldToggleCloseDrawable() {
        subject.setCloseVisible(false);
        Assert.assertFalse(subject.isCloseVisible());

        subject.setCloseVisible(true);
        Assert.assertTrue(subject.isCloseVisible());
    }

    @Test
    public void draw_shouldUpdateCloseBounds() {
        subject.setLeft(0);
        subject.setTop(0);
        subject.setRight(100);
        subject.setBottom(200);
        subject.onSizeChanged(100, 200, 0, 0);

        int expectedTop = 0;
        int expectedLeft = (int) (100 - CloseableContainer.CLOSE_REGION_SIZE_DP);
        Canvas canvas = new Canvas();
        subject.draw(canvas);
        Rect closeBounds = subject.getCloseBounds();
        Assert.assertEquals(closeBounds.top, expectedTop);
        Assert.assertEquals(closeBounds.bottom, (int) (expectedTop + CloseableContainer.CLOSE_REGION_SIZE_DP));
        Assert.assertEquals(closeBounds.left, expectedLeft);
        Assert.assertEquals(closeBounds.right, (int) (expectedLeft + CloseableContainer.CLOSE_REGION_SIZE_DP));
    }

    @Test
    public void draw_withoutCloseBoundsChanged_shouldNotUpdateCloseBounds() {
        Canvas canvas = new Canvas();
        subject.draw(canvas);
        Rect originalCloseBounds = subject.getCloseBounds();

        subject.setCloseBounds(new Rect(40, 41, 42, 43));
        subject.setCloseBoundChanged(false);
        subject.draw(canvas);

        Assert.assertEquals(subject.getCloseBounds(), originalCloseBounds);
    }

    @Test
    public void onInterceptTouchEvent_closeRegionDown_shouldReturnTrue() {
        boolean intercepted = subject.onInterceptTouchEvent(closeRegionDown);
        Assert.assertTrue(intercepted);
    }

    @Test
    public void onInterceptTouchEvent_contentRegionDown_returnsTrue() {
        boolean intercepted = subject.onInterceptTouchEvent(contentRegionDown);
        Assert.assertFalse(intercepted);
    }

    @Test
    public void
    onTouchEvent_closeRegionDown_thenCloseRegionUp_shouldTogglePressedStateAfterDelay() {
        Assert.assertFalse(subject.isClosePressed());

        subject.onTouchEvent(closeRegionDown);
        Assert.assertTrue(subject.isClosePressed());

        subject.onTouchEvent(closeRegionUp);
        Assert.assertTrue(subject.isClosePressed());
    }

    @Test
    public void onTouchEvent_closeRegionDown_thenCloseRegionCancel_shouldTogglePressedState() {
        subject.onTouchEvent(closeRegionDown);
        subject.onTouchEvent(closeRegionCancel);
        Assert.assertFalse(subject.isClosePressed());
    }

    @Test
    public void onTouchEvent_closeRegionDown_thenContentRegionCancel_shouldTogglePressedState() {
        subject.onTouchEvent(closeRegionDown);
        subject.onTouchEvent(contentRegionCancel);
        Assert.assertFalse(subject.isClosePressed());
    }

    @Test
    public void onTouchEvent_closeRegionDown_withCloseNotVisible_withSetCloseAlwaysInteractableFalse_shouldTogglePressedState() {
        subject.setCloseAlwaysInteractable(false);
        subject.setCloseVisible(false);
        subject.onTouchEvent(closeRegionDown);
        Assert.assertFalse(subject.isClosePressed());
    }

    @Test
    public void onTouchEvent_closeRegionDown_withCloseNotVisible_withSetCloseAlwaysInteractableDefault_shouldTogglePressedState() {
        // The default of mCloseAlwaysInteractable is true
        subject.setCloseVisible(false);
        subject.onTouchEvent(closeRegionDown);
        Assert.assertTrue(subject.isClosePressed());
    }

    @Test
    public void pointInCloseBounds_noSlop_shouldReturnValidValues() {
        Rect bounds = new Rect();
        bounds.left = 10;
        bounds.right = 20;
        bounds.top = 100;
        bounds.bottom = 200;
        subject.setCloseBounds(bounds);

        Assert.assertFalse(subject.pointInCloseBounds(9, 99, 0));
        Assert.assertFalse(subject.pointInCloseBounds(9, 100, 0));
        Assert.assertFalse(subject.pointInCloseBounds(9, 199, 0));
        Assert.assertFalse(subject.pointInCloseBounds(9, 200, 0));
        Assert.assertFalse(subject.pointInCloseBounds(10, 99, 0));
        Assert.assertTrue(subject.pointInCloseBounds(10, 100, 0));
        Assert.assertTrue(subject.pointInCloseBounds(10, 199, 0));
        Assert.assertFalse(subject.pointInCloseBounds(10, 200, 0));

        Assert.assertFalse(subject.pointInCloseBounds(19, 99, 0));
        Assert.assertTrue(subject.pointInCloseBounds(19, 100, 0));
        Assert.assertTrue(subject.pointInCloseBounds(19, 199, 0));
        Assert.assertFalse(subject.pointInCloseBounds(19, 200, 0));
        Assert.assertFalse(subject.pointInCloseBounds(20, 99, 0));
        Assert.assertFalse(subject.pointInCloseBounds(20, 100, 0));
        Assert.assertFalse(subject.pointInCloseBounds(20, 199, 0));
        Assert.assertFalse(subject.pointInCloseBounds(20, 200, 0));
    }

    @Test
    public void pointInCloseBounds_slop_shouldReturnValidValues() {
        int slop = 3;

        // Same as above, but adjust given 3 px slop
        Rect bounds = new Rect();
        bounds.left = 13;
        bounds.right = 17;
        bounds.top = 103;
        bounds.bottom = 197;
        subject.setCloseBounds(bounds);

        Assert.assertFalse(subject.pointInCloseBounds(9, 99, slop));
        Assert.assertFalse(subject.pointInCloseBounds(9, 100, slop));
        Assert.assertFalse(subject.pointInCloseBounds(9, 199, slop));
        Assert.assertFalse(subject.pointInCloseBounds(9, 200, slop));
        Assert.assertFalse(subject.pointInCloseBounds(10, 99, slop));
        Assert.assertTrue(subject.pointInCloseBounds(10, 100, slop));
        Assert.assertTrue(subject.pointInCloseBounds(10, 199, slop));
        Assert.assertFalse(subject.pointInCloseBounds(10, 200, slop));

        Assert.assertFalse(subject.pointInCloseBounds(19, 99, slop));
        Assert.assertTrue(subject.pointInCloseBounds(19, 100, slop));
        Assert.assertTrue(subject.pointInCloseBounds(19, 199, slop));
        Assert.assertFalse(subject.pointInCloseBounds(19, 200, slop));
        Assert.assertFalse(subject.pointInCloseBounds(20, 99, slop));
        Assert.assertFalse(subject.pointInCloseBounds(20, 100, slop));
        Assert.assertFalse(subject.pointInCloseBounds(20, 199, slop));
        Assert.assertFalse(subject.pointInCloseBounds(20, 200, slop));
    }

    @Test
    public void shouldAllowPress_shouldRespectSetCloseAlwaysInteractable() {
        subject.setCloseVisible(false);
        subject.setCloseAlwaysInteractable(false);
        Assert.assertFalse(subject.shouldAllowPress());

        subject.setCloseVisible(false);
        subject.setCloseAlwaysInteractable(true);
        Assert.assertTrue(subject.shouldAllowPress());

        subject.setCloseVisible(true);
        subject.setCloseAlwaysInteractable(false);
        Assert.assertTrue(subject.shouldAllowPress());

        subject.setCloseVisible(true);
        subject.setCloseAlwaysInteractable(true);
        Assert.assertTrue(subject.shouldAllowPress());
    }
}
