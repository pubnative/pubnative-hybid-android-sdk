// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.browser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import net.pubnative.lite.sdk.utils.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
public class BaseWebViewClientTest {

    @Mock
    private BrowserActivity.WebViewCloseListener mockCloseListener;
    @Mock
    private BaseWebViewClient.WebViewClientCallback mockCallback;
    @Mock
    private WebView mockWebView;
    @Mock
    private Context mockContext;
    @Mock
    private WebResourceRequest mockRequest;
    @Mock
    private Uri mockUri;

    private MockedStatic<Uri> mockedUriStatic;
    private BaseWebViewClient baseWebViewClient;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockedUriStatic = mockStatic(Uri.class);
        when(Uri.parse(anyString())).thenReturn(mockUri);

        when(mockWebView.getContext()).thenReturn(mockContext);
        when(mockRequest.getUrl()).thenReturn(mockUri);

        baseWebViewClient = new BaseWebViewClient(mockCloseListener);
        baseWebViewClient.setWebViewClientCallback(mockCallback);
    }

    @After
    public void tearDown() {
        mockedUriStatic.close();
    }

    // --- Simple Callback Passthrough Tests ---

    @Test
    public void onPageStarted_withCallback_invokesCallback() {
        baseWebViewClient.onPageStarted(mockWebView, "https://example.com", mock(Bitmap.class));
        verify(mockCallback).onPageStartedLoading("https://example.com");
    }

    @Test
    public void onPageFinished_withNullCallback_doesNothing() {
        baseWebViewClient.setWebViewClientCallback(null);
        baseWebViewClient.onPageFinished(mockWebView, "https://example.com");
        // No exception should be thrown, and we can't verify a null object.
    }

    @Test
    public void onReceivedError_withCallback_invokesCallback() {
        baseWebViewClient.onReceivedError(mockWebView, 404, "Not Found", "https://example.com");
        verify(mockCallback).onGeneralError(404, "Not Found", "https://example.com");
    }

    // --- shouldOverrideUrlLoading (Legacy, API < 24) ---
    @Test
    public void shouldOverrideUrlLoading_legacy_withCallback_returnsCallbackValue() {
        when(mockCallback.shouldOverrideUrlLoading("https://example.com")).thenReturn(true);
        assertTrue(baseWebViewClient.shouldOverrideUrlLoading(mockWebView, "https://example.com"));
    }

    @Test
    public void shouldOverrideUrlLoading_legacy_withNullCallback_returnsFalse() {
        // Setup mock for HTTPS URL - this should delegate to shouldOverrideUrlLoadingInternal
        when(mockUri.getScheme()).thenReturn("https");
        when(mockUri.getHost()).thenReturn("example.com");
        when(mockUri.toString()).thenReturn("https://example.com");

        baseWebViewClient.setWebViewClientCallback(null);
        assertFalse(baseWebViewClient.shouldOverrideUrlLoading(mockWebView, "https://example.com"));
    }

    // --- shouldOverrideUrlLoading (Modern, API >= 24) ---
    @Test
    @Config(sdk = Build.VERSION_CODES.N)
    public void shouldOverrideUrlLoading_modern_withIntentScheme_returnsTrue() {
        // Mock the intent URL and its parsing
        String intentUrl = "intent://test#Intent;scheme=test;package=com.test;end";
        when(mockRequest.getUrl().toString()).thenReturn(intentUrl);
        when(mockUri.getScheme()).thenReturn("intent");
        when(mockUri.toString()).thenReturn(intentUrl);
        when(mockUri.getHost()).thenReturn(null);

        // Mock Intent.parseUri static method
        try (MockedStatic<Intent> mockedIntent = mockStatic(Intent.class)) {
            Intent mockIntent = mock(Intent.class);
            mockedIntent.when(() -> Intent.parseUri(eq(intentUrl), eq(Intent.URI_INTENT_SCHEME))).thenReturn(mockIntent);
            when(mockIntent.getPackage()).thenReturn("com.test");

            // Mock PackageManager to return null (app not installed)
            PackageManager mockPackageManager = mock(PackageManager.class);
            when(mockWebView.getContext()).thenReturn(mockContext);
            when(mockContext.getPackageManager()).thenReturn(mockPackageManager);
            when(mockIntent.resolveActivity(mockPackageManager)).thenReturn(null);
            when(mockIntent.getStringExtra("browser_fallback_url")).thenReturn(null);

            // Since app is not installed and no fallback URL, it should try market URI
            Uri marketUri = mock(Uri.class);
            when(Uri.parse("market://details?id=com.test")).thenReturn(marketUri);

            BaseWebViewClient spiedClient = spy(baseWebViewClient);
            doReturn(true).when(spiedClient).forceHandleDeepLink(marketUri, mockWebView);

            boolean result = spiedClient.shouldOverrideUrlLoading(mockWebView, mockRequest);
            assertTrue(result);
        }
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.N)
    public void shouldOverrideUrlLoading_modern_withMarketUrlAndId_handlesDeepLinkAndCloses() {
        when(mockUri.toString()).thenReturn("https://play.google.com/store/apps/details?id=com.example.app");
        when(mockUri.getHost()).thenReturn("play.google.com");
        when(mockUri.getQueryParameter("id")).thenReturn("com.example.app");
        // Mock the static Uri.parse for the internal call
        Uri marketUri = mock(Uri.class);
        when(Uri.parse("market://details?id=com.example.app")).thenReturn(marketUri);

        BaseWebViewClient spiedClient = spy(baseWebViewClient);
        doReturn(true).when(spiedClient).forceHandleDeepLink(marketUri, mockWebView);

        boolean result = spiedClient.shouldOverrideUrlLoading(mockWebView, mockRequest);

        assertTrue(result);
        verify(mockCloseListener).onWebViewCloseRequested();
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.N)
    public void shouldOverrideUrlLoading_modern_withHttpScheme_delegatesToInternal() {
        when(mockUri.getScheme()).thenReturn("https");
        when(mockRequest.getUrl().toString()).thenReturn("https://example.com");
        when(mockCallback.shouldOverrideUrlLoading("https://example.com")).thenReturn(true);

        assertTrue(baseWebViewClient.shouldOverrideUrlLoading(mockWebView, mockRequest));
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.N)
    public void shouldOverrideUrlLoading_modern_withCustomScheme_handlesDeepLinkAndCloses() {
        when(mockUri.getScheme()).thenReturn("custom-app");
        BaseWebViewClient spiedClient = spy(baseWebViewClient);
        doReturn(true).when(spiedClient).forceHandleDeepLink(mockUri, mockWebView);

        boolean result = spiedClient.shouldOverrideUrlLoading(mockWebView, mockRequest);

        assertTrue(result);
        verify(mockCloseListener).onWebViewCloseRequested();
    }

    // --- forceHandleDeepLink Tests ---
    @Test
    public void forceHandleDeepLink_onSuccess_startsActivityAndReturnsTrue() {
        boolean result = baseWebViewClient.forceHandleDeepLink(mockUri, mockWebView);

        assertTrue(result);
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mockContext).startActivity(intentCaptor.capture());

        Intent capturedIntent = intentCaptor.getValue();
        assertEquals(Intent.ACTION_VIEW, capturedIntent.getAction());
        assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, capturedIntent.getFlags());
        assertEquals(mockUri, capturedIntent.getData());
    }

    @Test
    public void forceHandleDeepLink_onRuntimeException_returnsFalse() {
        // Assume Logger is a static class in the SDK
        try (MockedStatic<Logger> mockedLogger = mockStatic(Logger.class)) {
            doThrow(new RuntimeException("Activity not found")).when(mockContext).startActivity(any(Intent.class));

            boolean result = baseWebViewClient.forceHandleDeepLink(mockUri, mockWebView);

            assertFalse(result);
            mockedLogger.verify(() -> Logger.e(anyString(), eq("Activity not found")));
        }
    }

    @Test
    public void onPageStarted_withNullCallback_doesNothing() {
        baseWebViewClient.setWebViewClientCallback(null);
        // Should not throw exception
        baseWebViewClient.onPageStarted(mockWebView, "https://example.com", mock(Bitmap.class));
    }

    @Test
    public void onPageFinished_withCallback_invokesCallback() {
        baseWebViewClient.onPageFinished(mockWebView, "https://example.com");
        verify(mockCallback).onPageFinishedLoading("https://example.com");
    }

    @Test
    public void onReceivedError_withNullCallback_doesNothing() {
        baseWebViewClient.setWebViewClientCallback(null);
        // Should not throw exception
        baseWebViewClient.onReceivedError(mockWebView, 404, "Not Found", "https://example.com");
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void onReceivedError_modern_withCallback_invokesCallback() {
        android.webkit.WebResourceError mockError = mock(android.webkit.WebResourceError.class);
        when(mockError.getErrorCode()).thenReturn(404);
        when(mockError.getDescription()).thenReturn("Not Found");
        when(mockRequest.getUrl().toString()).thenReturn("https://example.com");

        baseWebViewClient.onReceivedError(mockWebView, mockRequest, mockError);

        verify(mockCallback).onGeneralError(404, "Not Found", "https://example.com");
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void onReceivedError_modern_withNullCallback_doesNothing() {
        baseWebViewClient.setWebViewClientCallback(null);
        android.webkit.WebResourceError mockError = mock(android.webkit.WebResourceError.class);

        // Should not throw exception
        baseWebViewClient.onReceivedError(mockWebView, mockRequest, mockError);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void onReceivedHttpError_withCallback_invokesCallback() {
        android.webkit.WebResourceResponse mockResponse = mock(android.webkit.WebResourceResponse.class);

        baseWebViewClient.onReceivedHttpError(mockWebView, mockRequest, mockResponse);

        verify(mockCallback).onHttpError(mockRequest, mockResponse);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void onReceivedHttpError_withNullCallback_doesNothing() {
        baseWebViewClient.setWebViewClientCallback(null);
        android.webkit.WebResourceResponse mockResponse = mock(android.webkit.WebResourceResponse.class);

        // Should not throw exception
        baseWebViewClient.onReceivedHttpError(mockWebView, mockRequest, mockResponse);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.O)
    public void onRenderProcessGone_withCallback_invokesCallbackAndReturnsTrue() {
        android.webkit.RenderProcessGoneDetail mockDetail = mock(android.webkit.RenderProcessGoneDetail.class);

        boolean result = baseWebViewClient.onRenderProcessGone(mockWebView, mockDetail);

        assertTrue(result);
        verify(mockCallback).onRenderProcessGone();
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.O)
    public void onRenderProcessGone_withNullCallback_returnsFalse() {
        baseWebViewClient.setWebViewClientCallback(null);
        android.webkit.RenderProcessGoneDetail mockDetail = mock(android.webkit.RenderProcessGoneDetail.class);

        boolean result = baseWebViewClient.onRenderProcessGone(mockWebView, mockDetail);

        assertFalse(result);
    }

    // --- Market/Play Store URL Tests ---

    @Test
    public void shouldOverrideUrlLoading_legacy_withMarketAndroidComHost_handlesDeepLink() {
        when(mockUri.getHost()).thenReturn("market.android.com");
        when(mockUri.getScheme()).thenReturn("https");
        when(mockUri.toString()).thenReturn("https://market.android.com/details?id=com.test");
        when(mockUri.getQueryParameter("id")).thenReturn("com.test");

        Uri marketUri = mock(Uri.class);
        when(Uri.parse("market://details?id=com.test")).thenReturn(marketUri);

        BaseWebViewClient spiedClient = spy(baseWebViewClient);
        doReturn(true).when(spiedClient).forceHandleDeepLink(marketUri, mockWebView);

        boolean result = spiedClient.shouldOverrideUrlLoading(mockWebView, "https://market.android.com/details?id=com.test");

        assertTrue(result);
        verify(mockCloseListener).onWebViewCloseRequested();
    }

    @Test
    public void shouldOverrideUrlLoading_legacy_withMarketScheme_handlesDeepLink() {
        when(mockUri.getScheme()).thenReturn("market");
        when(mockUri.getHost()).thenReturn(null);
        when(mockUri.toString()).thenReturn("market://details?id=com.test");
        when(mockUri.getQueryParameter("id")).thenReturn("com.test");

        Uri marketUri = mock(Uri.class);
        when(Uri.parse("market://details?id=com.test")).thenReturn(marketUri);

        BaseWebViewClient spiedClient = spy(baseWebViewClient);
        doReturn(true).when(spiedClient).forceHandleDeepLink(marketUri, mockWebView);

        boolean result = spiedClient.shouldOverrideUrlLoading(mockWebView, "market://details?id=com.test");

        assertTrue(result);
        verify(mockCloseListener).onWebViewCloseRequested();
    }

    @Test
    public void shouldOverrideUrlLoading_legacy_withPlayGoogleComPrefix_handlesDeepLink() {
        when(mockUri.getHost()).thenReturn("example.com");
        when(mockUri.getScheme()).thenReturn("https");
        when(mockUri.toString()).thenReturn("play.google.com/store/apps/details?id=com.test");
        when(mockUri.getQueryParameter("id")).thenReturn("com.test");

        Uri marketUri = mock(Uri.class);
        when(Uri.parse("market://details?id=com.test")).thenReturn(marketUri);

        BaseWebViewClient spiedClient = spy(baseWebViewClient);
        doReturn(true).when(spiedClient).forceHandleDeepLink(marketUri, mockWebView);

        boolean result = spiedClient.shouldOverrideUrlLoading(mockWebView, "play.google.com/store/apps/details?id=com.test");

        assertTrue(result);
        verify(mockCloseListener).onWebViewCloseRequested();
    }

    @Test
    public void shouldOverrideUrlLoading_legacy_withMarketAndroidComPrefix_handlesDeepLink() {
        when(mockUri.getHost()).thenReturn("example.com");
        when(mockUri.getScheme()).thenReturn("https");
        when(mockUri.toString()).thenReturn("market.android.com/details?id=com.test");
        when(mockUri.getQueryParameter("id")).thenReturn("com.test");

        Uri marketUri = mock(Uri.class);
        when(Uri.parse("market://details?id=com.test")).thenReturn(marketUri);

        BaseWebViewClient spiedClient = spy(baseWebViewClient);
        doReturn(true).when(spiedClient).forceHandleDeepLink(marketUri, mockWebView);

        boolean result = spiedClient.shouldOverrideUrlLoading(mockWebView, "market.android.com/details?id=com.test");

        assertTrue(result);
        verify(mockCloseListener).onWebViewCloseRequested();
    }

    @Test
    public void shouldOverrideUrlLoading_legacy_withPlayStoreUrlNoPackageId_handlesDeepLink() {
        when(mockUri.getHost()).thenReturn("play.google.com");
        when(mockUri.getScheme()).thenReturn("https");
        when(mockUri.toString()).thenReturn("https://play.google.com/store");
        when(mockUri.getQueryParameter("id")).thenReturn(null);

        BaseWebViewClient spiedClient = spy(baseWebViewClient);
        doReturn(true).when(spiedClient).forceHandleDeepLink(mockUri, mockWebView);

        boolean result = spiedClient.shouldOverrideUrlLoading(mockWebView, "https://play.google.com/store");

        assertTrue(result);
    }

    @Test
    public void shouldOverrideUrlLoading_legacy_withHttpScheme_delegatesToCallback() {
        when(mockUri.getScheme()).thenReturn("http");
        when(mockUri.getHost()).thenReturn("example.com");
        when(mockUri.toString()).thenReturn("http://example.com");
        when(mockCallback.shouldOverrideUrlLoading("http://example.com")).thenReturn(false);

        boolean result = baseWebViewClient.shouldOverrideUrlLoading(mockWebView, "http://example.com");

        assertFalse(result);
        verify(mockCallback).shouldOverrideUrlLoading("http://example.com");
    }

    @Test
    public void shouldOverrideUrlLoading_legacy_withCustomScheme_handlesDeepLink() {
        when(mockUri.getScheme()).thenReturn("myapp");
        when(mockUri.getHost()).thenReturn("action");
        when(mockUri.toString()).thenReturn("myapp://action");

        BaseWebViewClient spiedClient = spy(baseWebViewClient);
        doReturn(true).when(spiedClient).forceHandleDeepLink(mockUri, mockWebView);

        boolean result = spiedClient.shouldOverrideUrlLoading(mockWebView, "myapp://action");

        assertTrue(result);
        verify(mockCloseListener).onWebViewCloseRequested();
    }

    @Test
    public void shouldOverrideUrlLoading_legacy_withCustomSchemeFailedDeepLink_doesNotClose() {
        when(mockUri.getScheme()).thenReturn("myapp");
        when(mockUri.getHost()).thenReturn("action");
        when(mockUri.toString()).thenReturn("myapp://action");

        BaseWebViewClient spiedClient = spy(baseWebViewClient);
        doReturn(false).when(spiedClient).forceHandleDeepLink(mockUri, mockWebView);

        boolean result = spiedClient.shouldOverrideUrlLoading(mockWebView, "myapp://action");

        assertFalse(result);
        verify(mockCloseListener, org.mockito.Mockito.never()).onWebViewCloseRequested();
    }

    // --- Intent URL Handling Tests ---

    @Test
    public void shouldOverrideUrlLoading_legacy_withIntentUrl_appInstalled_launchesApp() {
        String intentUrl = "intent://scan#Intent;scheme=zxing;package=com.google.zxing.client.android;end";
        when(mockUri.getScheme()).thenReturn("intent");
        when(mockUri.toString()).thenReturn(intentUrl);

        try (MockedStatic<Intent> mockedIntent = mockStatic(Intent.class)) {
            Intent mockIntent = mock(Intent.class);
            mockedIntent.when(() -> Intent.parseUri(eq(intentUrl), eq(Intent.URI_INTENT_SCHEME)))
                    .thenReturn(mockIntent);

            PackageManager mockPackageManager = mock(PackageManager.class);
            android.content.ComponentName mockComponent = mock(android.content.ComponentName.class);
            when(mockContext.getPackageManager()).thenReturn(mockPackageManager);
            when(mockIntent.resolveActivity(mockPackageManager)).thenReturn(mockComponent);

            boolean result = baseWebViewClient.shouldOverrideUrlLoading(mockWebView, intentUrl);

            assertTrue(result);
            verify(mockContext).startActivity(mockIntent);
            verify(mockCloseListener).onWebViewCloseRequested();
        }
    }

    @Test
    public void shouldOverrideUrlLoading_legacy_withIntentUrl_appNotInstalled_withFallbackUrl_loadsFallback() {
        String intentUrl = "intent://test#Intent;scheme=test;package=com.test;S.browser_fallback_url=https://fallback.com;end";
        when(mockUri.getScheme()).thenReturn("intent");
        when(mockUri.toString()).thenReturn(intentUrl);

        try (MockedStatic<Intent> mockedIntent = mockStatic(Intent.class)) {
            Intent mockIntent = mock(Intent.class);
            mockedIntent.when(() -> Intent.parseUri(eq(intentUrl), eq(Intent.URI_INTENT_SCHEME)))
                    .thenReturn(mockIntent);

            PackageManager mockPackageManager = mock(PackageManager.class);
            when(mockContext.getPackageManager()).thenReturn(mockPackageManager);
            when(mockIntent.resolveActivity(mockPackageManager)).thenReturn(null);
            when(mockIntent.getStringExtra("browser_fallback_url")).thenReturn("https://fallback.com");

            boolean result = baseWebViewClient.shouldOverrideUrlLoading(mockWebView, intentUrl);

            assertTrue(result);
            verify(mockWebView).loadUrl("https://fallback.com");
        }
    }

    @Test
    public void shouldOverrideUrlLoading_legacy_withIntentUrl_appNotInstalled_noFallback_withPackage_opensMarket() {
        String intentUrl = "intent://test#Intent;scheme=test;package=com.test;end";
        when(mockUri.getScheme()).thenReturn("intent");
        when(mockUri.toString()).thenReturn(intentUrl);

        try (MockedStatic<Intent> mockedIntent = mockStatic(Intent.class)) {
            Intent mockIntent = mock(Intent.class);
            mockedIntent.when(() -> Intent.parseUri(eq(intentUrl), eq(Intent.URI_INTENT_SCHEME)))
                    .thenReturn(mockIntent);

            PackageManager mockPackageManager = mock(PackageManager.class);
            when(mockContext.getPackageManager()).thenReturn(mockPackageManager);
            when(mockIntent.resolveActivity(mockPackageManager)).thenReturn(null);
            when(mockIntent.getStringExtra("browser_fallback_url")).thenReturn(null);
            when(mockIntent.getPackage()).thenReturn("com.test");

            Uri marketUri = mock(Uri.class);
            when(Uri.parse("market://details?id=com.test")).thenReturn(marketUri);

            BaseWebViewClient spiedClient = spy(baseWebViewClient);
            doReturn(true).when(spiedClient).forceHandleDeepLink(marketUri, mockWebView);

            boolean result = spiedClient.shouldOverrideUrlLoading(mockWebView, intentUrl);

            assertTrue(result);
            verify(mockCloseListener).onWebViewCloseRequested();
        }
    }

    @Test
    public void shouldOverrideUrlLoading_legacy_withIntentUrl_appNotInstalled_noFallback_noPackage_returnsFalse() {
        String intentUrl = "intent://test#Intent;scheme=test;end";
        when(mockUri.getScheme()).thenReturn("intent");
        when(mockUri.toString()).thenReturn(intentUrl);

        try (MockedStatic<Intent> mockedIntent = mockStatic(Intent.class)) {
            Intent mockIntent = mock(Intent.class);
            mockedIntent.when(() -> Intent.parseUri(eq(intentUrl), eq(Intent.URI_INTENT_SCHEME)))
                    .thenReturn(mockIntent);

            PackageManager mockPackageManager = mock(PackageManager.class);
            when(mockContext.getPackageManager()).thenReturn(mockPackageManager);
            when(mockIntent.resolveActivity(mockPackageManager)).thenReturn(null);
            when(mockIntent.getStringExtra("browser_fallback_url")).thenReturn(null);
            when(mockIntent.getPackage()).thenReturn(null);

            boolean result = baseWebViewClient.shouldOverrideUrlLoading(mockWebView, intentUrl);

            assertFalse(result);
        }
    }

    @Test
    public void shouldOverrideUrlLoading_legacy_withIntentUrl_appNotInstalled_emptyPackage_returnsFalse() {
        String intentUrl = "intent://test#Intent;scheme=test;package=;end";
        when(mockUri.getScheme()).thenReturn("intent");
        when(mockUri.toString()).thenReturn(intentUrl);

        try (MockedStatic<Intent> mockedIntent = mockStatic(Intent.class)) {
            Intent mockIntent = mock(Intent.class);
            mockedIntent.when(() -> Intent.parseUri(eq(intentUrl), eq(Intent.URI_INTENT_SCHEME)))
                    .thenReturn(mockIntent);

            PackageManager mockPackageManager = mock(PackageManager.class);
            when(mockContext.getPackageManager()).thenReturn(mockPackageManager);
            when(mockIntent.resolveActivity(mockPackageManager)).thenReturn(null);
            when(mockIntent.getStringExtra("browser_fallback_url")).thenReturn(null);
            when(mockIntent.getPackage()).thenReturn("");

            boolean result = baseWebViewClient.shouldOverrideUrlLoading(mockWebView, intentUrl);

            assertFalse(result);
        }
    }

    @Test
    public void shouldOverrideUrlLoading_legacy_withIntentUrl_appNotInstalled_emptyFallback_withPackage_opensMarket() {
        String intentUrl = "intent://test#Intent;scheme=test;package=com.test;end";
        when(mockUri.getScheme()).thenReturn("intent");
        when(mockUri.toString()).thenReturn(intentUrl);

        try (MockedStatic<Intent> mockedIntent = mockStatic(Intent.class)) {
            Intent mockIntent = mock(Intent.class);
            mockedIntent.when(() -> Intent.parseUri(eq(intentUrl), eq(Intent.URI_INTENT_SCHEME)))
                    .thenReturn(mockIntent);

            PackageManager mockPackageManager = mock(PackageManager.class);
            when(mockContext.getPackageManager()).thenReturn(mockPackageManager);
            when(mockIntent.resolveActivity(mockPackageManager)).thenReturn(null);
            when(mockIntent.getStringExtra("browser_fallback_url")).thenReturn("");
            when(mockIntent.getPackage()).thenReturn("com.test");

            Uri marketUri = mock(Uri.class);
            when(Uri.parse("market://details?id=com.test")).thenReturn(marketUri);

            BaseWebViewClient spiedClient = spy(baseWebViewClient);
            doReturn(true).when(spiedClient).forceHandleDeepLink(marketUri, mockWebView);

            boolean result = spiedClient.shouldOverrideUrlLoading(mockWebView, intentUrl);

            assertTrue(result);
            verify(mockCloseListener).onWebViewCloseRequested();
        }
    }

    @Test
    public void shouldOverrideUrlLoading_legacy_withIntentUrl_uriSyntaxException_returnsFalse() {
        String intentUrl = "intent://malformed";
        when(mockUri.getScheme()).thenReturn("intent");
        when(mockUri.toString()).thenReturn(intentUrl);

        try (MockedStatic<Intent> mockedIntent = mockStatic(Intent.class);
             MockedStatic<Logger> mockedLogger = mockStatic(Logger.class)) {

            mockedIntent.when(() -> Intent.parseUri(eq(intentUrl), eq(Intent.URI_INTENT_SCHEME)))
                    .thenThrow(new java.net.URISyntaxException(intentUrl, "Invalid URI"));

            boolean result = baseWebViewClient.shouldOverrideUrlLoading(mockWebView, intentUrl);

            assertFalse(result);
            mockedLogger.verify(() -> Logger.e(anyString(), anyString()));
        }
    }

    @Test
    public void shouldOverrideUrlLoading_legacy_withIntentUrl_activityNotFoundException_returnsFalse() {
        String intentUrl = "intent://test#Intent;scheme=test;package=com.test;end";
        when(mockUri.getScheme()).thenReturn("intent");
        when(mockUri.toString()).thenReturn(intentUrl);

        try (MockedStatic<Intent> mockedIntent = mockStatic(Intent.class);
             MockedStatic<Logger> mockedLogger = mockStatic(Logger.class)) {

            mockedIntent.when(() -> Intent.parseUri(eq(intentUrl), eq(Intent.URI_INTENT_SCHEME)))
                    .thenThrow(new android.content.ActivityNotFoundException("Not found"));

            boolean result = baseWebViewClient.shouldOverrideUrlLoading(mockWebView, intentUrl);

            assertFalse(result);
            mockedLogger.verify(() -> Logger.e(anyString(), anyString()));
        }
    }

    @Test
    public void shouldOverrideUrlLoading_legacy_withIntentUrl_genericException_returnsFalse() {
        String intentUrl = "intent://test#Intent;scheme=test;package=com.test;end";
        when(mockUri.getScheme()).thenReturn("intent");
        when(mockUri.toString()).thenReturn(intentUrl);

        try (MockedStatic<Intent> mockedIntent = mockStatic(Intent.class);
             MockedStatic<Logger> mockedLogger = mockStatic(Logger.class)) {

            mockedIntent.when(() -> Intent.parseUri(eq(intentUrl), eq(Intent.URI_INTENT_SCHEME)))
                    .thenThrow(new RuntimeException("Generic error"));

            boolean result = baseWebViewClient.shouldOverrideUrlLoading(mockWebView, intentUrl);

            assertFalse(result);
            mockedLogger.verify(() -> Logger.e(anyString(), anyString()));
        }
    }
}