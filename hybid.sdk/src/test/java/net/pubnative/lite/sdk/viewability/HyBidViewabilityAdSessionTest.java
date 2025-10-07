// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
package net.pubnative.lite.sdk.viewability;

import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.After;
import org.mockito.Mock;
import org.junit.Test;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.view.View;

import net.pubnative.lite.sdk.viewability.baseom.BaseFriendlyObstructionPurpose;
import net.pubnative.lite.sdk.viewability.baseom.BaseViewabilityManager;

/**
 * Created by shubhamkeshri on 19.09.25.
 */

@RunWith(RobolectricTestRunner.class)
public class HyBidViewabilityAdSessionTest {

    @Mock
    private HyBidViewabilityManager mockViewabilityManager;

    @Mock
    private View mockView;

    private TestHyBidViewabilityAdSession adSession;

    @Before
    public void setUp() {
        openMocks(this);
        adSession = new TestHyBidViewabilityAdSession(mockViewabilityManager);
    }

    @After
    public void tearDown() {

    }

    private static class TestHyBidViewabilityAdSession extends HyBidViewabilityAdSession {
        public TestHyBidViewabilityAdSession(BaseViewabilityManager viewabilityManager) {
            super(viewabilityManager);
        }
    }

    @Test
    public void fireLoaded_shouldCallViewabilityManager_whenAdEventsNotNullAndEnabled() {
        Object adEvents = new Object();
        adSession.mAdEvents = adEvents;

        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(true);

        adSession.fireLoaded();

        verify(mockViewabilityManager).fireLoaded(adEvents);
    }

    @Test
    public void testFireLoaded_shouldNotCallViewabilityManager_whenAdEventsNull() {
        adSession.mAdEvents = null;
        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(true);

        adSession.fireLoaded();

        verify(mockViewabilityManager, never()).fireLoaded(any());
    }

    @Test
    public void testFireImpression_shouldCallViewabilityManager_whenAdEventsNotNullAndEnabled() {
        Object adEvents = new Object();
        adSession.mAdEvents = adEvents;

        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(true);

        adSession.fireImpression();

        verify(mockViewabilityManager).fireImpression(adEvents);
    }

    @Test
    public void testStopAdSession_shouldCallViewabilityManager_andClearAdSession() {
        Object adSessionObj = new Object();
        adSession.mAdSession = adSessionObj;

        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(true);

        adSession.stopAdSession();

        verify(mockViewabilityManager).stopAdSession(adSessionObj);
        assert adSession.mAdSession == null;
    }

    @Test
    public void testStopAdSession_shouldNotCallViewabilityManager_whenAdSessionNull() {
        adSession.mAdSession = null;
        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(true);

        adSession.stopAdSession();

        verify(mockViewabilityManager, never()).stopAdSession(any());
    }

    @Test
    public void testAddFriendlyObstruction_shouldCallViewabilityManager_whenAdSessionAndViewNotNull() {
        Object adSessionObj = new Object();
        adSession.mAdSession = adSessionObj;

        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(true);

        adSession.addFriendlyObstruction(mockView, BaseFriendlyObstructionPurpose.CLOSE_AD, "TestReason");

        verify(mockViewabilityManager).addFriendlyObstruction(adSessionObj, mockView, BaseFriendlyObstructionPurpose.CLOSE_AD, "TestReason");
    }

    @Test
    public void testAddFriendlyObstruction_shouldNotCallViewabilityManager_whenViewNull() {
        adSession.mAdSession = new Object();

        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(true);

        adSession.addFriendlyObstruction(null, BaseFriendlyObstructionPurpose.CLOSE_AD, "TestReason");

        verify(mockViewabilityManager, never()).addFriendlyObstruction(any(), any(), any(), any());
    }

    @Test
    public void testAddFriendlyObstruction_shouldNotCallViewabilityManager_whenAdSessionNull() {
        adSession.mAdSession = null;

        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(true);

        adSession.addFriendlyObstruction(mockView, BaseFriendlyObstructionPurpose.CLOSE_AD, "TestReason");

        verify(mockViewabilityManager, never()).addFriendlyObstruction(any(), any(), any(), any());
    }

    @Test
    public void testMethods_shouldSkipExecution_whenViewabilityMeasurementDisabled() {
        adSession.mAdSession = new Object();
        adSession.mAdEvents = new Object();

        when(mockViewabilityManager.isViewabilityMeasurementEnabled()).thenReturn(false);

        //calling HyBidViewabilityAdSession's method to interact with ViewabilityManager.
        adSession.fireLoaded();
        adSession.fireImpression();
        adSession.stopAdSession();
        adSession.addFriendlyObstruction(mockView, BaseFriendlyObstructionPurpose.CLOSE_AD, "TestReason");

        // Only `isViewabilityMeasurementEnabled` is called, nothing else
        verify(mockViewabilityManager, atLeastOnce()).isViewabilityMeasurementEnabled();

        // No calls possible since ViewabilityMeasurement is Disabled
        verify(mockViewabilityManager, never()).fireLoaded(any());
        verify(mockViewabilityManager, never()).fireImpression(any());
        verify(mockViewabilityManager, never()).stopAdSession(any());
        verify(mockViewabilityManager, never()).addFriendlyObstruction(any(), any(), any(), any());
    }

    @Test
    public void testMethods_shouldSkipExecution_whenViewabilityManagerIsNull() {
        TestHyBidViewabilityAdSession sessionWithNullManager =
                new TestHyBidViewabilityAdSession(null);

        sessionWithNullManager.fireLoaded();
        sessionWithNullManager.fireImpression();
        sessionWithNullManager.stopAdSession();
        sessionWithNullManager.addFriendlyObstruction(mockView, BaseFriendlyObstructionPurpose.CLOSE_AD, "TestReason");

        // No calls possible since manager is null
        verifyNoMoreInteractions(mockViewabilityManager);
    }
}