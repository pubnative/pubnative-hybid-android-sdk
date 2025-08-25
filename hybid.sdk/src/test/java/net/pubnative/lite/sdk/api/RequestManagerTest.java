// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mockStatic;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.testing.TestUtil;
import net.pubnative.lite.sdk.utils.AtomManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
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

            String actualJsonString = (String) adSessionMap.get("Ad_Session_Data");
            JSONObject actualJson = new JSONObject(actualJsonString);

            assertEquals("The number of keys in the JSON should be 4", 4, actualJson.length());

            assertEquals("rendering success", actualJson.getString("Rendering_status"));
            assertEquals(0.8, actualJson.getDouble("Viewability"), 0.001);
            assertEquals("native", actualJson.getString("Ad format"));
            assertEquals("0.009", actualJson.getString("Bid price"));
        } catch (JSONException e) {
            fail("Test failed due to an unexpected JSONException: " + e.getMessage());
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

            String actualJsonString = (String) adSessionMap.get("Ad_Session_Data");
            JSONObject actualJson = new JSONObject(actualJsonString);

            // 1. Verify the total number of keys to ensure strict matching
            assertEquals("The number of keys in the JSON should be 6", 6, actualJson.length());

            // 2. Verify each key-value pair individually
            assertEquals("rendering success", actualJson.getString("Rendering_status"));
            assertEquals(0.8, actualJson.getDouble("Viewability"), 0.001); // Use a delta for doubles
            assertEquals("native", actualJson.getString("Ad format"));
            assertEquals("creative_test_123", actualJson.getString("creative_id"));
            assertEquals("campaign_test_123", actualJson.getString("campaign_id"));
            assertEquals("0.009", actualJson.getString("Bid price"));
        } catch (JSONException e) {
            fail("Test failed due to an unexpected JSONException: " + e.getMessage());
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
