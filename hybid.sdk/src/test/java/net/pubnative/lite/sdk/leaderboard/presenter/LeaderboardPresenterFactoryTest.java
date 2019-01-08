package net.pubnative.lite.sdk.leaderboard.presenter;

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

@RunWith(RobolectricTestRunner.class)
public class LeaderboardPresenterFactoryTest {
    @Mock
    private BannerPresenter.Listener mMockListener;
    private Ad mTestAd;

    @InjectMocks
    private LeaderboardPresenterFactory mSubject;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void createLeaderboardPresenter_withHTML() {
        mTestAd = TestUtil.createTestLeaderboardAd();

        assertThat(mSubject.createPresenter(mTestAd, mMockListener)).isNotNull();
    }

    @Test
    public void fromCreativeType_withHTML() {
        assertThat(mSubject.fromCreativeType(ApiAssetGroupType.MRAID_LEADERBOARD, mTestAd))
                .isInstanceOf(MraidLeaderboardPresenter.class);
    }
}
