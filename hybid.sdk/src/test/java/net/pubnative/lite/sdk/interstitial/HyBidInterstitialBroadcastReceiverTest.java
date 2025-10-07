// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import net.pubnative.lite.sdk.CustomEndCardListener;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.lite.sdk.utils.PNLocalBroadcastManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;


@RunWith(RobolectricTestRunner.class)
public class HyBidInterstitialBroadcastReceiverTest {

    @Mock
    private PNLocalBroadcastManager mockLocalBroadcastManager;
    @Mock
    private HyBidInterstitialBroadcastReceiver.Listener mockListener;
    @Mock
    private InterstitialPresenter mockPresenter;
    @Mock
    private InterstitialPresenter.Listener mockPresenterListener;
    @Mock
    private VideoListener mockVideoListener;
    @Mock
    private CustomEndCardListener mockCustomEndCardListener;

    private Context context;
    private long broadcastId = 12345L;
    private HyBidInterstitialBroadcastReceiver broadcastReceiver;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();
        // Use the testable constructor to inject mocks
        broadcastReceiver = new HyBidInterstitialBroadcastReceiver(broadcastId, mockLocalBroadcastManager, new IntentFilter());
        broadcastReceiver.setListener(mockListener);
    }

    // --- Action Enum Tests ---

    @Test
    public void action_from_withValidAction_returnsCorrectEnum() {
        // Test that the from() method correctly maps a string to an enum
        HyBidInterstitialBroadcastReceiver.Action result = HyBidInterstitialBroadcastReceiver.Action.from("net.pubnative.hybid.interstitial.show");
        assertEquals(HyBidInterstitialBroadcastReceiver.Action.SHOW, result);
    }

    @Test
    public void action_from_withInvalidAction_returnsNone() {
        // Test the fallback case for the from() method
        HyBidInterstitialBroadcastReceiver.Action result = HyBidInterstitialBroadcastReceiver.Action.from("some.invalid.action");
        assertEquals(HyBidInterstitialBroadcastReceiver.Action.NONE, result);
    }

    // --- Lifecycle Tests (register/destroy) ---

    @Test
    public void register_whenNotDestroyed_registersReceiver() {
        broadcastReceiver.register();
        verify(mockLocalBroadcastManager).registerReceiver(eq(broadcastReceiver), any(IntentFilter.class));
    }

    @Test
    public void destroy_unregistersReceiver() {
        broadcastReceiver.destroy();
        verify(mockLocalBroadcastManager).unregisterReceiver(broadcastReceiver);
    }

    @Test
    public void register_whenDestroyed_doesNothing() {
        broadcastReceiver.destroy();
        broadcastReceiver.register();
        // Verify register was never called after destroy
        verify(mockLocalBroadcastManager, never()).registerReceiver(any(BroadcastReceiver.class), any(IntentFilter.class));
    }

    // --- onReceive Tests ---

    @Test
    public void onReceive_withCorrectBroadcastId_notifiesListener() {
        Intent intent = new Intent(HyBidInterstitialBroadcastReceiver.Action.CLICK.getId());
        Bundle extras = new Bundle();
        extras.putLong(HyBidInterstitialBroadcastReceiver.BROADCAST_ID, broadcastId);
        intent.putExtras(extras);

        broadcastReceiver.onReceive(context, intent);

        // 1. Create an ArgumentCaptor for the Bundle.
        ArgumentCaptor<Bundle> bundleCaptor = ArgumentCaptor.forClass(Bundle.class);

        // 2. Use the captor in the verify() call.
        //    Note: When using a matcher like a captor, all arguments must have matchers (e.g., eq()).
        verify(mockListener).onReceivedAction(eq(HyBidInterstitialBroadcastReceiver.Action.CLICK), bundleCaptor.capture());

        // 3. Get the captured Bundle and assert its contents.
        Bundle capturedBundle = bundleCaptor.getValue();
        assertNotNull(capturedBundle);
        assertEquals(broadcastId, capturedBundle.getLong(HyBidInterstitialBroadcastReceiver.BROADCAST_ID));
    }

    @Test
    public void onReceive_withIncorrectBroadcastId_doesNothing() {
        Intent intent = new Intent(HyBidInterstitialBroadcastReceiver.Action.CLICK.getId());
        intent.putExtra(HyBidInterstitialBroadcastReceiver.BROADCAST_ID, 99999L); // Different ID

        broadcastReceiver.onReceive(context, intent);

        verify(mockListener, never()).onReceivedAction(any(), any());
    }

    @Test
    public void onReceive_whenListenerIsNull_doesNothing() {
        broadcastReceiver.setListener(null);
        Intent intent = new Intent(HyBidInterstitialBroadcastReceiver.Action.CLICK.getId());
        intent.putExtra(HyBidInterstitialBroadcastReceiver.BROADCAST_ID, broadcastId);

        // Should not throw a NullPointerException
        broadcastReceiver.onReceive(context, intent);
    }

    // --- handleAction Tests ---

    @Test
    public void handleAction_withNullListener_doesNothing() {
        // Should not throw a NullPointerException
        broadcastReceiver.handleAction(HyBidInterstitialBroadcastReceiver.Action.SHOW, null, mockPresenter, null, mockVideoListener, mockCustomEndCardListener);
    }

    @Test
    public void handleAction_forPresenterEvents_callsCorrectListener() {
        broadcastReceiver.handleAction(HyBidInterstitialBroadcastReceiver.Action.SHOW, null, mockPresenter, mockPresenterListener);
        verify(mockPresenterListener).onInterstitialShown(mockPresenter);

        broadcastReceiver.handleAction(HyBidInterstitialBroadcastReceiver.Action.CLICK, null, mockPresenter, mockPresenterListener);
        verify(mockPresenterListener).onInterstitialClicked(mockPresenter);

        broadcastReceiver.handleAction(HyBidInterstitialBroadcastReceiver.Action.DISMISS, null, mockPresenter, mockPresenterListener);
        verify(mockPresenterListener).onInterstitialDismissed(mockPresenter);

        broadcastReceiver.handleAction(HyBidInterstitialBroadcastReceiver.Action.ERROR, null, mockPresenter, mockPresenterListener);
        verify(mockPresenterListener).onInterstitialError(mockPresenter);
    }

    @Test
    public void handleAction_forVideoEvents_callsCorrectListener() {
        broadcastReceiver.handleAction(HyBidInterstitialBroadcastReceiver.Action.VIDEO_START, null, mockPresenter, mockPresenterListener, mockVideoListener, null);
        verify(mockVideoListener).onVideoStarted();

        broadcastReceiver.handleAction(HyBidInterstitialBroadcastReceiver.Action.VIDEO_FINISH, null, mockPresenter, mockPresenterListener, mockVideoListener, null);
        verify(mockVideoListener).onVideoFinished();
    }

    @Test
    public void handleAction_forVideoDismiss_withExtras_providesProgress() {
        Bundle extras = new Bundle();
        extras.putInt(HyBidInterstitialBroadcastReceiver.VIDEO_PROGRESS, 50);

        broadcastReceiver.handleAction(HyBidInterstitialBroadcastReceiver.Action.VIDEO_DISMISS, extras, mockPresenter, mockPresenterListener, mockVideoListener, null);
        verify(mockVideoListener).onVideoDismissed(50);
    }

    @Test
    public void handleAction_forVideoDismiss_withNullExtras_providesDefaultProgress() {
        broadcastReceiver.handleAction(HyBidInterstitialBroadcastReceiver.Action.VIDEO_DISMISS, null, mockPresenter, mockPresenterListener, mockVideoListener, null);
        verify(mockVideoListener).onVideoDismissed(-1);
    }
}