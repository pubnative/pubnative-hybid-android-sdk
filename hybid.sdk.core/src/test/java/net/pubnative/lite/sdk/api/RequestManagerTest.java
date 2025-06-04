// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mockStatic;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.testing.TestUtil;
import net.pubnative.lite.sdk.utils.AtomManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
public class RequestManagerTest {
    private RequestManager mSubject;
    @Before
    public void setup() {
        mSubject = new RequestManager();
    }

    @Test
    public void testAdSessionDataWithValidResponseWithNoCampaignOrCreativeIds(){
        try (MockedStatic<AtomManager> mockedStatic = mockStatic(AtomManager.class)) {
            Ad ad = TestUtil.createTestBannerAd();
            ad.assetgroupid = ApiAssetGroupType.VAST_MRECT;
            ArgumentCaptor<HashMap<String, Object>> adSessionMapCaptor = ArgumentCaptor.forClass(HashMap.class);
            mSubject.sendAdSessionDataToAtom(ad,0.8);
            mockedStatic.verify(() -> AtomManager.setAdSessionData(adSessionMapCaptor.capture()));
            HashMap<String, Object> adSessionMap = adSessionMapCaptor.getValue();
            assertEquals("{\"Rendering_status\":\"rendering success\",\"Viewability\":0.8,\"Ad format\":\"native\",\"Bid price\":\"0.009\"}", adSessionMap.get("Ad_Session_Data"));

        }
    }

    @Test
    public void testAdSessionDataWithValidResponse(){
        try (MockedStatic<AtomManager> mockedStatic = mockStatic(AtomManager.class)) {
            Ad ad = TestUtil.createTestAdForAtomAdSession();
            ArgumentCaptor<HashMap<String, Object>> adSessionMapCaptor = ArgumentCaptor.forClass(HashMap.class);
            mSubject.sendAdSessionDataToAtom(ad,0.8);
            mockedStatic.verify(() -> AtomManager.setAdSessionData(adSessionMapCaptor.capture()));
            HashMap<String, Object> adSessionMap = adSessionMapCaptor.getValue();
            assertEquals("{\"Rendering_status\":\"rendering success\",\"Viewability\":0.8,\"Ad format\":\"native\",\"creative_id\":\"creative_test_123\",\"campaign_id\":\"campaign_test_123\",\"Bid price\":\"0.009\"}", adSessionMap.get("Ad_Session_Data"));
        }
    }

    @Test
    public void testAdSessionDataWithNullResponseShouldNotCallAtomManager(){
        try (MockedStatic<AtomManager> mockedStatic = mockStatic(AtomManager.class)) {
            Ad ad = null;
            mSubject.sendAdSessionDataToAtom(ad,0.8);
            mockedStatic.verifyNoInteractions();
        }
    }
}
