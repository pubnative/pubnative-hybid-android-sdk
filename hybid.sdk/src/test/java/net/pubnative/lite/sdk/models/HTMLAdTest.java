package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.content.Context;
import net.pubnative.lite.sdk.mraid.model.HTMLAd;
import net.pubnative.lite.sdk.mraid.model.LandingPageHandler;
import net.pubnative.lite.sdk.utils.AdCustomCTAManager;
import net.pubnative.lite.sdk.utils.AdEndCardManager;
import net.pubnative.lite.sdk.utils.ClickThroughTimerManager;
import net.pubnative.lite.sdk.utils.SkipOffsetManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
public class HTMLAdTest {

    @Mock
    private Context mockContext;
    @Mock
    private Ad mockAd;
    @Mock
    private EndCardData mockEndCardData;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = openMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    // --- Constructor Tests ---

    @Test
    public void constructor_withNullAd_initializesDefaults() {
        HTMLAd htmlAd = new HTMLAd(mockContext, null, HTMLAd.AdType.INTERSTITIAL);
        assertNull(htmlAd.getLink());
        assertNull(htmlAd.getSkipDelay());
        assertNull(htmlAd.getEndCardData());
        assertEquals(0, htmlAd.getClickThroughTimer());
    }

    @Test
    public void constructor_withInterstitialType_usesHTMLSkipOffset() {
        try (MockedStatic<SkipOffsetManager> mockedManager = mockStatic(SkipOffsetManager.class)) {
            mockedManager.when(() -> SkipOffsetManager.getHTMLSkipOffset(any(), eq(true))).thenReturn(5);
            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);
            assertEquals(Integer.valueOf(5000), htmlAd.getSkipDelay());
        }
    }

    @Test
    public void constructor_withRewardedType_usesMraidRewardedSkipOffset() {
        try (MockedStatic<SkipOffsetManager> mockedManager = mockStatic(SkipOffsetManager.class)) {
            mockedManager.when(() -> SkipOffsetManager.getHTMLSkipOffset(any(), eq(false))).thenReturn(10);
            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.REWARDED);
            assertEquals(Integer.valueOf(10000), htmlAd.getSkipDelay());
        }
    }

    @Test
    public void constructor_whenEndCardIsDisabled_doesNotSetEndCardData() {
        // This test covers the path where AdEndCardManager.shouldShowCustomEndcard(ad) is false.
        try (MockedStatic<AdEndCardManager> mockedManager = mockStatic(AdEndCardManager.class)) {
            when(mockAd.getCustomEndCard()).thenReturn(mockEndCardData);
            mockedManager.when(() -> AdEndCardManager.shouldShowCustomEndcard(mockAd)).thenReturn(false);

            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);

            assertNull(htmlAd.getEndCardData());
            assertNull(htmlAd.getEndCardCloseDelay());
        }
    }

    // --- Getter/Setter Tests ---

    @Test
    public void setLink_updatesTheLink() {
        HTMLAd htmlAd = new HTMLAd(mockContext, null, HTMLAd.AdType.INTERSTITIAL);
        assertNull(htmlAd.getLink());
        String newLink = "https://new.link";
        htmlAd.setLink(newLink);
        assertEquals(newLink, htmlAd.getLink());
    }

    @Test
    public void setAndGetClickThroughTimerListener_worksCorrectly() {
        ClickThroughTimerManager.ClickThroughTimerListener mockListener = mock(ClickThroughTimerManager.ClickThroughTimerListener.class);
        HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);
        assertNull(htmlAd.getClickThroughTimerListener());
        htmlAd.setClickThroughTimerListener(mockListener);
        assertEquals(mockListener, htmlAd.getClickThroughTimerListener());
    }

    // --- hasReducedCloseSize() Tests ---

    @Test
    public void hasReducedCloseSize_whenAllConditionsMet_returnsTrue() {
        when(mockAd.getAdExperience()).thenReturn(AdExperience.PERFORMANCE);
        when(mockAd.isIconSizeReduced()).thenReturn(true);
        HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);
        assertTrue(htmlAd.hasReducedCloseSize());
    }

    @Test
    public void hasReducedCloseSize_whenAdIsNull_returnsFalse() {
        HTMLAd htmlAd = new HTMLAd(mockContext, null, HTMLAd.AdType.INTERSTITIAL);
        assertFalse(htmlAd.hasReducedCloseSize());
    }

    @Test
    public void hasReducedCloseSize_whenExperienceIsNotPerformance_returnsFalse() {
        when(mockAd.getAdExperience()).thenReturn(AdExperience.BRAND);
        when(mockAd.isIconSizeReduced()).thenReturn(true);
        HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);
        assertFalse(htmlAd.hasReducedCloseSize());
    }

    @Test
    public void hasReducedCloseSize_whenIconSizeIsNull_returnsFalse() {
        when(mockAd.getAdExperience()).thenReturn(AdExperience.PERFORMANCE);
        when(mockAd.isIconSizeReduced()).thenReturn(null);
        HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);
        assertFalse(htmlAd.hasReducedCloseSize());
    }

    @Test
    public void hasReducedCloseSize_whenIconSizeIsFalse_returnsFalse() {
        when(mockAd.getAdExperience()).thenReturn(AdExperience.PERFORMANCE);
        when(mockAd.isIconSizeReduced()).thenReturn(false);
        HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);
        assertFalse(htmlAd.hasReducedCloseSize());
    }

    @Test(expected = NullPointerException.class)
    public void hasReducedCloseSize_whenAdExperienceIsNull_throwsNullPointerException() {
        when(mockAd.getAdExperience()).thenReturn(null);
        when(mockAd.isIconSizeReduced()).thenReturn(true);
        HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);
        htmlAd.hasReducedCloseSize();
    }

    // --- shouldInitEndCardView() Tests ---

    @Test
    public void shouldInitEndCardView_whenAllConditionsMet_returnsTrue() {
        try (MockedStatic<AdEndCardManager> mockedManager = mockStatic(AdEndCardManager.class)) {
            mockedManager.when(() -> AdEndCardManager.shouldShowCustomEndcard(mockAd)).thenReturn(true);
            when(mockAd.getLink()).thenReturn("some link");
            when(mockAd.getCustomEndCard()).thenReturn(mockEndCardData);
            when(mockEndCardData.getContent()).thenReturn("some_content");
            when(mockAd.isPerformanceAd()).thenReturn(true);

            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);

            assertTrue(htmlAd.shouldInitEndCardView());
        }
    }

    @Test
    public void shouldInitEndCardView_whenContentIsEmpty_returnsFalse() {
        try (MockedStatic<AdEndCardManager> mockedManager = mockStatic(AdEndCardManager.class)) {
            mockedManager.when(() -> AdEndCardManager.shouldShowCustomEndcard(mockAd)).thenReturn(true);
            when(mockAd.getCustomEndCard()).thenReturn(mockEndCardData);
            when(mockEndCardData.getContent()).thenReturn(""); // Empty content
            when(mockAd.isPerformanceAd()).thenReturn(true);

            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);
            assertFalse(htmlAd.shouldInitEndCardView());
        }
    }

    @Test
    public void shouldInitEndCardView_whenAdIsNotPerformance_returnsFalse() {
        try (MockedStatic<AdEndCardManager> mockedManager = mockStatic(AdEndCardManager.class)) {
            mockedManager.when(() -> AdEndCardManager.shouldShowCustomEndcard(mockAd)).thenReturn(true);
            when(mockAd.getCustomEndCard()).thenReturn(mockEndCardData);
            when(mockEndCardData.getContent()).thenReturn("some_content");
            when(mockAd.isPerformanceAd()).thenReturn(false); // Condition to test

            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);
            assertFalse(htmlAd.shouldInitEndCardView());
        }
    }

    @Test
    public void shouldInitEndCardView_whenAdIsNulledAfterConstruction_returnsFalse() throws Exception {
        try (MockedStatic<AdEndCardManager> mockedManager = mockStatic(AdEndCardManager.class)) {
            mockedManager.when(() -> AdEndCardManager.shouldShowCustomEndcard(mockAd)).thenReturn(true);
            when(mockAd.getLink()).thenReturn("https://example.com");
            when(mockAd.getCustomEndCard()).thenReturn(mockEndCardData);
            when(mockEndCardData.getContent()).thenReturn("some_content");
            when(mockAd.isPerformanceAd()).thenReturn(true);

            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);

            // Use reflection to set the internal 'ad' field to null after construction
            Field adField = HTMLAd.class.getDeclaredField("ad");
            adField.setAccessible(true);
            adField.set(htmlAd, null); // Condition fails here

            assertFalse(htmlAd.shouldInitEndCardView());
        }
    }

    @Test
    public void shouldInitEndCardView_whenEndCardDataIsNull_returnsFalse() {
        try (MockedStatic<AdEndCardManager> mockedManager = mockStatic(AdEndCardManager.class)) {
            mockedManager.when(() -> AdEndCardManager.shouldShowCustomEndcard(mockAd)).thenReturn(true);
            when(mockAd.getLink()).thenReturn("https://example.com");
            when(mockAd.getCustomEndCard()).thenReturn(null); // Condition to test
            when(mockAd.isPerformanceAd()).thenReturn(true);

            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);

            assertFalse(htmlAd.shouldInitEndCardView());
        }
    }

    @Test
    public void shouldInitEndCardView_whenEndCardContentIsEmpty_returnsFalse() {
        try (MockedStatic<AdEndCardManager> mockedManager = mockStatic(AdEndCardManager.class)) {
            mockedManager.when(() -> AdEndCardManager.shouldShowCustomEndcard(mockAd)).thenReturn(true);
            when(mockAd.getLink()).thenReturn("https://example.com");
            when(mockAd.getCustomEndCard()).thenReturn(mockEndCardData);
            when(mockEndCardData.getContent()).thenReturn(""); // Condition to test
            when(mockAd.isPerformanceAd()).thenReturn(true);

            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);

            assertFalse(htmlAd.shouldInitEndCardView());
        }
    }

    @Test
    public void shouldInitEndCardView_whenShouldNotShowCustomEndCard_returnsFalse() {
        try (MockedStatic<AdEndCardManager> mockedManager = mockStatic(AdEndCardManager.class)) {
            mockedManager.when(() -> AdEndCardManager.shouldShowCustomEndcard(mockAd)).thenReturn(false); // Condition to test
            when(mockAd.getLink()).thenReturn("https://example.com");
            when(mockAd.getCustomEndCard()).thenReturn(mockEndCardData);
            when(mockEndCardData.getContent()).thenReturn("some_content");
            when(mockAd.isPerformanceAd()).thenReturn(true);

            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);

            assertFalse(htmlAd.shouldInitEndCardView());
        }
    }

    @Test
    public void shouldInitEndCardView_whenAdIsNull_returnsFalse() {
        // This is tested by passing null to the constructor.
        HTMLAd htmlAd = new HTMLAd(mockContext, null, HTMLAd.AdType.INTERSTITIAL);
        assertFalse(htmlAd.shouldInitEndCardView());
    }

    @Test
    public void shouldInitEndCardView_whenEndCardContentIsNull_returnsFalse() {
        try (MockedStatic<AdEndCardManager> mockedManager = mockStatic(AdEndCardManager.class)) {
            mockedManager.when(() -> AdEndCardManager.shouldShowCustomEndcard(mockAd)).thenReturn(true);
            when(mockAd.getLink()).thenReturn("https://example.com");
            when(mockAd.getCustomEndCard()).thenReturn(mockEndCardData);
            when(mockEndCardData.getContent()).thenReturn(null); // Condition to test
            when(mockAd.isPerformanceAd()).thenReturn(true);

            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);
            assertFalse(htmlAd.shouldInitEndCardView());
        }
    }

    // --- hasLandingPage() Tests ---

    @Test
    public void hasLandingPage_whenAllConditionsMet_returnsTrue() {
        try (MockedConstruction<LandingPageHandler> mockedHandler = mockConstruction(LandingPageHandler.class,
                (mock, context) -> {
                    when(mock.isLandingPageEnabled()).thenReturn(true);
                    when(mock.getCustomisationString()).thenReturn("some_string");
                })) {
            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);
            assertTrue(htmlAd.hasLandingPage());
        }
    }

    @Test
    public void hasLandingPage_whenHandlerIsNull_returnsFalse() {
        HTMLAd htmlAd = new HTMLAd(mockContext, null, HTMLAd.AdType.INTERSTITIAL);
        assertFalse(htmlAd.hasLandingPage());
    }

    @Test
    public void hasLandingPage_whenNotEnabled_returnsFalse() {
        try (MockedConstruction<LandingPageHandler> mockedHandler = mockConstruction(LandingPageHandler.class,
                (mock, context) -> when(mock.isLandingPageEnabled()).thenReturn(false))) {
            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);
            assertFalse(htmlAd.hasLandingPage());
        }
    }

    @Test
    public void hasLandingPage_whenCustomisationStringIsEmpty_returnsFalse() {
        // This test covers the third failure point: !TextUtils.isEmpty(...)
        try (MockedConstruction<LandingPageHandler> mockedHandler = mockConstruction(LandingPageHandler.class,
                (mock, context) -> {
                    when(mock.isLandingPageEnabled()).thenReturn(true);
                    // The customisation string is empty.
                    when(mock.getCustomisationString()).thenReturn("");
                })) {
            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);
            assertFalse(htmlAd.hasLandingPage());
        }
    }

    @Test
    public void hasLandingPage_whenCustomisationStringIsNull_returnsFalse() {
        // This test covers the other case for the third failure point.
        try (MockedConstruction<LandingPageHandler> mockedHandler = mockConstruction(LandingPageHandler.class,
                (mock, context) -> {
                    when(mock.isLandingPageEnabled()).thenReturn(true);
                    // The customisation string is null.
                    when(mock.getCustomisationString()).thenReturn(null);
                })) {
            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);
            assertFalse(htmlAd.hasLandingPage());
        }
    }

    // --- isCustomCTAEnabled() Tests ---

    @Test
    public void isCustomCTAEnabled_whenAllConditionsMet_returnsTrue() {
        try (MockedStatic<AdCustomCTAManager> mockedManager = mockStatic(AdCustomCTAManager.class)) {
            when(mockAd.getCustomCta(any(), anyBoolean())).thenReturn(mock(CustomCTAData.class));
            mockedManager.when(() -> AdCustomCTAManager.isEnabled(mockAd)).thenReturn(true);

            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);
            assertTrue(htmlAd.isCustomCTAEnabled());
        }
    }

    @Test
    public void isCustomCTAEnabled_whenAdIsNull_returnsFalse() {
        HTMLAd htmlAd = new HTMLAd(mockContext, null, HTMLAd.AdType.INTERSTITIAL);
        assertFalse(htmlAd.isCustomCTAEnabled());
    }

    @Test
    public void isCustomCTAEnabled_whenCustomCtaDataIsNull_returnsFalse() {
        when(mockAd.getCustomCta(any(), anyBoolean())).thenReturn(null);
        HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);
        assertFalse(htmlAd.isCustomCTAEnabled());
    }

    @Test
    public void isCustomCTAEnabled_whenManagerReturnsFalse_returnsFalse() {
        // This test covers the third failure point: AdCustomCTAManager.isEnabled()
        try (MockedStatic<AdCustomCTAManager> mockedManager = mockStatic(AdCustomCTAManager.class)) {
            // The first two conditions are met...
            when(mockAd.getCustomCta(any(), anyBoolean())).thenReturn(mock(CustomCTAData.class));
            // ...but the final condition is false.
            mockedManager.when(() -> AdCustomCTAManager.isEnabled(mockAd)).thenReturn(false);

            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);

            assertFalse(htmlAd.isCustomCTAEnabled());
        }
    }

    @Test
    public void simpleGetters_returnValueFromAd() {
        // This test covers the simple getters that just retrieve data from the Ad object.
        when(mockAd.getLink()).thenReturn("https://example.com");
        when(mockAd.getClickThroughTimer()).thenReturn(5);
        when(mockAd.getCustomCta(any(Context.class), anyBoolean())).thenReturn(mock(CustomCTAData.class));

        // Use try-with-resources for all necessary static and construction mocks
        try (MockedStatic<AdCustomCTAManager> mockedCTAManager = mockStatic(AdCustomCTAManager.class);
             MockedStatic<SkipOffsetManager> mockedSkipManager = mockStatic(SkipOffsetManager.class);
             MockedConstruction<LandingPageHandler> mockedLpHandler = mockConstruction(LandingPageHandler.class)) {

            // Stub the static manager calls made by the constructor
            mockedCTAManager.when(() -> AdCustomCTAManager.getCustomCtaDelay(mockAd)).thenReturn(3);
            mockedSkipManager.when(() -> SkipOffsetManager.getNativeCloseButtonDelay(any())).thenReturn(7);

            // Create the object to be tested
            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);

            // Verify all getters return the expected values
            assertEquals("https://example.com", htmlAd.getLink());
            assertEquals(5, htmlAd.getClickThroughTimer());
            assertNotNull(htmlAd.getCustomCTAData());
            assertEquals(Integer.valueOf(3), htmlAd.getCustomCTADelay());

            assertEquals(Integer.valueOf(7000), htmlAd.getNativeButtonCloseDelay());
            assertNotNull(htmlAd.getLandingPage());
            // Verify it's the instance created in the constructor
            assertEquals(mockedLpHandler.constructed().get(0), htmlAd.getLandingPage());
        }
    }

    @Test
    public void getCloseDelay_returnsSkipDelay() {
        // This test verifies that getCloseDelay() is an alias for getSkipDelay().
        try (MockedStatic<SkipOffsetManager> mockedManager = mockStatic(SkipOffsetManager.class)) {
            mockedManager.when(() -> SkipOffsetManager.getHTMLSkipOffset(any(), anyBoolean())).thenReturn(10);

            HTMLAd htmlAd = new HTMLAd(mockContext, mockAd, HTMLAd.AdType.INTERSTITIAL);

            assertEquals(Integer.valueOf(10000), htmlAd.getSkipDelay());
            assertEquals(htmlAd.getSkipDelay(), htmlAd.getCloseDelay());
        }
    }


}