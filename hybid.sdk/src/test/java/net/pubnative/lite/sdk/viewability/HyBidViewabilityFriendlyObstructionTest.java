// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
package net.pubnative.lite.sdk.viewability;

import org.junit.runner.RunWith;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.robolectric.RobolectricTestRunner;

import android.view.View;

import net.pubnative.lite.sdk.viewability.baseom.BaseFriendlyObstructionPurpose;

import static org.junit.Assert.*;

/**
 * Created by shubhamkeshri on 22.09.25.
 */

@RunWith(RobolectricTestRunner.class)
public class HyBidViewabilityFriendlyObstructionTest {

    private View mockView;
    private BaseFriendlyObstructionPurpose mockPurpose;
    private String reason;

    @Before
    public void setUp() {
        mockView = Mockito.mock(View.class);
        mockPurpose = Mockito.mock(BaseFriendlyObstructionPurpose.class);
        reason = "Test Reason";
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testConstructorAndGetters() {
        HyBidViewabilityFriendlyObstruction obstruction =
                new HyBidViewabilityFriendlyObstruction(mockView, mockPurpose, reason);

        // Verify values are correctly set
        assertEquals(mockView, obstruction.getView());
        assertEquals(mockPurpose, obstruction.getPurpose());
        assertEquals(reason, obstruction.getReason());
    }

    @Test
    public void testNullValues() {
        HyBidViewabilityFriendlyObstruction obstruction =
                new HyBidViewabilityFriendlyObstruction(null, null, null);

        // Verify null handling
        assertNull(obstruction.getView());
        assertNull(obstruction.getPurpose());
        assertNull(obstruction.getReason());
    }
}
