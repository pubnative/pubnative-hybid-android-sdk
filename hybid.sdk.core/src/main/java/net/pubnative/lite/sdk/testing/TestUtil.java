package net.pubnative.lite.sdk.testing;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdData;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.AdResponse;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by erosgarciaponte on 24.01.18.
 */

public class TestUtil {

    public static Ad createTestInterstitialAd() {
        return createTestAd(ApiAssetGroupType.MRAID_320x480);
    }

    public static Ad createTestVideoInterstitialAd() {
        return createTestAd(ApiAssetGroupType.VAST_INTERSTITIAL);
    }

    public static AdRequest createTestAdRequest() {
        AdRequest request = new AdRequest();
        request.apptoken = "dde3c298b47648459f8ada4a982fa92d";
        request.os = "android";
        request.osver = "8.1.0";
        request.dnt = "0";
        request.mf = "points,revenuemodel,contentinfo";
        request.al = "s";
        request.gid = "d98374d3-3b69-4a4b-a2c1-9dcb4c588849";
        request.zoneid = "2";
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
        ad.meta = createMockMeta();
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

    private static List<AdData> createMockMeta() {
        List<AdData> meta = new ArrayList<>(3);

        AdData pointsMeta = new AdData();
        pointsMeta.type = "points";
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

        meta.add(pointsMeta);
        meta.add(revenueModelMeta);
        meta.add(contentInfoMeta);

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
}
