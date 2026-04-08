// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.banner.presenter;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.test.core.app.ApplicationProvider;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.viewability.FriendlyObstructionReasonConstants;
import net.pubnative.lite.sdk.viewability.baseom.BaseFriendlyObstructionPurpose;
import net.pubnative.lite.sdk.visibility.ImpressionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class MraidAdPresenterTest {

    @Mock
    private Context mockContext;
    @Mock
    private Ad mockAd;
    @Mock
    private AdSize mockAdSize;
    @Mock
    private AdPresenter.Listener mockListener;
    @Mock
    private AdPresenter.ImpressionListener mockImpressionListener;
    @Mock
    private ReportingController mockReportingController;
    @Mock
    private MRAIDView mockMraidView;
    @Mock
    private View mockWatermark;

    private MockedStatic<CheckUtils.NoThrow> mockedCheckUtils;
    private MockedStatic<HyBid> mockedHyBid;
    private MockedStatic<ImpressionManager> mockedImpressionManager;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockContext = ApplicationProvider.getApplicationContext();

        mockedCheckUtils = mockStatic(CheckUtils.NoThrow.class);
        mockedHyBid = mockStatic(HyBid.class);
        mockedImpressionManager = mockStatic(ImpressionManager.class);

        when(HyBid.getReportingController()).thenReturn(mockReportingController);

        when(CheckUtils.NoThrow.checkArgument(anyBoolean(), anyString())).thenReturn(true);
    }

    @After
    public void tearDown() {
        mockedCheckUtils.close();
        mockedHyBid.close();
        mockedImpressionManager.close();
    }

    @Test
    public void load_withUrl_createsMraidBannerWithUrl() {
        String testUrl = "https://example.com/ad.html";
        when(mockAd.getAssetUrl(anyString())).thenReturn(testUrl);

        final List<List<Object>> allConstructorArgs = new ArrayList<>();
        try (MockedConstruction<MRAIDBanner> mockedBanner = mockConstruction(MRAIDBanner.class, (mock, context) -> {
            allConstructorArgs.add(new ArrayList<>(context.arguments()));
        })) {
            MraidAdPresenter presenter = new MraidAdPresenter(mockContext, mockAd, mockAdSize, ImpressionTrackingMethod.AD_VIEWABLE, null);
            presenter.load();

            assertEquals(1, mockedBanner.constructed().size());
            List<?> arguments = allConstructorArgs.get(0);
            assertEquals(testUrl, arguments.get(1)); // URL is the 2nd argument
        }
    }

    @Test
    public void load_withHtml_createsMraidBannerWithHtml() {
        String testHtml = "<html>...</html>";
        when(mockAd.getAssetUrl(anyString())).thenReturn(null);
        when(mockAd.getAssetHtml(anyString())).thenReturn(testHtml);

        final List<List<Object>> allConstructorArgs = new ArrayList<>();
        try (MockedConstruction<MRAIDBanner> mockedBanner = mockConstruction(MRAIDBanner.class, (mock, context) -> {
            allConstructorArgs.add(new ArrayList<>(context.arguments()));
        })) {
            MraidAdPresenter presenter = new MraidAdPresenter(mockContext, mockAd, mockAdSize, ImpressionTrackingMethod.AD_VIEWABLE, null);
            presenter.load();

            List<?> arguments = allConstructorArgs.get(0);
            assertEquals(testHtml, arguments.get(2)); // HTML is the 3rd argument
        }
    }

    @Test
    public void mraidViewLoaded_whenTrackingRendered_invokesImpression() {
        when(mockAd.getImpressionTrackingMethod()).thenReturn("rendered");
        when(mockAd.getAssetUrl(anyString())).thenReturn("https://example.com"); // Needed for load()

        // Mock the MRAIDBanner that will be created during load()
        try (MockedConstruction<MRAIDBanner> mockedBanner = mockConstruction(MRAIDBanner.class)) {
            MraidAdPresenter presenter = new MraidAdPresenter(mockContext, mockAd, mockAdSize, ImpressionTrackingMethod.AD_VIEWABLE, null);
            presenter.setListener(mockListener);
            presenter.setImpressionListener(mockImpressionListener);

            // Call load() first to ensure mMRAIDBanner is initialized
            presenter.load();

            // Simulate the callback
            presenter.mraidViewLoaded(mockMraidView);

            verify(mockListener).onAdLoaded(eq(presenter), any(FrameLayout.class));
            verify(mockImpressionListener).onImpression();
        }
    }

    @Test
    public void mraidViewLoaded_whenTrackingViewable_doesNotInvokeImpression() {
        when(mockAd.getAssetUrl(anyString())).thenReturn("https://example.com"); // Needed for load()

        // Mock the MRAIDBanner that will be created during load()
        try (MockedConstruction<MRAIDBanner> mockedBanner = mockConstruction(MRAIDBanner.class)) {
            MraidAdPresenter presenter = new MraidAdPresenter(mockContext, mockAd, mockAdSize, ImpressionTrackingMethod.AD_VIEWABLE, null);
            presenter.setListener(mockListener);
            presenter.setImpressionListener(mockImpressionListener);

            // Call load() first to ensure mMRAIDBanner is initialized
            presenter.load();

            // Simulate the callback
            presenter.mraidViewLoaded(mockMraidView);

            verify(mockListener).onAdLoaded(eq(presenter), any(FrameLayout.class));
            verify(mockImpressionListener, never()).onImpression();
        }
    }

    @Test
    public void addFriendlyObstruction_callsMraidBanner() {
        // Arrange
        View mockObstruction = new View(mockContext);
        when(mockAd.getAssetUrl(anyString())).thenReturn("https://example.com");

        try (MockedConstruction<MRAIDBanner> mockedBanner = mockConstruction(MRAIDBanner.class)) {
            MraidAdPresenter presenter = new MraidAdPresenter(mockContext, mockAd, mockAdSize, ImpressionTrackingMethod.AD_VIEWABLE, null);
            presenter.load();

            MRAIDBanner bannerInstance = mockedBanner.constructed().get(0);

            // Act
            presenter.addFriendlyObstruction(mockObstruction);

            // Assert
            verify(bannerInstance).addViewabilityFriendlyObstruction(eq(mockObstruction), eq(BaseFriendlyObstructionPurpose.OTHER), eq(FriendlyObstructionReasonConstants.WATERMARK_OBSTRUCTION_REASON));
        }
    }

    @Test
    public void mraidViewLoaded_whenDestroyed_returnsEarly() {
        when(mockAd.getAssetUrl(anyString())).thenReturn("https://example.com");

        try (MockedConstruction<MRAIDBanner> ignored = mockConstruction(MRAIDBanner.class)) {
            MraidAdPresenter presenter = new MraidAdPresenter(mockContext, mockAd, mockAdSize, ImpressionTrackingMethod.AD_VIEWABLE, null);
            presenter.setListener(mockListener);
            presenter.load();
            presenter.destroy();

            presenter.mraidViewLoaded(mockMraidView);

            verify(mockListener, never()).onAdLoaded(any(), any());
        }
    }

    @Test
    public void mraidViewLoaded_withWatermark_addsWatermarkToContainer() {
        when(mockAd.getAssetUrl(anyString())).thenReturn("https://example.com");

        try (MockedConstruction<MRAIDBanner> mockedBanner = mockConstruction(MRAIDBanner.class);
             MockedConstruction<FrameLayout> mockedFrameLayout = mockConstruction(FrameLayout.class)) {

            MraidAdPresenter presenter = new MraidAdPresenter(mockContext, mockAd, mockAdSize, ImpressionTrackingMethod.AD_VIEWABLE, mockWatermark);
            presenter.setListener(mockListener);
            presenter.load();

            presenter.mraidViewLoaded(mockMraidView);

            FrameLayout containerInstance = mockedFrameLayout.constructed().get(0);
            verify(containerInstance).addView(mockedBanner.constructed().get(0));
            verify(containerInstance).addView(mockWatermark);
        }
    }

    @Test
    public void mraidViewLoaded_withRenderedTracking_triggersImpression() {
        when(mockAd.getAssetUrl(anyString())).thenReturn("https://example.com");

        try (MockedConstruction<MRAIDBanner> ignored = mockConstruction(MRAIDBanner.class)) {
            MraidAdPresenter presenter = new MraidAdPresenter(mockContext, mockAd, mockAdSize, ImpressionTrackingMethod.AD_RENDERED, null);
            presenter.setListener(mockListener);
            presenter.setImpressionListener(mockImpressionListener);
            presenter.load();

            presenter.mraidViewLoaded(mockMraidView);
            verify(mockImpressionListener).onImpression();
        }
    }
}