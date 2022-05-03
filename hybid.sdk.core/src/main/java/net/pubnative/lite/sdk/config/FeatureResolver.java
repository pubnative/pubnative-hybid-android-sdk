package net.pubnative.lite.sdk.config;

import android.text.TextUtils;

import net.pubnative.lite.sdk.models.RemoteConfigAppFeatures;
import net.pubnative.lite.sdk.models.RemoteConfigFeature;
import net.pubnative.lite.sdk.models.RemoteConfigModel;

import java.util.List;

public class FeatureResolver {
    private final RemoteConfigAppFeatures mAppFeaturesModel;

    public FeatureResolver(RemoteConfigModel configModel) {
        if (configModel != null && configModel.app_config != null && configModel.app_config.features != null) {
            this.mAppFeaturesModel = configModel.app_config.features;
        } else {
            this.mAppFeaturesModel = null;
        }
    }

    public boolean isAdFormatEnabled(String feature) {
        if (TextUtils.isEmpty(feature)) {
            return false;
        }

        if (mAppFeaturesModel != null
                && mAppFeaturesModel.ad_formats != null) {
            List<String> supportedFormats = mAppFeaturesModel.ad_formats;
            return supportedFormats.contains(feature);
        } else {
            return true;
        }
    }

    public boolean isRenderingSupported(String feature) {
        if (TextUtils.isEmpty(feature)) {
            return false;
        }

        if (mAppFeaturesModel != null
                && mAppFeaturesModel.rendering != null) {
            List<String> supportedRendering = mAppFeaturesModel.rendering;
            return supportedRendering.contains(feature);
        } else {
            return true;
        }
    }

    public boolean isReportingModeEnabled(String feature) {
        if (TextUtils.isEmpty(feature)) {
            return false;
        }

        if (mAppFeaturesModel != null
                && mAppFeaturesModel.reporting != null) {
            List<String> supportedReporting = mAppFeaturesModel.reporting;
            return supportedReporting.contains(feature);
        } else {
            return !feature.equals(RemoteConfigFeature.Reporting.AD_EVENTS);
        }
    }

    public boolean isDiagnosticsModeEnabled(String feature) {
        if (TextUtils.isEmpty(feature)) {
            return false;
        }

        if (mAppFeaturesModel != null
                && mAppFeaturesModel.reporting != null) {
            List<String> supportedReporting = mAppFeaturesModel.reporting;
            return supportedReporting.contains(feature);
        } else {
            return !feature.equals(RemoteConfigFeature.Reporting.DIAGNOSTIC_REPORT);
        }
    }

    public boolean isUserConsentSupported(String feature) {
        if (TextUtils.isEmpty(feature)) {
            return false;
        }

        if (mAppFeaturesModel != null
                && mAppFeaturesModel.user_consent != null) {
            List<String> supportedConsent = mAppFeaturesModel.user_consent;
            return supportedConsent.contains(feature);
        } else {
            return true;
        }
    }
}
