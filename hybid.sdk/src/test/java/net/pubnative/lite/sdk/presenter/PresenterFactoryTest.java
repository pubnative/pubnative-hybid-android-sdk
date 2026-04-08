// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.presenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.content.Context;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.utils.AdTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class PresenterFactoryTest {

    @Mock
    private Ad mockAd;
    @Mock
    private AdPresenter mockBasePresenter;
    @Mock
    private AdPresenter.Listener mockBannerListener;
    @Mock
    private AdPresenter.ImpressionListener mockImpressionListener;
    @Mock
    private AdTracker mockAdTracker;
    @Mock
    private ReportingController mockReportingController;

    private Context context;
    private AutoCloseable closeable;
    private TestPresenterFactory subject;

    // A concrete implementation of the abstract class for testing purposes
    private static class TestPresenterFactory extends PresenterFactory {
        public TestPresenterFactory(Context context, IntegrationType integrationType) {
            super(context, integrationType);
        }

        @Override
        protected AdPresenter fromCreativeType(int assetGroupId, Ad ad, AdSize adSize, ImpressionTrackingMethod trackingMethod, View watermark) {
            return null;
        }
    }

    @Before
    public void setUp() {
        closeable = openMocks(this);
        context = ApplicationProvider.getApplicationContext();
        // Spy the test implementation to verify calls to its methods
        subject = spy(new TestPresenterFactory(context, IntegrationType.STANDALONE));
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void createPresenter_withValidAdAndNoTracker_createsAndWrapsPresenter() {
        when(subject.fromCreativeType(anyInt(), any(Ad.class), any(), any(ImpressionTrackingMethod.class), any()))
                .thenReturn(mockBasePresenter);

        try (MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
             MockedConstruction<AdTracker> adTrackerConstruction = mockConstruction(AdTracker.class);
             MockedConstruction<AdPresenterDecorator> decoratorConstruction = mockConstruction(AdPresenterDecorator.class)) {

            mockedHyBid.when(HyBid::getReportingController).thenReturn(mockReportingController);

            AdPresenter result = subject.createPresenter(mockAd, mockAdTracker, AdSize.SIZE_320x50, ImpressionTrackingMethod.AD_VIEWABLE, mockBannerListener, mockImpressionListener, null);

            // 1. Verify the factory method was called
            verify(subject).fromCreativeType(mockAd.assetgroupid, mockAd, AdSize.SIZE_320x50, ImpressionTrackingMethod.AD_VIEWABLE, null);

            // 2. Verify a new AdTracker was NOT created (since one was provided)
            assertEquals(0, adTrackerConstruction.constructed().size());

            // 3. Verify a decorator was created
            assertEquals(1, decoratorConstruction.constructed().size());
            AdPresenterDecorator decorator = decoratorConstruction.constructed().get(0);
            assertEquals(decorator, result);

            // 4. Verify listeners were set on the base presenter
            verify(mockBasePresenter).setListener(decorator);
            verify(mockBasePresenter).setImpressionListener(decorator);
            verify(mockBasePresenter).setVideoListener(decorator);
            verify(mockBasePresenter).setMRaidListener(decorator);
        }
    }

    @Test
    public void createPresenter_withNullAdTracker_createsNewTracker() {
        when(subject.fromCreativeType(anyInt(), any(Ad.class), any(), any(ImpressionTrackingMethod.class), any()))
                .thenReturn(mockBasePresenter);

        try (MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
             MockedConstruction<AdTracker> adTrackerConstruction = mockConstruction(AdTracker.class);
             MockedConstruction<AdPresenterDecorator> decoratorConstruction = mockConstruction(AdPresenterDecorator.class)) {

            mockedHyBid.when(HyBid::getReportingController).thenReturn(mockReportingController);

            // Call with a null AdTracker
            subject.createPresenter(mockAd, null, AdSize.SIZE_320x50, ImpressionTrackingMethod.AD_VIEWABLE, mockBannerListener, mockImpressionListener, null);

            // Verify that a new AdTracker was constructed
            assertEquals(1, adTrackerConstruction.constructed().size());
        }
    }

    @Test
    public void createPresenter_whenAdIsNull_returnsNull() {
        AdPresenter result = subject.createPresenter(null, mockAdTracker, AdSize.SIZE_320x50, ImpressionTrackingMethod.AD_VIEWABLE, mockBannerListener, mockImpressionListener, null);
        assertNull(result);
    }

    @Test
    public void createPresenter_whenFromCreativeTypeReturnsNull_returnsNull() {
        // Make the factory method return null
        when(subject.fromCreativeType(anyInt(), any(Ad.class), any(), any(ImpressionTrackingMethod.class), any()))
                .thenReturn(null);

        AdPresenter result = subject.createPresenter(mockAd, mockAdTracker, AdSize.SIZE_320x50, ImpressionTrackingMethod.AD_VIEWABLE, mockBannerListener, mockImpressionListener, null);
        assertNull(result);
    }

    @Test
    public void getContext_returnsCorrectContext() {
        assertEquals(context, subject.getContext());
    }

    @Test
    public void fromCreativeType_withoutTrackingMethod_usesDefaultTrackingAndNullWatermark() {
        when(subject.fromCreativeType(123, mockAd, AdSize.SIZE_320x50, ImpressionTrackingMethod.AD_VIEWABLE, null))
                .thenReturn(mockBasePresenter);

        AdPresenter result = subject.fromCreativeType(123, mockAd, AdSize.SIZE_320x50);
        assertEquals(mockBasePresenter, result);
    }

    @Test
    public void fromCreativeType_withTrackingMethodButNoWatermark_usesProvidedTrackingAndNullWatermark() {
        when(subject.fromCreativeType(456, mockAd, AdSize.SIZE_728x90, ImpressionTrackingMethod.AD_RENDERED, null))
                .thenReturn(mockBasePresenter);

        AdPresenter result = subject.fromCreativeType(456, mockAd, AdSize.SIZE_728x90, ImpressionTrackingMethod.AD_RENDERED);
        assertEquals(mockBasePresenter, result);
    }
}