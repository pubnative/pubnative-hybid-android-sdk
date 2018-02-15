package net.pubnative.lite.sdk.models;

import android.location.Location;
import android.os.Build;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.location.PNLiteLocationManager;
import net.pubnative.lite.sdk.utils.PNCrypto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.Locale;

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
    private PNLiteLocationManager mLocationManager;

    @InjectMocks
    private AdRequestFactory mSubject;

    @Before
    public void setup() {
        initMocks(this);
        when(mMockDeviceInfo.getModel()).thenReturn("Nexus5X");
        when(mMockDeviceInfo.getOSVersion()).thenReturn("8.1.0");
        when(mMockDeviceInfo.getAdvertisingId()).thenReturn("aabbccdd");
        when(mMockDeviceInfo.getLocale()).thenReturn(new Locale("EN", "US"));

        Location mockLocation = new Location("");
        mockLocation.setLatitude(12.126543);
        mockLocation.setLongitude(15.151534);
        when(mLocationManager.getUserLocation()).thenReturn(mockLocation);
    }

    @Test
    public void createAdRequest() {
        AdRequest request = mSubject.createAdRequest("2", "s");
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
        Assert.assertEquals("points,revenuemodel,contentinfo", request.mf);
    }
}
