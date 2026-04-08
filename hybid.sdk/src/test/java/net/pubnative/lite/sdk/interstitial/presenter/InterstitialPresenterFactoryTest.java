// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial.presenter;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.SkipOffset;
import net.pubnative.lite.sdk.testing.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by erosgarciaponte on 24.01.18.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29)
public class InterstitialPresenterFactoryTest {
    @Mock
    private InterstitialPresenter.Listener mMockListener;
    private Ad mTestAd;

    private InterstitialPresenterFactory mSubject;
    private AutoCloseable mocksCloseable;

    @Before
    public void setup() {
        mocksCloseable = MockitoAnnotations.openMocks(this);
        Context context = ApplicationProvider.getApplicationContext();
        mSubject = new InterstitialPresenterFactory(context, "test_zone_id");
    }

    @org.junit.After
    public void tearDown() throws Exception {
        if (mocksCloseable != null) {
            mocksCloseable.close();
        }
    }

    @Test
    public void createInterstitialPresenter_withHTML() {
        mTestAd = TestUtil.createTestInterstitialAd();

        assertThat(mSubject.createInterstitialPresenter(mTestAd, mMockListener, IntegrationType.STANDALONE)).isNotNull();
    }

    @Test
    public void createInterstitialPresenter_withVAST() {
        mTestAd = TestUtil.createTestVideoInterstitialAd();
        assertThat(mSubject.createInterstitialPresenter(mTestAd, mMockListener, IntegrationType.STANDALONE)).isNotNull();
    }

    @Test
    public void fromCreativeType_withHTML() {
        assertThat(mSubject.fromCreativeType(ApiAssetGroupType.MRAID_320x480, mTestAd, new SkipOffset(0, true), new SkipOffset(0, true), IntegrationType.STANDALONE, null))
                .isInstanceOf(MraidInterstitialPresenter.class);
    }

    @Test
    public void fromCreativeType_withVAST() {
        assertThat(mSubject.fromCreativeType(ApiAssetGroupType.VAST_INTERSTITIAL, mTestAd, new SkipOffset(0, true), new SkipOffset(0, true), IntegrationType.STANDALONE, null))
                .isInstanceOf(VastInterstitialPresenter.class);
    }

    @Test
    public void createInterstitialPresenter_withWatermarkData_createsPresenterWithWatermark() {
        mTestAd = TestUtil.createTestInterstitialAd();
        String watermarkData = "watermark_data_string";
        InterstitialPresenter presenter = mSubject.createInterstitialPresenter(mTestAd, new SkipOffset(0, true), new SkipOffset(0, true), mMockListener, IntegrationType.STANDALONE, watermarkData);
        assertThat(presenter).isNotNull();
    }

    @Test
    public void fromCreativeType_withWatermark_createsPresenterWithWatermark() {
        String watermarkData = "watermark_data_string";
        InterstitialPresenter presenter = mSubject.fromCreativeType(ApiAssetGroupType.MRAID_320x480, mTestAd, new SkipOffset(0, true), new SkipOffset(0, true), IntegrationType.STANDALONE, watermarkData);
        assertThat(presenter).isInstanceOf(MraidInterstitialPresenter.class);
    }
}
