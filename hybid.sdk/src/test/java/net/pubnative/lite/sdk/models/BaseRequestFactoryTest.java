// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.models.bidstream.DeviceExtension;
import net.pubnative.lite.sdk.models.bidstream.Extension;
import net.pubnative.lite.sdk.models.bidstream.Signal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import java.util.List;
import java.util.TimeZone;

@RunWith(RobolectricTestRunner.class)
public class BaseRequestFactoryTest {

    @Mock
    private DeviceInfo mockDeviceInfo;

    private BaseRequestFactory baseRequestFactory;
    private AutoCloseable closeable;
    private TimeZone defaultTimeZone;

    @Before
    public void setUp() {
        closeable = openMocks(this);
        baseRequestFactory = new BaseRequestFactory();
        // Store the default timezone to restore it later
        defaultTimeZone = TimeZone.getDefault();
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
        // Restore the default timezone to avoid side effects on other tests
        TimeZone.setDefault(defaultTimeZone);
    }

    @Test
    public void formatUTCTime_returnsCorrectOffsetInMinutes() {
        // Berlin is in CEST (GMT+2) during August.
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+02:00"));

        Integer offsetMinutes = baseRequestFactory.formatUTCTime();

        assertEquals(Integer.valueOf(120), offsetMinutes);
    }

    @Test
    public void fillExtensionsObject_withValidDeviceInfo_createsPopulatedExtension() {
        when(mockDeviceInfo.getInputLanguages()).thenReturn(List.of("en"));
        when(mockDeviceInfo.isBatteryCharging()).thenReturn(1);
        when(mockDeviceInfo.getBatteryLevel()).thenReturn(80);
        when(mockDeviceInfo.isPowerSaveMode()).thenReturn(0);
        when(mockDeviceInfo.getFreeMemoryMb()).thenReturn(1024);
        when(mockDeviceInfo.getTotalMemoryMb()).thenReturn(2048);
        when(mockDeviceInfo.isDarkMode()).thenReturn(1);
        when(mockDeviceInfo.isHeadsetOn()).thenReturn(0);
        when(mockDeviceInfo.getSoundSetting()).thenReturn("1");
        when(mockDeviceInfo.isDndEnabled()).thenReturn(0);
        when(mockDeviceInfo.isAirplaneModeEnabled()).thenReturn(0);

        Extension extension = (Extension) baseRequestFactory.fillExtensionsObject(mockDeviceInfo);

        assertNotNull(extension);
        assertEquals(Integer.valueOf(1), extension.charging);
        assertEquals(Integer.valueOf(80), extension.batterylevel);
        assertEquals(Integer.valueOf(0), extension.batterysaver);
        assertEquals(Integer.valueOf(1024), extension.diskspace);
        assertEquals(Integer.valueOf(2048), extension.totaldisk);
        assertEquals(Integer.valueOf(1), extension.darkmode);
        assertEquals(Integer.valueOf(0), extension.headset);
        assertEquals(Integer.valueOf(1), extension.ringmute);
        assertEquals(Integer.valueOf(0), extension.dnd);
        assertEquals(Integer.valueOf(0), extension.airplane);
        assertEquals(List.of("en"), extension.inputlanguages);
    }

    @Test
    public void fillExtensionsObject_withNullDeviceInfo_returnsNull() {
        Signal result = baseRequestFactory.fillExtensionsObject(null);
        assertNull(result);
    }

    @Test
    public void fillExtensionsObject_withInvalidSoundSetting_setsRingMuteToNull() {
        when(mockDeviceInfo.getSoundSetting()).thenReturn("invalid");
        Extension extension = (Extension) baseRequestFactory.fillExtensionsObject(mockDeviceInfo);
        assertNotNull(extension);
        assertNull(extension.ringmute);
    }

    @Test
    public void fillBidStreamExtensionsObject_withValidDeviceInfo_createsPopulatedDeviceExtension() {
        when(mockDeviceInfo.getFreeMemoryMb()).thenReturn(512);
        when(mockDeviceInfo.getTotalMemoryMb()).thenReturn(1024);
        when(mockDeviceInfo.isDarkMode()).thenReturn(0);

        DeviceExtension deviceExtension = baseRequestFactory.fillBidStreamExtensionsObject(mockDeviceInfo);

        assertNotNull(deviceExtension);
        assertEquals(Integer.valueOf(512), deviceExtension.diskspace);
        assertEquals(Integer.valueOf(1024), deviceExtension.totaldisk);
        assertEquals(Integer.valueOf(0), deviceExtension.darkmode);
    }

    @Test
    public void fillBidStreamExtensionsObject_withNullDeviceInfo_returnsNull() {
        DeviceExtension result = baseRequestFactory.fillBidStreamExtensionsObject(null);
        assertNull(result);
    }
}