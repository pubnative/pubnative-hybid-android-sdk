package net.pubnative.lite.sdk.models;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
        activity = Robolectric.buildActivity(Activity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void testImpressionCallbackWithValidListener() {

        NativeAd model = spy(NativeAd.class);
        NativeAd.Listener listener = mock(NativeAd.Listener.class);
        View adView = spy(new View(applicationContext));
        model.mListener = listener;
        model.invokeOnImpression(adView);
        verify(listener, times(1)).onAdImpression(eq(model), eq(adView));
    }

    @Test
    public void testClickCallbackWithValidListener() {

        NativeAd model = spy(NativeAd.class);
        NativeAd.Listener listener = mock(NativeAd.Listener.class);
        View adView = spy(new View(applicationContext));
        model.mListener = listener;
        model.invokeOnClick(adView);
        verify(listener, times(1)).onAdClick(eq(model), eq(adView));
    }

    @Test
    public void testCallbacksWithNullListener() {

        NativeAd model = spy(NativeAd.class);
        model.mListener = null;
        model.invokeOnClick(null);
        model.invokeOnImpression(null);
    }

    @Test
    public void testStartTrackingWithClickableView() {

        Ad adDataModel = spy(AdTestModel.class);
        adDataModel.link = "http://www.google.com";
        NativeAd model = spy(NativeAd.class);
        model.mAd = adDataModel;
        NativeAd.Listener listener = mock(NativeAd.Listener.class);
        View adView = spy(new View(activity));
        View clickableView = spy(new View(activity));
        model.startTracking(adView, clickableView, listener);
        verify(clickableView, times(1)).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void testStartTrackingWithClickableViewForValidClickListener() {

        Ad adDataModel = spy(AdTestModel.class);
        NativeAd model = spy(NativeAd.class);
        model.mAd = adDataModel;
        NativeAd.Listener listener = mock(NativeAd.Listener.class);
        View adView = spy(new View(activity));
        View clickableView = spy(new View(activity));
        model.startTracking(adView, clickableView, listener);
        verify(adView, never()).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void testStartTrackingWithoutClickableView() {

        Ad adDataModel = spy(AdTestModel.class);
        adDataModel.link = "http://www.google.com";
        NativeAd model = spy(NativeAd.class);
        model.mAd = adDataModel;
        NativeAd.Listener listener = mock(NativeAd.Listener.class);
        View adView = spy(new View(activity));
        model.startTracking(adView, listener);
        verify(adView, times(1)).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void getAsset_withRightData_returnDataModel() {
        String assetName = "text";
        String assetValue = "value";

        Ad adModel = spy(AdTestModel.class);
        AdData adDataModel = spy(AdData.class);
        NativeAd model = spy(NativeAd.class);
        adDataModel.data = new HashMap<>();
        adModel.assets = new ArrayList<>();
        adModel.meta = new ArrayList<>();
        adModel.beacons = new ArrayList<>();
        adDataModel.data.put(assetName, assetValue);
        adDataModel.type = assetName;
        adModel.assets.add(adDataModel);
        adModel.meta.add(adDataModel);
        adModel.beacons.add(adDataModel);
        model.mAd = adModel;

        assertThat(model.mAd.getAsset(assetName)).isEqualTo(adDataModel);
    }
}
