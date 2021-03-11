package net.pubnative.lite.sdk;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.testing.TestUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AdCacheTest {
    private static final String TEST_ZONE_ID_1 = "2";
    private static final String TEST_ZONE_ID_2 = "3";

    private AdCache adCache;

    @Before
    public void setUp() {
        adCache = new AdCache();
    }

    @After
    public void tearDown() {
        adCache = null;
    }

    @Test
    public void testCacheInspect() {
        Ad mRectAd = TestUtil.createTestMRectAd();
        Ad bannerAd = TestUtil.createTestBannerAd();

        adCache.put(TEST_ZONE_ID_1, mRectAd);
        adCache.put(TEST_ZONE_ID_2, bannerAd);

        Assert.assertEquals(adCache.inspect(TEST_ZONE_ID_1), mRectAd);
        Assert.assertEquals(adCache.inspect(TEST_ZONE_ID_2), bannerAd);
        Assert.assertNotEquals(adCache.inspect(TEST_ZONE_ID_2), mRectAd);
        Assert.assertNotEquals(adCache.inspect(TEST_ZONE_ID_1), bannerAd);
    }

    @Test
    public void testCacheRemove() {
        Ad mRectAd = TestUtil.createTestMRectAd();
        Ad bannerAd = TestUtil.createTestBannerAd();

        adCache.put(TEST_ZONE_ID_1, mRectAd);
        adCache.put(TEST_ZONE_ID_2, bannerAd);

        Assert.assertEquals(adCache.remove(TEST_ZONE_ID_1), mRectAd);
        Assert.assertEquals(adCache.remove(TEST_ZONE_ID_2), bannerAd);
    }

    @Test
    public void testInspectAfterRemove() {
        Ad mRectAd = TestUtil.createTestMRectAd();
        Ad bannerAd = TestUtil.createTestBannerAd();

        adCache.put(TEST_ZONE_ID_1, mRectAd);
        adCache.put(TEST_ZONE_ID_2, bannerAd);

        Assert.assertEquals(adCache.remove(TEST_ZONE_ID_1), mRectAd);
        Assert.assertEquals(adCache.remove(TEST_ZONE_ID_2), bannerAd);

        Assert.assertNull(adCache.inspect(TEST_ZONE_ID_1));
        Assert.assertNull(adCache.inspect(TEST_ZONE_ID_2));
    }
}
