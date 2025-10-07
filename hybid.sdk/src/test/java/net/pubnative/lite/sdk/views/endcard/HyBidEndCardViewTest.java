// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.views.endcard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.R;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.models.EndCardData;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.utils.ViewUtils;
import net.pubnative.lite.sdk.vpaid.helpers.BitmapHelper;
import net.pubnative.lite.sdk.vpaid.helpers.SimpleTimer;
import net.pubnative.lite.sdk.vpaid.utils.ImageUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;


@RunWith(RobolectricTestRunner.class)
public class HyBidEndCardViewTest {

    private Context context;
    private HyBidEndCardView endCardView;

    @Mock
    private HyBidEndCardView.EndCardViewListener mockListener;
    @Mock
    private EndCardData mockEndCardData;
    @Mock
    private Bitmap mockBitmap;

    private MockedStatic<BitmapHelper> mockedBitmapHelper;
    private MockedStatic<HyBid> mockedHyBid;
    private MockedStatic<ViewUtils> mockedViewUtils;
    private MockedStatic<ImageUtils> mockedImageUtils;
    private MockedStatic<PNHttpClient> mockedHttpClient;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();

        mockedBitmapHelper = mockStatic(BitmapHelper.class);
        mockedHyBid = mockStatic(HyBid.class);
        mockedViewUtils = mockStatic(ViewUtils.class);
        mockedImageUtils = mockStatic(ImageUtils.class);
        mockedHttpClient = mockStatic(PNHttpClient.class);

        when(HyBid.getNormalCloseXmlResource()).thenReturn(12345);
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockBitmap);
    }

    @After
    public void tearDown() {
        mockedBitmapHelper.close();
        mockedHyBid.close();
        mockedViewUtils.close();
        mockedImageUtils.close();
        mockedHttpClient.close();
    }

    @Test
    public void constructor_initializesControlViews() {
        // Test that the constructor creates the necessary UI controls.
        endCardView = new HyBidEndCardView(context);
        assertNotNull(endCardView.findViewById(R.id.end_card_skip_view));
        assertNotNull(endCardView.findViewById(R.id.button_fullscreen_close));
        assertNotNull(endCardView.findViewById(R.id.endcard_close_countdown_view));
    }

    @Test
    public void skipButton_onClick_invokesListener() {
        // Test that clicking the skip button correctly notifies the listener.
        endCardView = new HyBidEndCardView(context);
        endCardView.setEndCardViewListener(mockListener);

        ImageView skipView = endCardView.findViewById(R.id.end_card_skip_view);
        skipView.performClick();

        verify(mockListener).onSkip();
    }

    @Test
    public void show_withStaticEndCard_showsImageViewAndNotifiesSuccess() {
        // Test the logic for displaying a static image end card.
        when(mockEndCardData.getType()).thenReturn(EndCardData.Type.STATIC_RESOURCE);
        when(mockEndCardData.isCustom()).thenReturn(false);

        endCardView = new HyBidEndCardView(context);
        endCardView.setEndCardViewListener(mockListener);
        endCardView.show(mockEndCardData, "image_uri");

        verify(ImageUtils.class);
        ImageUtils.setScaledImage(any(ImageView.class), eq("image_uri"));
        verify(mockListener).onLoadSuccess(false);
        verify(mockListener).onShow(false, Reporting.Key.END_CARD_STATIC);
    }

    @Test
    public void show_withHtmlEndCard_rendersHtml() {
        // Test the logic for rendering an HTML end card.
        when(mockEndCardData.getType()).thenReturn(EndCardData.Type.HTML_RESOURCE);
        when(mockEndCardData.getContent()).thenReturn("<html>...</html>");

        // We will capture the constructor arguments to verify the correct HTML was passed.
        final List<List<Object>> allConstructorArgs = new ArrayList<>();
        try (MockedConstruction<MRAIDBanner> mockedBanner = mockConstruction(MRAIDBanner.class, (mock, context) -> {
            // This lambda is called when a new MRAIDBanner is created. We save the arguments.
            allConstructorArgs.add(new ArrayList<>(context.arguments()));
        })) {
            endCardView = new HyBidEndCardView(context);
            endCardView.setEndCardViewListener(mockListener);

            endCardView.show(mockEndCardData, null);

            // Verify that the MRAIDBanner was constructed with the correct HTML content
            List<?> arguments = allConstructorArgs.get(0);
            assertEquals("<html>...</html>", arguments.get(2)); // HTML is the 3rd argument
        }
    }

    @Test
    public void show_withIFrameEndCard_makesHttpRequest() {
        // Test the logic for fetching and rendering an IFrame end card.
        String iframeUrl = "https://example.com/iframe.html";
        when(mockEndCardData.getType()).thenReturn(EndCardData.Type.IFRAME_RESOURCE);
        when(mockEndCardData.getContent()).thenReturn(iframeUrl);

        endCardView = new HyBidEndCardView(context);
        endCardView.setEndCardViewListener(mockListener);

        endCardView.show(mockEndCardData, null);

        // Verify that an HTTP request was made to fetch the IFrame content.
        mockedHttpClient.verify(() -> PNHttpClient.makeRequest(any(), eq(iframeUrl), any(), any(), anyBoolean(), any()));
    }

    @Test
    public void showSkipButton_startsTimerWhichShowsButtonOnFinish() {
        // We will capture the listener from the SimpleTimer's constructor.
        final SimpleTimer.Listener[] capturedListener = new SimpleTimer.Listener[1];

        try (MockedConstruction<SimpleTimer> mockedTimer = mockConstruction(SimpleTimer.class,
                // This lambda gives us access to the mock and the context of its creation
                (mock, context) -> {
                    // The listener is the second argument (index 1) in the constructor
                    capturedListener[0] = (SimpleTimer.Listener) context.arguments().get(1);
                })) {

            endCardView = new HyBidEndCardView(context);

            endCardView.showSkipButton();

            // Verify a timer was created and started
            SimpleTimer timer = mockedTimer.constructed().get(0);
            verify(timer).start();

            // Simulate the timer finishing
            capturedListener[0].onFinish();

            // Verify the skip button is now visible
            ImageView skipView = endCardView.findViewById(R.id.end_card_skip_view);
            assertEquals(View.VISIBLE, skipView.getVisibility());
        }
    }
}