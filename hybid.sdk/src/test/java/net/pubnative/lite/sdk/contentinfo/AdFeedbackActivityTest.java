// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.contentinfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import net.pubnative.lite.sdk.mraid.MRAIDInterstitial;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.utils.URLValidator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

import java.time.Duration;
import android.os.Looper;


@RunWith(RobolectricTestRunner.class)
public class AdFeedbackActivityTest {

    private static final String VALID_URL = "https://example.com/feedback";

    // Helper to create a valid intent for launching the activity
    private Intent createValidIntent(Context context, ResultReceiver receiver) {
        Intent intent = new Intent(context, AdFeedbackActivity.class);
        intent.putExtra(AdFeedbackActivity.EXTRA_FEEDBACK_FORM_URL, VALID_URL);
        intent.putExtra(AdFeedbackActivity.EXTRA_FEEDBACK_FORM_DATA, new AdFeedbackData.Builder().build());
        intent.putExtra(AdFeedbackActivity.EXTRA_FEEDBACK_FORM_CALLBACK, receiver);
        return intent;
    }

    @Test
    public void onCreate_withMissingUrl_sendsErrorAndFinishes() {
        Context context = ApplicationProvider.getApplicationContext();
        ResultReceiver mockReceiver = mock(ResultReceiver.class);
        Intent intent = new Intent(context, AdFeedbackActivity.class);
        intent.putExtra(AdFeedbackActivity.EXTRA_FEEDBACK_FORM_DATA, new AdFeedbackData.Builder().build());
        intent.putExtra(AdFeedbackActivity.EXTRA_FEEDBACK_FORM_CALLBACK, mockReceiver);

        try (ActivityScenario<AdFeedbackActivity> scenario = ActivityScenario.launch(intent)) {
            assertEquals(Lifecycle.State.DESTROYED, scenario.getState());
        }
        // Verify the error was sent at least once, accommodating the double call.
        verify(mockReceiver, atLeastOnce()).send(AdFeedbackFormHelper.FeedbackFormAction.ERROR.code, null);
    }

    @Test
    public void onCreate_withMissingData_sendsErrorAndFinishes() {
        Context context = ApplicationProvider.getApplicationContext();
        ResultReceiver mockReceiver = mock(ResultReceiver.class);
        Intent intent = new Intent(context, AdFeedbackActivity.class);
        intent.putExtra(AdFeedbackActivity.EXTRA_FEEDBACK_FORM_URL, VALID_URL);
        intent.putExtra(AdFeedbackActivity.EXTRA_FEEDBACK_FORM_CALLBACK, mockReceiver);

        try (ActivityScenario<AdFeedbackActivity> scenario = ActivityScenario.launch(intent)) {
            assertEquals(Lifecycle.State.DESTROYED, scenario.getState());
        }
        // Verify the error was sent at least once.
        verify(mockReceiver, atLeastOnce()).send(AdFeedbackFormHelper.FeedbackFormAction.ERROR.code, null);
    }

    @Test
    public void mraidViewLoaded_withValidUrl_showsContainerAndSendsOpenAction() {
        ResultReceiver mockReceiver = mock(ResultReceiver.class);
        Intent intent = createValidIntent(ApplicationProvider.getApplicationContext(), mockReceiver);

        try (MockedConstruction<MRAIDInterstitial> mockedInterstitial = mockConstruction(MRAIDInterstitial.class);
             MockedStatic<URLValidator> mockedValidator = mockStatic(URLValidator.class)) {

            when(URLValidator.isValidURL(VALID_URL)).thenReturn(true);

            try (ActivityScenario<AdFeedbackActivity> scenario = ActivityScenario.launch(intent)) {
                MRAIDInterstitial interstitial = mockedInterstitial.constructed().get(0);

                // Simulate the MRAID view loading successfully by calling the method on the activity
                scenario.onActivity(activity -> activity.mraidViewLoaded(mock(MRAIDView.class)));

                verify(mockReceiver).send(AdFeedbackFormHelper.FeedbackFormAction.OPEN.code, null);
                verify(interstitial).show(any(Activity.class), any(), anyString());
            }
        }
    }

    @Test
    public void mraidViewError_sendsErrorAndFinishes() {
        ResultReceiver mockReceiver = mock(ResultReceiver.class);
        Intent intent = createValidIntent(ApplicationProvider.getApplicationContext(), mockReceiver);

        try (ActivityScenario<AdFeedbackActivity> scenario = ActivityScenario.launch(intent)) {
            // Simulate an MRAID error
            scenario.onActivity(activity -> activity.mraidViewError(mock(MRAIDView.class)));

            verify(mockReceiver).send(AdFeedbackFormHelper.FeedbackFormAction.ERROR.code, null);
            scenario.onActivity(activity -> assertTrue(activity.isFinishing()));
        }
    }

    @Test
    public void mraidViewClose_finishesActivity() {
        Intent intent = createValidIntent(ApplicationProvider.getApplicationContext(), mock(ResultReceiver.class));
        try (ActivityScenario<AdFeedbackActivity> scenario = ActivityScenario.launch(intent)) {
            // Simulate the MRAID close event
            scenario.onActivity(activity -> activity.mraidViewClose(mock(MRAIDView.class)));
            scenario.onActivity(activity -> assertTrue(activity.isFinishing()));
        }
    }

    @Test
    public void feedbackTimer_onFinish_finishesActivity() {
        Intent intent = createValidIntent(ApplicationProvider.getApplicationContext(), mock(ResultReceiver.class));
        try (ActivityScenario<AdFeedbackActivity> scenario = ActivityScenario.launch(intent)) {
            // Advance the clock past the 10-second timeout
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(11));

            scenario.onActivity(activity -> assertTrue(activity.isFinishing()));
        }
    }

    @Test
    public void onDestroy_sendsCloseAction() {
        ResultReceiver mockReceiver = mock(ResultReceiver.class);
        Intent intent = createValidIntent(ApplicationProvider.getApplicationContext(), mockReceiver);
        try (ActivityScenario<AdFeedbackActivity> scenario = ActivityScenario.launch(intent)) {
            // Closing the scenario triggers onDestroy
        }
        verify(mockReceiver).send(AdFeedbackFormHelper.FeedbackFormAction.CLOSE.code, null);
    }
}