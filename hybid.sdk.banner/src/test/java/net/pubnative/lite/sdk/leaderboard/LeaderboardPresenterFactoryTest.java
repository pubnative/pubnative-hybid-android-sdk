package net.pubnative.lite.sdk.leaderboard;

import com.google.common.truth.Truth;

import net.pubnative.lite.sdk.banner.presenter.MraidAdPresenter;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.leaderboard.presenter.LeaderboardPresenterFactory;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.testing.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class LeaderboardPresenterFactoryTest {
    @Mock
    private AdPresenter.Listener mMockListener;
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

        Truth.assertThat(mSubject.createPresenter(mTestAd, AdSize.SIZE_728x90, mMockListener)).isNotNull();
    }

    @Test
    public void fromCreativeType_withHTML() {
        Truth.assertThat(mSubject.fromCreativeType(ApiAssetGroupType.MRAID_728x90, mTestAd, AdSize.SIZE_728x90))
                .isInstanceOf(MraidAdPresenter.class);
    }
}
