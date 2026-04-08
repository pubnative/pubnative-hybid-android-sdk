// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.presenter;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;

import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastReceiver;
import net.pubnative.lite.sdk.rewarded.activity.HyBidRewardedActivity;
import net.pubnative.lite.sdk.rewarded.activity.MraidRewardedActivity;
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

@RunWith(RobolectricTestRunner.class)
public class MraidRewardedPresenterTest {

    private MraidRewardedPresenter presenter;
    private Application application;

    @Mock
    private Context mockContext;
    @Mock
    private Ad mockAd;
    @Mock
    private HyBidRewardedBroadcastReceiver mockReceiver;
    @Mock
    MraidRewardedPresenter.Listener mockListener;
    @Captor
    private ArgumentCaptor<Intent> intentCaptor;

    @Before
    public void setUp() {
        openMocks(this);
        application = ApplicationProvider.getApplicationContext();

        when(mockContext.getApplicationContext()).thenReturn(application);

        presenter = new MraidRewardedPresenter(mockContext, mockAd, "zone123", null);

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
        verify(mockListener).onRewardedLoaded(presenter);
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

        assertEquals(MraidRewardedActivity.class.getName(), startedIntent.getComponent().getClassName());
        assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, startedIntent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK);
        assertEquals("zone123", startedIntent.getStringExtra(HyBidRewardedActivity.EXTRA_ZONE_ID));
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
        HyBidRewardedBroadcastReceiver.Action mockAction = HyBidRewardedBroadcastReceiver.Action.OPEN;
        Bundle bundle = new Bundle();
        presenter.setListener(mockListener);

        presenter.onReceivedAction(mockAction, bundle);

        verify(mockReceiver).handleAction(eq(mockAction), eq(presenter), eq(bundle), eq(mockListener), eq(null), eq(null));
    }

    @Test
    public void testShow_IncludesWatermark_WhenProvided() {
        // create presenter with non-empty watermark
        presenter = new MraidRewardedPresenter(mockContext, mockAd, "zone123", "watermarkMraidRewarded");
        replacePrivateVariableWithMock("mBroadcastReceiver", mockReceiver);

        presenter.show();

        verify(mockReceiver).register();
        verify(mockContext).startActivity(intentCaptor.capture());

        Intent startedIntent = intentCaptor.getValue();
        assertNotNull(startedIntent);

        assertEquals(MraidRewardedActivity.class.getName(), startedIntent.getComponent().getClassName());
        assertEquals("watermarkMraidRewarded", startedIntent.getStringExtra(HyBidRewardedActivity.EXTRA_WATERMARK_DATA));
    }

    private void replacePrivateVariableWithMock(String fieldName, Object b) {
        try {
            Class<?> runnerClass = Class.forName("net.pubnative.lite.sdk.rewarded.presenter.MraidRewardedPresenter");
            assertNotNull(runnerClass);

            Field field = MraidRewardedPresenter.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(presenter, b);
        } catch (Exception e) {
            fail("Reflection failed: " + e);
        }
    }
}
