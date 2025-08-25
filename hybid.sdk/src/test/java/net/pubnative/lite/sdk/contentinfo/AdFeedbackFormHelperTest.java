// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.contentinfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.Intent;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.contentinfo.listeners.AdFeedbackLoadListener;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowApplication;

@RunWith(RobolectricTestRunner.class)
public class AdFeedbackFormHelperTest {

    @Mock
    private Ad mockAd;
    @Mock
    private AdFeedbackLoadListener mockListener;
    @Captor
    private ArgumentCaptor<HyBidError> errorCaptor;

    private AdFeedbackFormHelper subject;
    private Context context;
    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        subject = new AdFeedbackFormHelper();
        context = RuntimeEnvironment.getApplication();
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void showFeedbackForm_withValidInput_startsActivityWithCorrectIntent() {
        String testUrl = "https://example.com";
        String processedUrl = "https://example.com?apptoken=[APPTOKEN]&creativeId=test-creative";

        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class);
             MockedConstruction<AdFeedbackDataCollector> mockedCollector = mockConstruction(AdFeedbackDataCollector.class,
                     (mock, context) -> when(mock.collectData(any(), any(), any())).thenReturn(new AdFeedbackData.Builder().build()));
             MockedConstruction<FeedbackMacros> mockedMacros = mockConstruction(FeedbackMacros.class,
                     (mock, context) -> when(mock.processUrl(anyString(), any())).thenReturn(processedUrl))) {

            subject.showFeedbackForm(context, testUrl, mockAd, "banner", IntegrationType.STANDALONE, mockListener);

            Intent startedIntent = ShadowApplication.getInstance().getNextStartedActivity();
            assertNotNull(startedIntent);

            assertEquals(AdFeedbackActivity.class.getName(), startedIntent.getComponent().getClassName());
            assertEquals(processedUrl, startedIntent.getStringExtra(AdFeedbackActivity.EXTRA_FEEDBACK_FORM_URL));
            assertEquals(subject, startedIntent.getParcelableExtra(AdFeedbackActivity.EXTRA_FEEDBACK_FORM_CALLBACK));
        }
    }

    @Test
    public void showFeedbackForm_withInvalidUrl_invokesOnLoadFailed() {
        subject.showFeedbackForm(context, "", mockAd, "banner", IntegrationType.STANDALONE, mockListener);

        verify(mockListener).onLoadFailed(errorCaptor.capture());
        assertEquals(HyBidErrorCode.ERROR_LOADING_FEEDBACK, errorCaptor.getValue().getErrorCode());

        Intent startedIntent = ShadowApplication.getInstance().getNextStartedActivity();
        assertNull(startedIntent);
    }

    @Test
    public void onReceiveResult_withOpenCode_invokesOnLoad() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class);
             MockedConstruction<AdFeedbackDataCollector> mockedCollector = mockConstruction(AdFeedbackDataCollector.class);
             MockedConstruction<FeedbackMacros> mockedMacros = mockConstruction(FeedbackMacros.class)) {

            // Set the listener first by calling showFeedbackForm
            subject.showFeedbackForm(context, "https://example.com", mockAd, "banner", IntegrationType.STANDALONE, mockListener);

            // Directly call onReceiveResult to simulate a callback from the activity
            subject.onReceiveResult(AdFeedbackFormHelper.FeedbackFormAction.OPEN.code, null);

            verify(mockListener).onLoad("");
        }
    }

    @Test
    public void onReceiveResult_withCloseCode_invokesOnFormClosed() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class);
             MockedConstruction<AdFeedbackDataCollector> mockedCollector = mockConstruction(AdFeedbackDataCollector.class);
             MockedConstruction<FeedbackMacros> mockedMacros = mockConstruction(FeedbackMacros.class)) {

            subject.showFeedbackForm(context, "https://example.com", mockAd, "banner", IntegrationType.STANDALONE, mockListener);

            subject.onReceiveResult(AdFeedbackFormHelper.FeedbackFormAction.CLOSE.code, null);

            verify(mockListener).onFormClosed();
        }
    }

    @Test
    public void onReceiveResult_withErrorCode_invokesOnLoadFailed() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class);
             MockedConstruction<AdFeedbackDataCollector> mockedCollector = mockConstruction(AdFeedbackDataCollector.class);
             MockedConstruction<FeedbackMacros> mockedMacros = mockConstruction(FeedbackMacros.class)) {

            subject.showFeedbackForm(context, "https://example.com", mockAd, "banner", IntegrationType.STANDALONE, mockListener);

            subject.onReceiveResult(AdFeedbackFormHelper.FeedbackFormAction.ERROR.code, null);

            verify(mockListener).onLoadFailed(errorCaptor.capture());
            assertEquals(HyBidErrorCode.ERROR_LOADING_FEEDBACK, errorCaptor.getValue().getErrorCode());
        }
    }
}
