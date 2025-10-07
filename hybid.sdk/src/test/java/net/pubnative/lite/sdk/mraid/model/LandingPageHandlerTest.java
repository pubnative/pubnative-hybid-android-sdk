// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.mraid.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.util.Base64;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class LandingPageHandlerTest {

    @Mock
    private Ad mockAd;
    @Mock
    private LandingPageHandler.LandingPageCallback mockCallback;

    private LandingPageHandler landingPageHandler;
    private MockedStatic<Logger> mockedLogger;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockedLogger = Mockito.mockStatic(Logger.class);
    }

    @After
    public void tearDown() {
        mockedLogger.close();
    }

    // --- Constructor and State Tests ---

    @Test
    public void isLandingPageEnabled_whenAdReturnsTrue_returnsTrue() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);
        assertTrue(landingPageHandler.isLandingPageEnabled());
    }

    @Test
    public void isLandingPageEnabled_whenAdReturnsFalse_returnsFalse() {
        when(mockAd.isLandingPage()).thenReturn(false);
        landingPageHandler = new LandingPageHandler(mockAd);
        assertFalse(landingPageHandler.isLandingPageEnabled());
    }

    @Test
    public void isLandingPageEnabled_whenAdReturnsNull_returnsFalse() {
        when(mockAd.isLandingPage()).thenReturn(null);
        landingPageHandler = new LandingPageHandler(mockAd);
        assertFalse(landingPageHandler.isLandingPageEnabled());
    }

    @Test
    public void getUpdatedDelay_adjustsDelayCorrectly() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);

        // Initial delay is 30000
        assertEquals(29000, landingPageHandler.getUpdatedDelay());
        // After one adjustment, it's 29000
        assertEquals(28000, landingPageHandler.getUpdatedDelay());
    }

    // --- URL Parsing Tests ---

    @Test
    public void parseAdExperienceUrl_forSetCustomisation_setsStringAndCallsCallback() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);
        landingPageHandler.setCallback(mockCallback);

        String customText = "{\"key\":\"value\"}";
        String base64Text = Base64.encodeToString(customText.getBytes(), Base64.NO_WRAP);
        String commandUrl = "verveadexperience://setcustomisation?text=" + base64Text;

        landingPageHandler.parseAdExperienceUrl(commandUrl);

        assertEquals(customText, landingPageHandler.getCustomisationString());
        verify(mockCallback).setLandingPageUseCustomClose(false);
        verify(mockCallback).setLandingPageSkipTimer();
    }

    @Test
    public void parseAdExperienceUrl_forCloseDelay_setsValidDelay() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);

        String delay = "15000";
        String base64Text = Base64.encodeToString(delay.getBytes(), Base64.NO_WRAP);
        String commandUrl = "verveadexperience://closedelay?text=" + base64Text;

        landingPageHandler.parseAdExperienceUrl(commandUrl);

        assertEquals(Integer.valueOf(15000), landingPageHandler.getLandingPageDelay());
    }

    @Test
    public void parseAdExperienceUrl_forCloseDelay_withOversizedValue_setsMaxDelay() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);

        String delay = "40000"; // > 30000 max
        String base64Text = Base64.encodeToString(delay.getBytes(), Base64.DEFAULT);
        String commandUrl = "verveadexperience://closedelay?text=" + base64Text;

        landingPageHandler.parseAdExperienceUrl(commandUrl);

        assertEquals(Integer.valueOf(30000), landingPageHandler.getLandingPageDelay());
    }

    @Test
    public void parseAdExperienceUrl_forCloseDelay_withInvalidNumber_doesNotChangeDelay() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);
        Integer initialDelay = landingPageHandler.getLandingPageDelay();

        String delay = "not_a_number";
        String base64Text = Base64.encodeToString(delay.getBytes(), Base64.DEFAULT);
        String commandUrl = "verveadexperience://closedelay?text=" + base64Text;

        landingPageHandler.parseAdExperienceUrl(commandUrl);

        assertEquals(initialDelay, landingPageHandler.getLandingPageDelay());
    }

    // --- Behavior Handling Tests (Implicitly tests handleLandingPageBehavior) ---

    @Test
    public void parseAdExperienceUrl_forFinalPage_withBehaviorIC_cancelsBehavior() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);
        landingPageHandler.setCallback(mockCallback);
        landingPageHandler.setLandingBehaviourString("ic");

        landingPageHandler.parseAdExperienceUrl("verveadexperience://setfinalpage");

        assertTrue(landingPageHandler.isFinalPage());
        verify(mockCallback).cancelLandingPageBehaviour();
    }

    @Test
    public void parseAdExperienceUrl_forFinalPage_withBehaviorC_showsTimer() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);
        landingPageHandler.setCallback(mockCallback);
        landingPageHandler.setLandingBehaviourString("c");

        landingPageHandler.parseAdExperienceUrl("verveadexperience://setfinalpage");

        assertTrue(landingPageHandler.isFinalPage());

        verify(mockCallback).showCountDownTimer();
    }

    @Test
    public void parseAdExperienceUrl_forFinalPage_withBehaviorNC_hidesTimer() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);
        landingPageHandler.setCallback(mockCallback);
        landingPageHandler.setLandingBehaviourString("nc");

        landingPageHandler.parseAdExperienceUrl("verveadexperience://setfinalpage");

        assertTrue(landingPageHandler.isFinalPage());
        verify(mockCallback).hideCountDownTimer();
    }

    @Test
    public void parseAdExperienceUrl_forFinalPage_withNullBehavior_showsTimer() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);
        landingPageHandler.setCallback(mockCallback);
        landingPageHandler.setLandingBehaviourString(null);

        landingPageHandler.parseAdExperienceUrl("verveadexperience://setfinalpage");

        assertTrue(landingPageHandler.isFinalPage());
        verify(mockCallback).showCountDownTimer();
    }

    @Test
    public void simpleGettersAndSetters_workAsExpected() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);

        landingPageHandler.setCustomisationString("custom");
        assertEquals("custom", landingPageHandler.getCustomisationString());

        landingPageHandler.setLandingBehaviourString("behavior");
        assertEquals("behavior", landingPageHandler.getLandingBehaviourString());

        landingPageHandler.setLandingPageDelay(12345);
        assertEquals(Integer.valueOf(12345), landingPageHandler.getLandingPageDelay());

        landingPageHandler.setIsTimerFinished(true);
        assertTrue(landingPageHandler.isTimerFinished());
    }

    @Test
    public void getUpdatedDelay_whenDelayIsSmall_doesNotAdjust() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);
        landingPageHandler.setLandingPageDelay(500);

        assertEquals(500, landingPageHandler.getUpdatedDelay());
        assertEquals(Integer.valueOf(500), landingPageHandler.getLandingPageDelay());
    }

    @Test
    public void parseAdExperienceUrl_forCloseDelay_withNegativeValue_setsMaxDelay() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);
        String delay = "-100";
        String base64Text = Base64.encodeToString(delay.getBytes(), Base64.NO_WRAP);
        String commandUrl = "verveadexperience://closedelay?text=" + base64Text;
        landingPageHandler.parseAdExperienceUrl(commandUrl);
        assertEquals(Integer.valueOf(30000), landingPageHandler.getLandingPageDelay());
    }

    @Test
    public void parseAdExperienceUrl_forLandingBehaviour_setsString() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);
        String behavior = "c";
        String base64Text = Base64.encodeToString(behavior.getBytes(), Base64.NO_WRAP);
        String commandUrl = "verveadexperience://landingbehaviour?text=" + base64Text;

        landingPageHandler.parseAdExperienceUrl(commandUrl);

        assertEquals(behavior, landingPageHandler.getLandingBehaviourString());
    }

    @Test
    public void parseAdExperienceUrl_withEmptyCustomisation_doesNotCallCallback() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);
        landingPageHandler.setCallback(mockCallback);

        String customText = "";
        String base64Text = Base64.encodeToString(customText.getBytes(), Base64.NO_WRAP);
        String commandUrl = "verveadexperience://setcustomisation?text=" + base64Text;

        landingPageHandler.parseAdExperienceUrl(commandUrl);

        assertNull(landingPageHandler.getCustomisationString());

        verify(mockCallback, never()).setLandingPageSkipTimer();
    }

    @Test
    public void parseAdExperienceUrl_onNumberFormatException_logsError() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);
        String delay = "not_a_number";
        String base64Text = Base64.encodeToString(delay.getBytes(), Base64.NO_WRAP);
        String commandUrl = "verveadexperience://closedelay?text=" + base64Text;

        landingPageHandler.parseAdExperienceUrl(commandUrl);

        mockedLogger.verify(() -> Logger.d(anyString(), anyString()));
    }

    @Test
    public void parseAdExperienceUrl_onRuntimeException_logsError() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);
        String commandUrl = "verveadexperience://setcustomisation?text=invalid_base64";

        landingPageHandler.parseAdExperienceUrl(commandUrl);

        mockedLogger.verify(() -> Logger.d(anyString(), anyString()));
    }

    @Test
    public void handleLandingPageBehavior_withBehaviorCAndTimerFinished_doesNotShowTimer() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);
        landingPageHandler.setCallback(mockCallback);
        landingPageHandler.setLandingBehaviourString("c");
        landingPageHandler.setIsTimerFinished(true);

        landingPageHandler.parseAdExperienceUrl("verveadexperience://setfinalpage");

        verify(mockCallback, never()).showCountDownTimer();
    }

    @Test
    public void handleLandingPageBehavior_withNullCallback_doesNotCrash() {
        when(mockAd.isLandingPage()).thenReturn(true);
        landingPageHandler = new LandingPageHandler(mockAd);
        landingPageHandler.setCallback(null);
        landingPageHandler.setLandingBehaviourString("c");

        landingPageHandler.parseAdExperienceUrl("verveadexperience://setfinalpage");
    }
}