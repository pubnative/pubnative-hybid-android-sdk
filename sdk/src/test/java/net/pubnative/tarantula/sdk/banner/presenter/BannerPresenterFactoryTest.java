package net.pubnative.tarantula.sdk.banner.presenter;

import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.models.ApiAssetGroupType;
import net.pubnative.tarantula.sdk.utils.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by erosgarciaponte on 24.01.18.
 */
@RunWith(RobolectricTestRunner.class)
public class BannerPresenterFactoryTest {
    @Mock
    private BannerPresenter.Listener mMockListener;
    private Ad mTestAd;

    @InjectMocks
    private BannerPresenterFactory mSubject;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void createBannerPresenter_withHTML() {
        mTestAd = TestUtil.createTestBannerAd();

        assertThat(mSubject.createBannerPresenter(mTestAd, mMockListener)).isNotNull();
    }

    @Test
    public void fromCreativeType_withHTML() {
        assertThat(mSubject.fromCreativeType(ApiAssetGroupType.MRAID_BANNER_1, mTestAd))
                .isInstanceOf(MraidBannerPresenter.class);
    }
}
