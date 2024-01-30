package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.models.bidstream.DeviceExtension;
import net.pubnative.lite.sdk.models.bidstream.Extension;
import net.pubnative.lite.sdk.models.bidstream.Signal;

import java.util.List;
import java.util.TimeZone;

public class BaseRequestFactory {

    protected Integer formatUTCTime() {
        TimeZone localTimeZone = TimeZone.getDefault();
        int rawOffsetMillis = localTimeZone.getOffset(System.currentTimeMillis());
        return rawOffsetMillis / (60 * 1000);
    }

    protected Signal fillExtensionsObject(DeviceInfo deviceInfo) {
        if (deviceInfo != null) {
            List<String> inputLanguages = deviceInfo.getInputLanguages();
            Integer isCharging = deviceInfo.isBatteryCharging();
            Integer batteryLevel = deviceInfo.getBatteryLevel();
            Integer batterySaver = deviceInfo.isPowerSaveMode();
            Integer freeSpace = deviceInfo.getFreeMemoryMb();
            Integer totalSpace = deviceInfo.getTotalMemoryMb();
            Integer darkMode = deviceInfo.isDarkMode();
            Integer headset = deviceInfo.isHeadsetOn();
            Integer ring_mute = null;
            if (deviceInfo.getSoundSetting() != null) {
                try {
                    ring_mute = Integer.parseInt(deviceInfo.getSoundSetting());
                } catch (Exception ignored) {
                }
            }
            Integer dndEnabled = deviceInfo.isDndEnabled();
            Integer bluetooth = deviceInfo.isBluetoothEnabled();
            Integer airplaneModeEnabled = deviceInfo.isAirplaneModeEnabled();
            return new Extension(inputLanguages, isCharging, batteryLevel, batterySaver,
                    freeSpace, totalSpace, darkMode, dndEnabled, airplaneModeEnabled, bluetooth, headset, ring_mute);
        } else {
            return null;
        }
    }

    protected DeviceExtension fillBidStreamExtensionsObject(DeviceInfo deviceInfo) {
        if (deviceInfo != null) {
            List<String> inputLanguages = deviceInfo.getInputLanguages();
            Integer isCharging = deviceInfo.isBatteryCharging();
            Integer batteryLevel = deviceInfo.getBatteryLevel();
            Integer batterySaver = deviceInfo.isPowerSaveMode();
            Integer freeSpace = deviceInfo.getFreeMemoryMb();
            Integer totalSpace = deviceInfo.getTotalMemoryMb();
            Integer darkMode = deviceInfo.isDarkMode();
            Integer headset = deviceInfo.isHeadsetOn();
            Integer ring_mute = null;
            if (deviceInfo.getSoundSetting() != null) {
                try {
                    ring_mute = Integer.parseInt(deviceInfo.getSoundSetting());
                } catch (Exception ignored) {
                }
            }
            Integer dndEnabled = deviceInfo.isDndEnabled();
            Integer bluetooth = deviceInfo.isBluetoothEnabled();
            Integer airplaneModeEnabled = deviceInfo.isAirplaneModeEnabled();
            return new DeviceExtension(inputLanguages, isCharging, batteryLevel, batterySaver,
                    freeSpace, totalSpace, darkMode, dndEnabled, airplaneModeEnabled, bluetooth, headset, ring_mute);
        } else {
            return null;
        }
    }
}