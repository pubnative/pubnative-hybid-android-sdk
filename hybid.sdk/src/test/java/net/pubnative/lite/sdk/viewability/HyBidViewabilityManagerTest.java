// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
package net.pubnative.lite.sdk.viewability;

import static org.mockito.MockitoAnnotations.openMocks;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;

import android.view.View;
import android.webkit.WebView;

import com.iab.omid.library.pubnativenet.Omid;
import com.iab.omid.library.pubnativenet.adsession.AdEvents;
import com.iab.omid.library.pubnativenet.adsession.AdSession;
import com.iab.omid.library.pubnativenet.adsession.AdSessionConfiguration;
import com.iab.omid.library.pubnativenet.adsession.AdSessionContext;
import com.iab.omid.library.pubnativenet.adsession.CreativeType;
import com.iab.omid.library.pubnativenet.adsession.FriendlyObstructionPurpose;
import com.iab.omid.library.pubnativenet.adsession.ImpressionType;
import com.iab.omid.library.pubnativenet.adsession.Owner;
import com.iab.omid.library.pubnativenet.adsession.Partner;
import com.iab.omid.library.pubnativenet.adsession.media.InteractionType;
import com.iab.omid.library.pubnativenet.adsession.media.MediaEvents;
import com.iab.omid.library.pubnativenet.adsession.media.Position;
import com.iab.omid.library.pubnativenet.adsession.media.VastProperties;

import net.pubnative.lite.sdk.viewability.baseom.BaseFriendlyObstructionPurpose;
import net.pubnative.lite.sdk.viewability.baseom.MediaEventType;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by shubhamkeshri on 22.09.25.
 */

@RunWith(RobolectricTestRunner.class)
public class HyBidViewabilityManagerTest {

    private HyBidViewabilityManager viewabilityManager;

    private Application application;

    @Mock
    AdSession mockAdSession;

    @Mock
    AdEvents mockAdEvents;

    @Mock
    MediaEvents mockMediaEvents;

    @Mock
    View mockView;

    @Mock
    WebView mockWebView;


