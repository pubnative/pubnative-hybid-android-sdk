package net.pubnative.lite.sdk.interstitial.presenter;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.testing.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by erosgarciaponte on 24.01.18.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29)
public class InterstitialPresenterFactoryTest {
    @Mock
    private InterstitialPresenter.Listener mMockListener;
    private Ad mTestAd;

    @InjectMocks
    private InterstitialPresenterFactory mSubject;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void createInterstitialPresenter_withHTML() {
        mTestAd = TestUtil.createTestInterstitialAd();

        assertThat(mSubject.createInterstitialPresenter(mTestAd, mMockListener)).isNotNull();
    }

    @Test
    public void createInterstitialPresenter_withVAST() {
        mTestAd = TestUtil.createTestVideoInterstitialAd();
        assertThat(mSubject.createInterstitialPresenter(mTestAd, mMockListener)).isNotNull();
    }

    @Test
    public void fromCreativeType_withHTML() {
        assertThat(mSubject.fromCreativeType(ApiAssetGroupType.MRAID_320x480, mTestAd, 0, 0))
                .isInstanceOf(MraidInterstitialPresenter.class);
    }

    @Test
    public void fromCreativeType_withVAST() {
        assertThat(mSubject.fromCreativeType(ApiAssetGroupType.VAST_INTERSTITIAL, mTestAd, 0, 0))
                .isInstanceOf(VastInterstitialPresenter.class);
    }
}
