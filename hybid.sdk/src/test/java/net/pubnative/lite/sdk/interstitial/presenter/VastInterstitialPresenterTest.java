package net.pubnative.lite.sdk.interstitial.presenter;

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
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.interstitial.activity.HyBidInterstitialActivity;
import net.pubnative.lite.sdk.interstitial.activity.VastInterstitialActivity;
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

/**
 * Created by shubhamkeshri on 03.10.25.
 */

@RunWith(RobolectricTestRunner.class)
public class VastInterstitialPresenterTest {

    private VastInterstitialPresenter presenter;
    private Application application;

    @Mock
    private Context mockContext;
    @Mock
    private Ad mockAd;
    @Mock
    private VastInterstitialPresenter.Listener mockListener;
    @Mock
    private VideoListener mockVideoListener;
    @Mock
    private CustomEndCardListener mockEndCardListener;
    @Mock
    private HyBidInterstitialBroadcastReceiver mockReceiver;
    @Captor
    private ArgumentCaptor<Intent> intentCaptor;


    @Before
    public void setUp() {
        openMocks(this);
        application = ApplicationProvider.getApplicationContext();

        when(mockContext.getApplicationContext()).thenReturn(application);

        presenter = new VastInterstitialPresenter(mockContext, mockAd, "zone123", 15, IntegrationType.STANDALONE, null);

        replacePrivateVariableWithMock("mBroadcastReceiver", mockReceiver);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testConstructorWithNullContextDoesNotCrash() {
        VastInterstitialPresenter p = new VastInterstitialPresenter(null, mockAd, "zone", 10, IntegrationType.STANDALONE, null);
        assertNotNull(p); // Should not throw
    }

    @Test
    public void testSetters() {
        presenter.setListener(mockListener);
        presenter.setVideoListener(mockVideoListener);
        presenter.setCustomEndCardListener(mockEndCardListener);

        // Load should call onInterstitialLoaded
        presenter.load();
        verify(mockListener).onInterstitialLoaded(presenter);
        assertTrue(presenter.isReady());
    }

    @Test
    public void testLoadWhenDestroyedDoesNotNotifyListener() {
        presenter.destroy();
        presenter.setListener(mockListener);
        presenter.load();
        verify(mockListener, never()).onInterstitialLoaded(any());
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
        assertEquals(VastInterstitialActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK);
        assertEquals("zone123", intent.getStringExtra(HyBidInterstitialActivity.EXTRA_ZONE_ID));
        assertEquals(15, intent.getIntExtra(HyBidInterstitialActivity.EXTRA_SKIP_OFFSET, -1));
        assertEquals(IntegrationType.STANDALONE.getCode(), intent.getStringExtra(HyBidInterstitialActivity.INTEGRATION_TYPE));
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
        HyBidInterstitialBroadcastReceiver.Action action = HyBidInterstitialBroadcastReceiver.Action.CLICK;

        presenter.setListener(mockListener);
        presenter.setVideoListener(mockVideoListener);
        presenter.setCustomEndCardListener(mockEndCardListener);

        presenter.onReceivedAction(action, extras);

        verify(mockReceiver).handleAction(eq(action), eq(extras), eq(presenter),
                eq(mockListener), eq(mockVideoListener), eq(mockEndCardListener));
    }

    @Test
    public void testShow_IncludesWatermark_WhenProvided() {
        // create presenter with non-empty watermark
        presenter = new VastInterstitialPresenter(mockContext, mockAd, "zone123", 15, IntegrationType.STANDALONE, "watermarkVastInterstitial");
        replacePrivateVariableWithMock("mBroadcastReceiver", mockReceiver);

        presenter.show();

        verify(mockReceiver).register();
        verify(mockContext).startActivity(intentCaptor.capture());

        Intent intent = intentCaptor.getValue();
        assertNotNull(intent);

        assertEquals(VastInterstitialActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals("watermarkVastInterstitial", intent.getStringExtra(HyBidInterstitialActivity.EXTRA_WATERMARK_DATA));
    }

    private void replacePrivateVariableWithMock(String fieldName, Object b) {
        try {
            Class<?> runnerClass = Class.forName("net.pubnative.lite.sdk.interstitial.presenter.VastInterstitialPresenter");
            assertNotNull(runnerClass);

            Field field = VastInterstitialPresenter.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(presenter, b);
        } catch (Exception ignore) {
        }
    }
}
