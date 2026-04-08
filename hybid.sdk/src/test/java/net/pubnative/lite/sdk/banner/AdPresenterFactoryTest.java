// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.banner;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

import com.google.common.truth.Truth;

import net.pubnative.lite.sdk.banner.presenter.BannerPresenterFactory;
import net.pubnative.lite.sdk.banner.presenter.MraidAdPresenter;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.testing.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class AdPresenterFactoryTest {
    @Mock
    private AdPresenter.Listener mMockListener;
    private Ad mTestAd;

    private BannerPresenterFactory mSubject;

    @Before
    public void setup() {
        initMocks(this);
        Context context = ApplicationProvider.getApplicationContext();
        mSubject = new BannerPresenterFactory(context, IntegrationType.STANDALONE);
    }

    @Test
    public void createBannerPresenter_withHTML() {
        mTestAd = TestUtil.createTestBannerAd();

        Truth.assertThat(mSubject.createPresenter(mTestAd, AdSize.SIZE_320x50, mMockListener)).isNotNull();
    }

    @Test
    public void fromCreativeType_withHTML() {
        mTestAd = TestUtil.createTestBannerAd();
        Truth.assertThat(mSubject.fromCreativeType(ApiAssetGroupType.MRAID_320x50, mTestAd, AdSize.SIZE_320x50))
                .isInstanceOf(MraidAdPresenter.class);
    }
}
