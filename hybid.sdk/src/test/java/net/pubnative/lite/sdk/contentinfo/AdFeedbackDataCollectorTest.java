// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.contentinfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.UserDataManager;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.vpaid.enums.AudioState;

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
public class AdFeedbackDataCollectorTest {

    @Mock
    private DeviceInfo mockDeviceInfo;
    @Mock
    private Ad mockAd;
    @Mock
    private UserDataManager mockUserDataManager;

    private AdFeedbackDataCollector subject;
    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        subject = new AdFeedbackDataCollector(mockDeviceInfo, IntegrationType.STANDALONE);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void collectData_withFullData_populatesAllFields() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            setupHyBidMocks(mockedHyBid);
            when(HyBid.isInitialized()).thenReturn(true);
            when(HyBid.getAppToken()).thenReturn("test_app_token");
            when(HyBid.getAppVersion()).thenReturn("1.0.0");

            when(mockDeviceInfo.getModel()).thenReturn("Pixel Test");
            when(mockDeviceInfo.getOSVersion()).thenReturn("12");

            when(mockAd.getZoneId()).thenReturn("test_zone");
            when(mockAd.getCreativeId()).thenReturn("test_creative_id");
            when(mockAd.getImpressionId()).thenReturn("test_impression_id");
            when(mockAd.hasEndCard()).thenReturn(true);
            when(mockAd.getVast()).thenReturn("vast_creative");
            when(mockAd.getAudioState()).thenReturn(null);

            AdFeedbackData result = subject.collectData(mockAd, "banner", IntegrationType.STANDALONE);

            assertEquals("test_app_token", result.getAppToken());
            assertEquals("test_sdk_version", result.getSdkVersion());
            assertEquals("1.0.0", result.getAppVersion());
            assertEquals("banner", result.getAdFormat());
            assertEquals("s", result.getIntegrationType());
            assertEquals("ON", result.getAudioState());
            assertEquals("Pixel Test Android 12", result.getDeviceInfo());
            assertEquals("test_zone", result.getZoneId());
            assertEquals("test_creative_id", result.getCreativeId());
            assertEquals("test_impression_id", result.getImpressionBeacon());
            assertEquals("true", result.getHasEndCard());
            assertEquals("vast_creative", result.getCreative());
        }
    }

    @Test
    public void collectData_whenHyBidNotInitialized_omitsDependentFields() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            when(HyBid.isInitialized()).thenReturn(false);

            mockedHyBid.when(() -> HyBid.getSDKVersionInfo(any(IntegrationType.class))).thenReturn("test_sdk_version");

            AudioState mockAudioState = mock(AudioState.class);
            when(mockAudioState.getStateName()).thenReturn("ON");
            mockedHyBid.when(() -> HyBid.getVideoAudioStatus()).thenReturn(mockAudioState);

            AdFeedbackData result = subject.collectData(mockAd, "banner", IntegrationType.STANDALONE);

            assertNull(result.getAppToken());
        }
    }

    @Test
    public void collectData_usesAudioStateFromAdWhenAvailable() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            setupHyBidMocks(mockedHyBid);

            when(mockAd.getAudioState()).thenReturn("ad_audio_off");

            AdFeedbackData result = subject.collectData(mockAd, "banner", IntegrationType.STANDALONE);

            assertEquals("ad_audio_off", result.getAudioState());
        }
    }

    @Test
    public void collectData_prioritizesVastForCreative() {
        // FIX: Added the static mock context and helper call
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            setupHyBidMocks(mockedHyBid);

            when(mockAd.getVast()).thenReturn("vast_data");
            when(mockAd.getAssetUrl(APIAsset.HTML_BANNER)).thenReturn("html_url");
            when(mockAd.getAssetHtml(APIAsset.HTML_BANNER)).thenReturn("html_content");

            AdFeedbackData result = subject.collectData(mockAd, "banner", IntegrationType.STANDALONE);

            assertEquals("vast_data", result.getCreative());
        }
    }

    @Test
    public void collectData_usesAssetUrlWhenVastIsNull() {
        // FIX: Added the static mock context and helper call
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            setupHyBidMocks(mockedHyBid);

            when(mockAd.getVast()).thenReturn(null);
            when(mockAd.getAssetUrl(APIAsset.HTML_BANNER)).thenReturn("html_url");
            when(mockAd.getAssetHtml(APIAsset.HTML_BANNER)).thenReturn("html_content");

            AdFeedbackData result = subject.collectData(mockAd, "banner", IntegrationType.STANDALONE);

            assertEquals("html_url", result.getCreative());
        }
    }

    /** Helper method to set up common HyBid static mocks to avoid repetition */
    private void setupHyBidMocks(MockedStatic<HyBid> mockedHyBid) {
        AudioState mockAudioState = mock(AudioState.class);
        when(mockAudioState.getStateName()).thenReturn("ON");

        mockedHyBid.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
        mockedHyBid.when(() -> HyBid.getVideoAudioStatus()).thenReturn(mockAudioState);

        // FIX: Added the missing mock for getSDKVersionInfo
        mockedHyBid.when(() -> HyBid.getSDKVersionInfo(any(IntegrationType.class))).thenReturn("test_sdk_version");
    }
}