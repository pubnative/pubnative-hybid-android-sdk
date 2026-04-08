// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * Robolectric-based tests for DeviceInfo.isHeadsetOn() method
 * to achieve 100% code coverage including API 23+ paths
 */
@RunWith(RobolectricTestRunner.class)
public class DeviceInfoHeadsetTest {

    @Mock
    private Context mockContext;

    @Mock
    private Resources mockResources;

    @Mock
    private WindowManager mockWindowManager;

    @Mock
    private Display mockDisplay;

    @Mock
    private AudioManager mockAudioManager;

    private DeviceInfo deviceInfo;
    private AutoCloseable mockitoCloseable;

    @Before
    public void setUp() {
        mockitoCloseable = MockitoAnnotations.openMocks(this);

        // Setup basic mocks needed for DeviceInfo constructor
        when(mockContext.getApplicationContext()).thenReturn(mockContext);
        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockContext.getSystemService(Context.WINDOW_SERVICE)).thenReturn(mockWindowManager);
        when(mockWindowManager.getDefaultDisplay()).thenReturn(mockDisplay);

        // Mock Display.getSize() to set the Point dimensions
        doAnswer(invocation -> {
            Point point = invocation.getArgument(0);
            point.x = 1080;
            point.y = 1920;
            return null;
        }).when(mockDisplay).getSize(any(Point.class));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.density = 1.0f;
        when(mockResources.getDisplayMetrics()).thenReturn(displayMetrics);
    }

    // ========== API 23+ (Marshmallow and above) Tests ==========

    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void isHeadsetOn_apiM_devicesNull_returnsNull() {
        when(mockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);
        when(mockAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)).thenReturn(null);

        deviceInfo = new DeviceInfo(mockContext);
        Integer result = deviceInfo.isHeadsetOn();

        assertNull(result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void isHeadsetOn_apiM_wiredHeadsetDetected_returnsOne() {
        when(mockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);

        AudioDeviceInfo mockDevice = mock(AudioDeviceInfo.class);
        when(mockDevice.getType()).thenReturn(AudioDeviceInfo.TYPE_WIRED_HEADSET);
        AudioDeviceInfo[] devices = new AudioDeviceInfo[]{mockDevice};
        when(mockAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)).thenReturn(devices);

        deviceInfo = new DeviceInfo(mockContext);
        Integer result = deviceInfo.isHeadsetOn();

        assertEquals(Integer.valueOf(1), result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void isHeadsetOn_apiM_wiredHeadphonesDetected_returnsOne() {
        when(mockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);

        AudioDeviceInfo mockDevice = mock(AudioDeviceInfo.class);
        when(mockDevice.getType()).thenReturn(AudioDeviceInfo.TYPE_WIRED_HEADPHONES);
        AudioDeviceInfo[] devices = new AudioDeviceInfo[]{mockDevice};
        when(mockAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)).thenReturn(devices);

        deviceInfo = new DeviceInfo(mockContext);
        Integer result = deviceInfo.isHeadsetOn();

        assertEquals(Integer.valueOf(1), result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void isHeadsetOn_apiM_noHeadsetDetected_returnsZero() {
        when(mockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);

        AudioDeviceInfo mockDevice = mock(AudioDeviceInfo.class);
        when(mockDevice.getType()).thenReturn(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);
        AudioDeviceInfo[] devices = new AudioDeviceInfo[]{mockDevice};
        when(mockAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)).thenReturn(devices);

        deviceInfo = new DeviceInfo(mockContext);
        Integer result = deviceInfo.isHeadsetOn();

        assertEquals(Integer.valueOf(0), result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void isHeadsetOn_apiM_emptyDevicesArray_returnsZero() {
        when(mockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);

        AudioDeviceInfo[] devices = new AudioDeviceInfo[]{};
        when(mockAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)).thenReturn(devices);

        deviceInfo = new DeviceInfo(mockContext);
        Integer result = deviceInfo.isHeadsetOn();

        assertEquals(Integer.valueOf(0), result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void isHeadsetOn_apiM_multipleDevicesWithHeadset_returnsOne() {
        when(mockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);

        AudioDeviceInfo mockDevice1 = mock(AudioDeviceInfo.class);
        when(mockDevice1.getType()).thenReturn(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);

        AudioDeviceInfo mockDevice2 = mock(AudioDeviceInfo.class);
        when(mockDevice2.getType()).thenReturn(AudioDeviceInfo.TYPE_WIRED_HEADSET);

        AudioDeviceInfo[] devices = new AudioDeviceInfo[]{mockDevice1, mockDevice2};
        when(mockAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)).thenReturn(devices);

        deviceInfo = new DeviceInfo(mockContext);
        Integer result = deviceInfo.isHeadsetOn();

        assertEquals(Integer.valueOf(1), result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void isHeadsetOn_apiM_multipleDevicesNoHeadset_returnsZero() {
        when(mockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);

        AudioDeviceInfo mockDevice1 = mock(AudioDeviceInfo.class);
        when(mockDevice1.getType()).thenReturn(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);

        AudioDeviceInfo mockDevice2 = mock(AudioDeviceInfo.class);
        when(mockDevice2.getType()).thenReturn(AudioDeviceInfo.TYPE_BUILTIN_EARPIECE);

        AudioDeviceInfo mockDevice3 = mock(AudioDeviceInfo.class);
        when(mockDevice3.getType()).thenReturn(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP);

        AudioDeviceInfo[] devices = new AudioDeviceInfo[]{mockDevice1, mockDevice2, mockDevice3};
        when(mockAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)).thenReturn(devices);

        deviceInfo = new DeviceInfo(mockContext);
        Integer result = deviceInfo.isHeadsetOn();

        assertEquals(Integer.valueOf(0), result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.N)
    public void isHeadsetOn_apiN_wiredHeadsetDetected_returnsOne() {
        when(mockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);

        AudioDeviceInfo mockDevice = mock(AudioDeviceInfo.class);
        when(mockDevice.getType()).thenReturn(AudioDeviceInfo.TYPE_WIRED_HEADSET);
        AudioDeviceInfo[] devices = new AudioDeviceInfo[]{mockDevice};
        when(mockAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)).thenReturn(devices);

        deviceInfo = new DeviceInfo(mockContext);
        Integer result = deviceInfo.isHeadsetOn();

        assertEquals(Integer.valueOf(1), result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.O)
    public void isHeadsetOn_apiO_wiredHeadphonesDetected_returnsOne() {
        when(mockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);

        AudioDeviceInfo mockDevice = mock(AudioDeviceInfo.class);
        when(mockDevice.getType()).thenReturn(AudioDeviceInfo.TYPE_WIRED_HEADPHONES);
        AudioDeviceInfo[] devices = new AudioDeviceInfo[]{mockDevice};
        when(mockAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)).thenReturn(devices);

        deviceInfo = new DeviceInfo(mockContext);
        Integer result = deviceInfo.isHeadsetOn();

        assertEquals(Integer.valueOf(1), result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.P)
    public void isHeadsetOn_apiP_noHeadsetDetected_returnsZero() {
        when(mockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);

        AudioDeviceInfo mockDevice = mock(AudioDeviceInfo.class);
        when(mockDevice.getType()).thenReturn(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP);
        AudioDeviceInfo[] devices = new AudioDeviceInfo[]{mockDevice};
        when(mockAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)).thenReturn(devices);

        deviceInfo = new DeviceInfo(mockContext);
        Integer result = deviceInfo.isHeadsetOn();

        assertEquals(Integer.valueOf(0), result);
    }

    // ========== API 21-22 (Lollipop) Tests ==========

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP)
    public void isHeadsetOn_apiLollipop_wiredHeadsetOn_returnsOne() {
        when(mockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);
        when(mockAudioManager.isWiredHeadsetOn()).thenReturn(true);
        when(mockAudioManager.isBluetoothScoOn()).thenReturn(false);
        when(mockAudioManager.isBluetoothA2dpOn()).thenReturn(false);

        deviceInfo = new DeviceInfo(mockContext);
        Integer result = deviceInfo.isHeadsetOn();

        assertEquals(Integer.valueOf(1), result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP)
    public void isHeadsetOn_apiLollipop_bluetoothScoOn_returnsOne() {
        when(mockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);
        when(mockAudioManager.isWiredHeadsetOn()).thenReturn(false);
        when(mockAudioManager.isBluetoothScoOn()).thenReturn(true);
        when(mockAudioManager.isBluetoothA2dpOn()).thenReturn(false);

        deviceInfo = new DeviceInfo(mockContext);
        Integer result = deviceInfo.isHeadsetOn();

        assertEquals(Integer.valueOf(1), result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP)
    public void isHeadsetOn_apiLollipop_bluetoothA2dpOn_returnsOne() {
        when(mockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);
        when(mockAudioManager.isWiredHeadsetOn()).thenReturn(false);
        when(mockAudioManager.isBluetoothScoOn()).thenReturn(false);
        when(mockAudioManager.isBluetoothA2dpOn()).thenReturn(true);

        deviceInfo = new DeviceInfo(mockContext);
        Integer result = deviceInfo.isHeadsetOn();

        assertEquals(Integer.valueOf(1), result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP)
    public void isHeadsetOn_apiLollipop_allHeadsetsOn_returnsOne() {
        when(mockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);
        when(mockAudioManager.isWiredHeadsetOn()).thenReturn(true);
        when(mockAudioManager.isBluetoothScoOn()).thenReturn(true);
        when(mockAudioManager.isBluetoothA2dpOn()).thenReturn(true);

        deviceInfo = new DeviceInfo(mockContext);
        Integer result = deviceInfo.isHeadsetOn();

        assertEquals(Integer.valueOf(1), result);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP)
    public void isHeadsetOn_apiLollipop_noHeadsetOn_returnsZero() {
        when(mockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);
        when(mockAudioManager.isWiredHeadsetOn()).thenReturn(false);
        when(mockAudioManager.isBluetoothScoOn()).thenReturn(false);
        when(mockAudioManager.isBluetoothA2dpOn()).thenReturn(false);

        deviceInfo = new DeviceInfo(mockContext);
        Integer result = deviceInfo.isHeadsetOn();

        assertEquals(Integer.valueOf(0), result);
    }
}

