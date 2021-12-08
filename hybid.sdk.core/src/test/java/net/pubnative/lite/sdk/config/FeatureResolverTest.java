package net.pubnative.lite.sdk.config;

import net.pubnative.lite.sdk.models.RemoteConfigAppConfig;
import net.pubnative.lite.sdk.models.RemoteConfigAppFeatures;
import net.pubnative.lite.sdk.models.RemoteConfigFeature;
import net.pubnative.lite.sdk.models.RemoteConfigModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class FeatureResolverTest {

    @Test
    public void testNullConfig() {
        FeatureResolver featureResolver = new FeatureResolver(null);
        Assert.assertTrue(featureResolver.isAdFormatEnabled(RemoteConfigFeature.AdFormat.BANNER));
        Assert.assertTrue(featureResolver.isRenderingSupported(RemoteConfigFeature.Rendering.VAST));
        Assert.assertTrue(featureResolver.isReportingModeEnabled(RemoteConfigFeature.Reporting.AD_ERRORS));
        Assert.assertTrue(featureResolver.isUserConsentSupported(RemoteConfigFeature.UserConsent.GDPR));
    }

    @Test
    public void testNullAppFeatures() {
        RemoteConfigModel configModel = createRemoteConfigModel();
        configModel.app_config.features = null;
        FeatureResolver featureResolver = new FeatureResolver(configModel);
        Assert.assertTrue(featureResolver.isAdFormatEnabled(RemoteConfigFeature.AdFormat.BANNER));
        Assert.assertTrue(featureResolver.isRenderingSupported(RemoteConfigFeature.Rendering.VAST));
        Assert.assertTrue(featureResolver.isReportingModeEnabled(RemoteConfigFeature.Reporting.AD_ERRORS));
        Assert.assertTrue(featureResolver.isUserConsentSupported(RemoteConfigFeature.UserConsent.GDPR));
    }

    @Test
    public void testReportingWhenNullConfig() {
        FeatureResolver featureResolver = new FeatureResolver(null);
        Assert.assertFalse(featureResolver.isReportingModeEnabled(RemoteConfigFeature.Reporting.AD_EVENTS));
        Assert.assertTrue(featureResolver.isReportingModeEnabled(RemoteConfigFeature.Reporting.AD_ERRORS));
    }

    @Test
    public void testEmptyFeatures() {
        RemoteConfigModel configModel = createRemoteConfigModel();
        configModel.app_config.features.ad_formats = new ArrayList<>();
        configModel.app_config.features.rendering = new ArrayList<>();
        configModel.app_config.features.reporting = new ArrayList<>();
        configModel.app_config.features.user_consent = new ArrayList<>();
        FeatureResolver featureResolver = new FeatureResolver(configModel);
        Assert.assertFalse(featureResolver.isAdFormatEnabled(RemoteConfigFeature.AdFormat.BANNER));
        Assert.assertFalse(featureResolver.isRenderingSupported(RemoteConfigFeature.Rendering.VAST));
        Assert.assertFalse(featureResolver.isReportingModeEnabled(RemoteConfigFeature.Reporting.AD_ERRORS));
        Assert.assertFalse(featureResolver.isUserConsentSupported(RemoteConfigFeature.UserConsent.GDPR));
    }

    @Test
    public void testAdFormats() {
        List<String> formatFeatures = Arrays.asList(RemoteConfigFeature.AdFormat.BANNER,
                RemoteConfigFeature.AdFormat.INTERSTITIAL,
                RemoteConfigFeature.AdFormat.REWARDED,
                RemoteConfigFeature.AdFormat.NATIVE);
        RemoteConfigModel configModel = createRemoteConfigModel();
        configModel.app_config.features.ad_formats = formatFeatures;
        FeatureResolver featureResolver = new FeatureResolver(configModel);
        Assert.assertTrue(featureResolver.isAdFormatEnabled(RemoteConfigFeature.AdFormat.NATIVE));
        Assert.assertTrue(featureResolver.isAdFormatEnabled(RemoteConfigFeature.AdFormat.BANNER));
        Assert.assertTrue(featureResolver.isAdFormatEnabled(RemoteConfigFeature.AdFormat.REWARDED));
        Assert.assertTrue(featureResolver.isAdFormatEnabled(RemoteConfigFeature.AdFormat.INTERSTITIAL));
    }

    @Test
    public void testAdFormatsNativeDisabled() {
        List<String> formatFeatures = Arrays.asList(RemoteConfigFeature.AdFormat.BANNER,
                RemoteConfigFeature.AdFormat.INTERSTITIAL,
                RemoteConfigFeature.AdFormat.REWARDED);
        RemoteConfigModel configModel = createRemoteConfigModel();
        configModel.app_config.features.ad_formats = formatFeatures;
        FeatureResolver featureResolver = new FeatureResolver(configModel);
        Assert.assertFalse(featureResolver.isAdFormatEnabled(RemoteConfigFeature.AdFormat.NATIVE));
        Assert.assertTrue(featureResolver.isAdFormatEnabled(RemoteConfigFeature.AdFormat.BANNER));
        Assert.assertTrue(featureResolver.isAdFormatEnabled(RemoteConfigFeature.AdFormat.REWARDED));
        Assert.assertTrue(featureResolver.isAdFormatEnabled(RemoteConfigFeature.AdFormat.INTERSTITIAL));
    }

    @Test
    public void testAdRendering() {
        List<String> renderingFeatures = Arrays.asList(RemoteConfigFeature.Rendering.VAST,
                RemoteConfigFeature.Rendering.MRAID);
        RemoteConfigModel configModel = createRemoteConfigModel();
        configModel.app_config.features.rendering = renderingFeatures;
        FeatureResolver featureResolver = new FeatureResolver(configModel);
        Assert.assertTrue(featureResolver.isRenderingSupported(RemoteConfigFeature.Rendering.VAST));
        Assert.assertTrue(featureResolver.isRenderingSupported(RemoteConfigFeature.Rendering.MRAID));
    }

    @Test
    public void testReporting() {
        List<String> reportingFeatures = Arrays.asList(RemoteConfigFeature.Reporting.AD_ERRORS,
                RemoteConfigFeature.Reporting.AD_EVENTS);
        RemoteConfigModel configModel = createRemoteConfigModel();
        configModel.app_config.features.reporting = reportingFeatures;
        FeatureResolver featureResolver = new FeatureResolver(configModel);
        Assert.assertTrue(featureResolver.isReportingModeEnabled(RemoteConfigFeature.Reporting.AD_EVENTS));
        Assert.assertTrue(featureResolver.isReportingModeEnabled(RemoteConfigFeature.Reporting.AD_ERRORS));
    }

    @Test
    public void testUserConsent() {
        List<String> consentFeatures = Arrays.asList(RemoteConfigFeature.UserConsent.CCPA,
                RemoteConfigFeature.UserConsent.GDPR);
        RemoteConfigModel configModel = createRemoteConfigModel();
        configModel.app_config.features.user_consent = consentFeatures;
        FeatureResolver featureResolver = new FeatureResolver(configModel);
        Assert.assertTrue(featureResolver.isUserConsentSupported(RemoteConfigFeature.UserConsent.CCPA));
        Assert.assertTrue(featureResolver.isUserConsentSupported(RemoteConfigFeature.UserConsent.GDPR));
    }

    private RemoteConfigModel createRemoteConfigModel() {
        RemoteConfigModel configModel = new RemoteConfigModel();
        configModel.app_config = new RemoteConfigAppConfig();
        configModel.app_config.features = new RemoteConfigAppFeatures();
        return configModel;
    }
}
