// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.contentinfo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.mraid.MRAIDInterstitial;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.utils.URLValidator;
import net.pubnative.lite.sdk.utils.UrlHandler;

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

@RunWith(RobolectricTestRunner.class)
public class AdFeedbackViewTest {

    @Mock
    private Activity mockActivity;
    @Mock
    private AdFeedbackView.AdFeedbackLoadListener mockListener;
    @Mock
    private MRAIDView mockMraidView;

    private MockedStatic<TextUtils> mockedTextUtils;
    private MockedStatic<URLValidator> mockedUrlValidator;
    private MockedStatic<HyBid> mockedHyBid;
    private MockedStatic<Uri> mockedUriStatic;

    private Context mockContext;
    private AdFeedbackView adFeedbackView;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockContext = RuntimeEnvironment.getApplication();
        adFeedbackView = new AdFeedbackView();

        mockedTextUtils = mockStatic(TextUtils.class);
        mockedUrlValidator = mockStatic(URLValidator.class);
        mockedHyBid = mockStatic(HyBid.class);
        mockedUriStatic = mockStatic(Uri.class);

        when(TextUtils.isEmpty(any())).thenAnswer(invocation -> {
            CharSequence s = invocation.getArgument(0);
            return s == null || s.length() == 0;
        });
    }

    @After
    public void tearDown() {
        mockedTextUtils.close();
        mockedUrlValidator.close();
        mockedHyBid.close();
        mockedUriStatic.close();
    }

    private MockedConstruction<AdFeedbackDataCollector> mockDataCollector() {
        return mockConstruction(AdFeedbackDataCollector.class, (mock, context) -> {
            when(mock.collectData(any(), any(), any())).thenReturn(new AdFeedbackData.Builder().build());
        });
    }

    @Test
    public void prepare_onUrlParseException_reportsException() {
        String badUrl = "bad-url-that-causes-exception";

        // We use the mockedUriStatic object from setUp to define the behavior.
        mockedUriStatic.when(() -> Uri.parse(badUrl))
                .thenThrow(new RuntimeException("Test URI Parse Exception"));

        try (MockedConstruction<AdFeedbackDataCollector> mockedCollector = mockDataCollector()) {
            adFeedbackView.prepare(mockContext, badUrl, mockListener);
            mockedHyBid.verify(() -> HyBid.reportException(any(RuntimeException.class)));
        }
    }

    @Test
    public void mraidViewLoaded_notifiesListenerAndSetsReady() {
        try (MockedConstruction<MRAIDInterstitial> mockedInterstitial = mockConstruction(MRAIDInterstitial.class);
             MockedConstruction<FeedbackJSInterface> mockedJsInterface = mockConstruction(FeedbackJSInterface.class);
             MockedConstruction<AdFeedbackDataCollector> mockedCollector = mockDataCollector()) {

            adFeedbackView.prepare(mockContext, "https://example.com", mockListener);
            adFeedbackView.mraidViewLoaded(mockMraidView);

            verify(mockListener).onLoadFinished();
            verify(mockedJsInterface.constructed().get(0)).submitData(any(), eq(mockMraidView));
        }
    }

    @Test
    public void mraidViewError_notifiesListener() {
        try (MockedConstruction<AdFeedbackDataCollector> mockedCollector = mockDataCollector()) {
            adFeedbackView.prepare(mockContext, "https://example.com", mockListener);
            adFeedbackView.mraidViewError(mockMraidView);
            verify(mockListener).onLoadFailed(any(HyBidError.class));
        }
    }

    @Test
    public void mraidViewClose_notifiesListener() {
        try (MockedConstruction<AdFeedbackDataCollector> mockedCollector = mockDataCollector()) {
            adFeedbackView.prepare(mockContext, "https://example.com", mockListener);
            adFeedbackView.mraidViewClose(mockMraidView);
            verify(mockListener).onFormClosed();
        }
    }

    @Test
    public void mraidNativeFeatureOpenBrowser_delegatesToUrlHandler() {
        try (MockedConstruction<UrlHandler> mockedHandler = mockConstruction(UrlHandler.class);
             MockedConstruction<AdFeedbackDataCollector> mockedCollector = mockDataCollector()) {

            adFeedbackView.prepare(mockContext, "https://example.com", mockListener);
            adFeedbackView.mraidNativeFeatureOpenBrowser("https://google.com");
            verify(mockedHandler.constructed().get(0)).handleUrl("https://google.com", null, null);
        }
    }

    @Test
    public void showFeedbackForm_withNonActivityContext_callsLoadFailed() {
        try (MockedConstruction<AdFeedbackDataCollector> mockedCollector = mockDataCollector()) {
            adFeedbackView.prepare(mockContext, "https://example.com", mockListener);
            adFeedbackView.showFeedbackForm(mockContext, "https://example.com");
            verify(mockListener).onLoadFailed(any(HyBidError.class));
        }
    }

    @Test
    public void showFeedbackForm_whenNotReady_callsLoadFailed() {
        try (MockedConstruction<AdFeedbackDataCollector> mockedCollector = mockDataCollector()) {
            adFeedbackView.prepare(mockContext, "https://example.com", mockListener);
            adFeedbackView.showFeedbackForm(mockActivity, "https://example.com");
            verify(mockListener).onLoadFailed(any(HyBidError.class));
        }
    }

    @Test
    public void showFeedbackForm_whenReadyAndUrlIsValid_showsMraidInterstitial() {
        try (MockedConstruction<MRAIDInterstitial> mockedInterstitial = mockConstruction(MRAIDInterstitial.class);
             MockedConstruction<AdFeedbackDataCollector> mockedCollector = mockDataCollector()) {

            adFeedbackView.prepare(mockContext, "https://example.com", mockListener);
            adFeedbackView.mraidViewLoaded(mockMraidView);

            when(URLValidator.isValidURL("https://example.com")).thenReturn(true);
            when(mockedInterstitial.constructed().get(0).isLoaded()).thenReturn(true);

            adFeedbackView.showFeedbackForm(mockActivity, "https://example.com");

            verify(mockedInterstitial.constructed().get(0)).show(eq(mockActivity), any(), anyString());
        }
    }
}