    @Before
    public void setUp() {
        openMocks(this);
        application = ApplicationProvider.getApplicationContext();
        viewabilityManager = new HyBidViewabilityManager(application);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testCreatePartner() {
        Partner partner = viewabilityManager.createPartner();
        assertNotNull(partner);
        assertEquals("Pubnativenet", partner.getName());
    }

    @Test
    public void testPartnerVersion() {
        String partnerVersion = "1.1.1";
        try (MockedStatic<Omid> mockedStatic = mockStatic(Omid.class)) {
            mockedStatic.when(Omid::getVersion).thenReturn(partnerVersion);

            assertEquals(partnerVersion, viewabilityManager.getPartnerVersion());
        }
    }

    @Test
    public void testViewabilityMeasurementActivated_whenOmidIdIsActivatedAndShouldMeasureVisibilityIsTrue_returnsTrue() {
        try (MockedStatic<Omid> mockedStatic = mockStatic(Omid.class)) {
            mockedStatic.when(Omid::isActive).thenReturn(true);
            assertTrue(viewabilityManager.isOmActive());
            viewabilityManager.setViewabilityMeasurementEnabled(true);

            assertTrue(viewabilityManager.isViewabilityMeasurementActivated());
        }
    }

    @Test
    public void testViewabilityMeasurementActivated_whenEitherOfOmidIdIsActiveOrShouldMeasureVisibilityIsFalse_returnsFalse() {
        try (MockedStatic<Omid> mockedStatic = mockStatic(Omid.class)) {

            //when 'Omid::isActive' is inactive & MeasureViewability is disabled
            mockedStatic.when(Omid::isActive).thenReturn(false);
            viewabilityManager.setViewabilityMeasurementEnabled(false);

            assertFalse(viewabilityManager.isViewabilityMeasurementActivated());

            //when 'Omid::isActive' is inactive & MeasureViewability is enabled
            mockedStatic.when(Omid::isActive).thenReturn(false);
            viewabilityManager.setViewabilityMeasurementEnabled(true);

            assertFalse(viewabilityManager.isViewabilityMeasurementActivated());

            //when 'Omid::isActive' is active & MeasureViewability is disabled
            mockedStatic.when(Omid::isActive).thenReturn(false);
            viewabilityManager.setViewabilityMeasurementEnabled(true);

            assertFalse(viewabilityManager.isViewabilityMeasurementActivated());
        }
    }

    @Test
    public void testGetOwner_whenVideoAd_returnJAVASCRIPT() {
        Owner owner = viewabilityManager.getOwner(true);
        assertEquals(Owner.JAVASCRIPT, owner);
    }

    @Test
    public void testGetOwner_whenNotVideoAd_returnNative() {
        Owner owner = viewabilityManager.getOwner(false);
        assertEquals(Owner.NATIVE, owner);
    }

    @Test
    public void testStartAndStopAdSession() {
        viewabilityManager.startAdSession(mockAdSession);
        verify(mockAdSession, times(1)).start();

        viewabilityManager.stopAdSession(mockAdSession);
        verify(mockAdSession, times(1)).finish();
    }

    @Test
    public void testRegisterAdView() {
        viewabilityManager.registerAdView(mockAdSession, mockView);
        verify(mockAdSession, times(1)).registerAdView(mockView);
    }

    @Test
    public void testAdSessionEvents_whenObject_isNotInstanceOfAdSession() {
        Object adSession = mock(Object.class);
        viewabilityManager.startAdSession(adSession);
        viewabilityManager.stopAdSession(adSession);
        viewabilityManager.registerAdView(adSession, mockView);

        //Do nothing when Object is not instance of AdSession
        verifyNoInteractions(adSession);
    }

    @Test
    public void testAddFriendlyObstruction_whenObject_isNotInstanceOfAdSession() {
        Object adSession = mock(Object.class);
        viewabilityManager.addFriendlyObstruction(adSession, mockView, mock(Enum.class), "some reason");

        //Do nothing when Object is not instance of AdSession
        verifyNoInteractions(adSession);
    }

    @Test
    public void testAddFriendlyObstruction_whenPurposeIsVideoControl() {
        String reason = "some reason";
        viewabilityManager.addFriendlyObstruction(mockAdSession, mockView, BaseFriendlyObstructionPurpose.VIDEO_CONTROLS, reason);

        verify(mockAdSession, times(1)).addFriendlyObstruction(eq(mockView), eq(FriendlyObstructionPurpose.VIDEO_CONTROLS), eq(reason));
    }

    @Test
    public void testAddFriendlyObstruction_whenPurposeIsNotVideoControl() {
        String reason = "some reason";
        viewabilityManager.addFriendlyObstruction(mockAdSession, mockView, BaseFriendlyObstructionPurpose.NOT_VISIBLE, reason);

        //BaseFriendlyObstructionPurpose is OTHER when its not VIDEO_CONTROLS.
        verify(mockAdSession, times(1)).addFriendlyObstruction(eq(mockView), eq(FriendlyObstructionPurpose.OTHER), eq(reason));
    }

    @Test
    public void testFireLoaded() {
        viewabilityManager.fireLoaded(mockAdEvents);
        verify(mockAdEvents, times(1)).loaded();
    }

    @Test
    public void testFireEventProperties() {
        VastProperties vastProps = VastProperties.createVastPropertiesForNonSkippableMedia(false, Position.STANDALONE);
        viewabilityManager.fireEventProperties(mockAdEvents, vastProps);
        verify(mockAdEvents, times(1)).loaded(vastProps);
    }

    @Test
    public void testFireImpression() {
        viewabilityManager.fireImpression(mockAdEvents);
        verify(mockAdEvents, times(1)).impressionOccurred();
    }

    @Test
    public void testMediaEvents_whenEventObj_isNotInstanceOfMediaEvent() {
        Object mediaEvent = mock(Object.class);
        viewabilityManager.fireMediaEventStart(mediaEvent, 30f, 0f);
        viewabilityManager.fireMediaEvents(MediaEventType.COMPLETE, mediaEvent);
        viewabilityManager.fireMediaEventVolumeChange(mediaEvent, 0.5f);

        verifyNoInteractions(mediaEvent);
    }

    @Test
    public void testFireMediaEvents_whenEventObj_isInstanceOfMediaEvent() {
        viewabilityManager.fireMediaEvents(MediaEventType.FIRST_QUARTILE, mockMediaEvents);
        verify(mockMediaEvents, times(1)).firstQuartile();

        viewabilityManager.fireMediaEvents(MediaEventType.MIDPOINT, mockMediaEvents);
        verify(mockMediaEvents, times(1)).midpoint();

        viewabilityManager.fireMediaEvents(MediaEventType.THIRD_QUARTILE, mockMediaEvents);
        verify(mockMediaEvents, times(1)).thirdQuartile();

        viewabilityManager.fireMediaEvents(MediaEventType.COMPLETE, mockMediaEvents);
        verify(mockMediaEvents, times(1)).complete();

        viewabilityManager.fireMediaEvents(MediaEventType.PAUSE, mockMediaEvents);
        verify(mockMediaEvents, times(1)).pause();

        viewabilityManager.fireMediaEvents(MediaEventType.RESUME, mockMediaEvents);
        verify(mockMediaEvents, times(1)).resume();

        viewabilityManager.fireMediaEvents(MediaEventType.BUFFER_START, mockMediaEvents);
        verify(mockMediaEvents, times(1)).bufferStart();

        viewabilityManager.fireMediaEvents(MediaEventType.BUFFER_FINISH, mockMediaEvents);
        verify(mockMediaEvents, times(1)).bufferFinish();

        viewabilityManager.fireMediaEvents(MediaEventType.SKIPPED, mockMediaEvents);
        verify(mockMediaEvents, times(1)).skipped();

        viewabilityManager.fireMediaEvents(MediaEventType.CLICK, mockMediaEvents);
        verify(mockMediaEvents, times(1)).adUserInteraction(eq(InteractionType.CLICK));

        viewabilityManager.fireMediaEventStart(mockMediaEvents, 30f, 0f);
        verify(mockMediaEvents, times(1)).start(30f, 0f);

        viewabilityManager.fireMediaEventVolumeChange(mockMediaEvents, 0.5f);
        verify(mockMediaEvents, times(1)).volumeChange(0.5f);

        verifyNoMoreInteractions(mockMediaEvents);
    }

    @Test
    public void testViewabilityMeasurementToggle() {
        viewabilityManager.setViewabilityMeasurementEnabled(false);
        assertFalse(viewabilityManager.isViewabilityMeasurementEnabled());

        viewabilityManager.setViewabilityMeasurementEnabled(true);
        assertTrue(viewabilityManager.isViewabilityMeasurementEnabled());
    }

    @Test
    public void testCreateMediaSession() {
        try (MockedStatic<MediaEvents> mockedStatic = mockStatic(MediaEvents.class)) {
            mockedStatic.when(() -> MediaEvents.createMediaEvents(any())).thenReturn(mockMediaEvents);

            assertEquals(mockMediaEvents, viewabilityManager.createMediaEvents(any()));
        }
    }

    @Test
    public void testCreateAdSession() {
        viewabilityManager.createPartner();
        AdSessionConfiguration config = viewabilityManager.getNativeAdSessionConfiguration();
        AdSessionContext context = viewabilityManager.createHtmlAdSessionContext(mockWebView);
        assertNotNull(config);
        assertNotNull(context);

        try (MockedStatic<AdSession> mockedStatic = mockStatic(AdSession.class)) {
            mockedStatic.when(() -> AdSession.createAdSession(config, context)).thenReturn(mockAdSession);
            assertEquals(mockAdSession, viewabilityManager.createAdSession(config, context));
        }
    }

    @Test
    public void testCreateAdSessionConfig() {
        assertNotNull(viewabilityManager.getWebAdSessionConfiguration(true, Owner.JAVASCRIPT));

        AdSessionConfiguration config2 = viewabilityManager.getNativeAdSessionConfiguration();
        assertNotNull(config2);
    }

    @Test
    public void testGetWebAdSessionConfiguration_whenAdTypeIsVideo() {
        AdSessionConfiguration mockAdSessionConfiguration = mock(AdSessionConfiguration.class);

        try (MockedStatic<AdSessionConfiguration> mockedStatic = mockStatic(AdSessionConfiguration.class)) {
            mockedStatic.when(() -> AdSessionConfiguration.createAdSessionConfiguration(
                            eq(CreativeType.DEFINED_BY_JAVASCRIPT),
                            eq(ImpressionType.DEFINED_BY_JAVASCRIPT),
                            eq(Owner.JAVASCRIPT),
                            eq(Owner.JAVASCRIPT),
                            eq(false)))
                    .thenReturn(mockAdSessionConfiguration);

            assertEquals(mockAdSessionConfiguration, viewabilityManager.getWebAdSessionConfiguration(true, Owner.JAVASCRIPT));
        }
    }

    @Test
    public void testGetWebAdSessionConfiguration_whenAdTypeIsNotVideo() {
        AdSessionConfiguration mockAdSessionConfiguration = mock(AdSessionConfiguration.class);

        try (MockedStatic<AdSessionConfiguration> mockedStatic = mockStatic(AdSessionConfiguration.class)) {
            mockedStatic.when(() -> AdSessionConfiguration.createAdSessionConfiguration(
                            eq(CreativeType.HTML_DISPLAY),
                            eq(ImpressionType.BEGIN_TO_RENDER),
                            eq(Owner.NATIVE),
                            eq(Owner.NONE),
                            eq(false)))
                    .thenReturn(mockAdSessionConfiguration);

            assertEquals(mockAdSessionConfiguration, viewabilityManager.getWebAdSessionConfiguration(false, Owner.NATIVE));
        }
    }

    @Test
    public void testCreateHtmlAdSessionContext() {
        viewabilityManager.createPartner();
        AdSessionContext context = viewabilityManager.createHtmlAdSessionContext(mockWebView);
        assertNotNull(context);
    }

    @Test
    public void testCreateNativeAdSessionContext() {
        AdSessionContext context = viewabilityManager.createNativeAdSessionContext(Collections.emptyList());
        assertNotNull(context);
    }

    @Test
    public void testCreateVastProperties() {
        VastProperties props = viewabilityManager.createVastPropertiesForNonSkippableMedia();
        assertNotNull(props);
    }


}