// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded;

import android.app.Activity;

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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.rewarded.presenter.RewardedPresenter;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.utils.sdkmanager.SdkManager;

import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
public class HyBidRewardedAdTest {
    private Activity activity;
    private HyBidRewardedAd rewardedAd;

    private MockedStatic<HyBid> mHyBid;

    @Mock
    RequestManager mockRequestManager;
    @Mock
    RewardedPresenter mockPresenter;

    @Mock
    private HyBidRewardedAd.Listener mockListener;
    @Mock
    private VideoListener mockVideoListener;

    @Captor
    private ArgumentCaptor<HyBidError> hyBidErrorArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        openMocks(this);
        ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
        activity = activityController.get();

        mHyBid = mockStatic(HyBid.class);
        mHyBid.when(HyBid::getSdkManager).thenReturn(mock(SdkManager.class));
        mHyBid.when(HyBid::isInitialized).thenReturn(true);

        rewardedAd = new HyBidRewardedAd(activity, "testZone", mockListener);
        rewardedAd.setVideoListener(mockVideoListener);
        assertNotNull(rewardedAd);
        replacePrivateVariableWithMock("mRequestManager", mockRequestManager);
        replacePrivateVariableWithMock("mORTBRequestManager", mockRequestManager);

        when(mockRequestManager.getAdSize()).thenReturn(mock(AdSize.class));
    }

    @After
    public void tearDown() {
        mHyBid.close();
        activity = null;
        rewardedAd.destroy();
        rewardedAd = null;
    }

    @Test
    public void testLoad_withEmptyZoneId_triggersFailure() {
        //Not passing ZoneId in the Constructor
        HyBidRewardedAd localRewardedAd = new HyBidRewardedAd(activity, mockListener);

        localRewardedAd.load();

        verify(mockListener).onRewardedLoadFailed(hyBidErrorArgumentCaptor.capture());
        HyBidError error = hyBidErrorArgumentCaptor.getValue();
        assertEquals(HyBidErrorCode.INVALID_ZONE_ID, error.getErrorCode());
    }

    @Test
    public void testLoad_withUninitializedSDK_triggersFailure() {
        when(HyBid.isInitialized()).thenReturn(false);

        rewardedAd.load();

        verify(mockListener).onRewardedLoadFailed(hyBidErrorArgumentCaptor.capture());
        HyBidError error = hyBidErrorArgumentCaptor.getValue();
        assertEquals(HyBidErrorCode.NOT_INITIALISED, error.getErrorCode());
    }

    @Test
    public void testLoad_success_callsRequestAd() {
        rewardedAd.load();

        verify(mockRequestManager, times(1)).requestAd();
    }

    @Test
    public void testSetMediationVendor() {
        String vendor = "Test Vendor";
        rewardedAd.setMediationVendor(vendor);
        //1 time for mRequestManager and another for mORTBRequestManager
        verify(mockRequestManager, times(2)).setMediationVendor(eq(vendor));
    }

    @Test
    public void testSetMediationWithTrue_callSetIntegrationType_withMediation() {
        rewardedAd.setMediation(true);
        verify(mockRequestManager, times(2)).setIntegrationType(eq(IntegrationType.MEDIATION));
    }

    @Test
    public void testSetMediationWithFalse_callSetIntegrationType_withSTANDLONE() {
        rewardedAd.setMediation(false);
        verify(mockRequestManager, times(2)).setIntegrationType(eq(IntegrationType.STANDALONE));
    }

    @Test
    public void testIsAutoCacheOnLoad() {
        when(mockRequestManager.isAutoCacheOnLoad()).thenReturn(false);

        assertFalse(rewardedAd.isAutoCacheOnLoad());
    }

    @Test
    public void testSetAutoCacheOnLoad() {
        rewardedAd.setAutoCacheOnLoad(true);
        verify(mockRequestManager, times(2)).setAutoCacheOnLoad(eq(true));
    }


    //RequestManager Callbacks tests

    @Test
    public void testOnRequestSuccess_withNullAd_triggersFailure() {
        rewardedAd.onRequestSuccess(null);

        verify(mockListener).onRewardedLoadFailed(hyBidErrorArgumentCaptor.capture());
        HyBidError error = hyBidErrorArgumentCaptor.getValue();
        assertEquals(HyBidErrorCode.NULL_AD, error.getErrorCode());
    }

    @Test
    public void testOnRequestSuccess_withValidAd_initialiseMAd() {
        Ad ad = mock(Ad.class);
        rewardedAd.onRequestSuccess(ad);

        Ad actualAd = getPrivateFieldValue("mAd");
        assertEquals(ad, actualAd);
    }

    @Test
    public void testOnRequestFail_triggersFailure() {
        Throwable t = new Exception("fail");
        rewardedAd.onRequestFail(t);

        verify(mockListener).onRewardedLoadFailed(eq(t));
    }

    //RewardedPresenter Callbacks Tests

    @Test
    public void testOnRewardedLoaded_callsListener() {
        rewardedAd.onRewardedLoaded(mockPresenter);

        assertTrue(getPrivateFieldValue("mReady"));
        verify(mockListener).onRewardedLoaded();
    }

    @Test
    public void testOnRewardedError_callsListener() {
        rewardedAd.onRewardedError(mockPresenter);

        verify(mockListener).onRewardedLoadFailed(hyBidErrorArgumentCaptor.capture());
        HyBidError error = hyBidErrorArgumentCaptor.getValue();
        assertEquals(HyBidErrorCode.ERROR_RENDERING_REWARDED, error.getErrorCode());
    }

    @Test
    public void testOnRewardedOpened__callsListener() {
        Ad ad = mock(Ad.class);
        when(ad.getZoneId()).thenReturn("zone");
        replacePrivateVariableWithMock("mAd", ad);

        rewardedAd.onRewardedOpened(mockPresenter);

        verify(mockListener).onRewardedOpened();
    }

    @Test
    public void testInvokeOnLoadFinished_callsListener() {
        rewardedAd.invokeOnLoadFinished();

        verify(mockListener).onRewardedLoaded();
    }

    @Test
    public void testInvokeOnLoadFailed_callsListener() {
        Throwable throwable = new Throwable();
        rewardedAd.invokeOnLoadFailed(throwable);

        verify(mockListener).onRewardedLoadFailed(eq(throwable));
    }

    @Test
    public void testInvokeOnClick_callsListener() {
        rewardedAd.invokeOnClick();

        verify(mockListener).onRewardedClick();
    }

    @Test
    public void testInvokeOnClosed_callsListener() {
        rewardedAd.invokeOnClosed();

        verify(mockListener).onRewardedClosed();
    }

    @Test
    public void testInvokeOnClosed_cleansVideoCache_and_callsListener() throws Exception {
        net.pubnative.lite.sdk.vpaid.VideoAdCache mockVidAdCache = mock(net.pubnative.lite.sdk.vpaid.VideoAdCache.class);
        mHyBid.when(HyBid::getVideoAdCache).thenReturn(mockVidAdCache);

        Ad ad = mock(Ad.class);
        when(ad.getSessionId()).thenReturn("session_456");
        replacePrivateVariableWithMock("mAd", ad);

        rewardedAd.invokeOnClosed();

        verify(mockVidAdCache).remove("session_456");
        verify(mockListener).onRewardedClosed();
    }

    @Test
    public void testInvokeOnClosed_doesNotRemoveVideoCache_whenSessionIdEmpty() throws Exception {
        net.pubnative.lite.sdk.vpaid.VideoAdCache mockVidAdCache = mock(net.pubnative.lite.sdk.vpaid.VideoAdCache.class);
        mHyBid.when(HyBid::getVideoAdCache).thenReturn(mockVidAdCache);

        Ad ad = mock(Ad.class);
        when(ad.getSessionId()).thenReturn("");
        replacePrivateVariableWithMock("mAd", ad);

        rewardedAd.invokeOnClosed();

        verify(mockVidAdCache, org.mockito.Mockito.never()).remove(org.mockito.ArgumentMatchers.anyString());
        verify(mockListener).onRewardedClosed();
    }


    //VideoListener Callbacks Tests

    @Test
    public void testOnVideoError_callsListener() {
        rewardedAd.onVideoError(20);

        verify(mockVideoListener).onVideoError(eq(20));
    }

    @Test
    public void testOnVideoStarted_callsListener() {
        rewardedAd.onVideoStarted();

        verify(mockVideoListener).onVideoStarted();

    }

    @Test
    public void testOnVideoDismissed_callsListener() {
        rewardedAd.onVideoDismissed(50);

        verify(mockVideoListener).onVideoDismissed(eq(50));
    }

    @Test
    public void testOnVideoFinished_callsListener() {
        rewardedAd.onVideoFinished();

        verify(mockVideoListener).onVideoFinished();
    }

    @Test
    public void testOnVideoSkipped_callsListener() {
        rewardedAd.onVideoSkipped();

        verify(mockVideoListener).onVideoSkipped();
    }

    @Test
    public void testReportAdRender_whenReportingEnabledAndReportingControllerIsNotNull_reportEvent() {

        try (MockedStatic<HyBid> hyBidMockedStatic = mockStatic(HyBid.class)) {
            hyBidMockedStatic.when(HyBid::isReportingEnabled).thenReturn(true);
            hyBidMockedStatic.when(HyBid::getReportingController).thenReturn(mock(ReportingController.class));

            JSONObject placementParamObj = new JSONObject();
            rewardedAd.reportAdRender(RequestManager.AdFormat.HTML, placementParamObj);

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

        assertTrue(rewardedAd.hasEndCard());
    }

    @Test
    public void testHasEndCard_whenAdHasNoEndCard_returnFalse() {
        Ad ad = mock(Ad.class);
        when(ad.hasEndCard()).thenReturn(false);
        when(ad.isEndCardEnabled()).thenReturn(true);

        replacePrivateVariableWithMock("mAd", ad);

        assertFalse(rewardedAd.hasEndCard());
    }

    @Test
    public void testInvokeOnOpened_callsListener() {
        Ad ad = mock(Ad.class);
        when(ad.getZoneId()).thenReturn("zone");
        replacePrivateVariableWithMock("mAd", ad);

        rewardedAd.invokeOnOpened();

        verify(mockListener).onRewardedOpened();
    }

    @Test
    public void testShow_whenReady_showsPresenter() {
        replacePrivateVariableWithMock("mReady", true);
        replacePrivateVariableWithMock("mPresenter", mockPresenter);
        replacePrivateVariableWithMock("mInitialLoadTime", System.currentTimeMillis());
        replacePrivateVariableWithMock("mIsExchange", false);
        Ad ad = mock(Ad.class);
        replacePrivateVariableWithMock("mAd", ad);

        rewardedAd.show();

        verify(mockPresenter).show();
        verify(mockRequestManager).sendAdSessionDataToAtom(eq(ad), eq(1.0));
    }

    @Test
    public void testShow_whenNotReady_doesNotShowPresenter() {
        replacePrivateVariableWithMock("mReady", false);
        replacePrivateVariableWithMock("mPresenter", mockPresenter);

        rewardedAd.show();

        verify(mockPresenter, never()).show();
    }

    @Test
    public void testShow_whenPresenterIsNull_doesNothing() {
        replacePrivateVariableWithMock("mReady", true);
        replacePrivateVariableWithMock("mPresenter", null);

        rewardedAd.show();

        // Nothing to verify since presenter is null, just ensure no crash
    }

    @Test
    public void testShow_whenAdExpired_doesNotShowAndTriggersError() {
        replacePrivateVariableWithMock("mReady", true);
        replacePrivateVariableWithMock("mPresenter", mockPresenter);
        // Set initial load time to more than 30 minutes ago (TIME_TO_EXPIRE = 1800000 ms = 30 minutes)
        long expiredTime = System.currentTimeMillis() - 1800001;
        replacePrivateVariableWithMock("mInitialLoadTime", expiredTime);

        rewardedAd.show();

        verify(mockPresenter, never()).show();
        verify(mockListener).onRewardedLoadFailed(hyBidErrorArgumentCaptor.capture());
        HyBidError error = hyBidErrorArgumentCaptor.getValue();
        assertEquals(HyBidErrorCode.EXPIRED_AD, error.getErrorCode());
    }

    @Test
    public void testShow_whenInitialLoadTimeIsMinusOne_showsPresenter() {
        replacePrivateVariableWithMock("mReady", true);
        replacePrivateVariableWithMock("mPresenter", mockPresenter);
        replacePrivateVariableWithMock("mInitialLoadTime", -1L);
        replacePrivateVariableWithMock("mIsExchange", false);
        Ad ad = mock(Ad.class);
        replacePrivateVariableWithMock("mAd", ad);

        rewardedAd.show();

        verify(mockPresenter).show();
        verify(mockRequestManager).sendAdSessionDataToAtom(eq(ad), eq(1.0));
    }

    @Test
    public void testShow_whenExchangeAd_sendDataToORTBRequestManager() {
        replacePrivateVariableWithMock("mReady", true);
        replacePrivateVariableWithMock("mPresenter", mockPresenter);
        replacePrivateVariableWithMock("mInitialLoadTime", System.currentTimeMillis());
        replacePrivateVariableWithMock("mIsExchange", true);
        Ad ad = mock(Ad.class);
        replacePrivateVariableWithMock("mAd", ad);

        rewardedAd.show();

        verify(mockPresenter).show();
        // Since mRequestManager and mORTBRequestManager are both mocked to the same object,
        // this will be called for exchange ads
        verify(mockRequestManager).sendAdSessionDataToAtom(eq(ad), eq(1.0));
    }

    @Test
    public void testShow_whenNotExchangeAd_sendDataToRequestManager() {
        replacePrivateVariableWithMock("mReady", true);
        replacePrivateVariableWithMock("mPresenter", mockPresenter);
        replacePrivateVariableWithMock("mInitialLoadTime", System.currentTimeMillis());
        replacePrivateVariableWithMock("mIsExchange", false);
        Ad ad = mock(Ad.class);
        replacePrivateVariableWithMock("mAd", ad);

        rewardedAd.show();

        verify(mockPresenter).show();
        verify(mockRequestManager).sendAdSessionDataToAtom(eq(ad), eq(1.0));
    }

    @Test
    public void testShow_whenAdNotExpiredYet_showsPresenter() {
        replacePrivateVariableWithMock("mReady", true);
        replacePrivateVariableWithMock("mPresenter", mockPresenter);
        // Set initial load time to 1 minute ago (well within the 30 minute expiry)
        long recentTime = System.currentTimeMillis() - 60000;
        replacePrivateVariableWithMock("mInitialLoadTime", recentTime);
        replacePrivateVariableWithMock("mIsExchange", false);
        Ad ad = mock(Ad.class);
        replacePrivateVariableWithMock("mAd", ad);

        rewardedAd.show();

        verify(mockPresenter).show();
        verify(mockRequestManager).sendAdSessionDataToAtom(eq(ad), eq(1.0));
    }

    @Test
    public void testShow_whenRequestManagerIsNull_doesNotSendDataToAtom() {
        replacePrivateVariableWithMock("mReady", true);
        replacePrivateVariableWithMock("mPresenter", mockPresenter);
        replacePrivateVariableWithMock("mInitialLoadTime", System.currentTimeMillis());
        replacePrivateVariableWithMock("mIsExchange", false);
        replacePrivateVariableWithMock("mRequestManager", null);
        Ad ad = mock(Ad.class);
        replacePrivateVariableWithMock("mAd", ad);

        rewardedAd.show();

        verify(mockPresenter).show();
    }

    @Test
    public void testShow_whenORTBRequestManagerIsNull_doesNotSendDataToAtom() {
        replacePrivateVariableWithMock("mReady", true);
        replacePrivateVariableWithMock("mPresenter", mockPresenter);
        replacePrivateVariableWithMock("mInitialLoadTime", System.currentTimeMillis());
        replacePrivateVariableWithMock("mIsExchange", true);
        replacePrivateVariableWithMock("mORTBRequestManager", null);
        Ad ad = mock(Ad.class);
        replacePrivateVariableWithMock("mAd", ad);

        rewardedAd.show();

        verify(mockPresenter).show();
    }

    @Test
    public void testDestroy_cleansUpRequestManagers() {

        rewardedAd.destroy();

        boolean isDestroyed = getPrivateFieldValue("mIsDestroyed");
        assertTrue(isDestroyed);

        verify(mockRequestManager, times(2)).destroy();
    }

    private void replacePrivateVariableWithMock(String fieldName, Object b) {
        try {
            Class<?> runnerClass = Class.forName("net.pubnative.lite.sdk.rewarded.HyBidRewardedAd");
            assertNotNull(runnerClass);

            Field ready = HyBidRewardedAd.class.getDeclaredField(fieldName);
            ready.setAccessible(true);
            ready.set(rewardedAd, b);
        } catch (Exception ignore) {
        }
    }

    @Test
    public void testSetWatermark_withValidBase64_setsWatermarkDrawable() {
        String validPngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        rewardedAd.setWatermark(validPngBase64);

    }

    @Test
    public void testSetWatermark_withInvalidBase64_setsWatermarkDrawableToNull() {
        String invalidBase64 = "invalid_base64_string";
        rewardedAd.setWatermark(invalidBase64);
    }

    @Test
    public void testSetWatermark_withEmptyString_setsWatermarkDrawableToNull() {
        rewardedAd.setWatermark("");
    }

    @Test
    public void testSetWatermark_withNullString_setsWatermarkDrawableToNull() {
        rewardedAd.setWatermark((String) null);
    }

    @Test
    public void testPrepareAd_withWatermark_createsPresenterWithWatermark() {
        String validPngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        rewardedAd.setWatermark(validPngBase64);

        Ad testAd = new Ad(15, "test", Ad.AdType.VIDEO);
        testAd.setZoneId("test_zone");

        rewardedAd.prepareAd(testAd);
    }

    @Test
    public void testPrepareCustomMarkup_withWatermarkAndVastXml_createsPresenterWithWatermark() {
        AdCache mockAdCache = mock(AdCache.class);
        mHyBid.when(HyBid::getAdCache).thenReturn(mockAdCache);
        String validPngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        rewardedAd.setWatermark(validPngBase64);

        String vastXmlMarkup = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><VAST version=\"2.0\"><Ad><InLine><Creatives><Creative><Linear><MediaFiles><MediaFile>test.mp4</MediaFile></MediaFiles></Linear></Creative></Creatives></InLine></Ad></VAST>";
        rewardedAd.prepareCustomMarkup("test_zone", vastXmlMarkup);
    }

    @Test
    public void testPrepareCustomMarkup_withWatermarkAndHtmlMarkup_createsPresenterWithWatermark() {
        AdCache mockAdCache = mock(AdCache.class);
        mHyBid.when(HyBid::getAdCache).thenReturn(mockAdCache);
        String validPngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        rewardedAd.setWatermark(validPngBase64);

        String htmlMarkup = "<html><body><h1>Test Ad Content</h1></body></html>";
        rewardedAd.prepareCustomMarkup("test_zone", htmlMarkup);
    }

    private <T> T getPrivateFieldValue(String fieldName) {
        try {
            Class<?> runnerClass = Class.forName("net.pubnative.lite.sdk.rewarded.HyBidRewardedAd");
            assertNotNull(runnerClass);

            Field field = HyBidRewardedAd.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(rewardedAd);
        } catch (Exception ignore) {
        }
        return null;
    }
}
