package net.pubnative.lite.sdk.mrect.presenter;

import net.pubnative.lite.sdk.banner.presenter.BannerPresenter;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.utils.TestUtil;

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
public class MRectPresenterFactoryTest {
    @Mock
    private BannerPresenter.Listener mMockListener;
    private Ad mTestAd;

    @InjectMocks
    private MRectPresenterFactory mSubject;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void createMRectPresenter_withHTML() {
        mTestAd = TestUtil.createTestMRectAd();

        assertThat(mSubject.createPresenter(mTestAd, mMockListener)).isNotNull();
    }

    @Test
    public void fromCreativeType_withHTML() {
        assertThat(mSubject.fromCreativeType(ApiAssetGroupType.MRAID_MRECT, mTestAd))
                .isInstanceOf(MraidMRectPresenter.class);
    }

    @Test
    public void createMRectPresenter_withVast() {
        mTestAd = TestUtil.createTestVideoMRectAd();

        assertThat(mSubject.createPresenter(mTestAd, mMockListener)).isNotNull();
    }

    @Test
    public void fromCreativeType_withVast() {
        assertThat(mSubject.fromCreativeType(ApiAssetGroupType.VAST_MRECT, mTestAd))
                .isInstanceOf(VastMRectPresenter.class);
    }
}
