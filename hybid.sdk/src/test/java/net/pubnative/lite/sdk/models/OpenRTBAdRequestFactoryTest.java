// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.TopicManager;
import net.pubnative.lite.sdk.UserDataManager;
import net.pubnative.lite.sdk.location.HyBidLocationManager;
import net.pubnative.lite.sdk.models.request.Device;
import net.pubnative.lite.sdk.models.request.Imp;
import net.pubnative.lite.sdk.models.request.OpenRTBAdRequest;
import net.pubnative.lite.sdk.models.request.User;
import net.pubnative.lite.sdk.models.request.UserAgent;
import net.pubnative.lite.sdk.utils.HyBidAdvertisingId;
import net.pubnative.lite.sdk.utils.sdkmanager.DisplayManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

@RunWith(RobolectricTestRunner.class)
public class OpenRTBAdRequestFactoryTest {

    @Mock
    private DeviceInfo mockDeviceInfo;
    @Mock
    private HyBidLocationManager mockLocationManager;
    @Mock
    private UserDataManager mockUserDataManager;
    @Mock
    private DisplayManager mockDisplayManager;
    @Mock
    private TopicManager mockTopicManager;
    @Mock
    private AdRequestFactory.Callback mockCallback;
    @Captor
    private ArgumentCaptor<AdRequest> adRequestCaptor;

    private OpenRTBAdRequestFactory subject;
    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = openMocks(this);
        // Use the injectable constructor for the class under test
        subject = spy(new OpenRTBAdRequestFactory(mockDeviceInfo, mockLocationManager, mockUserDataManager, mockDisplayManager, mockTopicManager));
        when(mockDeviceInfo.getOrientation()).thenReturn(DeviceInfo.Orientation.PORTRAIT);
        when(mockDeviceInfo.getOSVersion()).thenReturn("12");
        when(mockDeviceInfo.getUserAgent()).thenReturn("Test User Agent");
        when(mockDeviceInfo.getModel()).thenReturn("Pixel Test");
        when(mockDeviceInfo.getMake()).thenReturn("Google");
        when(mockDeviceInfo.getDeviceType()).thenReturn(4);
        when(mockDeviceInfo.getLocale()).thenReturn(Locale.ENGLISH);
        when(mockDeviceInfo.getPpi()).thenReturn("440");
        when(mockDeviceInfo.getPxratio()).thenReturn("2.75");
        when(mockDeviceInfo.getDeviceHeight()).thenReturn("1920");
        when(mockDeviceInfo.getDeviceWidth()).thenReturn("1080");
        when(mockDeviceInfo.getOrientation()).thenReturn(DeviceInfo.Orientation.PORTRAIT);

        Location mockLocation = new Location("");
        mockLocation.setLatitude(12.126543);
        mockLocation.setLongitude(15.151534);
        when(mockLocationManager.getUserLocation()).thenReturn(mockLocation);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void getImpressions_whenFormatIsVideo_returnsOnlyVideoImp() {
        subject.setAdFormat("video");
        List<Imp> imps = subject.getImpressions(AdSize.SIZE_INTERSTITIAL, null, null);

        assertEquals(1, imps.size());
        assertNotNull(imps.get(0).getVideo());
        assertNull(imps.get(0).getBanner());
    }

    @Test
    public void getImpressions_whenFormatIsNull_returnsBothImps() {
        subject.setAdFormat(null);
        List<Imp> imps = subject.getImpressions(AdSize.SIZE_INTERSTITIAL, null, null);

        assertEquals(2, imps.size());
    }

