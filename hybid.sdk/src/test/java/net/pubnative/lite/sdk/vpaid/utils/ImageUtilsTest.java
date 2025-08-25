// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.utils;

import android.graphics.Bitmap;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImageUtilsTest {

    @Mock
    private ImageView mockImageView;
    @Mock
    private ViewTreeObserver mockViewTreeObserver;
    @Mock
    private BitmapDecoder mockBitmapDecoder; // Our mocked dependency
    @Mock
    private Bitmap mockBitmap; // A mock bitmap to represent a successful decode

    // A captor to grab the listener that is added to the ViewTreeObserver
    @Captor
    private ArgumentCaptor<ViewTreeObserver.OnPreDrawListener> preDrawListenerCaptor;

    @Before
    public void setUp() {
        // Common setup: Ensure the mock ImageView returns our mock ViewTreeObserver
        when(mockImageView.getViewTreeObserver()).thenReturn(mockViewTreeObserver);
    }

    @Test
    public void setScaledImage_whenObserverIsAlive_addsListenerAndSetsBitmapOnPreDraw() {
        String fakeFilePath = "/fake/path/image.jpg";
        int viewWidth = 200;
        int viewHeight = 150;

        // 1. The observer is alive and well.
        when(mockViewTreeObserver.isAlive()).thenReturn(true);

        // 2. Configure the mock decoder to return a valid bitmap when called.
        when(mockBitmapDecoder.decodeFile(any(File.class), eq(viewWidth), eq(viewHeight)))
                .thenReturn(mockBitmap);

        // Call the testable method with our mock decoder
        ImageUtils.setScaledImage(mockImageView, fakeFilePath, mockBitmapDecoder);

        // --- Assert: Part 1 (Before the callback) ---
        // Verify the listener was added to the observer. We capture it for the next step.
        verify(mockViewTreeObserver).addOnPreDrawListener(preDrawListenerCaptor.capture());
        ViewTreeObserver.OnPreDrawListener listener = preDrawListenerCaptor.getValue();

        // --- Arrange: Part 2 (For the callback) ---
        // Simulate the view being measured right before the callback fires.
        when(mockImageView.getMeasuredWidth()).thenReturn(viewWidth);
        when(mockImageView.getMeasuredHeight()).thenReturn(viewHeight);

        // --- Act: Part 2 ---
        // Manually trigger the listener's onPreDraw method.
        listener.onPreDraw();

        // --- Assert: Part 2 (After the callback) ---
        // 1. Verify our decoder was called with the correct dimensions.
        verify(mockBitmapDecoder).decodeFile(any(File.class), eq(viewWidth), eq(viewHeight));

        // 2. Verify the decoded bitmap was set on the ImageView.
        verify(mockImageView).setImageBitmap(mockBitmap);

        // 3. Verify the listener was removed to prevent memory leaks and redundant calls.
        verify(mockViewTreeObserver).removeOnPreDrawListener(listener);
    }

    @Test
    public void setScaledImage_whenDecoderReturnsNull_doesNotSetBitmap() {
        String fakeFilePath = "/fake/path/image.jpg";
        when(mockViewTreeObserver.isAlive()).thenReturn(true);

        // Configure the mock decoder to return null, simulating a failed decode.
        when(mockBitmapDecoder.decodeFile(any(File.class), anyInt(), anyInt())).thenReturn(null);

        // Capture and trigger the listener just like in the success test.
        ImageUtils.setScaledImage(mockImageView, fakeFilePath, mockBitmapDecoder);
        verify(mockViewTreeObserver).addOnPreDrawListener(preDrawListenerCaptor.capture());
        preDrawListenerCaptor.getValue().onPreDraw();

        // Verify that setImageBitmap was NEVER called because the decoded bitmap was null.
        verify(mockImageView, never()).setImageBitmap(any(Bitmap.class));
    }

    @Test
    public void setScaledImage_whenViewTreeObserverIsDead_doesNothing() {
        // Simulate a scenario where the View is detached and the observer is no longer alive.
        when(mockViewTreeObserver.isAlive()).thenReturn(false);

        ImageUtils.setScaledImage(mockImageView, "any/path", mockBitmapDecoder);

        // Verify that since the observer was dead, we never even tried to add a listener.
        verify(mockViewTreeObserver, never()).addOnPreDrawListener(any());
    }
}