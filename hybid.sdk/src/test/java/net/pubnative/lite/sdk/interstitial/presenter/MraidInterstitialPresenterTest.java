package net.pubnative.lite.sdk.interstitial.presenter;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;

import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.interstitial.activity.HyBidInterstitialActivity;
import net.pubnative.lite.sdk.interstitial.activity.MraidInterstitialActivity;
import net.pubnative.lite.sdk.models.Ad;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.junit.Test;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowApplication;

import java.lang.reflect.Field;

/**
 * Created by shubhamkeshri on 06.10.25.
 */

@RunWith(RobolectricTestRunner.class)
public class MraidInterstitialPresenterTest {

    private MraidInterstitialPresenter presenter;
    private Application application;

    @Mock
    private Context mockContext;
    @Mock
    private Ad mockAd;
    @Mock
    private HyBidInterstitialBroadcastReceiver mockReceiver;
    @Mock
    MraidInterstitialPresenter.Listener mockListener;
    @Captor
    private ArgumentCaptor<Intent> intentCaptor;

    @Before
    public void setUp() {
        openMocks(this);
        application = ApplicationProvider.getApplicationContext();

        when(mockContext.getApplicationContext()).thenReturn(application);

        presenter = new MraidInterstitialPresenter(mockContext, mockAd, "zone123", 15, null);

        replacePrivateVariableWithMock("mBroadcastReceiver", mockReceiver);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testConstructor_WithValidContext() {
        assertNotNull(presenter.getAd());
        assertEquals(mockAd, presenter.getAd());
    }

    @Test
    public void testLoad_SetsReadyAndNotifiesListener() {
        presenter.setListener(mockListener);
        presenter.load();

        assertTrue(presenter.isReady());
        verify(mockListener).onInterstitialLoaded(presenter);
    }

    @Test
    public void testDestroy_ResetsState_AndAlsoDestroyBroadcastReceiver() {
        presenter.setListener(mockListener);
        presenter.load();
        presenter.destroy();

        assertFalse(presenter.isReady());
        verify(mockReceiver).destroy();
    }

    @Test
    public void testGetPlacementParams_ReturnsNull() {
        JSONObject params = presenter.getPlacementParams();
        assertNull(params);
    }

    @Test
    public void testShow_LaunchesActivityWithCorrectIntent() {
        presenter.show();

        verify(mockReceiver).register();
        verify(mockContext).startActivity(intentCaptor.capture());

        Intent startedIntent = intentCaptor.getValue();
        assertNotNull(startedIntent);

        assertEquals(MraidInterstitialActivity.class.getName(), startedIntent.getComponent().getClassName());
        assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, startedIntent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK);
        assertEquals(15, startedIntent.getIntExtra(HyBidInterstitialActivity.EXTRA_SKIP_OFFSET, -1));
        assertEquals("zone123", startedIntent.getStringExtra(HyBidInterstitialActivity.EXTRA_ZONE_ID));
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
    public void testOnReceivedAction_CallsBroadcastReceiver() {
        HyBidInterstitialBroadcastReceiver.Action mockAction = HyBidInterstitialBroadcastReceiver.Action.SHOW;
        Bundle bundle = new Bundle();
        presenter.setListener(mockListener);

        presenter.onReceivedAction(mockAction, bundle);

        verify(mockReceiver).handleAction(eq(mockAction), eq(bundle), eq(presenter), eq(mockListener));
    }

    @Test
    public void testShow_IncludesWatermark_WhenProvided() {
        // create presenter with non-empty watermark
        presenter = new MraidInterstitialPresenter(mockContext, mockAd, "zone123", 15, "watermarkMraidInterstitial");
        replacePrivateVariableWithMock("mBroadcastReceiver", mockReceiver);

        presenter.show();

        verify(mockReceiver).register();
        verify(mockContext).startActivity(intentCaptor.capture());

        Intent startedIntent = intentCaptor.getValue();
        assertNotNull(startedIntent);

        assertEquals(MraidInterstitialActivity.class.getName(), startedIntent.getComponent().getClassName());
        assertEquals("watermarkMraidInterstitial", startedIntent.getStringExtra(HyBidInterstitialActivity.EXTRA_WATERMARK_DATA));
    }

    private void replacePrivateVariableWithMock(String fieldName, Object b) {
        try {
            Class<?> runnerClass = Class.forName("net.pubnative.lite.sdk.interstitial.presenter.MraidInterstitialPresenter");
            assertNotNull(runnerClass);

            Field field = MraidInterstitialPresenter.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(presenter, b);
        } catch (Exception e) {
            fail("Reflection failed: " + e);
        }
    }
}
