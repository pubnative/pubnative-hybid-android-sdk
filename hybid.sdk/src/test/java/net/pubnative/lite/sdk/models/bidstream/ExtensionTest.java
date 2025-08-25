// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.bidstream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class ExtensionTest {

    @Test
    public void constructor_withAllParameters_assignsFieldsCorrectly() {
        List<String> inputLanguages = Arrays.asList("en-US", "de-DE");
        Integer charging = 1;
        Integer batteryLevel = 85;
        Integer batterySaver = 0;
        Integer diskSpace = 51200;
        Integer totalDisk = 122880;
        Integer darkMode = 1;
        Integer dnd = 0;
        Integer airplane = 0;
        Integer headset = 1;
        Integer ringMute = 0;

        Extension extension = new Extension(inputLanguages, charging, batteryLevel, batterySaver,
                diskSpace, totalDisk, darkMode, dnd, airplane, headset, ringMute);

        assertEquals(inputLanguages, extension.inputlanguages);
        assertEquals(charging, extension.charging);
        assertEquals(batteryLevel, extension.batterylevel);
        assertEquals(batterySaver, extension.batterysaver);
        assertEquals(diskSpace, extension.diskspace);
        assertEquals(totalDisk, extension.totaldisk);
        assertEquals(darkMode, extension.darkmode);
        assertEquals(dnd, extension.dnd);
        assertEquals(airplane, extension.airplane);
        assertEquals(headset, extension.headset);
        assertEquals(ringMute, extension.ringmute);
    }

    @Test
    public void defaultConstructor_initializesAllFieldsToNull() {
        Extension extension = new Extension();

        assertNull(extension.inputlanguages);
        assertNull(extension.charging);
        assertNull(extension.batterylevel);
        assertNull(extension.batterysaver);
        assertNull(extension.diskspace);
        assertNull(extension.totaldisk);
        assertNull(extension.darkmode);
        assertNull(extension.dnd);
        assertNull(extension.airplane);
        assertNull(extension.headset);
        assertNull(extension.ringmute);
    }
}
