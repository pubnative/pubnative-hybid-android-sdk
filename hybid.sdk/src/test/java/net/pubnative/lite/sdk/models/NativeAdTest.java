package net.pubnative.lite.sdk.models;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29)
public class NativeAdTest {
    Context applicationContext;
    Activity activity;

    @Spy
    NativeAd mNativeAd = new NativeAd();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
        activity = Robolectric.buildActivity(Activity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void testImpressionCallbackWithValidListener() {
        NativeAd.Listener listener = mock(NativeAd.Listener.class);
        View adView = spy(new View(applicationContext));
        mNativeAd.mAd = spy(AdTestModel.class);
        mNativeAd.mListener = listener;
        mNativeAd.invokeOnImpression(adView);
        verify(listener, times(1)).onAdImpression(eq(mNativeAd), eq(adView));
    }

    @Test
    public void testClickCallbackWithValidListener() {

        NativeAd.Listener listener = mock(NativeAd.Listener.class);
        View adView = spy(new View(applicationContext));
        mNativeAd.mListener = listener;
        mNativeAd.invokeOnClick(adView);
        verify(listener, times(1)).onAdClick(eq(mNativeAd), eq(adView));
    }

    @Test
    public void testCallbacksWithNullListener() {
        mNativeAd.mListener = null;
        mNativeAd.invokeOnClick(null);
        mNativeAd.invokeOnImpression(null);
    }

    @Test
    public void testStartTrackingWithClickableView() {
        Ad adDataModel = spy(AdTestModel.class);
        adDataModel.link = "http://www.google.com";
        mNativeAd.mAd = adDataModel;
        NativeAd.Listener listener = mock(NativeAd.Listener.class);
        View adView = spy(new View(activity));
        View clickableView = spy(new View(activity));
        mNativeAd.startTracking(adView, clickableView, listener);
        verify(clickableView, times(1)).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void testStartTrackingWithClickableViewForValidClickListener() {
        mNativeAd.mAd = spy(AdTestModel.class);
        NativeAd.Listener listener = mock(NativeAd.Listener.class);
        View adView = spy(new View(activity));
        View clickableView = spy(new View(activity));
        mNativeAd.startTracking(adView, clickableView, listener);
        verify(adView, never()).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void testStartTrackingWithoutClickableView() {
        Ad adDataModel = spy(AdTestModel.class);
        adDataModel.link = "http://www.google.com";
        mNativeAd.mAd = adDataModel;
        NativeAd.Listener listener = mock(NativeAd.Listener.class);
        View adView = spy(new View(activity));
        mNativeAd.startTracking(adView, listener);
        verify(adView, times(1)).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void getAsset_withRightData_returnDataModel() {
        String assetName = "text";
        String assetValue = "value";

        Ad adModel = spy(AdTestModel.class);
        AdData adDataModel = spy(AdData.class);
        adDataModel.data = new HashMap<>();
        adModel.assets = new ArrayList<>();
        adModel.meta = new ArrayList<>();
        adModel.beacons = new ArrayList<>();
        adDataModel.data.put(assetName, assetValue);
        adDataModel.type = assetName;
        adModel.assets.add(adDataModel);
        adModel.meta.add(adDataModel);
        adModel.beacons.add(adDataModel);
        mNativeAd.mAd = adModel;

        assertThat(mNativeAd.mAd.getAsset(assetName)).isEqualTo(adDataModel);
    }
}