// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
package net.pubnative.lite.sdk.vpaid.utils;

import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.FrameLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowBuild;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.pubnative.lite.sdk.models.ContentInfo;
import net.pubnative.lite.sdk.models.PositionX;
import net.pubnative.lite.sdk.models.PositionY;
import net.pubnative.lite.sdk.vpaid.models.vast.Icon;
import net.pubnative.lite.sdk.vpaid.models.vast.IconClickThrough;
import net.pubnative.lite.sdk.vpaid.models.vast.IconClicks;
import net.pubnative.lite.sdk.vpaid.models.vast.StaticResource;

// Using Robolectric to test methods with Android framework dependencies
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P) // Targeting a specific SDK version for consistency
public class UtilsTest {

    @Mock
    private Context mockContext;
    @Mock
    private ConnectivityManager mockConnectivityManager;
    @Mock
    private NetworkInfo mockNetworkInfo;
    @Mock
    private AudioManager mockAudioManager;
    @Mock
    private Icon mockIcon;
    @Mock
    private IconClicks mockIconClicks;
    @Mock
    private IconClickThrough mockIconClickThrough;
    @Mock
    private StaticResource mockStaticResource;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        Utils.setDebugMode(true);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    //--------------------------------- isDebug / setDebugMode ---------------------------------//
    @Test
    public void isDebug_whenSetToTrue_returnsTrue() {
        Utils.setDebugMode(true);
        assertTrue(Utils.isDebug());
    }

    @Test
    public void isDebug_whenSetToFalse_returnsFalse() {
        Utils.setDebugMode(false);
        assertFalse(Utils.isDebug());
    }

    //--------------------------------- isOnline ---------------------------------//
    @Test
    public void isOnline_withNullContext_returnsFalse() {
        assertFalse(Utils.isOnline(null));
    }

    @Test
    public void isOnline_whenConnectivityManagerIsNull_returnsFalse() {
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(null);
        assertFalse(Utils.isOnline(mockContext));
    }

    @Test
    public void isOnline_whenActiveNetworkIsNull_returnsFalse() {
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(null);
        assertFalse(Utils.isOnline(mockContext));
    }

