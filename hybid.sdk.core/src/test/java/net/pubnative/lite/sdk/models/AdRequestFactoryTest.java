// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.DisplayManager;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.UserDataManager;
import net.pubnative.lite.sdk.core.BuildConfig;
import net.pubnative.lite.sdk.location.HyBidLocationManager;
import net.pubnative.lite.sdk.utils.PNCrypto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class AdRequestFactoryTest {
    @Mock
    private DeviceInfo mMockDeviceInfo;
    @Mock
    private HyBidLocationManager mLocationManager;
    @Mock
    private UserDataManager mMockUserDataManager;
    @Mock
    private DisplayManager mMockDisplayManager;
    @InjectMocks
    private PNAdRequestFactory mSubject;

    @Mock
    Context mockContext;
    @Mock
    SharedPreferences mockPrefs;
    @Mock
    SharedPreferences.Editor mockEditor;


    @Before
    public void setup() {
        initMocks(this);

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

        when(mMockUserDataManager.isCCPAOptOut()).thenReturn(false);
        when(mMockDisplayManager.getDisplayManagerVersion("b", IntegrationType.HEADER_BIDDING)).thenReturn(String.format(Locale.ENGLISH, "%s_%s_%s",
                "sdkandroid", "hb", BuildConfig.SDK_VERSION));
        when(mMockDisplayManager.getDisplayManager()).thenReturn("HyBid");
    }

    @Test
    public void createAdRequest() {

//        AdRequest request = mSubject.buildRequest("aabbcc112233", "2", AdSize.SIZE_320x50, "aabbccdd", false, IntegrationType.HEADER_BIDDING,"m");
        PNAdRequest request = (PNAdRequest) mSubject.buildRequest("aabbcc112233", "2", AdSize.SIZE_320x50, "aabbccdd", false, IntegrationType.HEADER_BIDDING, "b", 0, false);
        Assert.assertEquals("aabbccdd", request.gid);
        Assert.assertEquals(PNCrypto.md5("aabbccdd"), request.gidmd5);
        Assert.assertEquals(PNCrypto.sha1("aabbccdd"), request.gidsha1);
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
        Assert.assertEquals(HyBid.OMSDK_VERSION, request.omidpv);
        Assert.assertEquals(HyBid.OM_PARTNER_NAME, request.omidpn);

        Assert.assertEquals(String.format(Locale.ENGLISH, "%s_%s_%s",
                "sdkandroid", "hb", BuildConfig.SDK_VERSION), request.displaymanagerver);
    }
}
