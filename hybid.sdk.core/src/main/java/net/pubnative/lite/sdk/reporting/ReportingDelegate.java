package net.pubnative.lite.sdk.reporting;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.analytics.ReportingEventCallback;
import net.pubnative.lite.sdk.config.ConfigManager;
import net.pubnative.lite.sdk.config.FeatureResolver;
import net.pubnative.lite.sdk.models.RemoteConfigFeature;
import net.pubnative.lite.sdk.utils.Logger;

import org.json.JSONObject;

public class ReportingDelegate implements ReportingEventCallback {
    private static final String TAG = ReportingDelegate.class.getSimpleName();
    private final Context mContext;
    private final ConfigManager mConfigManager;
    private final String mAppToken;

    public ReportingDelegate(Context context, ReportingController reportingController, ConfigManager configManager, String appToken) {
        mContext = context;
        mConfigManager = configManager;
        mAppToken = appToken;
        if (reportingController != null) {
            reportingController.addCallback(this);
        }
    }

    @Override
    public void onEvent(ReportingEvent event) {
        if (event != null) {
            if (mConfigManager != null) {
                FeatureResolver featureResolver = mConfigManager.getFeatureResolver();
                if (!TextUtils.isEmpty(event.getEventType())
                        && (event.getEventType().equals(Reporting.EventType.ERROR)
                        || event.getEventType().equals(Reporting.EventType.RENDER_ERROR))
                        && featureResolver.isReportingModeEnabled(RemoteConfigFeature.Reporting.AD_ERRORS)) {
                    reportEvent(event.getEventObject(), mAppToken);
                } else {
                    if (featureResolver.isReportingModeEnabled(RemoteConfigFeature.Reporting.AD_EVENTS)) {
                        reportEvent(event.getEventObject(), mAppToken);
                    }
                }
            }
        }
    }

    private void reportEvent(JSONObject jsonEvent, String appToken) {
        new LoggingRequest().doRequest(mContext, appToken, jsonEvent, new LoggingRequest.Listener() {
            @Override
            public void onLogSubmitted() {
                Logger.d(TAG, "Log submitted successfully");
            }

            @Override
            public void onLogError(Throwable error) {
                Logger.e(TAG, error.getMessage());
            }
        });
    }
}
