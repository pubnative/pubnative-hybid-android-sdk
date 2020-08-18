package net.pubnative.lite.sdk.models;

import android.content.Context;
import android.graphics.Point;
import android.location.Location;

import net.pubnative.lite.sdk.BuildConfig;
import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.UserDataManager;
import net.pubnative.lite.sdk.location.HyBidLocationManager;
import net.pubnative.lite.sdk.utils.PNCrypto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by erosgarciaponte on 24.01.18.
 */
@RunWith(RobolectricTestRunner.class)
public class AdRequestFactoryTest {
    @Mock
    private DeviceInfo mMockDeviceInfo;
    @Mock
    private HyBidLocationManager mLocationManager;
    @Mock
    private UserDataManager mMockUserDataManager;

    @InjectMocks
    private AdRequestFactory mSubject;

    @Before
    public void setup() {
        initMocks(this);
        when(mMockDeviceInfo.getModel()).thenReturn("Nexus5X");
        when(mMockDeviceInfo.getOSVersion()).thenReturn("8.1.0");
        when(mMockDeviceInfo.getAdvertisingId()).thenReturn("aabbccdd");
        when(mMockDeviceInfo.getAdvertisingIdMd5()).thenReturn(PNCrypto.md5("aabbccdd"));
        when(mMockDeviceInfo.getAdvertisingIdSha1()).thenReturn(PNCrypto.sha1("aabbccdd"));
        when(mMockDeviceInfo.getLocale()).thenReturn(new Locale("EN", "US"));
        when(mMockDeviceInfo.getDeviceHeight()).thenReturn("1080");
        when(mMockDeviceInfo.getDeviceWidth()).thenReturn("1920");
        when(mMockDeviceInfo.getOrientation()).thenReturn(DeviceInfo.Orientation.PORTRAIT);

        Location mockLocation = new Location("");
        mockLocation.setLatitude(12.126543);
        mockLocation.setLongitude(15.151534);
        when(mLocationManager.getUserLocation()).thenReturn(mockLocation);

        when(mMockUserDataManager.isCCPAOptOut()).thenReturn(false);
    }

    @Test
    public void createAdRequest() {
        AdRequest request = mSubject.buildRequest("2", "s", "aabbccdd", false, IntegrationType.HEADER_BIDDING);
        Assert.assertEquals("aabbccdd", request.gid);
        Assert.assertEquals(PNCrypto.md5("aabbccdd"), request.gidmd5);
        Assert.assertEquals(PNCrypto.sha1("aabbccdd"), request.gidsha1);
        Assert.assertEquals("2", request.zoneid);
        Assert.assertEquals("s", request.al);
        Assert.assertEquals("en", request.locale);
        Assert.assertEquals("android", request.os);
        Assert.assertEquals("8.1.0", request.osver);
        Assert.assertEquals("Nexus5X", request.devicemodel);
        Assert.assertEquals("0", request.testMode);
        Assert.assertEquals("0", request.coppa);
        Assert.assertEquals("12.126543", request.latitude);
        Assert.assertEquals("15.151534", request.longitude);
        Assert.assertEquals("points,revenuemodel,contentinfo,creativeid", request.mf);
        Assert.assertEquals("HyBid", request.displaymanager);
        Assert.assertEquals("1920", request.deviceWidth);
        Assert.assertEquals("1080", request.deviceHeight);
        Assert.assertEquals("portrait", request.orientation);
        Assert.assertEquals(String.format(Locale.ENGLISH, "%s_%s_%s",
                "sdkandroid", "hb", BuildConfig.VERSION_NAME), request.displaymanagerver);
    }
}
