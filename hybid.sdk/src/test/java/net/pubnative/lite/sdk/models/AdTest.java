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
import static org.mockito.Mockito.when;

import net.pubnative.lite.sdk.testing.TestUtil;
import net.pubnative.lite.sdk.utils.AtomManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
}