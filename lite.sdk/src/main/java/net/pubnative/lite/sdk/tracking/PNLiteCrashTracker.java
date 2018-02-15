package net.pubnative.lite.sdk.tracking;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.Map;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public final class PNLiteCrashTracker {
    @SuppressLint("StaticFieldLeak")
    static Client client;

    private PNLiteCrashTracker() {}

    public static Client init(Context androidContext) {
        client = new Client(androidContext);
        NativeInterface.configureClientObservers(client);
        return client;
    }

    public static Client init(Context androidContext, String apiKey) {
        client = new Client(androidContext, apiKey);
        NativeInterface.configureClientObservers(client);
        return client;
    }

    public static Client init(Context androidContext,
                              String apiKey,
                              boolean enableExceptionHandler) {
        client = new Client(androidContext, apiKey, enableExceptionHandler);
        NativeInterface.configureClientObservers(client);
        return client;
    }

    public static Client init(Context androidContext, Configuration config) {
        client = new Client(androidContext, config);
        NativeInterface.configureClientObservers(client);
        return client;
    }

    public static void setAppVersion(final String appVersion) {
        getClient().setAppVersion(appVersion);
    }

    public static String getContext() {
        return getClient().getContext();
    }

    public static void setContext(final String context) {
        getClient().setContext(context);
    }

    @Deprecated
    public static void setEndpoint(final String endpoint) {
        getClient().setEndpoint(endpoint);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public static void setBuildUUID(final String buildUuid) {
        getClient().setBuildUUID(buildUuid);
    }

    public static void setFilters(final String... filters) {
        getClient().setFilters(filters);
    }

    public static void setIgnoreClasses(final String... ignoreClasses) {
        getClient().setIgnoreClasses(ignoreClasses);
    }

    public static void setNotifyReleaseStages(final String... notifyReleaseStages) {
        getClient().setNotifyReleaseStages(notifyReleaseStages);
    }

    public static void setProjectPackages(final String... projectPackages) {
        getClient().setProjectPackages(projectPackages);
    }

    public static void setReleaseStage(final String releaseStage) {
        getClient().setReleaseStage(releaseStage);
    }

    public static void setSendThreads(final boolean sendThreads) {
        getClient().setSendThreads(sendThreads);
    }

    public static void setAutoCaptureSessions(boolean autoCapture) {
        getClient().setAutoCaptureSessions(autoCapture);
    }

    public static void setUser(final String id, final String email, final String name) {
        getClient().setUser(id, email, name);
    }

    public static void clearUser() {
        getClient().clearUser();
    }

    public static void setUserId(final String id) {
        getClient().setUserId(id);
    }

    public static void setUserEmail(final String email) {
        getClient().setUserEmail(email);
    }

    public static void setUserName(final String name) {
        getClient().setUserName(name);
    }

    public static void setErrorReportApiClient(ErrorReportApiClient errorReportApiClient) {
        getClient().setErrorReportApiClient(errorReportApiClient);
    }

    public static void setSessionTrackingApiClient(SessionTrackingApiClient apiClient) {
        getClient().setSessionTrackingApiClient(apiClient);
    }

    public static void beforeNotify(final BeforeNotify beforeNotify) {
        getClient().beforeNotify(beforeNotify);
    }

    public static void beforeRecordBreadcrumb(final BeforeRecordBreadcrumb beforeRecordBreadcrumb) {
        getClient().beforeRecordBreadcrumb(beforeRecordBreadcrumb);
    }

    public static void notify(final Throwable exception) {
        getClient().notify(exception);
    }

    public static void notify(final Throwable exception, final Callback callback) {
        getClient().notify(exception, callback);
    }

    public static void notify(String name,
                              String message,
                              StackTraceElement[] stacktrace,
                              Callback callback) {
        getClient().notify(name, message, stacktrace, callback);
    }

    public static void notify(final Throwable exception, final Severity severity) {
        getClient().notify(exception, severity);
    }

    public static void notify(final Throwable exception,
                              final MetaData metaData) {
        getClient().notify(exception, new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.getError().setMetaData(metaData);
            }
        });
    }

    @Deprecated
    public static void notify(final Throwable exception, final Severity severity,
                              final MetaData metaData) {
        getClient().notify(exception, new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.getError().setSeverity(severity);
                report.getError().setMetaData(metaData);
            }
        });
    }

    @Deprecated
    public static void notify(String name, String message,
                              StackTraceElement[] stacktrace, Severity severity,
                              MetaData metaData) {
        final Severity finalSeverity = severity;
        final MetaData finalMetaData = metaData;
        getClient().notify(name, message, stacktrace, new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.getError().setSeverity(finalSeverity);
                report.getError().setMetaData(finalMetaData);
            }
        });
    }

    @Deprecated
    public static void notify(String name, String message, String context,
                              StackTraceElement[] stacktrace, Severity severity,
                              MetaData metaData) {
        final String finalContext = context;
        final Severity finalSeverity = severity;
        final MetaData finalMetaData = metaData;
        getClient().notify(name, message, stacktrace, new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.getError().setSeverity(finalSeverity);
                report.getError().setMetaData(finalMetaData);
                report.getError().setContext(finalContext);
            }
        });
    }

    public static void internalClientNotify(final Throwable exception,
                                            Map<String, Object> clientData,
                                            boolean blocking,
                                            Callback callback) {
        getClient().internalClientNotify(exception, clientData, blocking, callback);
    }

    public static void addToTab(final String tab, final String key, final Object value) {
        getClient().addToTab(tab, key, value);
    }

    public static void clearTab(String tabName) {
        getClient().clearTab(tabName);
    }

    public static MetaData getMetaData() {
        return getClient().getMetaData();
    }

    public static void setMetaData(final MetaData metaData) {
        getClient().setMetaData(metaData);
    }

    public static void leaveBreadcrumb(String message) {
        getClient().leaveBreadcrumb(message);
    }

    public static void leaveBreadcrumb(String name,
                                       BreadcrumbType type, Map<String, String> metadata) {
        getClient().leaveBreadcrumb(name, type, metadata);
    }

    public static void setMaxBreadcrumbs(int numBreadcrumbs) {
        getClient().setMaxBreadcrumbs(numBreadcrumbs);
    }

    public static void clearBreadcrumbs() {
        getClient().clearBreadcrumbs();
    }

    public static void enableExceptionHandler() {
        getClient().enableExceptionHandler();
    }

    public static void disableExceptionHandler() {
        getClient().disableExceptionHandler();
    }

    public static void setLoggingEnabled(boolean enabled) {
        getClient().setLoggingEnabled(enabled);
    }

    public static void startSession() {
        getClient().startSession();
    }

    public static Client getClient() {
        if (client == null) {
            throw new IllegalStateException("You must call PNLiteCrashTracker.init before any"
                    + " other PNLiteCrashTracker methods");
        }

        return client;
    }
}