    @Test
    public void getDevice_withFullDeviceInfo_populatesAllDeviceFields() {
        when(mockDeviceInfo.getOSVersion()).thenReturn("12");
        when(mockDeviceInfo.getUserAgent()).thenReturn("Test User Agent");
        when(mockDeviceInfo.getModel()).thenReturn("Pixel Test");
        when(mockDeviceInfo.getMake()).thenReturn("Google");
        when(mockDeviceInfo.getDeviceType()).thenReturn(4);
        when(mockDeviceInfo.getCarrier()).thenReturn("Test Carrier");
        when(mockDeviceInfo.getMccmnc()).thenReturn("310260");
        when(mockDeviceInfo.getMccmncsim()).thenReturn("310260");
        when(mockDeviceInfo.getAdvertisingId()).thenReturn("test-ad-id");
        when(mockDeviceInfo.getLocale()).thenReturn(Locale.ENGLISH);
        when(mockDeviceInfo.getPpi()).thenReturn("440");
        when(mockDeviceInfo.getPxratio()).thenReturn("2.75");
        when(mockDeviceInfo.getDeviceHeight()).thenReturn("1920");
        when(mockDeviceInfo.getDeviceWidth()).thenReturn("1080");

        Device device = subject.getDevice();

        assertEquals("Android", device.getOs());
        assertEquals("12", device.getOsVersion());
        assertEquals("Test User Agent", device.getUserAgent());
        assertEquals("Pixel Test", device.getModel());
        assertEquals(Integer.valueOf(440), device.getPpi());
        assertEquals(Float.valueOf(2.75f), device.getPxratio());
        assertEquals(Integer.valueOf(1920), device.getH());
        assertEquals(Integer.valueOf(1080), device.getW());
        assertEquals("en", device.getLanguage());
    }

