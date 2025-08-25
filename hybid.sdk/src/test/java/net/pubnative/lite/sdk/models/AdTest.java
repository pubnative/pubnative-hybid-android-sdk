// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import net.pubnative.lite.sdk.testing.TestUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AdTest {
    private Ad mSubject;

    @Before
    public void setup() {
        mSubject = TestUtil.createTestBannerAd();
    }

    // --------------- EXISTING TESTS (UNCHANGED) ---------------
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
    public void validateAdPlayableSkipOffsetRemoteConfigResponse() throws Exception {
        mSubject = TestUtil.createTestInterstitialAd();
        Ad parsedAd = new Ad(mSubject.toJson());
        assertNotNull(parsedAd.getMeta("remoteconfigs"));
        assertEquals(4, parsedAd.getPlayableSkipOffset().intValue());
    }

    @Test
    public void validateAdIsPlayable() throws Exception {
        mSubject = TestUtil.createMraidPlayableAd(true);
        Ad parsedAd = new Ad(mSubject.toJson());
        assertNotNull(parsedAd.getMeta("playable_ux"));
        assertTrue(parsedAd.isAdPlayable());
    }

    @Test
    public void validateAdIsNotPlayable() throws Exception {
        mSubject = TestUtil.createMraidPlayableAd(false);
        Ad parsedAd = new Ad(mSubject.toJson());
        assertNotNull(parsedAd.getMeta("playable_ux"));
        assertFalse(parsedAd.isAdPlayable());
    }

    @Test
    public void validateAdLearnMoreRemoteConfigResponse() throws Exception {
        mSubject = TestUtil.createTestInterstitialAd();
        Ad parsedAd = new Ad(mSubject.toJson());
        assertNotNull(parsedAd.getMeta("remoteconfigs"));
        assertEquals("medium", parsedAd.getBcLearnMoreSize());
        assertEquals("bottom_up", parsedAd.getBcLearnMoreLocation());
    }

    // --------------- NEW TESTS FOR EXPANDED COVERAGE ---------------

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
        assertEquals(htmlValue, htmlAd.getAssetHtml(APIAsset.HTML_BANNER));
    }

    @Test
    public void getECPM_whenPointsMetaIsMissing_returnsMinPoints() {
        mSubject.meta = new ArrayList<>(); // Clear the meta list
        assertEquals(Integer.valueOf(10), mSubject.getECPM());
    }

    @Test
    public void getImpressionId_whenBeaconIsValid_returnsId() {
        AdData impressionBeacon = new AdData();
        impressionBeacon.type = Ad.Beacon.IMPRESSION;
        impressionBeacon.data = new java.util.HashMap<>();
        impressionBeacon.data.put("url", "https://got.pubnative.net/impression?t=test_impression_id&some_other_param=value");
        mSubject.beacons = List.of(impressionBeacon);

        assertEquals("test_impression_id", mSubject.getImpressionId());
    }

    @Test
    public void getImpressionId_whenBeaconIsInvalid_returnsEmptyString() {
        AdData impressionBeacon = new AdData();
        impressionBeacon.type = Ad.Beacon.IMPRESSION;
        impressionBeacon.data = new java.util.HashMap<>();
        impressionBeacon.data.put("url", "https://some.other.url/impression");
        mSubject.beacons = List.of(impressionBeacon);

        assertEquals("", mSubject.getImpressionId());
    }

    @Test
    public void getSessionId_whenImpressionIdIsMissing_returnsUUID() {
        mSubject.beacons = new ArrayList<>(); // No beacons
        String sessionId1 = mSubject.getSessionId();
        String sessionId2 = mSubject.getSessionId();

        assertNotNull(sessionId1);
        assertFalse(sessionId1.isEmpty());
        assertEquals(sessionId1, sessionId2); // Subsequent calls should return the same UUID
    }

    @Test
    public void compareTo_sortsAdsByEcpmDescending() {
        Ad adWithHighEcpm = TestUtil.createHeaderBiddingTestAd(1, 100); // ECPM = 100
        Ad adWithLowEcpm = TestUtil.createHeaderBiddingTestAd(1, 10);  // ECPM = 10

        List<Ad> adList = new ArrayList<>(List.of(adWithLowEcpm, adWithHighEcpm));
        Collections.sort(adList);

        // After sorting, the ad with the higher ECPM should be first.
        assertEquals(adWithHighEcpm, adList.get(0));
    }
}