// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ImpressionTrackingMethodTest {

    @Test
    public void fromString_withExactValues_returnsCorrectEnum() {
        assertEquals(ImpressionTrackingMethod.AD_RENDERED, ImpressionTrackingMethod.fromString("rendered"));
        assertEquals(ImpressionTrackingMethod.AD_VIEWABLE, ImpressionTrackingMethod.fromString("viewable"));
    }

    @Test
    public void fromString_withMixedCaseValues_returnsCorrectEnum() {
        assertEquals(ImpressionTrackingMethod.AD_RENDERED, ImpressionTrackingMethod.fromString("Rendered"));
        assertEquals(ImpressionTrackingMethod.AD_VIEWABLE, ImpressionTrackingMethod.fromString("VIEWABLE"));
    }

    @Test
    public void fromString_withNullOrEmptyValue_returnsAdViewableAsDefault() {
        assertEquals(ImpressionTrackingMethod.AD_VIEWABLE, ImpressionTrackingMethod.fromString(null));
        assertEquals(ImpressionTrackingMethod.AD_VIEWABLE, ImpressionTrackingMethod.fromString(""));
    }

    @Test
    public void fromString_withUnknownValue_returnsAdViewableAsDefault() {
        assertEquals(ImpressionTrackingMethod.AD_VIEWABLE, ImpressionTrackingMethod.fromString("unknown_value"));
    }

    @Test
    public void methodNameField_hasCorrectValue() {
        assertEquals("rendered", ImpressionTrackingMethod.AD_RENDERED.methodName);
        assertEquals("viewable", ImpressionTrackingMethod.AD_VIEWABLE.methodName);
    }
}
