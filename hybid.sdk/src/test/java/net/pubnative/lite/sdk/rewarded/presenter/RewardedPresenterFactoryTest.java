// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.presenter;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.testing.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class RewardedPresenterFactoryTest {

    private RewardedPresenterFactory mSubject;
    private Context context;

    @Mock
    private RewardedPresenter.Listener mockListener;

    private AutoCloseable mocksCloseable;

    @Before
    public void setUp() {
        mocksCloseable = MockitoAnnotations.openMocks(this);
        context = ApplicationProvider.getApplicationContext();
        mSubject = new RewardedPresenterFactory(context, "test_zone_id");
    }

    @org.junit.After
    public void tearDown() throws Exception {
        if (mocksCloseable != null) {
            mocksCloseable.close();
        }
    }

    @Test
    public void createRewardedPresenter_withWatermarkData_createsPresenterWithWatermark() {
        Ad ad = TestUtil.createTestVideoInterstitialAd();
        String watermarkData = "watermark_data_string";
        RewardedPresenter presenter = mSubject.createRewardedPresenter(ad, mockListener, IntegrationType.STANDALONE, watermarkData);
        assertNotNull("Should create presenter with watermark", presenter);
    }

    @Test
    public void fromCreativeType_withWatermark_createsPresenterWithWatermark() {
        Ad ad = TestUtil.createTestVideoInterstitialAd();
        String watermarkData = "watermark_data_string";
        RewardedPresenter presenter = mSubject.fromCreativeType(ApiAssetGroupType.VAST_INTERSTITIAL, ad, IntegrationType.STANDALONE, watermarkData);
        assertNotNull("Should create INTERSTITIAL presenter with watermark", presenter);
    }

    @Test
    public void fromCreativeType_withMraidAssetType_createsMraidPresenterWithWatermark() {
        Ad ad = TestUtil.createTestInterstitialAd();
        String watermarkData = "watermark_data_string";
        RewardedPresenter presenter = mSubject.fromCreativeType(ApiAssetGroupType.MRAID_320x480, ad, IntegrationType.STANDALONE, watermarkData);
        assertNotNull("Should create MRAID presenter with watermark", presenter);
    }

    @Test
    public void fromCreativeType_withUnsupportedAssetType_returnsNull() {
        Ad ad = TestUtil.createTestVideoInterstitialAd();
        String watermarkData = "watermark_data_string";
        RewardedPresenter presenter = mSubject.fromCreativeType(999, ad, IntegrationType.STANDALONE, watermarkData);
        assertNull("Should return null for unsupported asset type", presenter);
    }
}
