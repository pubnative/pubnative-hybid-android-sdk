package net.pubnative.tarantula.sdk.models;

import android.os.Build;

import net.pubnative.tarantula.sdk.DeviceInfo;
import net.pubnative.tarantula.sdk.utils.TarantulaCrypto;

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

    @InjectMocks
    private AdRequestFactory mSubject;

    @Before
    public void setup() {
        initMocks(this);
        when(mMockDeviceInfo.getAdvertisingId()).thenReturn("aabbccdd");
        when(mMockDeviceInfo.getLocale()).thenReturn(new Locale("EN", "US"));
    }

    @Test
    public void createAdRequest() {
        AdRequest request = mSubject.createAdRequest("2", "s");
        Assert.assertEquals("aabbccdd", request.gid);
        Assert.assertEquals(TarantulaCrypto.md5("aabbccdd"), request.gidmd5);
        Assert.assertEquals(TarantulaCrypto.sha1("aabbccdd"), request.gidsha1);
        Assert.assertEquals("2", request.zoneid);
        Assert.assertEquals("s", request.al);
        Assert.assertEquals("en", request.locale);
        Assert.assertEquals("android", request.os);
        Assert.assertEquals(Build.VERSION.RELEASE, request.osver);
        Assert.assertEquals(Build.MODEL, request.devicemodel);
        Assert.assertEquals("0", request.testMode);
        Assert.assertEquals("0", request.coppa);
        Assert.assertEquals("points,revenuemodel,contentinfo", request.mf);
    }
}
