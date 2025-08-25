// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.contentinfo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import net.pubnative.lite.sdk.mraid.MRAIDView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class FeedbackJSInterfaceTest {

    @Mock
    private MRAIDView mockMraidView;
    @Captor
    private ArgumentCaptor<String> jsCaptor;

    private FeedbackJSInterface feedbackJSInterface;
    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        feedbackJSInterface = new FeedbackJSInterface();
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void submitData_withFullData_injectsCorrectJavaScript() {
        AdFeedbackData data = new AdFeedbackData.Builder()
                .setAppToken("test_token")
                .setZoneId("test_zone")
                // A creative with characters that need escaping
                .setCreative("<div id=\"1\">'hello'</div>")
                .build();

        feedbackJSInterface.submitData(data, mockMraidView);

        verify(mockMraidView).injectJavaScript(jsCaptor.capture());
        String injectedJs = jsCaptor.getValue();

        assertTrue(injectedJs.contains("hybidFeedback.appToken = \"test_token\";"));
        assertTrue(injectedJs.contains("hybidFeedback.zoneId = \"test_zone\";"));
        // Verify that the creative string was properly escaped for JavaScript
        assertTrue(injectedJs.contains("hybidFeedback.creative = \"<div id=\\\"1\\\">'hello'</div>\";"));
    }

    @Test
    public void submitData_withPartialData_injectsPartialJavaScript() {
        AdFeedbackData data = new AdFeedbackData.Builder()
                .setAppToken("test_token")
                .setAdFormat("banner")
                .build();

        feedbackJSInterface.submitData(data, mockMraidView);

        verify(mockMraidView).injectJavaScript(jsCaptor.capture());
        String injectedJs = jsCaptor.getValue();

        assertTrue(injectedJs.contains("hybidFeedback.appToken = \"test_token\";"));
        assertTrue(injectedJs.contains("hybidFeedback.adFormat = \"banner\";"));
        // Verify that fields that were not set are not in the JS string
        assertFalse(injectedJs.contains("zoneId"));
    }

    @Test
    public void submitData_withNullData_doesNotInjectJavaScript() {
        feedbackJSInterface.submitData(null, mockMraidView);
        verify(mockMraidView, never()).injectJavaScript(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    public void submitData_withNullMraidView_doesNotCrash() {
        AdFeedbackData data = new AdFeedbackData.Builder().setAppToken("test_token").build();
        // This test passes if no NullPointerException is thrown
        feedbackJSInterface.submitData(data, null);
    }

    @Test
    public void submitData_withEmptyData_doesNotInjectJavaScript() {
        AdFeedbackData emptyData = new AdFeedbackData.Builder().build();
        feedbackJSInterface.submitData(emptyData, mockMraidView);
        verify(mockMraidView, never()).injectJavaScript(org.mockito.ArgumentMatchers.anyString());
    }
}
