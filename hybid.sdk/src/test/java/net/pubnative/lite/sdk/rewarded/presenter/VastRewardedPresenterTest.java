// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.presenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;


import net.pubnative.lite.sdk.CustomEndCardListener;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastReceiver;
import net.pubnative.lite.sdk.rewarded.activity.HyBidRewardedActivity;
import net.pubnative.lite.sdk.rewarded.activity.VastRewardedActivity;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowApplication;

import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
public class VastRewardedPresenterTest {

    private VastRewardedPresenter presenter;
    private Application application;

    @Mock
    private Context mockContext;
    @Mock
    private Ad mockAd;
    @Mock
    private VastRewardedPresenter.Listener mockListener;
    @Mock
    private VideoListener mockVideoListener;
    @Mock
    private CustomEndCardListener mockEndCardListener;
    @Mock
    private HyBidRewardedBroadcastReceiver mockReceiver;
    @Captor
    private ArgumentCaptor<Intent> intentCaptor;


    @Before
    public void setUp() {
        openMocks(this);
        application = ApplicationProvider.getApplicationContext();

        when(mockContext.getApplicationContext()).thenReturn(application);

        presenter = new VastRewardedPresenter(mockContext, mockAd, "zone123", IntegrationType.STANDALONE, null);

        replacePrivateVariableWithMock("mBroadcastReceiver", mockReceiver);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testConstructorWithNullContextDoesNotCrash() {
        VastRewardedPresenter p = new VastRewardedPresenter(null, mockAd, "zone", IntegrationType.STANDALONE, null);
        assertNotNull(p); // Should not throw
    }

    @Test
    public void testSetters() {
        presenter.setListener(mockListener);
        presenter.setVideoListener(mockVideoListener);
        presenter.setCustomEndCardListener(mockEndCardListener);


        // Load should call onRewardedLoaded
        presenter.load();
        verify(mockListener).onRewardedLoaded(presenter);
        assertTrue(presenter.isReady());
    }

    @Test
    public void testLoadWhenDestroyedDoesNotNotifyListener() {
        presenter.destroy();
        presenter.setListener(mockListener);
        presenter.load();
        verify(mockListener, never()).onRewardedLoaded(any());
        assertFalse(presenter.isReady());
    }

    @Test
    public void testGetAdReturnsAd() {
        assertEquals(mockAd, presenter.getAd());
    }

    @Test
    public void testGetPlacementParamsReturnsNull() {
        JSONObject params = presenter.getPlacementParams();
        assertNull(params);
    }

    @Test
    public void testShowRegistersReceiverAndStartsActivity() {
        presenter.show();

        verify(mockReceiver).register();
        verify(mockContext).startActivity(intentCaptor.capture());

        Intent intent = intentCaptor.getValue();
        assertEquals(VastRewardedActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK);
        assertEquals("zone123", intent.getStringExtra(HyBidRewardedActivity.EXTRA_ZONE_ID));
        assertEquals(IntegrationType.STANDALONE.getCode(), intent.getStringExtra(HyBidRewardedActivity.INTEGRATION_TYPE));
    }

    @Test
    public void testShow_WithNullBroadCastReceiver_LaunchesNoActivity() {
        replacePrivateVariableWithMock("mBroadcastReceiver", null);

        presenter.show();

        // As 'mBroadcastReceiver' is set as null via Reflection - No Activity will launch and Obviously No Interaction with Broadcast Receiver.
        ShadowApplication shadowApp = Shadows.shadowOf(application);
        Intent startedIntent = shadowApp.getNextStartedActivity();
        assertNull(startedIntent);

        verifyNoInteractions(mockReceiver);
    }

    @Test
    public void testDestroyResetsState() {
        presenter.setListener(mockListener);
        presenter.load();
        assertTrue(presenter.isReady());

        presenter.destroy();
        verify(mockReceiver).destroy();
        assertFalse(presenter.isReady());
    }

    @Test
    public void testOnReceivedActionDelegatesToReceiver() {
        Bundle extras = new Bundle();
        HyBidRewardedBroadcastReceiver.Action action = HyBidRewardedBroadcastReceiver.Action.CLICK;

        presenter.setListener(mockListener);
        presenter.setVideoListener(mockVideoListener);
        presenter.setCustomEndCardListener(mockEndCardListener);

        presenter.onReceivedAction(action, extras);

        verify(mockReceiver).handleAction(eq(action), eq(presenter), eq(extras), eq(mockListener),
                eq(presenter), eq(mockEndCardListener));
    }

    @Test
    public void testShow_IncludesWatermark_WhenProvided() {
        // create presenter with non-empty watermark
        presenter = new VastRewardedPresenter(mockContext, mockAd, "zone123", IntegrationType.STANDALONE, "watermarkVastRewarded");
        replacePrivateVariableWithMock("mBroadcastReceiver", mockReceiver);

        presenter.show();

        verify(mockReceiver).register();
        verify(mockContext).startActivity(intentCaptor.capture());

        Intent intent = intentCaptor.getValue();
        assertNotNull(intent);
        assertNotNull(intent.getComponent());

        assertEquals(VastRewardedActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals("watermarkVastRewarded", intent.getStringExtra(HyBidRewardedActivity.EXTRA_WATERMARK_DATA));
    }

    private void replacePrivateVariableWithMock(String fieldName, Object b) {
        try {
            Class<?> runnerClass = Class.forName("net.pubnative.lite.sdk.rewarded.presenter.VastRewardedPresenter");
            assertNotNull(runnerClass);

            Field field = VastRewardedPresenter.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(presenter, b);
        } catch (Exception ignore) {
        }
    }
}
