// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import android.graphics.Bitmap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CustomCTADataTest {

    private CustomCTAData customCTAData;
    private final String testIconUrl = "https://example.com/icon.png";
    private final String testLabel = "Click Here";

    @Before
    public void setUp() {
        customCTAData = new CustomCTAData(testIconUrl, testLabel);
    }

    @Test
    public void constructor_initializesFieldsCorrectly() {
        assertEquals(testIconUrl, customCTAData.getIconURL());
        assertEquals(testLabel, customCTAData.getLabel());

        // The bitmap should be null initially
        assertNull(customCTAData.getBitmap());
    }

    @Test
    public void setBitmap_and_getBitmap_workCorrectly() {
        Bitmap mockBitmap = mock(Bitmap.class);

        // The bitmap is initially null
        assertNull(customCTAData.getBitmap());

        customCTAData.setBitmap(mockBitmap);

        assertEquals(mockBitmap, customCTAData.getBitmap());
    }
}
