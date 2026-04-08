// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.mraid;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdData;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.EndCardData;
import net.pubnative.lite.sdk.utils.AtomManager;
import net.pubnative.lite.sdk.mraid.model.HTMLAd;
import net.pubnative.lite.sdk.views.endcard.HyBidEndCardView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Test class for VRVAdJI JavaScript interface and survey end-card integration.
 * Tests the integration between MRAIDView's JavaScript interface and AtomManager's survey data.
 */
@RunWith(RobolectricTestRunner.class)
public class MRAIDViewTest {

    @Mock
    private AtomManager mockAtomManager;

    private static final String SURVEY_DATA_KEY = "SurveyData";
    private static final String SURVEY_HTML_KEY = "SurveyHtml";

    private static final String CUSTOM_KEY = "CustomData";

    private static final String TEST_SURVEY_JSON = "{\"pages\":[" +
            "{\"id\":\"q1\",\"type\":\"single-choice\",\"question\":\"Question 1\",\"options\":[\"Option A\",\"Option B\"]}," +
            "{\"id\":\"q2\",\"type\":\"gauge\",\"question\":\"Rate this\",\"leftLabel\":\"Bad\",\"rightLabel\":\"Good\"}" +
            "]}";


    private MRAIDView mraidView;

    private static final String TEST_SURVEY_HTML_TEMPLATE = "<!DOCTYPE html><html><body>" +
            "<h1>Survey</h1>" +
            "<div id=\"survey-data\">{survey_data_json}</div>" +
            "</body></html>";

    private static final String TEST_CUSTOM_END_CARD_HTML = "<!DOCTYPE html><html><body>" +
            "<h1>End Card</h1>" +
            "<div id=\"survey\">{survey_data_json}</div>" +
            "</body></html>";

