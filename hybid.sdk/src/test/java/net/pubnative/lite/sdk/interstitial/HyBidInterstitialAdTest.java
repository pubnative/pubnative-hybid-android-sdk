// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial;

import android.app.Activity;
import android.app.Application;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import androidx.test.core.app.ApplicationProvider;

import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.utils.sdkmanager.SdkManager;

import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
public class HyBidInterstitialAdTest {
    private Activity activity;
    private HyBidInterstitialAd interstitialAd;

    private MockedStatic<HyBid> mHyBid;

    private Application application;

    @Mock
    RequestManager mockRequestManager;
    @Mock
    InterstitialPresenter mockPresenter;

    @Mock
    private HyBidInterstitialAd.Listener mockListener;
    @Mock
    private VideoListener mockVideoListener;

    @Captor
    private ArgumentCaptor<HyBidError> hyBidErrorArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        openMocks(this);
        application = ApplicationProvider.getApplicationContext();

        ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
        activity = activityController.get();

        mHyBid = mockStatic(HyBid.class);
        mHyBid.when(HyBid::getSdkManager).thenReturn(mock(SdkManager.class));
        mHyBid.when(HyBid::isInitialized).thenReturn(true);

        interstitialAd = new HyBidInterstitialAd(activity, "testZone", mockListener);
        interstitialAd.setVideoListener(mockVideoListener);
        assertNotNull(interstitialAd);
        replacePrivateVariableWithMock("mRequestManager", mockRequestManager);
        replacePrivateVariableWithMock("mORTBRequestManager", mockRequestManager);
    }

    @After
    public void tearDown() {
        mHyBid.close();
        activity = null;
        interstitialAd.destroy();
        interstitialAd = null;
    }

    @Test
    public void testLoad_withEmptyZoneId_triggersFailure() {
        //Not passing ZoneId in the Constructor
        HyBidInterstitialAd localInterstitialAd = new HyBidInterstitialAd(activity, mockListener);

        localInterstitialAd.load();

        verify(mockListener).onInterstitialLoadFailed(hyBidErrorArgumentCaptor.capture());
        HyBidError error = hyBidErrorArgumentCaptor.getValue();
        assertEquals(HyBidErrorCode.INVALID_ZONE_ID, error.getErrorCode());
    }

    @Test
    public void testLoad_withUninitializedSDK_triggersFailure() {
        when(HyBid.isInitialized()).thenReturn(false);

        interstitialAd.load();

        verify(mockListener).onInterstitialLoadFailed(hyBidErrorArgumentCaptor.capture());
        HyBidError error = hyBidErrorArgumentCaptor.getValue();
        assertEquals(HyBidErrorCode.NOT_INITIALISED, error.getErrorCode());
    }

    @Test
    public void testLoad_success_callsRequestAd() {
        interstitialAd.load();

        verify(mockRequestManager, times(1)).requestAd();
    }

    @Test
    public void testSetMediationVendor() {
        String vendor = "Test Vendor";
        interstitialAd.setMediationVendor(vendor);
        //1 time for mRequestManager and another for mORTBRequestManager
        verify(mockRequestManager, times(2)).setMediationVendor(eq(vendor));
    }

    @Test
    public void testSetMediationWithTrue_callSetIntegrationType_withMediation() {
        interstitialAd.setMediation(true);
        verify(mockRequestManager, times(2)).setIntegrationType(eq(IntegrationType.MEDIATION));
    }

    @Test
    public void testSetMediationWithFalse_callSetIntegrationType_withSTANDLONE() {
        interstitialAd.setMediation(false);
        verify(mockRequestManager, times(2)).setIntegrationType(eq(IntegrationType.STANDALONE));
    }

    @Test
    public void testIsAutoCacheOnLoad() {
        when(mockRequestManager.isAutoCacheOnLoad()).thenReturn(false);

        assertFalse(interstitialAd.isAutoCacheOnLoad());
    }

    @Test
    public void testSetAutoCacheOnLoad() {
        interstitialAd.setAutoCacheOnLoad(true);
        verify(mockRequestManager, times(2)).setAutoCacheOnLoad(eq(true));
    }


    //RequestManager Callbacks tests

    @Test
    public void testOnRequestSuccess_withNullAd_triggersFailure() {
        interstitialAd.onRequestSuccess(null);

        verify(mockListener).onInterstitialLoadFailed(hyBidErrorArgumentCaptor.capture());
        HyBidError error = hyBidErrorArgumentCaptor.getValue();
        assertEquals(HyBidErrorCode.NULL_AD, error.getErrorCode());
    }

    @Test
    public void testOnRequestSuccess_withValidAd_initialiseMAd() {
        Ad ad = mock(Ad.class);
        interstitialAd.onRequestSuccess(ad);

        Ad actualAd = getPrivateFieldValue("mAd");
        assertEquals(ad, actualAd);
    }

    @Test
    public void testOnRequestFail_triggersFailure() {
        Throwable t = new Exception("fail");
        interstitialAd.onRequestFail(t);

        verify(mockListener).onInterstitialLoadFailed(t);
    }

    //InterstitialPresenter Callbacks Tests

    @Test
    public void testOnInterstitialLoaded_callsListener() {
        interstitialAd.onInterstitialLoaded(mockPresenter);

        assertTrue(getPrivateFieldValue("mReady"));
        verify(mockListener).onInterstitialLoaded();
    }

    @Test
    public void testOnInterstitialError_callsListener() {
        interstitialAd.onInterstitialError(mockPresenter);

        verify(mockListener).onInterstitialLoadFailed(hyBidErrorArgumentCaptor.capture());
        HyBidError error = hyBidErrorArgumentCaptor.getValue();
        assertEquals(HyBidErrorCode.ERROR_RENDERING_INTERSTITIAL, error.getErrorCode());
    }

    @Test
    public void testOnInterstitialShown__callsListener() {
        interstitialAd.onInterstitialShown(mockPresenter);

        verify(mockListener).onInterstitialImpression();
    }

    @Test
    public void testInvokeOnLoadFinished_callsListener() {
        interstitialAd.invokeOnLoadFinished();

        verify(mockListener).onInterstitialLoaded();
    }

    @Test
    public void testInvokeOnLoadFailed_callsListener() {
        Throwable throwable = new Throwable();
        interstitialAd.invokeOnLoadFailed(throwable);

        verify(mockListener).onInterstitialLoadFailed(eq(throwable));
    }

    @Test
    public void testInvokeOnClick_callsListener() {
        interstitialAd.invokeOnClick();

        verify(mockListener).onInterstitialClick();
    }

    @Test
    public void testInvokeOnDismissed_callsListener() {
        interstitialAd.invokeOnDismissed();

        verify(mockListener).onInterstitialDismissed();
    }

    @Test
    public void testInvokeOnDismissed_cleansVideoCache_and_callsListener() throws Exception {
        net.pubnative.lite.sdk.vpaid.VideoAdCache mockVideoAdCache = mock(net.pubnative.lite.sdk.vpaid.VideoAdCache.class);
        mHyBid.when(HyBid::getVideoAdCache).thenReturn(mockVideoAdCache);

        Ad mockAd = mock(Ad.class);
        when(mockAd.getSessionId()).thenReturn("session_123");
        replacePrivateVariableWithMock("mAd", mockAd);

        interstitialAd.invokeOnDismissed();

        verify(mockVideoAdCache).remove("session_123");
        verify(mockListener).onInterstitialDismissed();
    }

    @Test
    public void testInvokeOnDismissed_doesNotRemoveVideoCache_whenSessionIdEmpty() throws Exception {
        net.pubnative.lite.sdk.vpaid.VideoAdCache mockVideoAdCache = mock(net.pubnative.lite.sdk.vpaid.VideoAdCache.class);
        mHyBid.when(HyBid::getVideoAdCache).thenReturn(mockVideoAdCache);

        Ad mockAd = mock(Ad.class);
        when(mockAd.getSessionId()).thenReturn("");
        replacePrivateVariableWithMock("mAd", mockAd);

        interstitialAd.invokeOnDismissed();

        verify(mockVideoAdCache, org.mockito.Mockito.never()).remove(org.mockito.ArgumentMatchers.anyString());
        verify(mockListener).onInterstitialDismissed();
    }

    //VideoListener Callbacks Tests

    @Test
    public void testOnVideoError_callsListener() {
        interstitialAd.onVideoError(20);

        verify(mockVideoListener).onVideoError(eq(20));
    }

    @Test
    public void testOnVideoStarted_callsListener() {
        interstitialAd.onVideoStarted();

        verify(mockVideoListener).onVideoStarted();

    }

    @Test
    public void testOnVideoDismissed_callsListener() {
        interstitialAd.onVideoDismissed(50);

        verify(mockVideoListener).onVideoDismissed(eq(50));
    }

    @Test
    public void testOnVideoFinished_callsListener() {
        interstitialAd.onVideoFinished();

        verify(mockVideoListener).onVideoFinished();
    }

    @Test
    public void testOnVideoSkipped_callsListener() {
        interstitialAd.onVideoSkipped();

        verify(mockVideoListener).onVideoSkipped();
    }

    @Test
    public void testReportAdRender_whenReportingEnabledAndReportingControllerIsNotNull_reportEvent() {

        try (MockedStatic<HyBid> hyBidMockedStatic = mockStatic(HyBid.class)) {
            hyBidMockedStatic.when(HyBid::isReportingEnabled).thenReturn(true);
            hyBidMockedStatic.when(HyBid::getReportingController).thenReturn(mock(ReportingController.class));

            JSONObject placementParamObj = new JSONObject();
            interstitialAd.reportAdRender(RequestManager.AdFormat.HTML, placementParamObj);

            ArgumentCaptor<ReportingEvent> eventArgumentCaptor = ArgumentCaptor.forClass(ReportingEvent.class);
            hyBidMockedStatic.verify(() -> HyBid.getReportingController().reportEvent(eventArgumentCaptor.capture()));

            ReportingEvent reportingEvent = eventArgumentCaptor.getValue();
            assertEquals(RequestManager.AdFormat.HTML, reportingEvent.getAdFormat());
            assertEquals(Reporting.EventType.RENDER, reportingEvent.getEventType());
            assertEquals(Reporting.Platform.ANDROID, reportingEvent.getPlatform());

        } catch (Exception ignore) {

        }
    }

    @Test
    public void testHasEndCard_withValidAd_returnTrue() {
        Ad ad = mock(Ad.class);
        when(ad.hasEndCard()).thenReturn(true);
        when(ad.isEndCardEnabled()).thenReturn(true);

        replacePrivateVariableWithMock("mAd", ad);

        assertTrue(interstitialAd.hasEndCard());
    }

    @Test
    public void testHasEndCard_whenAdHasNoEndCard_returnFalse() {
        Ad ad = mock(Ad.class);
        when(ad.hasEndCard()).thenReturn(false);
        when(ad.isEndCardEnabled()).thenReturn(true);

        replacePrivateVariableWithMock("mAd", ad);

        assertFalse(interstitialAd.hasEndCard());
    }

    @Test
    public void testInvokeOnImpression_callsListener() {
        interstitialAd.invokeOnImpression();

        verify(mockListener).onInterstitialImpression();
    }

    @Test
    public void testShow_whenReady_showsPresenter() {
        replacePrivateVariableWithMock("mReady", true);
        replacePrivateVariableWithMock("mPresenter", mockPresenter);

        boolean result = interstitialAd.show();

        assertTrue(result);
        verify(mockPresenter).show();
    }

    @Test
    public void testShow_whenNotReady_returnsFalse() {
        replacePrivateVariableWithMock("mReady", false);

        boolean result = interstitialAd.show();

        assertFalse(result);
    }

    @Test
    public void testDestroy_cleansUpRequestManagers() {

        interstitialAd.destroy();

        boolean isDestroyed = getPrivateFieldValue("mIsDestroyed");
        assertTrue(isDestroyed);

        verify(mockRequestManager, times(2)).destroy();
    }

    private void replacePrivateVariableWithMock(String fieldName, Object b) {
        try {
            Class<?> runnerClass = Class.forName("net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd");
            assertNotNull(runnerClass);

            Field ready = HyBidInterstitialAd.class.getDeclaredField(fieldName);
            ready.setAccessible(true);
            ready.set(interstitialAd, b);
        } catch (Exception ignore) {
        }
    }

    @Test
    public void testSetWatermark_withValidBase64_setsWatermarkDrawable() {
        String validPngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        interstitialAd.setWatermark(validPngBase64);
    }

    @Test
    public void testSetWatermark_withInvalidBase64_setsWatermarkDrawableToNull() {
        String invalidBase64 = "invalid_base64_string";
        interstitialAd.setWatermark(invalidBase64);
    }

    @Test
    public void testSetWatermark_withEmptyString_setsWatermarkDrawableToNull() {
        interstitialAd.setWatermark("");
    }

    @Test
    public void testSetWatermark_withNullString_setsWatermarkDrawableToNull() {
        interstitialAd.setWatermark((String) null);
    }

    @Test
    public void testPrepareAd_withWatermark_createsPresenterWithWatermark() {
        String validPngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        interstitialAd.setWatermark(validPngBase64);

        Ad testAd = new Ad(21, "test", Ad.AdType.HTML);
        testAd.setZoneId("test_zone");

        interstitialAd.prepareAd(testAd);
    }

    @Test
    public void testPrepareCustomMarkup_withWatermarkAndVastXml_createsPresenterWithWatermark() {
        AdCache mockAdCache = mock(AdCache.class);
        mHyBid.when(HyBid::getAdCache).thenReturn(mockAdCache);

        String validPngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        interstitialAd.setWatermark(validPngBase64);

        String vastXmlMarkup = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><VAST version=\"2.0\"><Ad><InLine><Creatives><Creative><Linear><MediaFiles><MediaFile>test.mp4</MediaFile></MediaFiles></Linear></Creative></Creatives></InLine></Ad></VAST>";

        interstitialAd.prepareCustomMarkup("test_zone", vastXmlMarkup);
    }

    @Test
    public void testPrepareCustomMarkup_withWatermarkAndHtmlMarkup_createsPresenterWithWatermark() {
        AdCache mockAdCache = mock(AdCache.class);
        mHyBid.when(HyBid::getAdCache).thenReturn(mockAdCache);

        String validPngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        interstitialAd.setWatermark(validPngBase64);

        String htmlMarkup = "<html><body><h1>Test Ad Content</h1></body></html>";
        interstitialAd.prepareCustomMarkup("test_zone", htmlMarkup);
    }

    private <T> T getPrivateFieldValue(String fieldName) {
        try {
            Class<?> runnerClass = Class.forName("net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd");
            assertNotNull(runnerClass);

            Field field = HyBidInterstitialAd.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(interstitialAd);
        } catch (Exception ignore) {
        }
        return null;
    }
}