    @Test
    public void getUser_withTopics_populatesUserData() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getAge).thenReturn("31");

            List<Topic> topics = List.of(new Topic(1, 1L, "v1"));
            when(mockTopicManager.getTopics()).thenReturn(topics);

            User user = subject.getUser();

            // 2026 (current year from context) - 31 = 1995
            assertEquals(Integer.valueOf(1995), user.getYearOfBirth());
            assertNotNull(user.getData());
            assertEquals(1, user.getData().size());
            assertEquals("1", user.getData().get(0).getSegment().get(0).getId());
        }
    }

    @Test
    public void buildRequest_withValidInputs_populatesAllObjectsCorrectly() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(() -> HyBid.getBundleId()).thenReturn("com.test.app");
            mockedHyBid.when(() -> HyBid.isTestMode()).thenReturn(false);

            when(mockDeviceInfo.getAdvertisingId()).thenReturn("test_adid");

            OpenRTBAdRequest request = (OpenRTBAdRequest) subject.buildRequest("test_token", "test_zone", AdSize.SIZE_320x50, "test_adid", false, IntegrationType.STANDALONE, null, 0, false);

            assertNotNull(request);
            assertEquals("test_zone", request.zoneId);

            // Verify nested objects
            assertNotNull(request.getApp());
            assertEquals("com.test.app", request.getApp().getBundle());

            assertNotNull(request.getDevice());
            assertEquals("Pixel Test", request.getDevice().getModel());
            assertEquals("test_adid", request.getDevice().getIfa());

            assertNotNull(request.getUser());
            assertNotNull(request.getRegs());

            assertNotNull(request.getImp());
            assertEquals(1, request.getImp().size());
            assertNotNull(request.getImp().get(0).getBanner());
        }
    }

    @Test
    public void buildRequest_withVideoCompatibleSize_returnsBothImpressions() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getBundleId).thenReturn("com.test.app");
            mockedHyBid.when(HyBid::isTestMode).thenReturn(false);
            when(mockDeviceInfo.getAdvertisingId()).thenReturn("test_adid");

            // Use a size that is video compatible, like MRECT
            OpenRTBAdRequest request = (OpenRTBAdRequest) subject.buildRequest("test_token", "test_zone", AdSize.SIZE_300x250, "test_adid", false, IntegrationType.STANDALONE, null, 0, false);

            assertNotNull(request.getImp());
            // Assert that for a video-compatible size, we get both impressions
            assertEquals(2, request.getImp().size());
        }
    }

    @Test
    public void createAdRequest_whenAdIdIsMissing_fetchesIdAndInvokesCallback() {
        when(mockDeviceInfo.getAdvertisingId()).thenReturn(null);
        when(mockDeviceInfo.getContext()).thenReturn(mock(Context.class));

        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class);
             MockedConstruction<HyBidAdvertisingId> mockedTask = Mockito.mockConstruction(HyBidAdvertisingId.class,
                     (mock, context) -> {
                         doAnswer(invocation -> {
                             HyBidAdvertisingId.Listener listener = invocation.getArgument(0);

                             // FIX: Update the mock's state to reflect the newly fetched ID
                             when(mockDeviceInfo.getAdvertisingId()).thenReturn("new-ad-id");

                             // Now, trigger the callback
                             listener.onHyBidAdvertisingIdFinish("new-ad-id", false);
                             return null;
                         }).when(mock).execute(any());
                     })) {

            mockedHyBid.when(HyBid::isTestMode).thenReturn(false);

            subject.createAdRequest("token", "zone", AdSize.SIZE_320x50, false, false, mockCallback);

            Shadows.shadowOf(Looper.getMainLooper()).idle();

            verify(mockCallback).onRequestCreated(adRequestCaptor.capture());
            OpenRTBAdRequest capturedRequest = (OpenRTBAdRequest) adRequestCaptor.getValue();

            assertNotNull(capturedRequest);
            assertEquals("new-ad-id", capturedRequest.getDevice().getIfa());
        }
    }

    @Test
    public void buildRequest_whenCoppaIsEnabled_setsDntCorrectly() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::isCoppaEnabled).thenReturn(true);

            OpenRTBAdRequest request = (OpenRTBAdRequest) subject.buildRequest("token", "zone", AdSize.SIZE_320x50, "adId", false, IntegrationType.STANDALONE, null, 0, false);

            // The getDnt() method is private, but we can inspect the Device object it creates
            assertEquals(1, request.getDevice().getDnt().intValue());
        }
    }

    // --- Setter Logic Tests ---
    @Test
    public void setAdFormat_whenHtml_returnsOnlyBannerImpression() {
        subject.setAdFormat("html");
        List<Imp> imps = subject.getImpressions(AdSize.SIZE_INTERSTITIAL, null, null);

        assertEquals(1, imps.size());
        assertNotNull(imps.get(0).getBanner());
        assertNull(imps.get(0).getVideo());
    }

    // --- Privacy Logic Tests ---
    @Test
    public void getDnt_whenConsentIsDenied_returnsOne() {
        when(mockUserDataManager.isConsentDenied()).thenReturn(true);
        Device device = subject.getDevice();
        assertEquals(Integer.valueOf(1), device.getDnt());
    }

    // =============================================================================================
    // getRegs and getExt Tests
    // =============================================================================================

    @Test
    public void buildRequest_withGppString_setsGppInRegsExt() {
        when(mockUserDataManager.getGppString()).thenReturn("test-gpp-string");
        when(mockUserDataManager.getGppSid()).thenReturn("2_6_7");

        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getBundleId).thenReturn("com.test.app");
            mockedHyBid.when(HyBid::isTestMode).thenReturn(false);

            OpenRTBAdRequest request = (OpenRTBAdRequest) subject.buildRequest(
                    "test_token", "test_zone", AdSize.SIZE_320x50, "test_adid",
                    false, IntegrationType.STANDALONE, null, 0, false);

            assertNotNull(request.getRegs());
            assertNotNull(request.getRegs().getExt());
        }
    }

    @Test
    public void buildRequest_withEmptyGppString_doesNotSetGpp() {
        when(mockUserDataManager.getGppString()).thenReturn("");
        when(mockUserDataManager.getGppSid()).thenReturn("");

        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getBundleId).thenReturn("com.test.app");
            mockedHyBid.when(HyBid::isTestMode).thenReturn(false);

            OpenRTBAdRequest request = (OpenRTBAdRequest) subject.buildRequest(
                    "test_token", "test_zone", AdSize.SIZE_320x50, "test_adid",
                    false, IntegrationType.STANDALONE, null, 0, false);

            assertNotNull(request.getRegs());
        }
    }

    @Test
    public void buildRequest_withNullGppString_doesNotCrash() {
        when(mockUserDataManager.getGppString()).thenReturn(null);
        when(mockUserDataManager.getGppSid()).thenReturn(null);

        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getBundleId).thenReturn("com.test.app");
            mockedHyBid.when(HyBid::isTestMode).thenReturn(false);

            OpenRTBAdRequest request = (OpenRTBAdRequest) subject.buildRequest(
                    "test_token", "test_zone", AdSize.SIZE_320x50, "test_adid",
                    false, IntegrationType.STANDALONE, null, 0, false);

            assertNotNull(request.getRegs());
        }
    }

    @Test
    public void buildRequest_withInvalidGppSid_handlesGracefully() {
        when(mockUserDataManager.getGppString()).thenReturn("test-gpp");
        when(mockUserDataManager.getGppSid()).thenReturn("invalid_abc_123");

        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getBundleId).thenReturn("com.test.app");
            mockedHyBid.when(HyBid::isTestMode).thenReturn(false);

            OpenRTBAdRequest request = (OpenRTBAdRequest) subject.buildRequest(
                    "test_token", "test_zone", AdSize.SIZE_320x50, "test_adid",
                    false, IntegrationType.STANDALONE, null, 0, false);

            assertNotNull(request.getRegs());
        }
    }

    // =============================================================================================
    // getUserData Tests for Topics
    // =============================================================================================

    @Test
    public void getUser_withNullTopics_hasEmptyData() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getAge).thenReturn(null);
            mockedHyBid.when(HyBid::getGender).thenReturn(null);

            when(mockTopicManager.getTopics()).thenReturn(null);

            User user = subject.getUser();

            assertNotNull(user);
            assertTrue(user.getData().isEmpty());
        }
    }

    @Test
    public void getUser_withEmptyTopics_hasEmptyData() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getAge).thenReturn(null);
            mockedHyBid.when(HyBid::getGender).thenReturn(null);

            when(mockTopicManager.getTopics()).thenReturn(new ArrayList<>());

            User user = subject.getUser();

            assertNotNull(user);
            assertTrue(user.getData().isEmpty());
        }
    }

    @Test
    public void getUser_withMultipleTaxonomyVersions_groupsCorrectly() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getAge).thenReturn(null);

            List<Topic> topics = new ArrayList<>();
            topics.add(new Topic(1, 1L, "v1"));
            topics.add(new Topic(2, 1L, "v1"));
            topics.add(new Topic(3, 2L, "v2"));
            when(mockTopicManager.getTopics()).thenReturn(topics);

            User user = subject.getUser();

            assertNotNull(user.getData());
            assertEquals(2, user.getData().size()); // Two taxonomy versions
        }
    }

    @Test
    public void getUser_withNullTopicManager_doesNotCrash() {
        OpenRTBAdRequestFactory factoryWithNullTopicManager = new OpenRTBAdRequestFactory(
                mockDeviceInfo, mockLocationManager, mockUserDataManager, mockDisplayManager, null);

        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getAge).thenReturn(null);

            User user = factoryWithNullTopicManager.getUser();

            assertNotNull(user);
            assertTrue(user.getData().isEmpty());
        }
    }

    // =============================================================================================
    // Additional User and Device Tests
    // =============================================================================================

    @Test
    public void getUser_withNullAge_returnsNullYearOfBirth() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getAge).thenReturn(null);
            mockedHyBid.when(HyBid::getGender).thenReturn(null);

            User user = subject.getUser();

            assertNull(user.getYearOfBirth());
        }
    }

    @Test
    public void getUser_withEmptyAge_returnsNullYearOfBirth() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getAge).thenReturn("");
            mockedHyBid.when(HyBid::getGender).thenReturn(null);

            User user = subject.getUser();

            assertNull(user.getYearOfBirth());
        }
    }

    @Test
    public void getUser_withGender_returnsGender() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getAge).thenReturn(null);
            mockedHyBid.when(HyBid::getGender).thenReturn("M");

            User user = subject.getUser();

            assertEquals("M", user.getGender());
        }
    }

    @Test
    public void getUser_withEmptyGender_returnsNull() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getAge).thenReturn(null);
            mockedHyBid.when(HyBid::getGender).thenReturn("");

            User user = subject.getUser();

            assertNull(user.getGender());
        }
    }

    @Test
    public void getDeviceGeo_withNullLocation_returnsNullLatLon() {
        when(mockLocationManager.getUserLocation()).thenReturn(null);

        Device device = subject.getDevice();

        assertNull(device.getGeo().getLat());
        assertNull(device.getGeo().getLon());
    }

    @Test
    public void getDeviceGeo_withLocation_returnsAccuracy() {
        Location mockLocation = new Location("");
        mockLocation.setLatitude(12.126543);
        mockLocation.setLongitude(15.151534);
        mockLocation.setAccuracy(50.5f);
        when(mockLocationManager.getUserLocation()).thenReturn(mockLocation);

        Device device = subject.getDevice();

        assertNotNull(device.getGeo().getAccuracy());
        assertEquals(Integer.valueOf(51), device.getGeo().getAccuracy()); // Rounded
    }

    // =============================================================================================
    // DNT (Do Not Track) Tests
    // =============================================================================================

    @Test
    public void getDnt_whenCCPAOptOut_returnsOne() {
        when(mockUserDataManager.isCCPAOptOut()).thenReturn(true);

        Device device = subject.getDevice();

        assertEquals(Integer.valueOf(1), device.getDnt());
    }

    @Test
    public void getDnt_whenAdvertisingIdEmpty_returnsOne() {
        when(mockDeviceInfo.getAdvertisingId()).thenReturn("");

        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::isCoppaEnabled).thenReturn(false);

            OpenRTBAdRequest request = (OpenRTBAdRequest) subject.buildRequest(
                    "token", "zone", AdSize.SIZE_320x50, "",
                    false, IntegrationType.STANDALONE, null, 0, false);

            assertEquals(Integer.valueOf(1), request.getDevice().getDnt());
        }
    }

    // =============================================================================================
    // Banner and Video Tests
    // =============================================================================================

    @Test
    public void getBanner_withNonInterstitialSize_setsExpdir() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getBannerImpression(AdSize.SIZE_320x50, null, IntegrationType.STANDALONE);

        assertNotNull(imp.getBanner());
        assertNotNull(imp.getBanner().getExpdir());
        assertEquals(2, imp.getBanner().getExpdir().size());
    }

    @Test
    public void getBanner_withInterstitialSize_doesNotSetExpdir() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getBannerImpression(AdSize.SIZE_INTERSTITIAL, null, IntegrationType.STANDALONE);

        assertNotNull(imp.getBanner());
        assertNull(imp.getBanner().getExpdir());
    }

    @Test
    public void getVideo_withNonInterstitialSize_setsStandalonePlacementSubtype() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getVideoImpression(AdSize.SIZE_300x250, null, IntegrationType.STANDALONE);

        assertNotNull(imp.getVideo());
        assertNull(imp.getVideo().getPlacement()); // Not set for non-interstitial
    }

    @Test
    public void getVideo_withInterstitialSize_setsInterstitialPlacement() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getVideoImpression(AdSize.SIZE_INTERSTITIAL, null, IntegrationType.STANDALONE);

        assertNotNull(imp.getVideo());
        assertNotNull(imp.getVideo().getPlacement());
    }

    // =============================================================================================
    // Video Compatible Sizes Tests
    // =============================================================================================

    @Test
    public void getImpressions_with320x480Size_returnsBothImps() {
        subject.setAdFormat(null);
        List<Imp> imps = subject.getImpressions(AdSize.SIZE_320x480, null, null);

        assertEquals(2, imps.size());
    }

    @Test
    public void getImpressions_with480x320Size_returnsBothImps() {
        subject.setAdFormat(null);
        List<Imp> imps = subject.getImpressions(AdSize.SIZE_480x320, null, null);

        assertEquals(2, imps.size());
    }

    @Test
    public void getImpressions_with768x1024Size_returnsBothImps() {
        subject.setAdFormat(null);
        List<Imp> imps = subject.getImpressions(AdSize.SIZE_768x1024, null, null);

        assertEquals(2, imps.size());
    }

    @Test
    public void getImpressions_with1024x768Size_returnsBothImps() {
        subject.setAdFormat(null);
        List<Imp> imps = subject.getImpressions(AdSize.SIZE_1024x768, null, null);

        assertEquals(2, imps.size());
    }

    @Test
    public void getImpressions_withNonVideoCompatibleSize_returnsOnlyBannerImp() {
        subject.setAdFormat(null);
        List<Imp> imps = subject.getImpressions(AdSize.SIZE_320x50, null, null);

        assertEquals(1, imps.size());
        assertNotNull(imps.get(0).getBanner());
    }

    @Test
    public void getImpressions_withVideoFormatForNonCompatibleSize_returnsBannerOnly() {
        subject.setAdFormat("video");
        // SIZE_320x50 is not video compatible
        List<Imp> imps = subject.getImpressions(AdSize.SIZE_320x50, null, null);

        assertEquals(1, imps.size());
        assertNotNull(imps.get(0).getBanner());
    }

    // =============================================================================================
    // setMediationVendor and setIntegrationType Tests
    // =============================================================================================

    @Test
    public void setMediationVendor_storesVendor() {
        subject.setMediationVendor("TestVendor");

        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion("TestVendor", IntegrationType.STANDALONE))
                .thenReturn("1.0.0-TestVendor");

        subject.setIntegrationType(IntegrationType.STANDALONE);

        Imp imp = subject.getBannerImpression(AdSize.SIZE_320x50, "TestVendor", IntegrationType.STANDALONE);

        assertEquals("1.0.0-TestVendor", imp.getDisplaymanagerver());
    }

    // =============================================================================================
    // createAdRequest Edge Cases
    // =============================================================================================

    @Test
    public void createAdRequest_withExistingAdId_doesNotFetch() {
        when(mockDeviceInfo.getAdvertisingId()).thenReturn("existing-ad-id");
        when(mockDeviceInfo.limitTracking()).thenReturn(false);

        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::isTestMode).thenReturn(false);

            subject.createAdRequest("token", "zone", AdSize.SIZE_320x50, false, false, mockCallback);

            verify(mockCallback).onRequestCreated(adRequestCaptor.capture());
            OpenRTBAdRequest capturedRequest = (OpenRTBAdRequest) adRequestCaptor.getValue();

            assertNotNull(capturedRequest);
            assertEquals("existing-ad-id", capturedRequest.getDevice().getIfa());
        }
    }

    @Test
    public void createAdRequest_withRejectedExecutionException_handlesGracefully() {
        when(mockDeviceInfo.getAdvertisingId()).thenReturn(null);
        when(mockDeviceInfo.getContext()).thenReturn(mock(Context.class));

        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class);
             MockedConstruction<HyBidAdvertisingId> mockedTask = Mockito.mockConstruction(HyBidAdvertisingId.class,
                     (mock, context) -> {
                         doAnswer(invocation -> {
                             throw new java.util.concurrent.RejectedExecutionException("Test exception");
                         }).when(mock).execute(any());
                     })) {

            mockedHyBid.when(HyBid::isTestMode).thenReturn(false);

            subject.createAdRequest("token", "zone", AdSize.SIZE_320x50, false, false, mockCallback);

            // Should still invoke callback even with exception
            verify(mockCallback).onRequestCreated(any());
        }
    }

    @Test
    public void createAdRequest_withGenericException_handlesGracefully() {
        when(mockDeviceInfo.getAdvertisingId()).thenReturn(null);
        when(mockDeviceInfo.getContext()).thenReturn(mock(Context.class));

        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class);
             MockedConstruction<HyBidAdvertisingId> mockedTask = Mockito.mockConstruction(HyBidAdvertisingId.class,
                     (mock, context) -> {
                         doAnswer(invocation -> {
                             throw new RuntimeException("Generic test exception");
                         }).when(mock).execute(any());
                     })) {

            mockedHyBid.when(HyBid::isTestMode).thenReturn(false);

            subject.createAdRequest("token", "zone", AdSize.SIZE_320x50, false, false, mockCallback);

            // Should still invoke callback even with exception
            verify(mockCallback).onRequestCreated(any());
        }
    }

    // =============================================================================================
    // Test Mode Tests
    // =============================================================================================

    @Test
    public void buildRequest_whenTestModeEnabled_setsTestToOne() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::isTestMode).thenReturn(true);
            mockedHyBid.when(HyBid::getBundleId).thenReturn("com.test.app");

            OpenRTBAdRequest request = (OpenRTBAdRequest) subject.buildRequest(
                    "token", "zone", AdSize.SIZE_320x50, "adId",
                    false, IntegrationType.STANDALONE, null, 0, false);

            assertEquals(Integer.valueOf(1), request.getTest());
        }
    }

    @Test
    public void buildRequest_whenTestModeDisabled_setsTestToZero() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::isTestMode).thenReturn(false);
            mockedHyBid.when(HyBid::getBundleId).thenReturn("com.test.app");

            OpenRTBAdRequest request = (OpenRTBAdRequest) subject.buildRequest(
                    "token", "zone", AdSize.SIZE_320x50, "adId",
                    false, IntegrationType.STANDALONE, null, 0, false);

            assertEquals(Integer.valueOf(0), request.getTest());
        }
    }

    // =============================================================================================
    // Device Info Edge Cases
    // =============================================================================================

    @Test
    public void getDevice_withNullLocaleLanguage_handlesGracefully() {
        Locale mockLocale = mock(Locale.class);
        when(mockLocale.getLanguage()).thenReturn("");
        when(mockDeviceInfo.getLocale()).thenReturn(mockLocale);

        Device device = subject.getDevice();

        assertNotNull(device);
    }

    @Test
    public void getDevice_withStructuredUserAgent_setsSua() {
        UserAgent sua = mock(UserAgent.class);
        when(mockDeviceInfo.getStructuredUserAgent()).thenReturn(sua);

        Device device = subject.getDevice();

        assertEquals(sua, device.getSua());
    }

    // =============================================================================================
    // App Object Tests
    // =============================================================================================

    @Test
    public void getApp_withKeywords_setsKeywords() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getBundleId).thenReturn("com.test.app");
            mockedHyBid.when(HyBid::getKeywords).thenReturn("sports,news,tech");

            OpenRTBAdRequest request = (OpenRTBAdRequest) subject.buildRequest(
                    "token", "zone", AdSize.SIZE_320x50, "adId",
                    false, IntegrationType.STANDALONE, null, 0, false);

            assertEquals("sports,news,tech", request.getApp().getKeywords());
        }
    }

    // =============================================================================================
    // Additional Banner Tests
    // =============================================================================================

    @Test
    public void getBanner_setsCorrectMimes() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getBannerImpression(AdSize.SIZE_320x50, null, IntegrationType.STANDALONE);

        assertNotNull(imp.getBanner().getMimes());
        assertTrue(imp.getBanner().getMimes().contains("text/html"));
        assertTrue(imp.getBanner().getMimes().contains("text/javascript"));
    }

    @Test
    public void getBanner_setsCorrectApis() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getBannerImpression(AdSize.SIZE_320x50, null, IntegrationType.STANDALONE);

        assertNotNull(imp.getBanner().getApi());
        assertEquals(4, imp.getBanner().getApi().size()); // MRAID_1, MRAID_2, MRAID_3, OMID_1
    }

    // =============================================================================================
    // Additional Video Tests
    // =============================================================================================

    @Test
    public void getVideo_setsCorrectMimes() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getVideoImpression(AdSize.SIZE_300x250, null, IntegrationType.STANDALONE);

        assertNotNull(imp.getVideo().getMimes());
        assertTrue(imp.getVideo().getMimes().contains("video/mp4"));
        assertTrue(imp.getVideo().getMimes().contains("video/webm"));
        assertTrue(imp.getVideo().getMimes().contains("video/3gpp"));
    }

    @Test
    public void getVideo_setsCorrectProtocols() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getVideoImpression(AdSize.SIZE_300x250, null, IntegrationType.STANDALONE);

        assertNotNull(imp.getVideo().getProtocols());
        assertEquals(12, imp.getVideo().getProtocols().size()); // VAST protocols
    }

    @Test
    public void getVideo_setsCorrectPlaybackMethods_forInterstitial() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getVideoImpression(AdSize.SIZE_INTERSTITIAL, null, IntegrationType.STANDALONE);

        assertNotNull(imp.getVideo().getPlaybackMethod());
        assertEquals(2, imp.getVideo().getPlaybackMethod().size());
    }

    @Test
    public void getVideo_setsCorrectPlaybackMethods_forNonInterstitial() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getVideoImpression(AdSize.SIZE_300x250, null, IntegrationType.STANDALONE);

        assertNotNull(imp.getVideo().getPlaybackMethod());
        assertEquals(2, imp.getVideo().getPlaybackMethod().size());
    }

    // =============================================================================================
    // Instl (Interstitial) Flag Tests
    // =============================================================================================

    @Test
    public void getBannerImpression_withInterstitialSize_setsInstlToOne() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getBannerImpression(AdSize.SIZE_INTERSTITIAL, null, IntegrationType.STANDALONE);

        assertEquals(Integer.valueOf(1), imp.getInstl());
    }

    @Test
    public void getBannerImpression_withNonInterstitialSize_setsInstlToZero() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getBannerImpression(AdSize.SIZE_320x50, null, IntegrationType.STANDALONE);

        assertEquals(Integer.valueOf(0), imp.getInstl());
    }

    @Test
    public void getVideoImpression_withInterstitialSize_setsInstlToOne() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getVideoImpression(AdSize.SIZE_INTERSTITIAL, null, IntegrationType.STANDALONE);

        assertEquals(Integer.valueOf(1), imp.getInstl());
    }

    @Test
    public void getVideoImpression_withNonInterstitialSize_setsInstlToZero() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getVideoImpression(AdSize.SIZE_300x250, null, IntegrationType.STANDALONE);

        assertEquals(Integer.valueOf(0), imp.getInstl());
    }

    // =============================================================================================
    // Banner/Video Size Tests
    // =============================================================================================

    @Test
    public void getBanner_withInterstitialSize_setsDefaultDimensions() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getBannerImpression(AdSize.SIZE_INTERSTITIAL, null, IntegrationType.STANDALONE);

        assertEquals(Integer.valueOf(320), imp.getBanner().getW());
        assertEquals(Integer.valueOf(480), imp.getBanner().getH());
    }

    @Test
    public void getBanner_withRegularSize_setsActualDimensions() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getBannerImpression(AdSize.SIZE_300x250, null, IntegrationType.STANDALONE);

        assertEquals(Integer.valueOf(300), imp.getBanner().getW());
        assertEquals(Integer.valueOf(250), imp.getBanner().getH());
    }

    @Test
    public void getVideo_withInterstitialSize_setsDefaultDimensions() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getVideoImpression(AdSize.SIZE_INTERSTITIAL, null, IntegrationType.STANDALONE);

        assertEquals(Integer.valueOf(320), imp.getVideo().getWidth());
        assertEquals(Integer.valueOf(480), imp.getVideo().getHeight());
    }

    @Test
    public void getVideo_withRegularSize_setsActualDimensions() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getVideoImpression(AdSize.SIZE_300x250, null, IntegrationType.STANDALONE);

        assertEquals(Integer.valueOf(300), imp.getVideo().getWidth());
        assertEquals(Integer.valueOf(250), imp.getVideo().getHeight());
    }

    // =============================================================================================
    // Secure and ClickBrowser Tests
    // =============================================================================================

    @Test
    public void getBannerImpression_setsSecureToOne() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getBannerImpression(AdSize.SIZE_320x50, null, IntegrationType.STANDALONE);

        assertEquals(Integer.valueOf(1), imp.getSecure());
    }

    @Test
    public void getBannerImpression_setsClickBrowserToOne() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getBannerImpression(AdSize.SIZE_320x50, null, IntegrationType.STANDALONE);

        assertEquals(Integer.valueOf(1), imp.getClickbrowser());
    }

    @Test
    public void getVideoImpression_setsSecureToOne() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getVideoImpression(AdSize.SIZE_300x250, null, IntegrationType.STANDALONE);

        assertEquals(Integer.valueOf(1), imp.getSecure());
    }

    @Test
    public void getVideoImpression_setsClickBrowserToOne() {
        when(mockDisplayManager.getDisplayManagerName()).thenReturn("TestManager");
        when(mockDisplayManager.getDisplayManagerVersion(any(), any())).thenReturn("1.0.0");

        Imp imp = subject.getVideoImpression(AdSize.SIZE_300x250, null, IntegrationType.STANDALONE);

        assertEquals(Integer.valueOf(1), imp.getClickbrowser());
    }
}
