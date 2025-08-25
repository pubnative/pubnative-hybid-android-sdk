// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.os.Bundle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Set;

import static org.junit.Assert.*;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.testing.TestUtil;

@RunWith(RobolectricTestRunner.class)
public class HeaderBiddingUtilsTest {

    Ad ad;

    @Test
    public void testGetBidECPM_twoDecimals() {
        ad = TestUtil.createHeaderBiddingTestAd(ApiAssetGroupType.MRAID_320x50, 2450);

        String keywords = HeaderBiddingUtils.getHeaderBiddingKeywords(ad, HeaderBiddingUtils.KeywordMode.TWO_DECIMALS);
        assertEquals("pn_bid:2.45", keywords);
    }

    @Test
    public void testGetBidECPM_threeDecimals() {
        ad = TestUtil.createHeaderBiddingTestAd(ApiAssetGroupType.MRAID_320x50, 2450);

        String keywords = HeaderBiddingUtils.getHeaderBiddingKeywords(ad, HeaderBiddingUtils.KeywordMode.THREE_DECIMALS);
        assertEquals("pn_bid:2.450", keywords);
    }

    @Test
    public void testGetHeaderBiddingKeywordsBundle() {
        ad = TestUtil.createHeaderBiddingTestAd(ApiAssetGroupType.MRAID_320x50, 3050);

        Bundle bundle = HeaderBiddingUtils.getHeaderBiddingKeywordsBundle(ad, HeaderBiddingUtils.KeywordMode.TWO_DECIMALS);
        assertEquals("3.05", bundle.getString(HeaderBiddingUtils.KEYS.PN_BID));
    }

    @Test
    public void testGetHeaderBiddingKeywordsSet() {
        ad = TestUtil.createHeaderBiddingTestAd(ApiAssetGroupType.MRAID_320x50, 1350);

        Set<String> set = HeaderBiddingUtils.getHeaderBiddingKeywordsSet(ad, HeaderBiddingUtils.KeywordMode.THREE_DECIMALS);
        assertTrue(set.contains("pn_bid:1.350"));
    }

    @Test
    public void testGetBidFromPoints_withTwoDecimals() {
        String bid = HeaderBiddingUtils.getBidFromPoints(2250, PrebidUtils.KeywordMode.TWO_DECIMALS);
        assertEquals("2.25", bid);
    }

    @Test
    public void testGetBidFromPoints_withThreeDecimals() {
        String bid = HeaderBiddingUtils.getBidFromPoints(2250, PrebidUtils.KeywordMode.THREE_DECIMALS);
        assertEquals("2.250", bid);
    }
}