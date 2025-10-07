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

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.visibility.ImpressionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    private MockedStatic<CheckUtils.NoThrow> mockedCheckUtils;
    private MockedStatic<HyBid> mockedHyBid;
    private MockedStatic<ImpressionManager> mockedImpressionManager;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

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
            MraidAdPresenter presenter = new MraidAdPresenter(mockContext, mockAd, mockAdSize, ImpressionTrackingMethod.AD_VIEWABLE);
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
            MraidAdPresenter presenter = new MraidAdPresenter(mockContext, mockAd, mockAdSize, ImpressionTrackingMethod.AD_VIEWABLE);
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
            MraidAdPresenter presenter = new MraidAdPresenter(mockContext, mockAd, mockAdSize, ImpressionTrackingMethod.AD_VIEWABLE);
            presenter.setListener(mockListener);
            presenter.setImpressionListener(mockImpressionListener);

            // Call load() first to ensure mMRAIDBanner is initialized
            presenter.load();

            // Simulate the callback
            presenter.mraidViewLoaded(mockMraidView);

            verify(mockListener).onAdLoaded(eq(presenter), any(MRAIDBanner.class));
            verify(mockImpressionListener).onImpression();
        }
    }

    @Test
    public void mraidViewLoaded_whenTrackingViewable_doesNotInvokeImpression() {
        when(mockAd.getAssetUrl(anyString())).thenReturn("https://example.com"); // Needed for load()

        // Mock the MRAIDBanner that will be created during load()
        try (MockedConstruction<MRAIDBanner> mockedBanner = mockConstruction(MRAIDBanner.class)) {
            MraidAdPresenter presenter = new MraidAdPresenter(mockContext, mockAd, mockAdSize, ImpressionTrackingMethod.AD_VIEWABLE);
            presenter.setListener(mockListener);
            presenter.setImpressionListener(mockImpressionListener);

            // Call load() first to ensure mMRAIDBanner is initialized
            presenter.load();

            // Simulate the callback
            presenter.mraidViewLoaded(mockMraidView);

            verify(mockListener).onAdLoaded(eq(presenter), any(MRAIDBanner.class));
            verify(mockImpressionListener, never()).onImpression();
        }
    }
}