    @Mock
    private Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        RuntimeEnvironment.getApplication();
        context = RuntimeEnvironment.getApplication();
        mraidView = new MRAIDView(
                context,
                "https://example.com",
                null,
                false,
                new String[]{},
                null,
                null,
                null,
                false,
                true
        );
    }

    @Test
    public void testVRVAdJI_getAtomJsData_returnsSurveyData() {
        HashMap<String, String> atomData = new HashMap<>();
        atomData.put(SURVEY_DATA_KEY, TEST_SURVEY_JSON);

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            String result = callGetAtomJsData(SURVEY_DATA_KEY);

            assertNotNull(result);
            assertEquals(TEST_SURVEY_JSON, result);
            verify(mockAtomManager).getAtomJSData();
        }
    }


    @Test
    public void testVRVAdJI_getAtomJsData_returnsNull_forInvalidKey() {
        HashMap<String, String> atomData = new HashMap<>();
        atomData.put(SURVEY_DATA_KEY, TEST_SURVEY_JSON);

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            String result = callGetAtomJsData("InvalidKey");

            assertNull(result);
        }
    }


    @Test
    public void testVRVAdJI_getAtomJsData_returnsNull_whenAtomJSDataIsNull() {
        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(null);

            String result = callGetAtomJsData(SURVEY_DATA_KEY);

            assertNull(result);
        }
    }


    @Test
    public void testVRVAdJI_getAtomJsData_returnsNull_forNullKey() {
        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            String result = callGetAtomJsData(null);

            assertNull(result);
        }
    }


    @Test
    public void testVRVAdJI_getAtomJsData_returnsNull_forEmptyKey() {
        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            String result = callGetAtomJsData("");

            assertNull(result);
        }
    }


    @Test
    public void testVRVAdJI_getAtomJsData_returnsNull_forEmptyValue() {
        HashMap<String, String> atomData = new HashMap<>();
        atomData.put(SURVEY_DATA_KEY, "");

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            String result = callGetAtomJsData(SURVEY_DATA_KEY);

            assertNull(result);
        }
    }


    @Test
    public void testVRVAdJI_getAtomJsData_returnsSurveyHtml() {
        HashMap<String, String> atomData = new HashMap<>();
        atomData.put(SURVEY_HTML_KEY, TEST_SURVEY_HTML_TEMPLATE);

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            String result = callGetAtomJsData(SURVEY_HTML_KEY);

            assertNotNull(result);
            assertEquals(TEST_SURVEY_HTML_TEMPLATE, result);
        }
    }


    @Test
    public void testVRVAdJI_getAtomJsData_returnsCustomKeyData() {
        HashMap<String, String> atomData = new HashMap<>();
        atomData.put(CUSTOM_KEY, "custom_value_123");

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            String result = callGetAtomJsData(CUSTOM_KEY);

            assertNotNull(result);
            assertEquals("custom_value_123", result);
        }
    }


    @Test
    public void testSetAtomJSDataFromCreative_validKeyValue_putsIntoMap() throws Exception {
        HashMap<String, String> atomData = new HashMap<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            callSetAtomJsData("TestKey", "TestValue");
            verify(mockAtomManager).putAtomJSData(eq("TestKey"),eq("TestValue"));
        }
    }


    @Test
    public void testSetAtomJSDataFromCreative_nullKey_doesNothing() throws Exception {
        HashMap<String, String> atomData = new HashMap<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            callSetAtomJsData(null, "TestValue");

            assertTrue(atomData.isEmpty());
        }
    }


    @Test
    public void testSetAtomJSDataFromCreative_emptyKey_doesNothing() throws Exception {
        HashMap<String, String> atomData = new HashMap<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            callSetAtomJsData("", "TestValue");

            assertTrue(atomData.isEmpty());
        }
    }


    @Test
    public void testSetAtomJSDataFromCreative_nullValue_doesNothing() throws Exception {
        HashMap<String, String> atomData = new HashMap<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            callSetAtomJsData("TestKey", null);

            assertFalse(atomData.containsKey("TestKey"));
        }
    }


    @Test
    public void testSetAtomJSDataFromCreative_emptyValue_doesNothing() throws Exception {
        HashMap<String, String> atomData = new HashMap<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            callSetAtomJsData("TestKey", "");

            assertFalse(atomData.containsKey("TestKey"));
        }
    }


    @Test
    public void testSetAtomJSDataFromCreative_nullAtomJSData_doesNotCrash() throws Exception {
        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(null);

            callSetAtomJsData("TestKey", "TestValue");
        }
    }


    @Test
    public void testSetAtomJSDataFromCreative_overwritesExistingValue() throws Exception {
        HashMap<String, String> atomData = new HashMap<>();
        atomData.put("TestKey", "OldValue");

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            callSetAtomJsData("TestKey", "NewValue");

            verify(mockAtomManager).putAtomJSData(eq("TestKey"),eq("NewValue"));

        }
    }

    @Test
    public void testVRVAdJI_onSurveyDataCollected_handlesValidDataCorrectly() throws Exception {
        String testJson = "{\"q1\":\"Option A\",\"q2\":75}";

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            callOnSurveyDataCollected(testJson);

            mockedAtom.verify(() -> AtomManager.setAdSessionData(any(HashMap.class)));
        }
    }


    @Test
    public void testVRVAdJI_onSurveyDataCollected_handlesNull() throws Exception {
        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            callOnSurveyDataCollected(null);

            mockedAtom.verify(() -> AtomManager.setAdSessionData(any(HashMap.class)), never());
        }
    }


    @Test
    public void testAdGetCustomEndCard_atomProvidesSurveyHtml_returnsAtomHtml() {
        Ad ad = createAdWithCustomEndCard(TEST_CUSTOM_END_CARD_HTML);

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            HashMap<String, String> atomData = new HashMap<>();
            atomData.put(SURVEY_HTML_KEY, TEST_SURVEY_HTML_TEMPLATE);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            EndCardData endCard = ad.getCustomEndCard();

            assertNotNull(endCard);
            assertEquals(EndCardData.Type.HTML_RESOURCE, endCard.getType());
            assertEquals(TEST_SURVEY_HTML_TEMPLATE, endCard.getContent());
        }
    }


    @Test
    public void testAdGetCustomEndCard_returnsStaticHtmlWhenAtomNull() {
        String staticHtml = "<!DOCTYPE html><html><body><h1>Static End Card</h1></body></html>";
        Ad ad = createAdWithCustomEndCard(staticHtml);

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(null);

            EndCardData endCard = ad.getCustomEndCard();

            assertNotNull(endCard);
            assertEquals(staticHtml, endCard.getContent());
        }
    }


    @Test
    public void testAdHasCustomEndCard_returnsTrue_whenAdserverAssetPresent() {
        Ad ad = createAdWithCustomEndCard(TEST_CUSTOM_END_CARD_HTML);

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(null);

            assertTrue(ad.hasCustomEndCard());
        }
    }

    @Test
    public void testAdHasCustomEndCard_returnsTrue_whenAtomHasSurveyHtml() {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            HashMap<String, String> atomData = new HashMap<>();
            atomData.put(SURVEY_HTML_KEY, TEST_SURVEY_HTML_TEMPLATE);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            assertTrue(ad.hasCustomEndCard());
        }
    }


    @Test
    public void testAdHasCustomEndCard_returnsFalse_whenNoDataAvailable() {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(null);

            assertFalse(ad.hasCustomEndCard());
        }
    }


    @Test
    public void testAdHasCustomEndCard_returnsFalse_whenAtomSurveyHtmlEmpty() {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            HashMap<String, String> atomData = new HashMap<>();
            atomData.put(SURVEY_HTML_KEY, "");
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            assertFalse(ad.hasCustomEndCard());
        }
    }

    @Test
    public void testCompleteFlow_atomProvidesBothKeys_getCustomEndCardReturnsHtml() {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            HashMap<String, String> atomData = new HashMap<>();
            atomData.put(SURVEY_DATA_KEY, TEST_SURVEY_JSON);
            atomData.put(SURVEY_HTML_KEY, TEST_SURVEY_HTML_TEMPLATE);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            assertTrue(ad.hasCustomEndCard());

            EndCardData endCard = ad.getCustomEndCard();
            assertNotNull(endCard);
            assertEquals(TEST_SURVEY_HTML_TEMPLATE, endCard.getContent());
        }
    }

    @Test
    public void testStartSkipTimer_showsSkipWhenEndCardPresentWithLink() throws Exception {
        MRAIDViewListener mockListener = mock(MRAIDViewListener.class);

        MRAIDView mraid = new MRAIDView(
                context,
                "https://example.com",
                null,
                false,
                new String[]{},
                mockListener,
                null,
                null,
                false,
                true
        );

        // Mock HTMLAd and ensure skip/close delays are zero to take the immediate branch
        HTMLAd mockHtmlAd = mock(HTMLAd.class);
        when(mockHtmlAd.getLandingPage()).thenReturn(null);
        when(mockHtmlAd.getSkipDelay()).thenReturn(0);
        when(mockHtmlAd.getNativeButtonCloseDelay()).thenReturn(0);
        when(mockHtmlAd.getCloseDelay()).thenReturn(0);
        when(mockHtmlAd.getLink()).thenReturn("https://example.com");

        mraid.setHtmlAd(mockHtmlAd);

        // Inject a non-null end card view so the timer should show the skip button
        HyBidEndCardView mockEndCardView = mock(HyBidEndCardView.class);
        java.lang.reflect.Field endCardField = MRAIDView.class.getDeclaredField("mEndCardView");
        endCardField.setAccessible(true);
        endCardField.set(mraid, mockEndCardView);

        // Call startSkipTimer
        java.lang.reflect.Method method = MRAIDView.class.getDeclaredMethod("startSkipTimer");
        method.setAccessible(true);
        method.invoke(mraid);

        // Verify the listener received skip button show and not close button show
        verify(mockListener, times(1)).mraidShowSkipButton();
        verify(mockListener, never()).mraidShowCloseButton();
    }


    @Test
    public void testReturnAtomJSDataToCreative_validKey_returnsSurveyData() throws Exception {
        HashMap<String, String> atomData = new HashMap<>();
        atomData.put(SURVEY_DATA_KEY, TEST_SURVEY_JSON);

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            Method method = MRAIDView.class.getDeclaredMethod("returnAtomJSDataToCreative", String.class);
            method.setAccessible(true);
            String result = (String) method.invoke(mraidView, SURVEY_DATA_KEY);

            assertNotNull(result);
            assertEquals(TEST_SURVEY_JSON, result);
        }
    }

    @Test
    public void testReturnAtomJSDataToCreative_nullKey_returnsNull() throws Exception {
        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            Method method = MRAIDView.class.getDeclaredMethod("returnAtomJSDataToCreative", String.class);
            method.setAccessible(true);
            String result = (String) method.invoke(mraidView, (String) null);

            assertNull(result);
        }
    }

    @Test
    public void testReturnAtomJSDataToCreative_emptyKey_returnsNull() throws Exception {
        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            Method method = MRAIDView.class.getDeclaredMethod("returnAtomJSDataToCreative", String.class);
            method.setAccessible(true);
            String result = (String) method.invoke(mraidView, "");

            assertNull(result);
        }
    }

    @Test
    public void testReturnAtomJSDataToCreative_nullAtomJSData_returnsNull() throws Exception {
        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(null);

            Method method = MRAIDView.class.getDeclaredMethod("returnAtomJSDataToCreative", String.class);
            method.setAccessible(true);
            String result = (String) method.invoke(mraidView, SURVEY_DATA_KEY);

            assertNull(result);
        }
    }


    @Test
    public void testSetAtomJSDataFromCreative_reflection_validKeyValue() throws Exception {
        HashMap<String, String> atomData = new HashMap<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            Method method = MRAIDView.class.getDeclaredMethod("putAtomJSDataFromCreative", String.class, String.class);
            method.setAccessible(true);
            method.invoke(mraidView, "MyKey", "MyValue");
            verify(mockAtomManager).putAtomJSData(eq("MyKey"),eq("MyValue"));
        }
    }

    @Test
    public void testSetAtomJSDataFromCreative_reflection_nullKey_doesNothing() throws Exception {
        HashMap<String, String> atomData = new HashMap<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            Method method = MRAIDView.class.getDeclaredMethod("putAtomJSDataFromCreative", String.class, String.class);
            method.setAccessible(true);
            method.invoke(mraidView, null, "MyValue");

            assertTrue(atomData.isEmpty());
        }
    }

    @Test
    public void testSetAtomJSDataFromCreative_reflection_nullValue_doesNothing() throws Exception {
        HashMap<String, String> atomData = new HashMap<>();

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);
            when(mockAtomManager.getAtomJSData()).thenReturn(atomData);

            Method method = MRAIDView.class.getDeclaredMethod("putAtomJSDataFromCreative", String.class, String.class);
            method.setAccessible(true);
            method.invoke(mraidView, "MyKey", null);

            assertFalse(atomData.containsKey("MyKey"));
        }
    }


    @Test
    public void testSendSurveyDataToAtom_reflection_validJson() throws Exception {
        String json = "{\"q1\":\"A\"}";

        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            Method method = MRAIDView.class.getDeclaredMethod("sendSurveyDataToAtom", String.class);
            method.setAccessible(true);
            method.invoke(mraidView, json);

            mockedAtom.verify(() -> AtomManager.setAdSessionData(any(HashMap.class)));
        }
    }

    @Test
    public void testSendSurveyDataToAtom_reflection_nullJson_doesNotCall() throws Exception {
        try (MockedStatic<AtomManager> mockedAtom = mockStatic(AtomManager.class)) {
            mockedAtom.when(AtomManager::getInstance).thenReturn(mockAtomManager);

            Method method = MRAIDView.class.getDeclaredMethod("sendSurveyDataToAtom", String.class);
            method.setAccessible(true);
            method.invoke(mraidView, (String) null);

            mockedAtom.verify(() -> AtomManager.setAdSessionData(any(HashMap.class)), never());
        }
    }


    private String callGetAtomJsData(String key) {
        try {
            Method method = MRAIDView.class.getDeclaredMethod("returnAtomJSDataToCreative", String.class);
            method.setAccessible(true);
            return (String) method.invoke(mraidView, key);
        } catch (Exception e) {
            return null;
        }
    }

    private void callSetAtomJsData(String key, String value) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = MRAIDView.class.getDeclaredMethod("putAtomJSDataFromCreative", String.class, String.class);
        method.setAccessible(true);
        method.invoke(mraidView, key, value);
    }

    private void callOnSurveyDataCollected(String json) {
        try {
            Method method = MRAIDView.class.getDeclaredMethod("sendSurveyDataToAtom", String.class);
            method.setAccessible(true);
            method.invoke(mraidView, json);
        } catch (Exception e) {
        }
    }

    private Ad createAdWithCustomEndCard(String html) {
        Ad ad = new Ad();
        ad.assets = new ArrayList<>();
        AdData adData = new AdData();
        adData.type = APIAsset.CUSTOM_END_CARD;
        adData.data = new HashMap<>();
        adData.data.put("html", html);
        ad.assets.add(adData);
        return ad;
    }

    @Test
    public void testDetermineSkipTimerDelay_withEndCardView() throws Exception {
        MRAIDView mraid = new MRAIDView(
                context,
                "https://example.com",
                null,
                false,
                new String[]{},
                null,
                null,
                null,
                false,
                true
        );

        HTMLAd mockHtmlAd = mock(HTMLAd.class);
        when(mockHtmlAd.getLandingPage()).thenReturn(null);
        when(mockHtmlAd.getSkipDelay()).thenReturn(5000);
        mraid.setHtmlAd(mockHtmlAd);

        HyBidEndCardView mockEndCardView = mock(HyBidEndCardView.class);
        java.lang.reflect.Field endCardField = MRAIDView.class.getDeclaredField("mEndCardView");
        endCardField.setAccessible(true);
        endCardField.set(mraid, mockEndCardView);

        java.lang.reflect.Method method = MRAIDView.class.getDeclaredMethod("determineSkipTimerDelay");
        method.setAccessible(true);
        Integer delay = (Integer) method.invoke(mraid);

        assertEquals(Integer.valueOf(5000), delay);
    }

    @Test
    public void testDetermineSkipTimerDelay_withCustomClose() throws Exception {
        MRAIDView mraid = new MRAIDView(
                context,
                "https://example.com",
                null,
                false,
                new String[]{},
                null,
                null,
                null,
                false,
                true
        );

        HTMLAd mockHtmlAd = mock(HTMLAd.class);
        when(mockHtmlAd.getLandingPage()).thenReturn(null);
        when(mockHtmlAd.getNativeButtonCloseDelay()).thenReturn(3000);
        when(mockHtmlAd.getCloseDelay()).thenReturn(2000);
        mraid.setHtmlAd(mockHtmlAd);

        java.lang.reflect.Field useCustomCloseField = MRAIDView.class.getDeclaredField("useCustomClose");
        useCustomCloseField.setAccessible(true);
        useCustomCloseField.set(mraid, true);

        java.lang.reflect.Method method = MRAIDView.class.getDeclaredMethod("determineSkipTimerDelay");
        method.setAccessible(true);
        Integer delay = (Integer) method.invoke(mraid);

        assertEquals(Integer.valueOf(3000), delay);
    }

    @Test
    public void testHandleTimerFinished_showsSkipButtonWhenEndCardAndLinkPresent() throws Exception {
        MRAIDViewListener mockListener = mock(MRAIDViewListener.class);
        MRAIDView mraid = new MRAIDView(
                context,
                "https://example.com",
                null,
                false,
                new String[]{},
                mockListener,
                null,
                null,
                false,
                true
        );

        HTMLAd mockHtmlAd = mock(HTMLAd.class);
        when(mockHtmlAd.getLink()).thenReturn("https://example.com");
        mraid.setHtmlAd(mockHtmlAd);

        HyBidEndCardView mockEndCardView = mock(HyBidEndCardView.class);
        java.lang.reflect.Field endCardField = MRAIDView.class.getDeclaredField("mEndCardView");
        endCardField.setAccessible(true);
        endCardField.set(mraid, mockEndCardView);

        java.lang.reflect.Method method = MRAIDView.class.getDeclaredMethod("handleTimerFinished");
        method.setAccessible(true);
        method.invoke(mraid);

        verify(mockListener).mraidShowSkipButton();
        verify(mockListener, never()).mraidShowCloseButton();
    }

    @Test
    public void testShowSkipOrCloseButton_withoutEndCard() throws Exception {
        MRAIDViewListener mockListener = mock(MRAIDViewListener.class);
        MRAIDView mraid = new MRAIDView(
                context,
                "https://example.com",
                null,
                false,
                new String[]{},
                mockListener,
                null,
                null,
                false,
                true
        );

        java.lang.reflect.Method method = MRAIDView.class.getDeclaredMethod("showSkipOrCloseButton");
        method.setAccessible(true);
        method.invoke(mraid);

        verify(mockListener).mraidShowCloseButton();
        verify(mockListener, never()).mraidShowSkipButton();
    }

    @Test
    public void testDetermineSkipTimerDelay_withLandingPage() throws Exception {
        MRAIDView mraid = new MRAIDView(
                context,
                "https://example.com",
                null,
                false,
                new String[]{},
                null,
                null,
                null,
                false,
                true
        );

        HTMLAd mockHtmlAd = mock(HTMLAd.class);
        net.pubnative.lite.sdk.mraid.model.LandingPageHandler mockLandingPage = mock(net.pubnative.lite.sdk.mraid.model.LandingPageHandler.class);
        when(mockHtmlAd.getLandingPage()).thenReturn(mockLandingPage);
        when(mockLandingPage.isLandingPageEnabled()).thenReturn(true);
        when(mockLandingPage.getUpdatedDelay()).thenReturn(4000);
        mraid.setHtmlAd(mockHtmlAd);

        java.lang.reflect.Method method = MRAIDView.class.getDeclaredMethod("determineSkipTimerDelay");
        method.setAccessible(true);
        Integer delay = (Integer) method.invoke(mraid);

        assertEquals(Integer.valueOf(4000), delay);
    }

    @Test
    public void testDetermineSkipTimerDelay_defaultPath() throws Exception {
        MRAIDView mraid = new MRAIDView(
                context,
                "https://example.com",
                null,
                false,
                new String[]{},
                null,
                null,
                null,
                false,
                true
        );

        HTMLAd mockHtmlAd = mock(HTMLAd.class);
        when(mockHtmlAd.getLandingPage()).thenReturn(null);
        when(mockHtmlAd.getCloseDelay()).thenReturn(7000);
        mraid.setHtmlAd(mockHtmlAd);

        java.lang.reflect.Method method = MRAIDView.class.getDeclaredMethod("determineSkipTimerDelay");
        method.setAccessible(true);
        Integer delay = (Integer) method.invoke(mraid);

        assertEquals(Integer.valueOf(7000), delay);
    }

    @Test
    public void testDetermineSkipTimerDelay_withCountdownView() throws Exception {
        MRAIDView mraid = new MRAIDView(
                context,
                "https://example.com",
                null,
                false,
                new String[]{},
                null,
                null,
                null,
                false,
                true
        );

        HTMLAd mockHtmlAd = mock(HTMLAd.class);
        when(mockHtmlAd.getLandingPage()).thenReturn(null);
        when(mockHtmlAd.getSkipDelay()).thenReturn(5000);
        mraid.setHtmlAd(mockHtmlAd);

        HyBidEndCardView mockEndCardView = mock(HyBidEndCardView.class);
        java.lang.reflect.Field endCardField = MRAIDView.class.getDeclaredField("mEndCardView");
        endCardField.setAccessible(true);
        endCardField.set(mraid, mockEndCardView);

        net.pubnative.lite.sdk.vpaid.widget.CountDownView mockCountdownView = mock(net.pubnative.lite.sdk.vpaid.widget.CountDownView.class);
        java.lang.reflect.Field countdownField = MRAIDView.class.getDeclaredField("mSkipCountdownView");
        countdownField.setAccessible(true);
        countdownField.set(mraid, mockCountdownView);

        java.lang.reflect.Method method = MRAIDView.class.getDeclaredMethod("determineSkipTimerDelay");
        method.setAccessible(true);
        method.invoke(mraid);

        verify(mockCountdownView).setVisibility(android.view.View.VISIBLE);
    }

    @Test
    public void testHandleTimerFinished_withLandingPage() throws Exception {
        MRAIDViewListener mockListener = mock(MRAIDViewListener.class);
        MRAIDView mraid = new MRAIDView(
                context,
                "https://example.com",
                null,
                false,
                new String[]{},
                mockListener,
                null,
                null,
                false,
                true
        );

        HTMLAd mockHtmlAd = mock(HTMLAd.class);
        net.pubnative.lite.sdk.mraid.model.LandingPageHandler mockLandingPage = mock(net.pubnative.lite.sdk.mraid.model.LandingPageHandler.class);
        when(mockHtmlAd.getLandingPage()).thenReturn(mockLandingPage);
        when(mockLandingPage.isLandingPageEnabled()).thenReturn(true);
        mraid.setHtmlAd(mockHtmlAd);

        java.lang.reflect.Method method = MRAIDView.class.getDeclaredMethod("handleTimerFinished");
        method.setAccessible(true);
        method.invoke(mraid);

        verify(mockLandingPage).setIsTimerFinished(true);
    }

    @Test
    public void testHandleTimerFinished_withCountdownView() throws Exception {
        MRAIDViewListener mockListener = mock(MRAIDViewListener.class);
        MRAIDView mraid = new MRAIDView(
                context,
                "https://example.com",
                null,
                false,
                new String[]{},
                mockListener,
                null,
                null,
                false,
                true
        );

        net.pubnative.lite.sdk.vpaid.widget.CountDownView mockCountdownView = mock(net.pubnative.lite.sdk.vpaid.widget.CountDownView.class);
        java.lang.reflect.Field countdownField = MRAIDView.class.getDeclaredField("mSkipCountdownView");
        countdownField.setAccessible(true);
        countdownField.set(mraid, mockCountdownView);

        java.lang.reflect.Method method = MRAIDView.class.getDeclaredMethod("handleTimerFinished");
        method.setAccessible(true);
        method.invoke(mraid);

        verify(mockCountdownView).setVisibility(android.view.View.GONE);
    }

    @Test
    public void testHandleTimerFinished_withoutLandingPage() throws Exception {
        MRAIDViewListener mockListener = mock(MRAIDViewListener.class);
        MRAIDView mraid = new MRAIDView(
                context,
                "https://example.com",
                null,
                false,
                new String[]{},
                mockListener,
                null,
                null,
                false,
                true
        );

        HTMLAd mockHtmlAd = mock(HTMLAd.class);
        when(mockHtmlAd.getLandingPage()).thenReturn(null);
        mraid.setHtmlAd(mockHtmlAd);

        java.lang.reflect.Method method = MRAIDView.class.getDeclaredMethod("handleTimerFinished");
        method.setAccessible(true);
        method.invoke(mraid);

        verify(mockListener).mraidShowCloseButton();
    }
}