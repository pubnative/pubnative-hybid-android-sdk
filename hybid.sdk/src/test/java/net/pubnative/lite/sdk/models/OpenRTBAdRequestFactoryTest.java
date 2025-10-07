// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
            mockedHyBid.when(HyBid::getAge).thenReturn("30");

            List<Topic> topics = List.of(new Topic(1, 1L, "v1"));
            when(mockTopicManager.getTopics()).thenReturn(topics);

            User user = subject.getUser();

            // 2025 (current year from context) - 30 = 1995
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


}
