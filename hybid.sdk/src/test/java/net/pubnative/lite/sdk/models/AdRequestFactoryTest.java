// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Looper;

import net.pubnative.lite.sdk.BuildConfig;
import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.TopicManager;
import net.pubnative.lite.sdk.UserDataManager;
import net.pubnative.lite.sdk.location.HyBidLocationManager;
import net.pubnative.lite.sdk.utils.HyBidAdvertisingId;
import net.pubnative.lite.sdk.utils.PNCrypto;
import net.pubnative.lite.sdk.utils.sdkmanager.DisplayManager;
import net.pubnative.lite.sdk.utils.sdkmanager.SdkManager;
import net.pubnative.lite.sdk.viewability.HybidViewabilityManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

import java.util.Locale;

@RunWith(RobolectricTestRunner.class)
public class AdRequestFactoryTest {
    @Mock
    private DeviceInfo mMockDeviceInfo;
    @Mock
    private HyBidLocationManager mLocationManager;
    @Mock
    private UserDataManager mMockUserDataManager;
    @Mock
    private SdkManager mSdkManager;
    @Mock
    private TopicManager mTopicManager;
    @Mock
    private HybidViewabilityManager mViewabilityManager;
    @Mock
    private DisplayManager mDisplayManager;
    @Mock
    private Context mockContext;
    @Mock
    private SharedPreferences mockPrefs;
    @Mock
    private SharedPreferences.Editor mockEditor;
    @Mock
    private AdRequestFactory.Callback mockCallback;
    @Captor
    private ArgumentCaptor<AdRequest> adRequestCaptor;

    @InjectMocks
    private PNAdRequestFactory mSubject;
    private AutoCloseable closeable;

    @Before
    public void setup() {
        closeable = openMocks(this);

        Mockito.when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs);
        Mockito.when(mockContext.getSharedPreferences(anyString(), anyInt()).edit()).thenReturn(mockEditor);
        Mockito.when(mockPrefs.getString(anyString(), anyString())).thenReturn("1234567");

        when(mMockDeviceInfo.getModel()).thenReturn("Nexus5X");
        when(mMockDeviceInfo.getMake()).thenReturn("Google");
        when(mMockDeviceInfo.getCarrier()).thenReturn("Movistar");
        when(mMockDeviceInfo.getOSVersion()).thenReturn("8.1.0");
        when(mMockDeviceInfo.getAdvertisingId()).thenReturn("aabbccdd");
        when(mMockDeviceInfo.getAdvertisingIdMd5()).thenReturn(PNCrypto.md5("aabbccdd"));
        when(mMockDeviceInfo.getAdvertisingIdSha1()).thenReturn(PNCrypto.sha1("aabbccdd"));
        when(mMockDeviceInfo.getLocale()).thenReturn(new Locale("EN", "US"));
        when(mMockDeviceInfo.getDeviceHeight()).thenReturn("1080");
        when(mMockDeviceInfo.getDeviceWidth()).thenReturn("1920");
        when(mMockDeviceInfo.getPpi()).thenReturn("440");
        when(mMockDeviceInfo.getPxratio()).thenReturn("2.75");
        when(mMockDeviceInfo.getConnectionType()).thenReturn(2);
        when(mMockDeviceInfo.getMccmnc()).thenReturn("321123");
        when(mMockDeviceInfo.getMccmncsim()).thenReturn("123321");
        when(mMockDeviceInfo.getOrientation()).thenReturn(DeviceInfo.Orientation.PORTRAIT);
        when(mMockDeviceInfo.getContext()).thenReturn(mockContext);

        Location mockLocation = new Location("");
        mockLocation.setLatitude(12.126543);
        mockLocation.setLongitude(15.151534);
        when(mLocationManager.getUserLocation()).thenReturn(mockLocation);

        when(mViewabilityManager.getPartnerName()).thenReturn("HyBid");
        when(mViewabilityManager.getPartnerVersion()).thenReturn("1.2.3");

        when(mDisplayManager.getDisplayManagerVersion(any(), any(IntegrationType.class)))
                .thenReturn(String.format(Locale.ENGLISH, "%s_%s_%s", "sdkandroid", "hb", BuildConfig.SDK_VERSION));
        when(mDisplayManager.getDisplayManagerName()).thenReturn("HyBid");

