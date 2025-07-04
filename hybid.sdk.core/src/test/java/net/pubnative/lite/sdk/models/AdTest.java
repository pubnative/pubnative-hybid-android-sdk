// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.testing.TestUtil;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class AdTest {
    private Ad mSubject;

    @Before
    public void setup() {
        mSubject = TestUtil.createTestBannerAd();
    }

    @Test
    public void validateAd() throws Exception {
        JSONObject json = mSubject.toJson();
        Ad parsedAd = new Ad(json);

        Assert.assertEquals(ApiAssetGroupType.MRAID_320x50, parsedAd.assetgroupid);
        Assert.assertEquals(new Integer(9), parsedAd.getECPM());

        Assert.assertEquals("<a href=\"https://ads.com/click/112770_1386565997\"><img src=\"https://cdn.pubnative.net/widget/v3/assets/320x50.jpg\" width=\"320\" height=\"50\" border=\"0\" alt=\"Advertisement\" /></a>", parsedAd.getAssetHtml(APIAsset.HTML_BANNER));
        Assert.assertEquals("https://pubnative.net/content-info", parsedAd.getContentInfoClickUrl());
        Assert.assertEquals("https://cdn.pubnative.net/static/adserver/contentinfo.png", parsedAd.getContentInfoIconUrl());

        List<AdData> clickBeacons = parsedAd.getBeacons(Ad.Beacon.CLICK);

        Assert.assertEquals("https://got.pubnative.net/click/rtb?aid=1036637", clickBeacons.get(0).getURL());

        List<AdData> impressionBeacons = parsedAd.getBeacons(Ad.Beacon.IMPRESSION);

        Assert.assertEquals("https://mock-dsp.pubnative.net/tracker/nurl?app_id=1036637&p=0.01", impressionBeacons.get(0).getURL());
    }

    @Test
    public void validateAdPlayableSkipOffsetRemoteConfigResponse() throws Exception {
        mSubject = TestUtil.createTestInterstitialAd();
        Ad parsedAd = new Ad(mSubject.toJson());
        Assert.assertNotNull(parsedAd.getMeta("remoteconfigs"));
        Assert.assertEquals(4, parsedAd.getPlayableSkipOffset().intValue());
    }

    @Test
    public void validateAdIsPlayable() throws Exception {
        mSubject = TestUtil.createMraidPlayableAd(true);
        Ad parsedAd = new Ad(mSubject.toJson());
        Assert.assertNotNull(parsedAd.getMeta("playable_ux"));
        Assert.assertTrue(parsedAd.isAdPlayable());
    }

    @Test
    public void validateAdIsNotPlayable() throws Exception {
        mSubject = TestUtil.createMraidPlayableAd(false);
        Ad parsedAd = new Ad(mSubject.toJson());
        Assert.assertNotNull(parsedAd.getMeta("playable_ux"));
        Assert.assertFalse(parsedAd.isAdPlayable());
    }
}
