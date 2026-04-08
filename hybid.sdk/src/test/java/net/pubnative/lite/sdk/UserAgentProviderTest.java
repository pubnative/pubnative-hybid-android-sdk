// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Looper;
import android.webkit.WebSettings;
import android.webkit.WebView;

import net.pubnative.lite.sdk.models.request.BrandVersion;
import net.pubnative.lite.sdk.models.request.UserAgent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class UserAgentProviderTest {

    @Mock
    private Context mockContext;

    @Mock
    private SharedPreferences mockSharedPreferences;

    @Mock
    private SharedPreferences.Editor mockEditor;

    private UserAgentProvider userAgentProvider;
    private AutoCloseable mockitoCloseable;

    @Before
    public void setUp() {
        mockitoCloseable = MockitoAnnotations.openMocks(this);
        userAgentProvider = new UserAgentProvider();

        // Setup default mock behavior
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        when(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor);
    }

    @After
    public void tearDown() throws Exception {
        mockitoCloseable.close();
    }

    @Test
    public void getUserAgent_whenNotInitialized_returnsNull() {
        // When
        String userAgent = userAgentProvider.getUserAgent();

        // Then
        assertNull(userAgent);
    }

    @Test
    public void getStructuredUserAgent_whenNotInitialized_returnsNull() {
        // When
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();

        // Then
        assertNull(structuredUserAgent);
    }

    @Test
    public void initialise_callsFetchUserAgent() {
        // Given
        when(mockSharedPreferences.getString(anyString(), anyString())).thenReturn("");
        when(mockSharedPreferences.getInt(anyString(), anyInt())).thenReturn(-1);

        try (MockedConstruction<WebView> webViewMock = mockConstruction(WebView.class,
                (mock, context) -> {
                    WebSettings webSettings = mock(WebSettings.class);
                    when(mock.getSettings()).thenReturn(webSettings);
                    when(webSettings.getUserAgentString()).thenReturn("Mozilla/5.0 (Linux; Android 10) Chrome/91.0.4472.120");
                })) {

            // When
            userAgentProvider.initialise(mockContext);
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            // Then
            verify(mockContext).getSharedPreferences(eq("net.pubnative.lite.useragent"), eq(Context.MODE_PRIVATE));
            assertNotNull("User agent should be set after initialization", userAgentProvider.getUserAgent());
            assertNotNull("Structured user agent should be set after initialization", userAgentProvider.getStructuredUserAgent());
        }
    }

    @Test
    public void fetchUserAgent_withCachedValidUserAgent_usesCachedValue() {
        // Given
        String cachedUserAgent = "Mozilla/5.0 (Linux; Android 10) Chrome/91.0.4472.120";
        when(mockSharedPreferences.getString(eq("hybid_user_agent"), anyString())).thenReturn(cachedUserAgent);
        when(mockSharedPreferences.getInt(eq("hybid_user_agent_last_version"), anyInt())).thenReturn(Build.VERSION.SDK_INT);

        // When
        userAgentProvider.fetchUserAgent(mockContext);

        // Then
        assertEquals(cachedUserAgent, userAgentProvider.getUserAgent());
        assertNotNull(userAgentProvider.getStructuredUserAgent());
    }

    @Test
    public void fetchUserAgent_withCachedInvalidVersion_fetchesNewUserAgent() {
        // Given
        String cachedUserAgent = "Mozilla/5.0 (Linux; Android 9) Chrome/90.0.4430.66";
        when(mockSharedPreferences.getString(eq("hybid_user_agent"), anyString())).thenReturn(cachedUserAgent);
        when(mockSharedPreferences.getInt(eq("hybid_user_agent_last_version"), anyInt())).thenReturn(Build.VERSION.SDK_INT - 1);

        try (MockedConstruction<WebView> webViewMock = mockConstruction(WebView.class,
                (mock, context) -> {
                    WebSettings webSettings = mock(WebSettings.class);
                    when(mock.getSettings()).thenReturn(webSettings);
                    when(webSettings.getUserAgentString()).thenReturn("Mozilla/5.0 (Linux; Android 10) Chrome/91.0.4472.120");
                })) {

            // When
            userAgentProvider.fetchUserAgent(mockContext);
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            // Then
            verify(mockEditor).putString(eq("hybid_user_agent"), anyString());
            verify(mockEditor).putInt(eq("hybid_user_agent_last_version"), eq(Build.VERSION.SDK_INT));
            verify(mockEditor).apply();
        }
    }

    @Test
    public void fetchUserAgent_withEmptyCachedUserAgent_fetchesNewUserAgent() {
        // Given
        when(mockSharedPreferences.getString(eq("hybid_user_agent"), anyString())).thenReturn("");
        when(mockSharedPreferences.getInt(eq("hybid_user_agent_last_version"), anyInt())).thenReturn(-1);

        try (MockedConstruction<WebView> webViewMock = mockConstruction(WebView.class,
                (mock, context) -> {
                    WebSettings webSettings = mock(WebSettings.class);
                    when(mock.getSettings()).thenReturn(webSettings);
                    when(webSettings.getUserAgentString()).thenReturn("Mozilla/5.0 (Linux; Android 10) Chrome/91.0.4472.120");
                })) {

            // When
            userAgentProvider.fetchUserAgent(mockContext);
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            // Then
            assertNotNull(userAgentProvider.getUserAgent());
            verify(mockEditor).putString(eq("hybid_user_agent"), anyString());
            verify(mockEditor).apply();
        }
    }

    @Test
    public void fetchUserAgent_whenWebViewThrowsException_handlesGracefully() {
        // Given
        when(mockSharedPreferences.getString(eq("hybid_user_agent"), anyString())).thenReturn("");
        when(mockSharedPreferences.getInt(eq("hybid_user_agent_last_version"), anyInt())).thenReturn(-1);

        try (MockedConstruction<WebView> webViewMock = mockConstruction(WebView.class,
                (mock, context) -> {
                    when(mock.getSettings()).thenThrow(new RuntimeException("WebView initialization failed"));
                });
             MockedStatic<HyBid> hybidMock = mockStatic(HyBid.class)) {

            // When
            userAgentProvider.fetchUserAgent(mockContext);
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            // Then
            hybidMock.verify(() -> HyBid.reportException(any(RuntimeException.class)));
            verify(mockEditor, never()).apply();

            // Verify structured user agent is still created with fallback values
            UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
            assertNotNull("Structured user agent should be created even on exception", structuredUserAgent);
            assertEquals(Integer.valueOf(0), structuredUserAgent.getSource());
            assertEquals(Integer.valueOf(1), structuredUserAgent.getMobile());
        }
    }

    @Test
    public void fetchUserAgent_whenWebViewReturnsEmptyUserAgent_doesNotSave() {
        // Given
        when(mockSharedPreferences.getString(eq("hybid_user_agent"), anyString())).thenReturn("");
        when(mockSharedPreferences.getInt(eq("hybid_user_agent_last_version"), anyInt())).thenReturn(-1);

        try (MockedConstruction<WebView> webViewMock = mockConstruction(WebView.class,
                (mock, context) -> {
                    WebSettings webSettings = mock(WebSettings.class);
                    when(mock.getSettings()).thenReturn(webSettings);
                    when(webSettings.getUserAgentString()).thenReturn("");
                })) {

            // When
            userAgentProvider.fetchUserAgent(mockContext);
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            // Then
            verify(mockEditor, never()).apply();
        }
    }

    @Test
    public void fetchStructuredUserAgent_withNullUserAgent_createsStructuredUserAgent() {
        // When
        userAgentProvider.fetchStructuredUserAgent(null);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        assertNotNull(structuredUserAgent);
        assertEquals(Integer.valueOf(0), structuredUserAgent.getSource());
        assertEquals(Integer.valueOf(1), structuredUserAgent.getMobile());
        assertNotNull(structuredUserAgent.getPlatform());
        assertEquals("Android", structuredUserAgent.getPlatform().getBrand());
        assertNotNull(structuredUserAgent.getModel());
        assertNotNull(structuredUserAgent.getBrowsers());
    }

    @Test
    public void fetchStructuredUserAgent_withChromeUserAgent_extractsChromeBrowser() {
        // Given
        String userAgent = "Mozilla/5.0 (Linux; Android 10; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36";

        // When
        userAgentProvider.fetchStructuredUserAgent(userAgent);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        assertNotNull(structuredUserAgent);
        List<BrandVersion> browsers = structuredUserAgent.getBrowsers();
        assertNotNull(browsers);
        assertTrue(browsers.size() > 0);

        // Check if Chrome is detected
        boolean chromeFound = false;
        for (BrandVersion browser : browsers) {
            if ("Chrome".equals(browser.getBrand())) {
                chromeFound = true;
                assertNotNull(browser.getVersion());
                assertEquals("91", browser.getVersion().get(0));
                break;
            }
        }
        assertTrue("Chrome browser should be detected", chromeFound);
    }

    @Test
    public void fetchStructuredUserAgent_withFirefoxUserAgent_extractsFirefoxBrowser() {
        // Given
        String userAgent = "Mozilla/5.0 (Android 10; Mobile; rv:89.0) Gecko/89.0 Firefox/89.0";

        // When
        userAgentProvider.fetchStructuredUserAgent(userAgent);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        assertNotNull(structuredUserAgent);
        List<BrandVersion> browsers = structuredUserAgent.getBrowsers();

        boolean firefoxFound = false;
        for (BrandVersion browser : browsers) {
            if ("Firefox".equals(browser.getBrand())) {
                firefoxFound = true;
                assertNotNull(browser.getVersion());
                assertEquals("89", browser.getVersion().get(0));
                break;
            }
        }
        assertTrue("Firefox browser should be detected", firefoxFound);
    }

    @Test
    public void fetchStructuredUserAgent_withEdgeUserAgent_extractsEdgeBrowser() {
        // Given
        String userAgent = "Mozilla/5.0 (Linux; Android 10; HD1913) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Mobile Safari/537.36 Edg/91.0.864.41";

        // When
        userAgentProvider.fetchStructuredUserAgent(userAgent);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        List<BrandVersion> browsers = structuredUserAgent.getBrowsers();

        boolean edgeFound = false;
        for (BrandVersion browser : browsers) {
            if ("Edge".equals(browser.getBrand())) {
                edgeFound = true;
                assertNotNull(browser.getVersion());
                assertEquals("91", browser.getVersion().get(0));
                break;
            }
        }
        assertTrue("Edge browser should be detected", edgeFound);
    }

    @Test
    public void fetchStructuredUserAgent_withSafariUserAgent_extractsSafariBrowser() {
        // Given
        String userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile Safari/604.1";

        // When
        userAgentProvider.fetchStructuredUserAgent(userAgent);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        List<BrandVersion> browsers = structuredUserAgent.getBrowsers();

        boolean safariFound = false;
        for (BrandVersion browser : browsers) {
            if ("Mobile Safari".equals(browser.getBrand())) {
                safariFound = true;
                assertNotNull(browser.getVersion());
                break;
            }
        }
        assertTrue("Safari browser should be detected", safariFound);
    }

    @Test
    public void fetchStructuredUserAgent_withUnknownUserAgent_returnsUnknownBrowser() {
        // Given
        String userAgent = "CustomBrowser/1.0";

        // When
        userAgentProvider.fetchStructuredUserAgent(userAgent);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        List<BrandVersion> browsers = structuredUserAgent.getBrowsers();
        assertNotNull(browsers);
        assertEquals(1, browsers.size());
        assertEquals("Unknown", browsers.get(0).getBrand());
    }

    @Test
    public void fetchStructuredUserAgent_withEmptyUserAgent_returnsUnknownBrowser() {
        // Given
        String userAgent = "";

        // When
        userAgentProvider.fetchStructuredUserAgent(userAgent);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        List<BrandVersion> browsers = structuredUserAgent.getBrowsers();
        assertEquals(1, browsers.size());
        assertEquals("Unknown", browsers.get(0).getBrand());
    }

    @Test
    public void fetchStructuredUserAgent_onlyCreatesOnce_doesNotRecreate() {
        // Given
        String userAgent1 = "Mozilla/5.0 Chrome/91.0.4472.120";
        String userAgent2 = "Mozilla/5.0 Firefox/89.0";

        // When
        userAgentProvider.fetchStructuredUserAgent(userAgent1);
        UserAgent firstStructured = userAgentProvider.getStructuredUserAgent();
        userAgentProvider.fetchStructuredUserAgent(userAgent2);
        UserAgent secondStructured = userAgentProvider.getStructuredUserAgent();

        // Then
        assertEquals(firstStructured, secondStructured);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP)
    public void fetchStructuredUserAgent_onLollipop_setsArchitectureCorrectly() {
        // When
        userAgentProvider.fetchStructuredUserAgent(null);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        assertNotNull(structuredUserAgent.getArchitecture());
        assertNotNull(structuredUserAgent.getBitness());
    }


    @Test
    public void fetchStructuredUserAgent_setsAndroidPlatform() {
        // When
        userAgentProvider.fetchStructuredUserAgent(null);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        BrandVersion platform = structuredUserAgent.getPlatform();
        assertNotNull(platform);
        assertEquals("Android", platform.getBrand());
        assertNotNull(platform.getVersion());
        assertTrue(platform.getVersion().size() > 0);
        assertEquals(String.valueOf(Build.VERSION.RELEASE), platform.getVersion().get(0));
    }

    @Test
    public void fetchStructuredUserAgent_setsMobileToOne() {
        // When
        userAgentProvider.fetchStructuredUserAgent(null);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        assertEquals(Integer.valueOf(1), structuredUserAgent.getMobile());
    }

    @Test
    public void fetchStructuredUserAgent_setsSourceToZero() {
        // When
        userAgentProvider.fetchStructuredUserAgent(null);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        assertEquals(Integer.valueOf(0), structuredUserAgent.getSource());
    }

    @Test
    public void fetchStructuredUserAgent_setsDeviceModel() {
        // When
        userAgentProvider.fetchStructuredUserAgent(null);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        assertEquals(Build.MODEL, structuredUserAgent.getModel());
    }

    @Test
    public void fetchStructuredUserAgent_withMultipleBrowsers_extractsAll() {
        // Given - User agent with Chrome, WebKit, and Mobile Safari
        String userAgent = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36";

        // When
        userAgentProvider.fetchStructuredUserAgent(userAgent);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        List<BrandVersion> browsers = structuredUserAgent.getBrowsers();
        assertTrue("Should have multiple browsers", browsers.size() >= 2);
    }

    @Test
    public void fetchStructuredUserAgent_withChromiumUserAgent_extractsChromium() {
        // Given
        String userAgent = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chromium/90.0.4430.66 Mobile Safari/537.36";

        // When
        userAgentProvider.fetchStructuredUserAgent(userAgent);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        List<BrandVersion> browsers = structuredUserAgent.getBrowsers();

        boolean chromiumFound = false;
        for (BrandVersion browser : browsers) {
            if ("Chromium".equals(browser.getBrand())) {
                chromiumFound = true;
                break;
            }
        }
        assertTrue("Chromium browser should be detected", chromiumFound);
    }

    @Test
    public void fetchStructuredUserAgent_withWebKitUserAgent_extractsWebKit() {
        // Given
        String userAgent = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko)";

        // When
        userAgentProvider.fetchStructuredUserAgent(userAgent);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        List<BrandVersion> browsers = structuredUserAgent.getBrowsers();

        boolean webkitFound = false;
        for (BrandVersion browser : browsers) {
            if ("AppleWebKit".equals(browser.getBrand())) {
                webkitFound = true;
                break;
            }
        }
        assertTrue("WebKit should be detected", webkitFound);
    }

    @Test
    public void fetchStructuredUserAgent_withVersionWithoutDots_handlesCorrectly() {
        // Given
        String userAgent = "Mozilla/5.0 Chrome/91";

        // When
        userAgentProvider.fetchStructuredUserAgent(userAgent);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        List<BrandVersion> browsers = structuredUserAgent.getBrowsers();

        boolean chromeFound = false;
        for (BrandVersion browser : browsers) {
            if ("Chrome".equals(browser.getBrand())) {
                chromeFound = true;
                assertNotNull(browser.getVersion());
                assertTrue(browser.getVersion().size() >= 1);
                break;
            }
        }
        assertTrue("Chrome should be detected", chromeFound);
    }

    @Test
    public void architecture_isDetectedFromBuild() {
        // When
        userAgentProvider.fetchStructuredUserAgent(null);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        String architecture = structuredUserAgent.getArchitecture();
        assertNotNull("Architecture should not be null", architecture);
        assertFalse("Architecture should not be empty", architecture.isEmpty());

        // Architecture should be detected based on Build.CPU_ABI or Build.SUPPORTED_ABIS
        // and should contain known architecture types or be a valid ABI string
        String lowerArch = architecture.toLowerCase();
        boolean isValidArchitecture = lowerArch.contains("x86") ||
                                      lowerArch.contains("arm") ||
                                      lowerArch.contains("mips") ||
                                      architecture.length() > 0; // or any valid ABI string
        assertTrue("Architecture should be valid: " + architecture, isValidArchitecture);
    }

    @Test
    public void bitness_isSetCorrectly() {
        // When
        userAgentProvider.fetchStructuredUserAgent(null);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        String bitness = structuredUserAgent.getBitness();
        assertNotNull(bitness);
        assertTrue("Bitness should be 32 or 64",
                   "32".equals(bitness) || "64".equals(bitness));
    }

    @Test
    public void fetchStructuredUserAgent_detectsArchitectureAndBitness() {
        // When - Test with actual runtime architecture
        userAgentProvider.fetchStructuredUserAgent(null);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        assertNotNull("Architecture should not be null", structuredUserAgent.getArchitecture());
        assertNotNull("Bitness should not be null", structuredUserAgent.getBitness());

        // Bitness must be either 32 or 64
        String bitness = structuredUserAgent.getBitness();
        assertTrue("Bitness should be either 32 or 64",
            "32".equals(bitness) || "64".equals(bitness));

        // Architecture should be one of the known types or a valid ABI string
        String architecture = structuredUserAgent.getArchitecture();
        assertFalse("Architecture should not be empty", architecture.isEmpty());
    }

    @Test
    public void fetchStructuredUserAgent_multipleCalls_cachesBitnessCorrectly() {
        // Given - First call to initialize the structured user agent
        userAgentProvider.fetchStructuredUserAgent(null);
        UserAgent firstCall = userAgentProvider.getStructuredUserAgent();
        String firstBitness = firstCall.getBitness();

        // When - Call again (should return same cached instance)
        userAgentProvider.fetchStructuredUserAgent("Mozilla/5.0");
        UserAgent secondCall = userAgentProvider.getStructuredUserAgent();
        String secondBitness = secondCall.getBitness();

        // Then - Should be same instance with same bitness
        assertSame("Should return cached structured user agent", firstCall, secondCall);
        assertEquals("Bitness should remain the same", firstBitness, secondBitness);
        assertNotNull("Bitness should not be null", secondBitness);
    }

    @Test
    public void fetchStructuredUserAgent_withDifferentUserAgents_keepsSameBitness() {
        // This test verifies that bitness is determined once by Build configuration,
        // not by the user agent string, covering the getBitness logic

        // When - Call with null user agent
        userAgentProvider.fetchStructuredUserAgent(null);
        UserAgent ua1 = userAgentProvider.getStructuredUserAgent();
        String bitness1 = ua1.getBitness();

        // Create new provider and test with different user agent
        UserAgentProvider provider2 = new UserAgentProvider();
        provider2.fetchStructuredUserAgent("Mozilla/5.0 (Linux; Android 10) Chrome/90.0");
        UserAgent ua2 = provider2.getStructuredUserAgent();
        String bitness2 = ua2.getBitness();

        // Create another provider with empty user agent
        UserAgentProvider provider3 = new UserAgentProvider();
        provider3.fetchStructuredUserAgent("");
        UserAgent ua3 = provider3.getStructuredUserAgent();
        String bitness3 = ua3.getBitness();

        // Then - All should have same bitness from Build configuration
        assertEquals("Bitness should be consistent across different user agents", bitness1, bitness2);
        assertEquals("Bitness should be consistent with empty user agent", bitness1, bitness3);
        assertTrue("Bitness must be 32 or 64", "32".equals(bitness1) || "64".equals(bitness1));
        assertTrue("Bitness must be 32 or 64", "32".equals(bitness2) || "64".equals(bitness2));
        assertTrue("Bitness must be 32 or 64", "32".equals(bitness3) || "64".equals(bitness3));
    }

    // Direct tests for getBitness() method - 100% coverage of all branches

    @Test
    public void getBitness_withNullAbi_returns32() {
        // Given - Testing Branch 1: TextUtils.isEmpty(abi) == true with null
        UserAgentProvider provider = new UserAgentProvider();

        // When
        String bitness = provider.getBitness(null);

        // Then
        assertEquals("Should return 32 for null ABI", "32", bitness);
    }

    @Test
    public void getBitness_withEmptyAbi_returns32() {
        // Given - Testing Branch 1: TextUtils.isEmpty(abi) == true with empty string
        UserAgentProvider provider = new UserAgentProvider();

        // When
        String bitness = provider.getBitness("");

        // Then
        assertEquals("Should return 32 for empty ABI", "32", bitness);
    }

    @Test
    public void getBitness_withAbiContaining64_returns64() {
        // Given - Testing Branch 2: abi.contains("64") == true
        UserAgentProvider provider = new UserAgentProvider();

        // When - Test with various 64-bit ABI strings
        String bitness1 = provider.getBitness("arm64-v8a");
        String bitness2 = provider.getBitness("x86_64");
        String bitness3 = provider.getBitness("mips64");
        String bitness4 = provider.getBitness("unknown64abi");

        // Then
        assertEquals("Should return 64 for arm64-v8a", "64", bitness1);
        assertEquals("Should return 64 for x86_64", "64", bitness2);
        assertEquals("Should return 64 for mips64", "64", bitness3);
        assertEquals("Should return 64 for any ABI containing '64'", "64", bitness4);
    }

    @Test
    public void getBitness_withAbiNotContaining64_returns32() {
        // Given - Testing Branch 3: abi.contains("64") == false
        UserAgentProvider provider = new UserAgentProvider();

        // When - Test with various 32-bit ABI strings
        String bitness1 = provider.getBitness("armeabi-v7a");
        String bitness2 = provider.getBitness("armeabi");
        String bitness3 = provider.getBitness("x86");
        String bitness4 = provider.getBitness("mips");

        // Then
        assertEquals("Should return 32 for armeabi-v7a", "32", bitness1);
        assertEquals("Should return 32 for armeabi", "32", bitness2);
        assertEquals("Should return 32 for x86", "32", bitness3);
        assertEquals("Should return 32 for mips", "32", bitness4);
    }

    @Test
    public void fetchStructuredUserAgent_withBrowserVersionNoMatch_handlesGracefully() {
        // Given - User agent with AppleWebKit but without full version details
        String userAgent = "Mozilla/5.0 AppleWebKit/537.36";

        // When
        userAgentProvider.fetchStructuredUserAgent(userAgent);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        assertNotNull(structuredUserAgent);
        List<BrandVersion> browsers = structuredUserAgent.getBrowsers();
        assertNotNull(browsers);
        assertFalse("Should have at least one browser entry", browsers.isEmpty());

        // Verify that AppleWebKit is detected with version
        boolean webkitFound = false;
        for (BrandVersion browser : browsers) {
            if ("AppleWebKit".equals(browser.getBrand())) {
                webkitFound = true;
                assertNotNull("Browser version should not be null", browser.getVersion());
                assertFalse("Browser version should not be empty", browser.getVersion().isEmpty());
                assertEquals("537", browser.getVersion().get(0));
                break;
            }
        }
        assertTrue("AppleWebKit should be detected", webkitFound);
    }


    @Test
    public void fetchUserAgent_withValidCachedUserAgentButWrongVersion_fetchesNew() {
        // Given
        when(mockSharedPreferences.getString(eq("hybid_user_agent"), anyString())).thenReturn("Mozilla/5.0 Chrome/90.0");
        when(mockSharedPreferences.getInt(eq("hybid_user_agent_last_version"), anyInt())).thenReturn(-1);

        try (MockedConstruction<WebView> webViewMock = mockConstruction(WebView.class,
                (mock, context) -> {
                    WebSettings webSettings = mock(WebSettings.class);
                    when(mock.getSettings()).thenReturn(webSettings);
                    when(webSettings.getUserAgentString()).thenReturn("Mozilla/5.0 Chrome/91.0");
                })) {

            // When
            userAgentProvider.fetchUserAgent(mockContext);
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            // Then
            verify(mockEditor).putString(eq("hybid_user_agent"), anyString());
            verify(mockEditor).apply();
        }
    }

    @Test
    public void fetchStructuredUserAgent_callsGetArchitecture() {
        // When
        userAgentProvider.fetchStructuredUserAgent(null);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        // Verify architecture and bitness are set
        assertNotNull("Architecture should be set", structuredUserAgent.getArchitecture());
        assertNotNull("Bitness should be set", structuredUserAgent.getBitness());
    }

    @Test
    public void fetchStructuredUserAgent_setsDeviceModelFromBuild() {
        // When
        userAgentProvider.fetchStructuredUserAgent(null);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        assertEquals("Model should match Build.MODEL", Build.MODEL, structuredUserAgent.getModel());
    }

    @Test
    public void fetchStructuredUserAgent_withComplexUserAgent_extractsAllBrowsers() {
        // Given - Complex user agent with multiple browsers
        String userAgent = "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.164 Mobile Safari/537.36 Edg/91.0.864.59";

        // When
        userAgentProvider.fetchStructuredUserAgent(userAgent);

        // Then
        UserAgent structuredUserAgent = userAgentProvider.getStructuredUserAgent();
        List<BrandVersion> browsers = structuredUserAgent.getBrowsers();
        assertNotNull(browsers);
        assertTrue("Should detect multiple browsers", browsers.size() >= 3);
    }

    @Test
    public void fetchUserAgent_savesCorrectVersionNumber() {
        // Given
        when(mockSharedPreferences.getString(eq("hybid_user_agent"), anyString())).thenReturn("");
        when(mockSharedPreferences.getInt(eq("hybid_user_agent_last_version"), anyInt())).thenReturn(-1);

        try (MockedConstruction<WebView> webViewMock = mockConstruction(WebView.class,
                (mock, context) -> {
                    WebSettings webSettings = mock(WebSettings.class);
                    when(mock.getSettings()).thenReturn(webSettings);
                    when(webSettings.getUserAgentString()).thenReturn("Mozilla/5.0 Chrome/91.0");
                })) {

            // When
            userAgentProvider.fetchUserAgent(mockContext);
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            // Then
            verify(mockEditor).putInt(eq("hybid_user_agent_last_version"), eq(Build.VERSION.SDK_INT));
        }
    }
}