        when(mSdkManager.getVisibilityManager()).thenReturn(mViewabilityManager);
        when(mSdkManager.getDisplayManager()).thenReturn(mDisplayManager);
        when(mMockDeviceInfo.getOrientation()).thenReturn(DeviceInfo.Orientation.PORTRAIT);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void buildRequest_withValidInputs_createsCorrectRequest() {
        PNAdRequest request = (PNAdRequest) mSubject.buildRequest("aabbcc112233", "2", AdSize.SIZE_320x50, "aabbccdd", false, IntegrationType.HEADER_BIDDING, "b", 0, false);
        Assert.assertEquals("aabbccdd", request.gid);
        Assert.assertEquals("2", request.zoneId);
        Assert.assertEquals("s", request.al);
        Assert.assertEquals("320", request.width);
        Assert.assertEquals("50", request.height);
        Assert.assertEquals("en", request.locale);
        Assert.assertEquals("en", request.language);
        Assert.assertEquals("android", request.os);
        Assert.assertEquals("8.1.0", request.osver);
        Assert.assertEquals("Nexus5X", request.devicemodel);
        Assert.assertEquals("Google", request.make);
        Assert.assertEquals("0", request.testMode);
        Assert.assertEquals("0", request.coppa);
        Assert.assertEquals("12.13", request.latitude);
        Assert.assertEquals("15.15", request.longitude);
        Assert.assertEquals("points,revenuemodel,contentinfo,creativeid,campaignid,bundleid,adexperience", request.mf);
        Assert.assertEquals("1,2,3,4,5,6,7,8,11,12,13,14", request.protocol);
        Assert.assertEquals("3,5,6,7", request.api);
        Assert.assertEquals("HyBid", request.displaymanager);
        Assert.assertEquals("sdkandroid_hb_" + BuildConfig.SDK_VERSION, request.displaymanagerver);
        Assert.assertEquals("1920", request.deviceWidth);
        Assert.assertEquals("1080", request.deviceHeight);
        Assert.assertEquals("portrait", request.orientation);
        Assert.assertEquals("0", request.impdepth);
        Assert.assertEquals("440", request.ppi);
        Assert.assertEquals("2.75", request.pxratio);
        Assert.assertEquals("2", request.connectiontype);
        Assert.assertEquals("Movistar", request.carrier);
        Assert.assertEquals("321123", request.mccmnc);
        Assert.assertEquals("123321", request.mccmncsim);
        Assert.assertEquals("1", request.js);
        Assert.assertEquals("0", request.ae);
        Assert.assertEquals("1234567", mockPrefs.getString("", ""));
        Assert.assertEquals("1.2.3", request.omidpv);
        Assert.assertEquals("HyBid", request.omidpn);
    }

    // --------------- NEW TESTS FOR EXPANDED COVERAGE ---------------

    @Test
    public void setIntegrationType_isReflectedInBuildRequest() {
        when(mDisplayManager.getDisplayManagerVersion(any(), eq(IntegrationType.MEDIATION)))
                .thenReturn("sdkandroid_m_version");

        mSubject.setIntegrationType(IntegrationType.MEDIATION);
        PNAdRequest request = (PNAdRequest) mSubject.buildRequest("token", "zone", AdSize.SIZE_320x50, "adId", false, IntegrationType.MEDIATION, "vendor", 0, false);

        assertEquals("sdkandroid_m_version", request.displaymanagerver);
    }

    @Test
    public void buildRequest_whenLimitTrackingIsTrue_setsDntAndOmitsGid() {
        PNAdRequest request = (PNAdRequest) mSubject.buildRequest("token", "zone", AdSize.SIZE_320x50, "adId", true, IntegrationType.STANDALONE, null, 0, false);

        assertEquals("1", request.dnt);
        assertNull(request.gid);
        assertNull(request.gidmd5);
        assertNull(request.gidsha1);
    }

    @Test
    public void buildRequest_whenCoppaIsEnabled_setsCoppaAndDnt() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::isCoppaEnabled).thenReturn(true);

            PNAdRequest request = (PNAdRequest) mSubject.buildRequest("token", "zone", AdSize.SIZE_320x50, "adId", false, IntegrationType.STANDALONE, null, 0, false);

            assertEquals("1", request.coppa);
            assertEquals("1", request.dnt);
        }
    }

    @Test
    public void buildRequest_whenLocationIsUnavailable_omitsLocationFields() {
        when(mLocationManager.getUserLocation()).thenReturn(null);

        PNAdRequest request = (PNAdRequest) mSubject.buildRequest("token", "zone", AdSize.SIZE_320x50, "adId", false, IntegrationType.STANDALONE, null, 0, false);

        assertNull(request.latitude);
        assertNull(request.longitude);
    }

    @Test
    public void createAdRequest_whenAdIdIsAvailable_invokesCallback() {
        // The @Before setup already provides a valid advertising ID
        when(mMockDeviceInfo.getAdvertisingId()).thenReturn("aabbccdd");

        mSubject.createAdRequest("token", "zone", AdSize.SIZE_320x50, false, false, mockCallback);

        // Robolectric executes the task synchronously in this case
        verify(mockCallback).onRequestCreated(adRequestCaptor.capture());

        PNAdRequest capturedRequest = (PNAdRequest) adRequestCaptor.getValue();
        assertNotNull(capturedRequest);
        assertEquals("zone", capturedRequest.zoneId);
    }

    @Test
    public void createAdRequest_whenAdIdIsMissing_fetchesIdAndInvokesCallback() {
        // Setup: Advertising ID is initially missing
        when(mMockDeviceInfo.getAdvertisingId()).thenReturn(null);

        try (MockedConstruction<HyBidAdvertisingId> mockedTask = Mockito.mockConstruction(HyBidAdvertisingId.class,
                (mock, context) -> {
                    doAnswer(invocation -> {
                        // Manually trigger the callback to simulate the async task finishing
                        HyBidAdvertisingId.Listener listener = invocation.getArgument(0);
                        listener.onHyBidAdvertisingIdFinish("new-ad-id", false);
                        return null; // Answers for void methods should return null
                    }).when(mock).execute(any());
                })) {

            mSubject.createAdRequest("token", "zone", AdSize.SIZE_320x50, false, false, mockCallback);

            Shadows.shadowOf(Looper.getMainLooper()).idle();

            verify(mockCallback).onRequestCreated(adRequestCaptor.capture());
            PNAdRequest capturedRequest = (PNAdRequest) adRequestCaptor.getValue();

            assertNotNull(capturedRequest);
            assertEquals("new-ad-id", capturedRequest.gid);
        }
    }
}