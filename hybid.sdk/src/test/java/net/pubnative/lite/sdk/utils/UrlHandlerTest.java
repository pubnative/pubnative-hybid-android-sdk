// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class UrlHandlerTest {
    @Mock
    Context mockContext;
    @Mock
    IntentHandler mockIntentHandler;

    UrlHandler urlHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        urlHandler = new UrlHandler(mockContext);
        // Inject mockIntentHandler
        try {
            java.lang.reflect.Field field = UrlHandler.class.getDeclaredField("mIntentHandler");
            field.setAccessible(true);
            field.set(urlHandler, mockIntentHandler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testHandleUrl_withDeeplinkHandled() {
        UrlHandler spyHandler = spy(urlHandler);
        doReturn(null).when(spyHandler).handleDeeplinkIfPresent(anyString(), anyString());
        spyHandler.handleUrl("http://test.com", "vrvdl://test?deeplinkUrl=deeplink", null);
        verify(spyHandler).handleDeeplinkIfPresent(anyString(), anyString());
    }

    @Test
    public void testHandleUrl_withEmptyUrl() {
        UrlHandler spyHandler = spy(urlHandler);
        doReturn("").when(spyHandler).handleDeeplinkIfPresent(anyString(), anyString());
        spyHandler.handleUrl("", "vrvdl://test?deeplinkUrl=deeplink", null);
        verify(spyHandler).handleDeeplinkIfPresent(anyString(), anyString());
    }

    @Test
    public void testHandleNavigation_playStore() {
        urlHandler.handleNavigation("https://play.google.com/store/apps/details?id=test", null);
        verify(mockIntentHandler).handleDeepLink(any(Uri.class));
    }

    @Test
    public void testHandleNavigation_httpInternal() {
        urlHandler.handleNavigation("http://test.com", "internal");
        verify(mockIntentHandler).handleBrowserLinkBrowserActivity(any(Uri.class));
    }

    @Test
    public void testHandleNavigation_httpExternal() {
        urlHandler.handleNavigation("http://test.com", null);
        verify(mockIntentHandler).handleBrowserLink(any(Uri.class));
    }

    @Test
    public void testHandleNavigation_deepLink() {
        urlHandler.handleNavigation("customscheme://test", null);
        verify(mockIntentHandler).handleDeepLink(any(Uri.class));
    }

    @Test
    public void testGetDeeplinkUrl() {
        Uri uri = Uri.parse("vrvdl://test?deeplinkUrl=deeplink");
        assertEquals("deeplink", urlHandler.getDeeplinkUrl(uri));
    }

    @Test
    public void testGetFallbackUrl() {
        Uri uri = Uri.parse("vrvdl://test?fallbackUrl=fallback");
        assertEquals("fallback", urlHandler.getFallbackUrl(uri));
    }

    @Test
    public void testHandleDeeplinkIfPresent_UriDeeplinkHandled() {
        String adLink = "vrvdl://test?deeplinkUrl=deeplink://open";
        when(mockIntentHandler.handleDeepLink(Uri.parse("deeplink://open"))).thenReturn(true);

        String result = urlHandler.handleDeeplinkIfPresent("http://original.com", adLink);

        assertNull(result);
    }

    @Test
    public void testHandleDeeplinkIfPresent_HttpDeeplinkHandled() {
        String adLink = "vrvdl://test?deeplinkUrl=http://www.example.com";
        when(mockIntentHandler.canHandleIntent(Uri.parse("http://www.example.com"))).thenReturn(true);
        String result = urlHandler.handleDeeplinkIfPresent("http://original.com", adLink);
        assertNull(result);
    }

    @Test
    public void testHandleDeeplinkIfPresent_HttpSDeeplinkHandled() {
        String adLink = "vrvdl://test?deeplinkUrl=https://www.example.com";
        when(mockIntentHandler.canHandleIntent(Uri.parse("https://www.example.com"))).thenReturn(true);
        String result = urlHandler.handleDeeplinkIfPresent("http://original.com", adLink);
        assertNull(result);
    }

    @Test
    public void testHandleDeeplinkIfPresent_UriDeeplinkNotHandledWithFallback() {
        String adLink = "vrvdl://test?deeplinkUrl=deeplink://open&fallbackUrl=http://fallback.com";

        String result = urlHandler.handleDeeplinkIfPresent("http://original.com", adLink);

        assertEquals("http://fallback.com", result);
    }

    @Test
    public void testHandleDeeplinkIfPresent_HttpDeeplinkNotHandledWithFallback() {
        String adLink = "vrvdl://test?deeplinkUrl=http://www.example.com&fallbackUrl=http://fallback.com";
        when(mockIntentHandler.canHandleIntent(Uri.parse("http://www.example.com"))).thenReturn(false);

        String result = urlHandler.handleDeeplinkIfPresent("http://original.com", adLink);

        assertEquals("http://fallback.com", result);
    }

    @Test
    public void testHandleDeeplinkIfPresent_HttpSDeeplinkNotHandledWithFallback() {
        String adLink = "vrvdl://test?deeplinkUrl=https://example.com&fallbackUrl=http://fallback.com";
        when(mockIntentHandler.canHandleIntent(Uri.parse("https://example.com"))).thenReturn(false);

        String result = urlHandler.handleDeeplinkIfPresent("http://original.com", adLink);

        assertEquals("http://fallback.com", result);
    }

    @Test
    public void testHandleDeeplinkIfPresent_UriDeeplinkNotHandledNoFallback() {
        String adLink = "vrvdl://test?deeplinkUrl=deeplink://open";

        String result = urlHandler.handleDeeplinkIfPresent("http://original.com", adLink);

        assertEquals("http://original.com", result);
    }

    @Test
    public void testHandleDeeplinkIfPresent_HttpDeeplinkNotHandledNoFallback() {
        String adLink = "vrvdl://test?deeplinkUrl=http://www.example.com";
        when(mockIntentHandler.canHandleIntent(Uri.parse("http://www.example.com"))).thenReturn(false);

        String result = urlHandler.handleDeeplinkIfPresent("http://original.com", adLink);

        assertEquals("http://original.com", result);
    }

    @Test
    public void testHandleDeeplinkIfPresent_HttpSDeeplinkNotHandledNoFallback() {
        String adLink = "vrvdl://test?deeplinkUrl=https://www.example.com";
        when(mockIntentHandler.canHandleIntent(Uri.parse("https://www.example.com"))).thenReturn(false);

        String result = urlHandler.handleDeeplinkIfPresent("http://original.com", adLink);

        assertEquals("http://original.com", result);
    }

    @Test
    public void testHandleDeeplinkIfPresent_noDeeplinkParamWithFallback() {
        String adLink = "vrvdl://test?fallbackUrl=http://fallback.com";

        String result = urlHandler.handleDeeplinkIfPresent("http://original.com", adLink);

        assertEquals("http://fallback.com", result);
    }

    @Test
    public void testHandleDeeplinkIfPresent_noDeeplinkSchema() {
        String adLink = "http://notdeeplink.com";

        String result = urlHandler.handleDeeplinkIfPresent("http://original.com", adLink);

        assertEquals("http://original.com", result);
    }

    @Test
    public void testHandleDeeplinkIfPresent_parsingException() {
        // Malformed URI to trigger exception
        String adLink = "vrvdl://test?deeplinkUrl=%E0%A4%A";

        String result = urlHandler.handleDeeplinkIfPresent("http://original.com", adLink);

        assertEquals("http://original.com", result);
    }

}
