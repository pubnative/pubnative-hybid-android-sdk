package net.pubnative.lite.sdk.tracking;

import java.io.IOException;
import java.io.File;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

class DeviceDataSummary implements JsonStream.Streamable {

    private final Boolean rooted = isRooted();

    @Override
    public void toStream(JsonStream writer) throws IOException {
        writer.beginObject();
        serialiseMinimalDeviceData(writer);
        writer.endObject();
    }

    void serialiseMinimalDeviceData(JsonStream writer) throws IOException {
        writer
                .name("manufacturer").value(android.os.Build.MANUFACTURER)
                .name("model").value(android.os.Build.MODEL)
                .name("jailbroken").value(rooted)
                .name("osName").value("android")
                .name("osVersion").value(android.os.Build.VERSION.RELEASE);
    }

    private static final String[] ROOT_INDICATORS = new String[]{
            // Common binaries
            "/system/xbin/su",
            "/system/bin/su",
            // < Android 5.0
            "/system/app/Superuser.apk",
            "/system/app/SuperSU.apk",
            // >= Android 5.0
            "/system/app/Superuser",
            "/system/app/SuperSU",
            // Fallback
            "/system/xbin/daemonsu",
            // Systemless root
            "/su/bin"
    };

    static Boolean isRooted() {
        if (android.os.Build.TAGS != null && android.os.Build.TAGS.contains("test-keys")) {
            return true;
        }

        try {
            for (String candidate : ROOT_INDICATORS) {
                if (new File(candidate).exists()) {
                    return true;
                }
            }
        } catch (Exception ignore) {
            return null;
        }
        return false;
    }
}
