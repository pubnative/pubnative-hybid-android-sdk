package net.pubnative.lite.sdk.tracking;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.Map;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public final class HyBidCrashTracker {
    @SuppressLint("StaticFieldLeak")
    static Client client;

    private HyBidCrashTracker() {
    }

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

    public static void setFilters(final String... filters) {
        getClient().setFilters(filters);
    }

    public static void setErrorReportApiClient(ErrorReportApiClient errorReportApiClient) {
        getClient().setErrorReportApiClient(errorReportApiClient);
    }

    public static void beforeNotify(final BeforeNotify beforeNotify) {
        getClient().beforeNotify(beforeNotify);
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

    public static MetaData getMetaData() {
        return getClient().getMetaData();
    }

    public static void setMetaData(final MetaData metaData) {
        getClient().setMetaData(metaData);
    }

    public static Client getClient() {
        if (client == null) {
            throw new IllegalStateException("You must call HyBidCrashTracker.init before any"
                    + " other HyBidCrashTracker methods");
        }

        return client;
    }
}
