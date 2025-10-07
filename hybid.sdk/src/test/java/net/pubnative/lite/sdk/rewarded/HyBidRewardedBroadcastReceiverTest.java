// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded;

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
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.rewarded.presenter.RewardedPresenter;
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
public class HyBidRewardedBroadcastReceiverTest {

    @Mock
    private PNLocalBroadcastManager mockLocalBroadcastManager;
    @Mock
    private HyBidRewardedBroadcastReceiver.Listener mockListener;
    @Mock
    private RewardedPresenter mockPresenter;
    @Mock
    private RewardedPresenter.Listener mockPresenterListener;
    @Mock
    private VideoListener mockVideoListener;
    @Mock
    private CustomEndCardListener mockCustomEndCardListener;

    private Context context;
    private final long broadcastId = 12345L;
    private HyBidRewardedBroadcastReceiver broadcastReceiver;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();
        broadcastReceiver = new HyBidRewardedBroadcastReceiver(broadcastId, mockLocalBroadcastManager, new IntentFilter());
        broadcastReceiver.setListener(mockListener);
    }

    // --- Action Enum Tests ---

    @Test
    public void action_from_withValidAction_returnsCorrectEnum() {
        HyBidRewardedBroadcastReceiver.Action result = HyBidRewardedBroadcastReceiver.Action.from("net.pubnative.hybid.rewarded.open");
        assertEquals(HyBidRewardedBroadcastReceiver.Action.OPEN, result);
    }

    @Test
    public void action_from_withInvalidAction_returnsNone() {
        HyBidRewardedBroadcastReceiver.Action result = HyBidRewardedBroadcastReceiver.Action.from("some.invalid.action");
        assertEquals(HyBidRewardedBroadcastReceiver.Action.NONE, result);
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
        verify(mockLocalBroadcastManager, never()).registerReceiver(any(BroadcastReceiver.class), any(IntentFilter.class));
    }

    // --- onReceive Tests ---

    @Test
    public void onReceive_withCorrectBroadcastId_notifiesListener() {
        Intent intent = new Intent(HyBidRewardedBroadcastReceiver.Action.CLICK.getId());
        Bundle extras = new Bundle();
        extras.putLong(HyBidRewardedBroadcastReceiver.BROADCAST_ID, broadcastId);
        intent.putExtras(extras);

        broadcastReceiver.onReceive(context, intent);

        ArgumentCaptor<Bundle> bundleCaptor = ArgumentCaptor.forClass(Bundle.class);
        verify(mockListener).onReceivedAction(eq(HyBidRewardedBroadcastReceiver.Action.CLICK), bundleCaptor.capture());

        Bundle capturedBundle = bundleCaptor.getValue();
        assertNotNull(capturedBundle);
        assertEquals(broadcastId, capturedBundle.getLong(HyBidRewardedBroadcastReceiver.BROADCAST_ID));
    }

    @Test
    public void onReceive_withIncorrectBroadcastId_doesNothing() {
        Intent intent = new Intent(HyBidRewardedBroadcastReceiver.Action.CLICK.getId());
        intent.putExtra(HyBidRewardedBroadcastReceiver.BROADCAST_ID, 99999L); // Different ID

        broadcastReceiver.onReceive(context, intent);

        verify(mockListener, never()).onReceivedAction(any(), any());
    }

    // --- handleAction Tests ---

    @Test
    public void handleAction_withNullListener_doesNothing() {
        broadcastReceiver.handleAction(HyBidRewardedBroadcastReceiver.Action.OPEN, mockPresenter, null, null, mockVideoListener, mockCustomEndCardListener);
        // No exception should be thrown
    }

    @Test
    public void handleAction_forPresenterEvents_callsCorrectListener() {
        broadcastReceiver.handleAction(HyBidRewardedBroadcastReceiver.Action.OPEN, mockPresenter, null, mockPresenterListener, null, null);
        verify(mockPresenterListener).onRewardedOpened(mockPresenter);

        broadcastReceiver.handleAction(HyBidRewardedBroadcastReceiver.Action.CLICK, mockPresenter, null, mockPresenterListener, null, null);
        verify(mockPresenterListener).onRewardedClicked(mockPresenter);

        broadcastReceiver.handleAction(HyBidRewardedBroadcastReceiver.Action.ERROR, mockPresenter, null, mockPresenterListener, null, null);
        verify(mockPresenterListener).onRewardedError(mockPresenter);
    }

    @Test
    public void handleAction_forClose_callsFinishedAndClosedListeners() {
        // Test the special case for CLOSE action
        broadcastReceiver.handleAction(HyBidRewardedBroadcastReceiver.Action.CLOSE, mockPresenter, null, mockPresenterListener, null, null);
        verify(mockPresenterListener).onRewardedFinished(mockPresenter);
        verify(mockPresenterListener).onRewardedClosed(mockPresenter);
    }

    @Test
    public void handleAction_forVideoEvents_callsCorrectListener() {
        broadcastReceiver.handleAction(HyBidRewardedBroadcastReceiver.Action.VIDEO_START, mockPresenter, null, mockPresenterListener, mockVideoListener, null);
        verify(mockVideoListener).onVideoStarted();

        broadcastReceiver.handleAction(HyBidRewardedBroadcastReceiver.Action.VIDEO_FINISH, mockPresenter, null, mockPresenterListener, mockVideoListener, null);
        verify(mockVideoListener).onVideoFinished();
    }

    @Test
    public void handleAction_forVideoDismiss_withExtras_providesProgress() {
        Bundle extras = new Bundle();
        extras.putInt(HyBidRewardedBroadcastReceiver.VIDEO_PROGRESS, 75);

        broadcastReceiver.handleAction(HyBidRewardedBroadcastReceiver.Action.VIDEO_DISMISS, mockPresenter, extras, mockPresenterListener, mockVideoListener, null);
        verify(mockVideoListener).onVideoDismissed(75);
    }

    @Test
    public void handleAction_forEndCardLoadSuccess_withExtras_providesFlag() {
        Bundle extras = new Bundle();
        extras.putBoolean(Reporting.Key.IS_CUSTOM_END_CARD, true);

        broadcastReceiver.handleAction(HyBidRewardedBroadcastReceiver.Action.END_CARD_LOAD_SUCCESS, mockPresenter, extras, mockPresenterListener, null, mockCustomEndCardListener);
        verify(mockCustomEndCardListener).onEndCardLoadSuccess(true);
    }
}