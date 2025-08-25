// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EndCardDataTest {

    @Test
    public void mainConstructor_withAllParameters_assignsAllFieldsCorrectly() {
        EndCardData.Type type = EndCardData.Type.HTML_RESOURCE;
        String content = "<html>...</html>";
        Boolean isCustom = true;

        EndCardData endCardData = new EndCardData(type, content, isCustom);

        assertEquals(type, endCardData.getType());
        assertEquals(content, endCardData.getContent());
        assertTrue(endCardData.isCustom());
    }

    @Test
    public void convenienceConstructor_assignsFieldsAndDefaultsIsCustomToFalse() {
        EndCardData.Type type = EndCardData.Type.STATIC_RESOURCE;
        String content = "https://example.com/image.png";

        EndCardData endCardData = new EndCardData(type, content);

        assertEquals(type, endCardData.getType());
        assertEquals(content, endCardData.getContent());

        // Verify the default value is set correctly
        assertFalse(endCardData.isCustom());
    }
}
