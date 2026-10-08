// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import net.pubnative.lite.sdk.testing.TestUtil;
import net.pubnative.lite.sdk.utils.AtomManager;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.TouchSizeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AdTest {
    private Ad mSubject;

    @Mock
    private AtomManager mockAtomManager;

    private static final String SURVEY_HTML_KEY = "SurveyHtml";


    private static final String TEST_SURVEY_HTML_TEMPLATE = "<!DOCTYPE html><html><body>" +
            "<h1>Survey</h1>" +
            "<div id=\"survey-data\">{survey_data_json}</div>" +
            "</body></html>";

    private static final String CUSTOM_END_CARD_WITHOUT_PLACEHOLDER = "<!DOCTYPE html><html><body>" +
            "<h1>Static End Card</h1>" +
            "<p>Thank you for watching!</p>" +
            "</body></html>";

    @Before
    public void setup() {
        mSubject = TestUtil.createTestBannerAd();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void isTopicsAPIEnabled_afterRemoval_returnsFalse() {
        assertFalse(mSubject.isTopicsAPIEnabled());
    }

    @Test
    public void validateAd() throws Exception {
        JSONObject json = mSubject.toJson();
        Ad parsedAd = new Ad(json);

        assertEquals(ApiAssetGroupType.MRAID_320x50, parsedAd.assetgroupid);
        assertEquals(Integer.valueOf(9), parsedAd.getECPM());
        assertEquals("<a href=\"https://ads.com/click/112770_1386565997\"><img src=\"https://cdn.pubnative.net/widget/v3/assets/320x50.jpg\" width=\"320\" height=\"50\" border=\"0\" alt=\"Advertisement\" /></a>", parsedAd.getAssetHtml(APIAsset.HTML_BANNER));
        assertEquals("https://pubnative.net/content-info", parsedAd.getContentInfoClickUrl());
        assertEquals("https://cdn.pubnative.net/static/adserver/contentinfo.png", parsedAd.getContentInfoIconUrl());

        List<AdData> clickBeacons = parsedAd.getBeacons(Ad.Beacon.CLICK);
        assertEquals("https://got.pubnative.net/click/rtb?aid=1036637", clickBeacons.get(0).getURL());

        List<AdData> impressionBeacons = parsedAd.getBeacons(Ad.Beacon.IMPRESSION);
        assertEquals("https://mock-dsp.pubnative.net/tracker/nurl?app_id=1036637&p=0.01", impressionBeacons.get(0).getURL());
    }

    @Test
    public void validateAdLearnMoreRemoteConfigResponse() throws Exception {
        mSubject = TestUtil.createTestInterstitialAd();
        Ad parsedAd = new Ad(mSubject.toJson());
        assertNotNull(parsedAd.getMeta("remoteconfigs"));
        assertEquals("medium", parsedAd.getBcLearnMoreSize());
        assertEquals("bottom_up", parsedAd.getBcLearnMoreLocation());
    }


    @Test
    public void constructor_withAdTypeVideo_createsVastAsset() {
        String vastValue = "<VAST>...</VAST>";
        Ad videoAd = new Ad(1, vastValue, Ad.AdType.VIDEO);

        assertEquals(1, videoAd.assetgroupid);
        assertEquals(vastValue, videoAd.getVast());
        assertNotNull(videoAd.getAsset(APIAsset.VAST));
    }

    @Test
    public void constructor_withAdTypeHtml_createsHtmlAsset() {
        String htmlValue = "<html>...</html>";
        Ad htmlAd = new Ad(2, htmlValue, Ad.AdType.HTML);

        assertEquals(2, htmlAd.assetgroupid);
        assertNotNull(htmlAd.getAsset(APIAsset.HTML_BANNER));
    }

    @Test
    public void getAsset_withNullAssets_returnsNull() {
        Ad ad = new Ad();
        ad.assets = null;

        assertNull(ad.getAsset(APIAsset.HTML_BANNER));
    }

    @Test
    public void getMeta_withNullMeta_returnsNull() {
        Ad ad = new Ad();
        ad.meta = null;

        assertNull(ad.getMeta("remoteconfigs"));
    }

    @Test
    public void getBeacons_withNullBeacons_returnsNull() {
        Ad ad = new Ad();
        ad.beacons = null;

        assertNull(ad.getBeacons(Ad.Beacon.IMPRESSION));
    }

    @Test
    public void setAndGetZoneId() {
        mSubject.setZoneId("zone_123");
        assertEquals("zone_123", mSubject.getZoneId());
    }

    @Test
    public void setAndGetLink() {
        mSubject.setLink("https://test.com");
        assertEquals("https://test.com", mSubject.getLink());
    }

    @Test
    public void setAndGetAdSourceName() {
        mSubject.setAdSourceName("TestSource");
        assertEquals("TestSource", mSubject.getAdSourceName());
    }

    @Test
    public void setAndGetHasEndCard() {
        mSubject.setHasEndCard(true);
        assertTrue(mSubject.hasEndCard());
        mSubject.setHasEndCard(false);
        assertFalse(mSubject.hasEndCard());
    }

    @Test
    public void getCustomEndCard_atomProvidesSurveyHtml_returnsAtomHtml() {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            HashMap<String, String> atomData = new HashMap<>();
            atomData.put(SURVEY_HTML_KEY, TEST_SURVEY_HTML_TEMPLATE);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            EndCardData result = ad.getCustomEndCard();

            assertNotNull(result);
            assertEquals(EndCardData.Type.HTML_RESOURCE, result.getType());
            assertEquals(TEST_SURVEY_HTML_TEMPLATE, result.getContent());
            assertTrue(ad.hasCustomEndCard());
        }
    }

    /**
     * When Atom provides SurveyHtml with null value, getCustomEndCard() should return null
     */
    @Test
    public void getCustomEndCard_atomReturnsSurveyHtmlNull_returnsNullEndCard() {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            HashMap<String, String> atomData = new HashMap<>();
            // SurveyHtml key not present -> .get() returns null
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            EndCardData result = ad.getCustomEndCard();

            assertNull(result);
        }
    }


    @Test
    public void getCustomEndCard_atomReturnsNull_fallsBackToAdserverAsset() {
        Ad ad = createAdWithCustomEndCard(CUSTOM_END_CARD_WITHOUT_PLACEHOLDER);

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(null);

            EndCardData result = ad.getCustomEndCard();

            assertNotNull(result);
            assertEquals(EndCardData.Type.HTML_RESOURCE, result.getType());
            assertEquals(CUSTOM_END_CARD_WITHOUT_PLACEHOLDER, result.getContent());
        }
    }


    @Test
    public void getCustomEndCard_atomReturnsNull_noAdserverAsset_returnsNull() {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(null);

            EndCardData result = ad.getCustomEndCard();

            assertNull(result);
        }
    }


    @Test
    public void getCustomEndCard_adserverAssetHtmlNull_atomNull_returnsNull() {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();
        AdData adData = new AdData();
        adData.type = APIAsset.CUSTOM_END_CARD;
        adData.data = new HashMap<>();
        adData.data.put("html", null);
        ad.assets.add(adData);

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(null);

            EndCardData result = ad.getCustomEndCard();

            assertNull(result);
        }
    }


    @Test
    public void hasCustomEndCard_adserverAssetExists_returnsTrue() {
        Ad ad = createAdWithCustomEndCard(CUSTOM_END_CARD_WITHOUT_PLACEHOLDER);

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(null);

            assertTrue(ad.hasCustomEndCard());
        }
    }


    @Test
    public void hasCustomEndCard_noAdserverAsset_atomHasSurveyHtml_returnsTrue() {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            HashMap<String, String> atomData = new HashMap<>();
            atomData.put(SURVEY_HTML_KEY, TEST_SURVEY_HTML_TEMPLATE);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            assertTrue(ad.hasCustomEndCard());
        }
    }


    @Test
    public void hasCustomEndCard_noAdserverAsset_atomReturnsNull_returnsFalse() {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(null);

            assertFalse(ad.hasCustomEndCard());
        }
    }


    @Test
    public void hasCustomEndCard_noAdserverAsset_atomSurveyHtmlNull_returnsFalse() {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            HashMap<String, String> atomData = new HashMap<>();
            // No SURVEY_HTML_KEY entry
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            assertFalse(ad.hasCustomEndCard());
        }
    }


    @Test
    public void hasCustomEndCard_noAdserverAsset_atomSurveyHtmlEmpty_returnsFalse() {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            HashMap<String, String> atomData = new HashMap<>();
            atomData.put(SURVEY_HTML_KEY, "");
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            assertFalse(ad.hasCustomEndCard());
        }
    }


    @Test
    public void hasCustomEndCard_noAdserverAsset_atomSurveyHtmlTooShort_returnsFalse() {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            HashMap<String, String> atomData = new HashMap<>();
            atomData.put(SURVEY_HTML_KEY, "abc");
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            assertFalse(ad.hasCustomEndCard());
        }
    }


    @Test
    public void hasCustomEndCard_noAdserverAsset_atomSurveyHtmlWhitespace_returnsFalse() {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            HashMap<String, String> atomData = new HashMap<>();
            atomData.put(SURVEY_HTML_KEY, "    ");
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            assertFalse(ad.hasCustomEndCard());
        }
    }


    @Test
    public void hasCustomEndCard_exceptionThrown_returnsFalse() {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenThrow(new RuntimeException("test"));

            assertFalse(ad.hasCustomEndCard());
        }
    }

    @Test
    public void hasCustomEndCard_bothAdserverAssetAndAtomPresent_returnsTrue() {
        Ad ad = createAdWithCustomEndCard(CUSTOM_END_CARD_WITHOUT_PLACEHOLDER);

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            HashMap<String, String> atomData = new HashMap<>();
            atomData.put(SURVEY_HTML_KEY, TEST_SURVEY_HTML_TEMPLATE);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            assertTrue(ad.hasCustomEndCard());
        }
    }


    private Ad createAdWithCustomEndCard(String html) {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();
        AdData adData = new AdData();
        adData.type = APIAsset.CUSTOM_END_CARD;
        adData.data = new HashMap<>();
        adData.data.put("html", html);
        ad.assets.add(adData);
        return ad;
    }

    //-------------------- VMA-1593: video_skip_offset percentage --------------------//

    @Test
    public void getVideoSkipOffsetPercentage_withPercentageValue_returnsIt() {
        Ad ad = createAdWithRemoteConfig("video_skip_offset", "20%");

        assertEquals("20%", ad.getVideoSkipOffsetPercentage());
    }

    @Test
    public void getVideoSkipOffset_withPercentageValue_doesNotLogDiscardWarning() {
        // "20%" is routed to getVideoSkipOffsetPercentage(), not discarded - it must not be
        // reported as such on the one config shape this ticket exists to support.
        Ad ad = createAdWithRemoteConfig("video_skip_offset", "20%");

        try (MockedStatic<Logger> mockedLogger = mockStatic(Logger.class)) {
            assertNull(ad.getVideoSkipOffset());

            mockedLogger.verify(() -> Logger.w(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()), never());
        }
    }

    @Test
    public void getRemoteConfig_withPercentageValueOnNonSkipOffsetField_stillLogsDiscardWarning() {
        // Unlike the six skip-offset fields, "endcard_close_delay" has no percentage accessor
        // routing it elsewhere, so a "%"-suffixed value here is a genuine misconfiguration.
        Ad ad = createAdWithRemoteConfig("endcard_close_delay", "20%");

        try (MockedStatic<Logger> mockedLogger = mockStatic(Logger.class)) {
            Integer result = ad.getRemoteConfig(RemoteConfig.END_CARD_CLOSE_DELAY);

            assertNull(result);
            mockedLogger.verify(() -> Logger.w(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()));
        }
    }

    @Test
    public void getVideoSkipOffsetPercentage_withIntegerValue_returnsNull() {
        Ad ad = createAdWithRemoteConfig("video_skip_offset", 5);

        assertNull(ad.getVideoSkipOffsetPercentage());
    }

    @Test
    public void getVideoSkipOffset_withUnparseableValue_logsWarningAndReturnsNull() {
        // Neither accessor accepts it, so initSkipTime's resolver falls back to the SDK default.
        Ad ad = createAdWithRemoteConfig("video_skip_offset", "abc");

        try (MockedStatic<Logger> mockedLogger = mockStatic(Logger.class)) {
            assertNull(ad.getVideoSkipOffset());

            mockedLogger.verify(() -> Logger.w(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()));
        }
        assertNull(ad.getVideoSkipOffsetPercentage());
    }

    @Test
    public void getVideoSkipOffsetPercentage_forPerformanceAd_usesPcField() {
        Ad ad = createAdWithRemoteConfigAndExperience(AdExperience.PERFORMANCE, "pc_video_skip_offset", "20%");

        assertEquals("20%", ad.getVideoSkipOffsetPercentage());
    }

    @Test
    public void getVideoSkipOffsetPercentage_forBrandAd_usesBcField() {
        Ad ad = createAdWithRemoteConfigAndExperience(AdExperience.BRAND, "bc_video_skip_offset", "20%");

        assertEquals("20%", ad.getVideoSkipOffsetPercentage());
    }

    @Test
    public void getVideoRewardedSkipOffsetPercentage_forGenericAd_returnsIt() {
        Ad ad = createAdWithRemoteConfig("rewarded_video_skip_offset", "20%");

        assertEquals("20%", ad.getVideoRewardedSkipOffsetPercentage());
    }

    @Test
    public void getVideoRewardedSkipOffsetPercentage_forPerformanceAd_usesPcField() {
        Ad ad = createAdWithRemoteConfigAndExperience(
                AdExperience.PERFORMANCE, "pc_rewarded_video_skip_offset", "20%");

        assertEquals("20%", ad.getVideoRewardedSkipOffsetPercentage());
    }

    @Test
    public void getCloseButtonTouchSize_whenAbsent_returnsNoGrowth() {
        // Nothing served -> keep today's behaviour (tap area equals the drawn button).
        assertEquals(TouchSizeManager.NO_GROWTH, new Ad().getCloseButtonTouchSize());
    }

    @Test
    public void getCloseButtonTouchSize_whenServedInRange_returnsValue() {
        Ad ad = createAdWithRemoteConfig("close_button_touch_size", 60);

        assertEquals(60, ad.getCloseButtonTouchSize());
    }

    @Test
    public void getCloseButtonTouchSize_whenAboveMax_isClampedTo100() {
        Ad ad = createAdWithRemoteConfig("close_button_touch_size", 250);

        assertEquals(100, ad.getCloseButtonTouchSize());
    }

    @Test
    public void getCloseButtonTouchSize_whenZeroOrNegative_returnsNoGrowth() {
        assertEquals(TouchSizeManager.NO_GROWTH,
                createAdWithRemoteConfig("close_button_touch_size", 0).getCloseButtonTouchSize());
        assertEquals(TouchSizeManager.NO_GROWTH,
                createAdWithRemoteConfig("close_button_touch_size", -5).getCloseButtonTouchSize());
    }

    @Test
    public void getCloseButtonInitialTouchSize_whenAbsent_returnsNoGrowth() {
        assertEquals(TouchSizeManager.NO_GROWTH, new Ad().getCloseButtonInitialTouchSize());
    }

    @Test
    public void getCloseButtonInitialTouchSize_whenZeroOrNegative_returnsNoGrowth() {
        // Not served (0 / negative) must leave the close on the normal size, i.e. today's behaviour.
        assertEquals(TouchSizeManager.NO_GROWTH,
                createAdWithRemoteConfig("close_button_initial_touch_size", 0).getCloseButtonInitialTouchSize());
        assertEquals(TouchSizeManager.NO_GROWTH,
                createAdWithRemoteConfig("close_button_initial_touch_size", -1).getCloseButtonInitialTouchSize());
    }

    @Test
    public void getCloseButtonInitialTouchSize_whenServedInRange_returnsValue() {
        assertEquals(1,
                createAdWithRemoteConfig("close_button_initial_touch_size", 1).getCloseButtonInitialTouchSize());
        assertEquals(10,
                createAdWithRemoteConfig("close_button_initial_touch_size", 10).getCloseButtonInitialTouchSize());
    }

    @Test
    public void getCloseButtonInitialTouchSize_whenAboveMax_isClampedTo100() {
        Ad ad = createAdWithRemoteConfig("close_button_initial_touch_size", 300);

        assertEquals(100, ad.getCloseButtonInitialTouchSize());
    }

    @Test
    public void getCloseButtonTouchSize_withNonNumericValue_returnsNoGrowth() {
        // Malformed server value: getRemoteConfig discards it (null) and we fall back to no growth.
        assertEquals(TouchSizeManager.NO_GROWTH,
                createAdWithRemoteConfig("close_button_touch_size", "abc").getCloseButtonTouchSize());
    }

    @Test
    public void getCloseButtonTouchSize_withNumericStringValue_isCoerced() {
        assertEquals(50,
                createAdWithRemoteConfig("close_button_touch_size", "50").getCloseButtonTouchSize());
    }

    @Test
    public void getCloseButtonTouchSize_withDoubleValue_isTruncated() {
        assertEquals(50,
                createAdWithRemoteConfig("close_button_touch_size", 50.7).getCloseButtonTouchSize());
    }

    @Test
    public void getCloseButtonInitialTouchSize_withNonNumericValue_returnsNoGrowth() {
        assertEquals(TouchSizeManager.NO_GROWTH,
                createAdWithRemoteConfig("close_button_initial_touch_size", "abc").getCloseButtonInitialTouchSize());
    }

    @Test
    public void impressionClicked_defaultsFalse_andSticksOnceSet() {
        Ad ad = new Ad();

        assertFalse(ad.isImpressionClicked());
        ad.setImpressionClicked();
        assertTrue(ad.isImpressionClicked());
    }

    private Ad createAdWithRemoteConfig(String field, Object value) {
        Ad ad = new Ad();
        ad.meta = new ArrayList<>();

        AdData remoteConfigAdData = new AdData();
        remoteConfigAdData.type = APIMeta.REMOTE_CONFIGS;
        remoteConfigAdData.data = new HashMap<>();

        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put(field, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        remoteConfigAdData.data.put("jsondata", jsonData);

        ad.meta.add(remoteConfigAdData);
        return ad;
    }

    private Ad createAdWithRemoteConfigAndExperience(String adExperience, String field, Object value) {
        Ad ad = createAdWithRemoteConfig(field, value);
        ad.assetgroupid = ApiAssetGroupType.VAST_INTERSTITIAL;

        AdData adExperienceData = new AdData();
        adExperienceData.type = APIMeta.AD_EXPERIENCE;
        adExperienceData.data = new HashMap<>();
        adExperienceData.data.put("text", adExperience);

        ad.meta.add(adExperienceData);
        return ad;
    }
}
