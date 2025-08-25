// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.testing;

import net.pubnative.lite.sdk.db.SessionImpression;
import net.pubnative.lite.sdk.models.APIMeta;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdData;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.AdResponse;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.PNAdRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestUtil {

    public static Ad createMraidPlayableAd(Boolean isPlayable) {
        Ad ad = new Ad();
        ad.assetgroupid = ApiAssetGroupType.MRAID_320x480;
        ad.assets = createMockAssets();
        try {
            ad.meta = createPlayableMockMeta(isPlayable);
        } catch (JSONException e) {
            ad.meta = null;
        }
        ad.beacons = createMockBeacons();
        return ad;
    }

    public static Ad createTestInterstitialAd() {
        return createTestAd(ApiAssetGroupType.MRAID_320x480);
    }

    public static Ad createTestVideoInterstitialAd() {
        return createTestAd(ApiAssetGroupType.VAST_INTERSTITIAL);
    }

    public static AdRequest createTestAdRequest() {
        PNAdRequest request = new PNAdRequest();
        request.appToken = "dde3c298b47648459f8ada4a982fa92d";
        request.os = "android";
        request.osver = "8.1.0";
        request.dnt = "0";
        request.mf = "points,revenuemodel,contentinfo";
        request.al = "s";
        request.gid = "d98374d3-3b69-4a4b-a2c1-9dcb4c588849";
        request.zoneId = "2";
        request.bundleid = "net.pubnative.lite.demo";
        request.testMode = "0";
        request.locale = "en";
        request.gidmd5 = "e74483c4b5e6dc78e088d9fb0243ae66";
        request.gidsha1 = "96e380195959b8e7e05d6c6029154dc99e7fe954";
        return request;
    }

    public static AdResponse createTestAdResponse() {
        final AdResponse response = new AdResponse();

        response.ads = new ArrayList<>(1);
        response.ads.add(createTestBannerAd());
        response.status = "ok";
        return response;
    }

    public static Ad createTestBannerAd() {
        return createTestAd(ApiAssetGroupType.MRAID_320x50);
    }

    public static Ad createTestAdForAtomAdSession() {
        return createTestAdForAtomAdSession(ApiAssetGroupType.MRAID_320x50);
    }

    public static Ad createTestMRectAd() {
        return createTestAd(ApiAssetGroupType.MRAID_300x250);
    }

    public static Ad createTestVideoMRectAd() {
        return createTestAd(ApiAssetGroupType.VAST_MRECT);
    }

    public static Ad createTestLeaderboardAd() {
        return createTestAd(ApiAssetGroupType.MRAID_728x90);
    }

    public static Ad createTestAd(int assetGroupId) {
        Ad ad = new Ad();
        ad.assetgroupid = assetGroupId;
        ad.assets = createMockAssets();
        try {
            ad.meta = createMockMeta();
        } catch (JSONException e) {
            ad.meta = null;
        }
        ad.beacons = createMockBeacons();
        return ad;
    }

    public static Ad createHeaderBiddingTestAd(int assetGroupId,Integer eCPM) {
        Ad ad = new Ad();
        ad.assetgroupid = assetGroupId;
        ad.assets = createMockAssets();
        try {
            ad.meta = createMockHeaderBiddingMeta(eCPM);
        } catch (JSONException e) {
            ad.meta = null;
        }
        ad.beacons = createMockBeacons();
        return ad;
    }

    public static Ad createTestAdForAtomAdSession(int assetGroupId) {
        Ad ad = new Ad();
        ad.assetgroupid = assetGroupId;
        ad.assets = createMockAssets();
        ad.meta = createMockMetaForAtomAdSession();
        ad.beacons = createMockBeacons();
        return ad;
    }

    private static List<AdData> createMockAssets() {
        List<AdData> assets = new ArrayList<>(1);

        AdData bannerAsset = new AdData();
        bannerAsset.type = "htmlbanner";
        bannerAsset.data = new HashMap<>(3);
        bannerAsset.data.put("w", 320);
        bannerAsset.data.put("h", 50);
        bannerAsset.data.put("html", "<a href=\"https://ads.com/click/112770_1386565997\"><img src=\"https://cdn.pubnative.net/widget/v3/assets/320x50.jpg\" width=\"320\" height=\"50\" border=\"0\" alt=\"Advertisement\" /></a>");

        assets.add(bannerAsset);

        return assets;
    }

    private static List<AdData> createPlayableMockMeta(Boolean isPlayable) throws JSONException {
        List<AdData> meta = new ArrayList<>(3);
        AdData playableMeta = new AdData();
        playableMeta.type = "playable_ux";
        playableMeta.data = new HashMap<>(1);
        playableMeta.data.put("boolean", isPlayable);
        meta.add(playableMeta);
        return meta;
    }

    private static List<AdData> createMockMeta() throws JSONException {
        List<AdData> meta = new ArrayList<>(3);

        AdData pointsMeta = new AdData();
        createPointsMetaData(pointsMeta);
        pointsMeta.data = new HashMap<>(1);
        pointsMeta.data.put("number", 9);

        AdData revenueModelMeta = new AdData();
        revenueModelMeta.type = "revenuemodel";
        revenueModelMeta.data = new HashMap<>(1);
        revenueModelMeta.data.put("text", "cpm");

        AdData contentInfoMeta = new AdData();
        contentInfoMeta.type = "contentinfo";
        contentInfoMeta.data = new HashMap<>(3);
        contentInfoMeta.data.put("link", "https://pubnative.net/content-info");
        contentInfoMeta.data.put("icon", "https://cdn.pubnative.net/static/adserver/contentinfo.png");
        contentInfoMeta.data.put("text", "Learn about this ad");

        AdData remoteConfigAdData = new AdData();
        remoteConfigAdData.type = "remoteconfigs";
        remoteConfigAdData.data = new HashMap<>(1);

        JSONObject remoteConfigJsonData = new JSONObject();
        remoteConfigJsonData.put("audiostate", "on");
        remoteConfigJsonData.put("close_inter_after_finished", false);
        remoteConfigJsonData.put("creative_autostorekit", true);
        remoteConfigJsonData.put("endcard_close_delay", 2);
        remoteConfigJsonData.put("endcardenabled", true);
        remoteConfigJsonData.put("fullscreen_clickability", true);
        remoteConfigJsonData.put("html_skip_offset", 5);
        remoteConfigJsonData.put("rewarded_html_skip_offset", 30);
        remoteConfigJsonData.put("rewarded_video_skip_offset", 30);
        remoteConfigJsonData.put("video_skip_offset", 5);
        remoteConfigJsonData.put("playable_skip_offset", 4);
        remoteConfigJsonData.put("bc_learn_more_size", "medium");
        remoteConfigJsonData.put("bc_learn_more_location", "bottom_up");

        remoteConfigAdData.data.put("jsondata", remoteConfigJsonData);

        meta.add(pointsMeta);
        meta.add(revenueModelMeta);
        meta.add(contentInfoMeta);
        meta.add(remoteConfigAdData);

        return meta;
    }

    private static void createPointsMetaData(AdData pointsMeta) {
        pointsMeta.type = "points";
    }

    public static List<AdData> createMockHeaderBiddingMeta(Integer eCPM) throws JSONException {
        List<AdData> meta = new ArrayList<>(1);

        AdData pointsMeta = new AdData();
        createPointsMetaData(pointsMeta);
        pointsMeta.data = new HashMap<>(1);
        pointsMeta.data.put("number", eCPM);

        meta.add(pointsMeta);

        return meta;
    }

    private static List<AdData> createMockMetaForAtomAdSession() {
        List<AdData> meta = new ArrayList<>(4);

        AdData pointsMeta = new AdData();
        createPointsMetaData(pointsMeta);
        pointsMeta.data = new HashMap<>(1);
        pointsMeta.data.put("number", 9);

        AdData creativeIdMeta = new AdData();
        creativeIdMeta.type = APIMeta.CREATIVE_ID;
        creativeIdMeta.data = new HashMap<>(1);
        creativeIdMeta.data.put("text", "creative_test_123");

        AdData campaignIdMeta = new AdData();
        campaignIdMeta.type = APIMeta.CAMPAIGN_ID;
        campaignIdMeta.data = new HashMap<>(1);
        campaignIdMeta.data.put("text", "campaign_test_123");

        AdData revenueModelMeta = new AdData();
        revenueModelMeta.type = "revenuemodel";
        revenueModelMeta.data = new HashMap<>(1);
        revenueModelMeta.data.put("text", "cpm");

        meta.add(pointsMeta);
        meta.add(creativeIdMeta);
        meta.add(campaignIdMeta);
        meta.add(revenueModelMeta);
        return meta;
    }

    private static List<AdData> createMockBeacons() {
        List<AdData> beacons = new ArrayList<>(3);

        beacons.addAll(createMockImpressionBeacons());
        beacons.addAll(createMockClickBeacons());

        return beacons;
    }

    public static List<AdData> createMockImpressionBeacons() {
        List<AdData> impressionBeacons = new ArrayList<>(1);

        AdData impressionBeacon = new AdData();
        impressionBeacon.type = "impression";
        impressionBeacon.data = new HashMap<>(1);
        impressionBeacon.data.put("url", "https://mock-dsp.pubnative.net/tracker/nurl?app_id=1036637&p=0.01");

        impressionBeacons.add(impressionBeacon);

        return impressionBeacons;
    }

    public static List<AdData> createMockClickBeacons() {
        List<AdData> clickBeacons = new ArrayList<>(1);

        AdData clickBeacon = new AdData();
        clickBeacon.type = "click";
        clickBeacon.data = new HashMap<>(1);
        clickBeacon.data.put("url", "https://got.pubnative.net/click/rtb?aid=1036637");

        clickBeacons.add(clickBeacon);

        return clickBeacons;
    }

    public static List<AdData> createMockLoadEventBeacons() {
        List<AdData> clickBeacons = new ArrayList<>(1);

        AdData clickBeacon = new AdData();
        clickBeacon.type = "load_event";
        clickBeacon.data = new HashMap<>(1);
        clickBeacon.data.put("url", "https://got.pubnative.net/loadevent/rtb?eventtype=[EVENT_TYPE]&error=[ERRORCODE]");

        clickBeacons.add(clickBeacon);

        return clickBeacons;
    }

    public static List<AdData> createMockCompanionAdEventsBeacons() {
        List<AdData> clickBeacons = new ArrayList<>(1);

        AdData clickBeacon = new AdData();
        clickBeacon.type = "companion_ad_event";
        clickBeacon.data = new HashMap<>(1);
        clickBeacon.data.put("url", "https://got.pubnative.net/companionadevent/rtb?eventtype=[EVENTTYPE]&error=[ERRORCODE]");

        clickBeacons.add(clickBeacon);

        return clickBeacons;
    }

    public static List<AdData> createMockCustomEndcardBeacons() {
        List<AdData> clickBeacons = new ArrayList<>(1);

        AdData clickBeacon = new AdData();
        clickBeacon.type = "custom_endcard_event";
        clickBeacon.data = new HashMap<>(1);
        clickBeacon.data.put("url", "https://got.pubnative.net/customendcardevent/rtb?eventtype=[EVENTTYPE]&error=[ERRORCODE]");

        clickBeacons.add(clickBeacon);

        return clickBeacons;
    }

    public static SessionImpression createTestSessionImpression() {
        SessionImpression sessionImpression = new SessionImpression();
        sessionImpression.setZoneId("4");
        sessionImpression.setSessionDuration(System.currentTimeMillis() - 100000);
        sessionImpression.setAgeOfApp(21323243L);

        return sessionImpression;
    }
}
