package net.pubnative.lite.sdk.mrect;

import com.google.common.truth.Truth;

import net.pubnative.lite.sdk.banner.presenter.MraidAdPresenter;
import net.pubnative.lite.sdk.banner.presenter.VastAdPresenter;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.mrect.presenter.MRectPresenterFactory;
import net.pubnative.lite.sdk.testing.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by erosgarciaponte on 24.01.18.
 */
@RunWith(RobolectricTestRunner.class)
public class MRectPresenterFactoryTest {
    @Mock
    private AdPresenter.Listener mMockListener;
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

        Truth.assertThat(mSubject.createPresenter(mTestAd, AdSize.SIZE_300x250, mMockListener)).isNotNull();
    }

    @Test
    public void fromCreativeType_withHTML() {
        Truth.assertThat(mSubject.fromCreativeType(ApiAssetGroupType.MRAID_300x250, mTestAd, AdSize.SIZE_300x250))
                .isInstanceOf(MraidAdPresenter.class);
    }

    @Test
    public void createMRectPresenter_withVast() {
        mTestAd = TestUtil.createTestVideoMRectAd();

        Truth.assertThat(mSubject.createPresenter(mTestAd, AdSize.SIZE_300x250, mMockListener)).isNotNull();
    }

    @Test
    public void fromCreativeType_withVast() {
        Truth.assertThat(mSubject.fromCreativeType(ApiAssetGroupType.VAST_MRECT, mTestAd, AdSize.SIZE_300x250))
                .isInstanceOf(VastAdPresenter.class);
    }
}
