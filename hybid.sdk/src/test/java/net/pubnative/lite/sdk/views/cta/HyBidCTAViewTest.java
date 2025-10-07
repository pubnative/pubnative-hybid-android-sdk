// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.views.cta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import net.pubnative.lite.sdk.utils.PNBitmapDownloader;
import net.pubnative.lite.sdk.views.helpers.ImageHelper;
import net.pubnative.lite.sdk.vpaid.helpers.SimpleTimer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class HyBidCTAViewTest {

    private Context context;
    private HyBidCTAView ctaView;

    @Mock
    private HyBidCTAView.CTAViewListener mockListener;
    @Mock
    private Bitmap mockBitmap;

    private MockedStatic<ImageHelper> mockedImageHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();
        ctaView = new HyBidCTAView(context);
        ctaView.setListener(mockListener);

        mockedImageHelper = Mockito.mockStatic(ImageHelper.class);
    }

    @After
    public void tearDown() {
        mockedImageHelper.close();
    }

    @Test
    public void constructor_initializesUiCorrectly() {
        // Test that the view starts in the correct initial state after construction.
        assertEquals(View.INVISIBLE, ctaView.getVisibility());
        assertFalse(ctaView.isLoaded());
    }

    @Test
    public void show_withBitmapAndNoDelay_showsImmediately() {
        HyBidCTAView ctaViewSpy = spy(ctaView);
        doNothing().when(ctaViewSpy).show();

        ctaViewSpy.show(mockBitmap, "Click Me", 0);

        assertTrue(ctaViewSpy.isLoaded());
        // Change the verification to expect 2 calls
        verify(ctaViewSpy, times(2)).show();
    }

    @Test
    public void show_withIconUrlAndDelay_startsTimer() {
        // Test that providing a delay correctly initializes and starts a timer.
        try (MockedConstruction<SimpleTimer> mockedTimer = mockConstruction(SimpleTimer.class)) {
            ctaView.show("https://example.com/icon.png", "Click Me", 5);

            // Verify a timer was created and started
            SimpleTimer timer = mockedTimer.constructed().get(0);
            verify(timer).start();
        }
    }

    @Test
    public void timer_onFinish_callsShowWhenLoaded() {
        // We will capture the listener from the SimpleTimer's constructor.
        final SimpleTimer.Listener[] capturedListener = new SimpleTimer.Listener[1];

        try (MockedConstruction<SimpleTimer> mockedTimer = mockConstruction(SimpleTimer.class,
                // This lambda gives us access to the mock and the context of its creation
                (mock, context) -> {
                    // The listener is the second argument (index 1) in the constructor
                    capturedListener[0] = (SimpleTimer.Listener) context.arguments().get(1);
                })) {

            HyBidCTAView ctaViewSpy = spy(ctaView);
            doNothing().when(ctaViewSpy).show();

            // 1. Call a public method that sets isLoaded = true AND starts the timer.
            // We provide a mockBitmap, which will set isLoaded to true.
            ctaViewSpy.show(mockBitmap, "Click Me", 5);

            // 2. Trigger the timer's finish event using the captured listener.
            capturedListener[0].onFinish();

            // 3. Verify the view is now shown because isLoaded was true when the timer finished.
            verify(ctaViewSpy).show();
        }
    }

    @Test
    public void downloader_onDownloadFinish_withValidBitmap_setsLoadedAndShows() {
        // Test the successful image download path.
        ArgumentCaptor<PNBitmapDownloader.DownloadListener> listenerCaptor = ArgumentCaptor.forClass(PNBitmapDownloader.DownloadListener.class);

        try (MockedConstruction<PNBitmapDownloader> mockedDownloader = mockConstruction(PNBitmapDownloader.class)) {
            HyBidCTAView ctaViewSpy = spy(ctaView);
            doNothing().when(ctaViewSpy).show();

            // show() with no delay will set showImmediately = true
            ctaViewSpy.show("https://example.com/icon.png", "Click Me", 0);

            // Capture the downloader's listener
            verify(mockedDownloader.constructed().get(0)).download(anyString(), anyInt(), anyInt(), listenerCaptor.capture());
            PNBitmapDownloader.DownloadListener downloadListener = listenerCaptor.getValue();

            // Trigger a successful download
            downloadListener.onDownloadFinish("url", mockBitmap);

            // Verify the view considers itself loaded and triggers the show() method
            assertTrue(ctaViewSpy.isLoaded());
            verify(ctaViewSpy).show();
        }
    }

    @Test
    public void downloader_onDownloadFailed_invokesFailListener() {
        // Test the failed image download path.
        ArgumentCaptor<PNBitmapDownloader.DownloadListener> listenerCaptor = ArgumentCaptor.forClass(PNBitmapDownloader.DownloadListener.class);
        try (MockedConstruction<PNBitmapDownloader> mockedDownloader = mockConstruction(PNBitmapDownloader.class)) {
            ctaView.show("https://example.com/icon.png", "Click Me", 0);

            verify(mockedDownloader.constructed().get(0)).download(anyString(), anyInt(), anyInt(), listenerCaptor.capture());
            PNBitmapDownloader.DownloadListener downloadListener = listenerCaptor.getValue();

            // Trigger a failed download
            downloadListener.onDownloadFailed("url", new Exception("Test Fail"));

            // Verify the correct listener callback was invoked
            assertFalse(ctaView.isLoaded());
            verify(mockListener).onFail();
        }
    }

    @Test
    public void click_onView_invokesClickListener() {
        // Test that clicking the main view triggers the listener.
        ctaView.performClick();
        verify(mockListener).onClick();
    }
}