    @Test
    public void isOnline_whenNetworkIsNotConnected_returnsFalse() {
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.isConnected()).thenReturn(false);
        assertFalse(Utils.isOnline(mockContext));
    }

    @Test
    public void isOnline_whenNetworkIsConnectedAndAvailable_returnsTrue() {
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.isConnected()).thenReturn(true);
        when(mockNetworkInfo.isAvailable()).thenReturn(true);
        assertTrue(Utils.isOnline(mockContext));
    }

    //--------------------------------- isEmulator ---------------------------------//
    @Test
    public void isEmulator_whenModelContainsGoogleSdk_returnsTrue() {
        ShadowBuild.setModel("google_sdk");
        assertTrue(Utils.isEmulator());
    }

    @Test
    public void isEmulator_whenManufacturerContainsGenymotion_returnsTrue() {
        ShadowBuild.setManufacturer("Genymotion");
        assertTrue(Utils.isEmulator());
    }

    @Test
    public void isEmulator_onRealDevice_returnsFalse() {
        ShadowBuild.setModel("Pixel 5");
        ShadowBuild.setManufacturer("Google");
        assertFalse(Utils.isEmulator());
    }

    //--------------------------------- getSystemVolume ---------------------------------//
    @Test
    public void getSystemVolume_withNullContext_returnsOne() {
        assertEquals(1.0f, Utils.getSystemVolume(null), 0.001);
    }

    @Test
    public void getSystemVolume_whenAudioManagerIsNull_returnsOne() {
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(null);
        assertEquals(1.0f, Utils.getSystemVolume(mockContext), 0.001);
    }

    @Test
    public void getSystemVolume_withValidAudioManager_returnsCorrectPercentage() {
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);
        when(mockAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)).thenReturn(12);
        when(mockAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)).thenReturn(15);
        // Expected: (12 * 100 / 15) / 100 = 80 / 100 = 0.8
        assertEquals(0.8f, Utils.getSystemVolume(mockContext), 0.001);
    }

    //--------------------------------- isPhoneMuted ---------------------------------//
    @Test
    public void isPhoneMuted_whenRingerIsSilent_returnsTrue() {
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);
        when(mockAudioManager.getRingerMode()).thenReturn(AudioManager.RINGER_MODE_SILENT);
        assertTrue(Utils.isPhoneMuted(mockContext));
    }

    @Test
    public void isPhoneMuted_whenRingerIsNormal_returnsFalse() {
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);
        when(mockAudioManager.getRingerMode()).thenReturn(AudioManager.RINGER_MODE_NORMAL);
        assertFalse(Utils.isPhoneMuted(mockContext));
    }

    //--------------------------------- calculateNewLayoutParams ---------------------------------//
    @Test
    public void calculateNewLayoutParams_withStretchOptionStretch_alwaysFillsContainer() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(0, 0);
        Utils.calculateNewLayoutParams(lp, 16, 9, 1920, 1080, Utils.StretchOption.STRETCH);
        assertEquals(1920, lp.width);
        assertEquals(1080, lp.height);
    }

    @Test
    public void calculateNewLayoutParams_withLandscapeVideoInPortraitContainer_andNoStretch_correctlyScales() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(0, 0);
        // 16:9 video in a 9:16 container
        Utils.calculateNewLayoutParams(lp, 16, 9, 1080, 1920, Utils.StretchOption.NO_STRETCH);
        // width should be 1080, height should be scaled down to 607 (1080 * 9/16)
        assertEquals(1080, lp.width);
        assertEquals(607, lp.height);
    }

    //--------------------------------- getStringFromStream ---------------------------------//
    @Test
    public void getStringFromStream_withValidInputStream_returnsCorrectString() throws IOException {
        String originalString = "Hello, World!";
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes(StandardCharsets.UTF_8));
        String result = Utils.getStringFromStream(inputStream);
        assertEquals(originalString, result);
    }

    @Test
    public void getStringFromStream_withEmptyStream_returnsEmptyString() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        String result = Utils.getStringFromStream(inputStream);
        assertEquals("", result);
    }

    //--------------------------------- parseDuration ---------------------------------//
    @Test
    public void parseDuration_withValidHhMmSs_returnsCorrectSeconds() {
        // 1 hour + 2 minutes + 3 seconds = 3600 + 120 + 3 = 3723
        assertEquals(Integer.valueOf(3723), Utils.parseDuration("01:02:03"));
    }

    @Test
    public void parseDuration_withDecimalSeconds_returnsCorrectlyRoundedSeconds() {
        assertEquals(Integer.valueOf(10), Utils.parseDuration("00:00:10.789"));
    }

    @Test
    public void parseDuration_withInvalidFormat_returnsNull() {
        // This will cause a NumberFormatException internally, which is caught.
        assertNull(Utils.parseDuration("invalid-time"));
    }

    //--------------------------------- parseDurationMillis ---------------------------------//
    @Test
    public void parseDurationMillis_withValidHhMmSs_returnsMillis() {
        assertEquals(Integer.valueOf(3723000), Utils.parseDurationMillis("01:02:03"));
    }

    @Test
    public void parseDurationMillis_withDecimalSeconds_keepsTheFraction() {
        assertEquals(Integer.valueOf(8500), Utils.parseDurationMillis("00:00:08.500"));
        assertEquals(Integer.valueOf(10789), Utils.parseDurationMillis("00:00:10.789"));
    }

    @Test
    public void parseDurationMillis_withInvalidFormat_returnsNull() {
        assertNull(Utils.parseDurationMillis("invalid-time"));
    }

    //--------------------------------- parsePercent ---------------------------------//
    @Test
    public void parsePercent_withValidInput_returnsPercentageValue() {
        assertEquals(Double.valueOf(75), Utils.parsePercent("75%"));
        assertEquals(Double.valueOf(50), Utils.parsePercent(" 50 % "));
        assertEquals(Double.valueOf(0), Utils.parsePercent("0%"));
        assertEquals(Double.valueOf(100), Utils.parsePercent("100%"));
    }

    @Test
    public void parsePercent_withDecimalInput_retainsFraction() {
        assertEquals(Double.valueOf(20.5), Utils.parsePercent("20.5%"));
    }

    @Test
    public void parsePercent_withoutPercentSign_isStillParsed() {
        assertEquals(Double.valueOf(20), Utils.parsePercent("20"));
    }

    @Test
    public void parsePercent_withInvalidInput_returnsNull() {
        assertNull(Utils.parsePercent("abc"));
        assertNull(Utils.parsePercent("%"));
        assertNull(Utils.parsePercent(""));
        assertNull(Utils.parsePercent(null));
    }

    //--------------------------------- isValidVastTimeOffset ---------------------------------//
    @Test
    public void isValidVastTimeOffset_withTimestamp_isValid() {
        assertTrue(Utils.isValidVastTimeOffset("00:00:08"));
        assertTrue(Utils.isValidVastTimeOffset("00:01:30"));
        assertTrue(Utils.isValidVastTimeOffset("01:00:00"));
        assertTrue(Utils.isValidVastTimeOffset("00:00:08.500"));
    }

    @Test
    public void isValidVastTimeOffset_withUnpaddedFields_isValid() {
        // DSPs aren't guaranteed to zero-pad; rejecting these would cost real impressions.
        assertTrue(Utils.isValidVastTimeOffset("0:00:05"));
        assertTrue(Utils.isValidVastTimeOffset("00:00:5"));
        assertTrue(Utils.isValidVastTimeOffset("1:2:3"));
    }

    @Test
    public void isValidVastTimeOffset_withFractionalDigitCountOtherThanThree_isValid() {
        // The IAB xsd requires exactly 3 fractional digits (\.\d\d\d); we deliberately accept
        // 1-3 so a DSP sending "00:00:08.5" isn't rejected over formatting rather than substance.
        assertTrue(Utils.isValidVastTimeOffset("00:00:08.5"));
        assertTrue(Utils.isValidVastTimeOffset("00:00:08.50"));
        assertTrue(Utils.isValidVastTimeOffset("00:00:08.500"));
    }

    @Test
    public void isValidVastTimeOffset_withPercentage_isValid() {
        assertTrue(Utils.isValidVastTimeOffset("20%"));
        assertTrue(Utils.isValidVastTimeOffset("0%"));
        assertTrue(Utils.isValidVastTimeOffset("100%"));
        assertTrue(Utils.isValidVastTimeOffset("20.5%"));
        assertTrue(Utils.isValidVastTimeOffset(" 20% "));
    }

    @Test
    public void isValidVastTimeOffset_withWhitespaceBeforePercentSign_isValid() {
        assertTrue(Utils.isValidVastTimeOffset("20 %"));
        assertTrue(Utils.isValidVastTimeOffset("20\t%"));
        assertTrue(Utils.isValidVastTimeOffset(" 20 % "));
    }

    @Test
    public void isValidVastTimeOffset_withPercentageOutOfRange_isInvalid() {
        assertFalse(Utils.isValidVastTimeOffset("150%"));
        assertFalse(Utils.isValidVastTimeOffset("-10%"));
    }

    @Test
    public void isValidVastTimeOffset_withScientificOrJavaLiteralSuffix_isInvalid() {
        // Double.parseDouble accepts Java literal syntax; the regex gate must reject it first.
        assertFalse(Utils.isValidVastTimeOffset("1e1%"));
        assertFalse(Utils.isValidVastTimeOffset("20d%"));
    }

    @Test
    public void isValidVastTimeOffset_withMalformedValue_isInvalid() {
        assertFalse(Utils.isValidVastTimeOffset("garbage"));
        assertFalse(Utils.isValidVastTimeOffset("20"));
        assertFalse(Utils.isValidVastTimeOffset("00:00"));
        assertFalse(Utils.isValidVastTimeOffset("00:70:00"));
        assertFalse(Utils.isValidVastTimeOffset("00:00:60"));
        assertFalse(Utils.isValidVastTimeOffset("2 0%"));
        assertFalse(Utils.isValidVastTimeOffset("%"));
        assertFalse(Utils.isValidVastTimeOffset(""));
        assertFalse(Utils.isValidVastTimeOffset(null));
    }

    //--------------------------------- parseContentInfo ---------------------------------//
    @Test
    public void parseContentInfo_withNullIcon_returnsNull() {
        assertNull(Utils.parseContentInfo(null));
    }

    @Test
    public void parseContentInfo_withIconMissingStaticResource_returnsNull() {
        when(mockIcon.getStaticResources()).thenReturn(null);
        assertNull(Utils.parseContentInfo(mockIcon));
    }

    @Test
    public void parseContentInfo_withValidFullIcon_parsesAllFieldsCorrectly() {
        // Arrange
        when(mockStaticResource.getText()).thenReturn("https://example.com/icon.png");
        when(mockIcon.getStaticResources()).thenReturn(Collections.singletonList(mockStaticResource));

        when(mockIconClickThrough.getText()).thenReturn("https://example.com/click");
        when(mockIconClicks.getIconClickThrough()).thenReturn(mockIconClickThrough);
        when(mockIcon.getIconClicks()).thenReturn(mockIconClicks);

        when(mockIcon.getWidth()).thenReturn("100");
        when(mockIcon.getHeight()).thenReturn("50");
        when(mockIcon.getXPosition()).thenReturn("right");
        when(mockIcon.getYPosition()).thenReturn("bottom");

        // Act
        ContentInfo contentInfo = Utils.parseContentInfo(mockIcon);

        // Assert
        assertNotNull(contentInfo);
        assertEquals("https://example.com/icon.png", contentInfo.getIconUrl());
        assertEquals("https://example.com/click", contentInfo.getLinkUrl());
        assertEquals(100, contentInfo.getWidth());
        assertEquals(50, contentInfo.getHeight());
        assertEquals(PositionX.RIGHT, contentInfo.getPositionX());
        assertEquals(PositionY.BOTTOM, contentInfo.getPositionY());
    }

    @Test
    public void parseContentInfo_withInvalidDimensions_setsDimensionsToNegativeOne() {
        when(mockStaticResource.getText()).thenReturn("https://example.com/icon.png");
        when(mockIcon.getStaticResources()).thenReturn(Collections.singletonList(mockStaticResource));
        when(mockIcon.getWidth()).thenReturn("invalid");
        when(mockIcon.getHeight()).thenReturn("50");

        ContentInfo contentInfo = Utils.parseContentInfo(mockIcon);

        assertNotNull(contentInfo);
        assertEquals(-1, contentInfo.getWidth());
        assertEquals(-1, contentInfo.getHeight());
    }
}