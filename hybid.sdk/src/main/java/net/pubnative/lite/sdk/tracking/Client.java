package net.pubnative.lite.sdk.tracking;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public class Client extends Observable implements Observer {

    private static final boolean BLOCKING = true;
    private static final String SHARED_PREF_KEY = "net.pubnative.lite.tracking";
    private static final String PN_LITE_CRASH_NAMESPACE = "net.pubnative.lite.tracking";
    private static final String USER_ID_KEY = "user.id";
    private static final String USER_NAME_KEY = "user.name";
    private static final String USER_EMAIL_KEY = "user.email";

    static final String MF_API_KEY = PN_LITE_CRASH_NAMESPACE + ".API_KEY";
    static final String MF_BUILD_UUID = PN_LITE_CRASH_NAMESPACE + ".BUILD_UUID";
    static final String MF_APP_VERSION = PN_LITE_CRASH_NAMESPACE + ".APP_VERSION";
    static final String MF_ENDPOINT = PN_LITE_CRASH_NAMESPACE + ".ENDPOINT";
    static final String MF_SESSIONS_ENDPOINT = PN_LITE_CRASH_NAMESPACE + ".SESSIONS_ENDPOINT";
    static final String MF_RELEASE_STAGE = PN_LITE_CRASH_NAMESPACE + ".RELEASE_STAGE";
    static final String MF_SEND_THREADS = PN_LITE_CRASH_NAMESPACE + ".SEND_THREADS";
    static final String MF_ENABLE_EXCEPTION_HANDLER =
            PN_LITE_CRASH_NAMESPACE + ".ENABLE_EXCEPTION_HANDLER";
    static final String MF_PERSIST_USER_BETWEEN_SESSIONS =
            PN_LITE_CRASH_NAMESPACE + ".PERSIST_USER_BETWEEN_SESSIONS";
    static final String MF_AUTO_CAPTURE_SESSIONS =
            PN_LITE_CRASH_NAMESPACE + ".AUTO_CAPTURE_SESSIONS";

    protected final Configuration config;
    private final Context appContext;
    protected final AppData appData;
    protected final DeviceData deviceData;
    final Breadcrumbs breadcrumbs;
    protected final User user = new User();
    protected final ErrorStore errorStore;

    final SessionStore sessionStore;

    private final EventReceiver eventReceiver;
    final SessionTracker sessionTracker;
    private ErrorReportApiClient errorReportApiClient;
    private SessionTrackingApiClient sessionTrackingApiClient;

    public Client(Context androidContext) {
        this(androidContext, null, true);
    }

    public Client(Context androidContext, String apiKey) {
        this(androidContext, apiKey, true);
    }

    public Client(Context androidContext,
                  String apiKey,
                  boolean enableExceptionHandler) {
        this(androidContext,
                createNewConfiguration(androidContext, apiKey, enableExceptionHandler));
    }

    public Client(Context androidContext, Configuration configuration) {
        warnIfNotAppContext(androidContext);
        appContext = androidContext.getApplicationContext();
        config = configuration;
        sessionStore = new SessionStore(config, appContext);

        ConnectivityManager cm =
                (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient(cm);
        errorReportApiClient = defaultHttpClient;
        sessionTrackingApiClient = defaultHttpClient;

        sessionTracker =
                new SessionTracker(configuration, this, sessionStore, sessionTrackingApiClient);
        eventReceiver = new EventReceiver(this);

        // Set up and collect constant app and device diagnostics
        SharedPreferences sharedPref =
                appContext.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);

        appData = new AppData(appContext, config, sessionTracker);
        deviceData = new DeviceData(appContext, sharedPref);

        // Set up breadcrumbs
        breadcrumbs = new Breadcrumbs();

        // Set sensible defaults
        setProjectPackages(appContext.getPackageName());

        if (config.getPersistUserBetweenSessions()) {
            // Check to see if a user was stored in the SharedPreferences
            user.setId(sharedPref.getString(USER_ID_KEY, deviceData.getUserId()));
            user.setName(sharedPref.getString(USER_NAME_KEY, null));
            user.setEmail(sharedPref.getString(USER_EMAIL_KEY, null));
        } else {
            user.setId(deviceData.getUserId());
        }

        if (appContext instanceof Application) {
            Application application = (Application) appContext;
            application.registerActivityLifecycleCallbacks(sessionTracker);
        } else {
            Logger.warn("HyBidCrashTracker is unable to setup automatic activity lifecycle "
                    + "breadcrumbs on API Levels below 14.");
        }

        errorReportApiClient = new DefaultHttpClient(cm);

        // populate from manifest (in the case where the constructor was called directly by the
        // User or no UUID was supplied)
        if (config.getBuildUUID() == null) {
            String buildUuid = null;
            try {
                PackageManager packageManager = appContext.getPackageManager();
                String packageName = appContext.getPackageName();
                ApplicationInfo ai =
                        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                buildUuid = ai.metaData.getString(MF_BUILD_UUID);
            } catch (Exception ignore) {
                Logger.warn("HyBidCrashTracker is unable to read build UUID from manifest.");
            }
            if (buildUuid != null) {
                config.setBuildUUID(buildUuid);
            }
        }

        // Create the error store that is used in the exception handler
        errorStore = new ErrorStore(config, appContext);

        // Install a default exception handler with this client
        if (config.getEnableExceptionHandler()) {
            enableExceptionHandler();
        }

        // register a receiver for automatic breadcrumbs

        Async.run(new Runnable() {
            @Override
            public void run() {
                appContext.registerReceiver(eventReceiver, EventReceiver.getIntentFilter());
                appContext.registerReceiver(new ConnectivityChangeReceiver(),
                        new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            }
        });

        config.addObserver(this);

        // Flush any on-disk errors
        errorStore.flushOnLaunch(errorReportApiClient);

        boolean isNotProduction = !AppData.RELEASE_STAGE_PRODUCTION.equals(
                AppData.guessReleaseStage(appContext));
        Logger.setEnabled(isNotProduction);
    }

    private class ConnectivityChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            boolean retryReports = networkInfo != null && networkInfo.isConnectedOrConnecting();

            if (retryReports) {
                errorStore.flushAsync(errorReportApiClient);
            }
        }
    }

    public void notifyHyBidObservers(NotifyType type) {
        setChanged();
        super.notifyObservers(type.getValue());
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (arg instanceof Integer) {
            NotifyType type = NotifyType.fromInt((Integer) arg);

            if (type != null) {
                notifyHyBidObservers(type);
            }
        }
    }

    private static Configuration createNewConfiguration(Context androidContext,
                                                        String apiKey,
                                                        boolean enableExceptionHandler) {
        Context appContext = androidContext.getApplicationContext();

        // Attempt to load API key and other config from AndroidManifest.xml, if not passed in
        boolean loadFromManifest = TextUtils.isEmpty(apiKey);

        if (loadFromManifest) {
            try {
                PackageManager packageManager = appContext.getPackageManager();
                String packageName = appContext.getPackageName();
                ApplicationInfo ai =
                        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                Bundle data = ai.metaData;
                apiKey = data.getString(MF_API_KEY);
            } catch (Exception ignore) {
                Logger.warn("HyBidCrashTracker is unable to read api key from manifest.");
            }
        }

        if (apiKey == null) {
            throw new NullPointerException("You must provide a HyBidCrashTracker API key");
        }

        // Build a configuration object
        Configuration newConfig = new Configuration(apiKey);
        newConfig.setEnableExceptionHandler(enableExceptionHandler);

        if (loadFromManifest) {
            try {
                PackageManager packageManager = appContext.getPackageManager();
                String packageName = appContext.getPackageName();
                ApplicationInfo ai =
                        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                Bundle data = ai.metaData;
                populateConfigFromManifest(newConfig, data);
            } catch (Exception ignore) {
                Logger.warn("HyBidCrashTracker is unable to read config from manifest.");
            }
        }
        return newConfig;
    }

    static Configuration populateConfigFromManifest(Configuration config,
                                                    Bundle data) {
        config.setBuildUUID(data.getString(MF_BUILD_UUID));
        config.setAppVersion(data.getString(MF_APP_VERSION));
        config.setReleaseStage(data.getString(MF_RELEASE_STAGE));

        String endpoint = data.getString(MF_ENDPOINT);

        if (endpoint != null) {
            config.setEndpoint(endpoint);
        }
        String sessionEndpoint = data.getString(MF_SESSIONS_ENDPOINT);

        if (sessionEndpoint != null) {
            config.setSessionEndpoint(sessionEndpoint);
        }

        config.setSendThreads(data.getBoolean(MF_SEND_THREADS, true));
        config.setPersistUserBetweenSessions(
                data.getBoolean(MF_PERSIST_USER_BETWEEN_SESSIONS, false));
        config.setAutoCaptureSessions(data.getBoolean(MF_AUTO_CAPTURE_SESSIONS, false));
        config.setEnableExceptionHandler(
                data.getBoolean(MF_ENABLE_EXCEPTION_HANDLER, true));
        return config;
    }

    public String getContext() {
        return config.getContext();
    }

    public void setContext(String context) {
        config.setContext(context);
    }

    public void setEndpoint(String endpoint) {
        config.setEndpoint(endpoint);
    }

    public void setFilters(String... filters) {
        config.setFilters(filters);
    }

    public void setProjectPackages(String... projectPackages) {
        config.setProjectPackages(projectPackages);
    }

    public void setUser(String id, String email, String name) {
        setUserId(id);
        setUserEmail(email);
        setUserName(name);
    }

    public void setUserId(String id) {
        setUserId(id, true);
    }

    void setUserId(String id, boolean notify) {
        user.setId(id);

        if (config.getPersistUserBetweenSessions()) {
            storeInSharedPrefs(USER_ID_KEY, id);
        }

        if (notify) {
            notifyHyBidObservers(NotifyType.USER);
        }
    }

    public void setUserEmail(String email) {
        setUserEmail(email, true);
    }

    void setUserEmail(String email, boolean notify) {
        user.setEmail(email);

        if (config.getPersistUserBetweenSessions()) {
            storeInSharedPrefs(USER_EMAIL_KEY, email);
        }

        if (notify) {
            notifyHyBidObservers(NotifyType.USER);
        }
    }

    public void setUserName(String name) {
        setUserName(name, true);
    }

    void setUserName(String name, boolean notify) {
        user.setName(name);

        if (config.getPersistUserBetweenSessions()) {
            storeInSharedPrefs(USER_NAME_KEY, name);
        }

        if (notify) {
            notifyHyBidObservers(NotifyType.USER);
        }
    }

    @SuppressWarnings("ConstantConditions")
    void setErrorReportApiClient(ErrorReportApiClient errorReportApiClient) {
        if (errorReportApiClient == null) {
            throw new IllegalArgumentException("ErrorReportApiClient cannot be null.");
        }
        this.errorReportApiClient = errorReportApiClient;
    }

    @SuppressWarnings("ConstantConditions")
    void setSessionTrackingApiClient(SessionTrackingApiClient apiClient) {
        if (apiClient == null) {
            throw new IllegalArgumentException("SessionTrackingApiClient cannot be null.");
        }
        this.sessionTrackingApiClient = apiClient;
    }

    public void beforeNotify(BeforeNotify beforeNotify) {
        config.beforeNotify(beforeNotify);
    }

    public void beforeRecordBreadcrumb(BeforeRecordBreadcrumb beforeRecordBreadcrumb) {
        config.beforeRecordBreadcrumb(beforeRecordBreadcrumb);
    }

    public void notify(Throwable exception) {
        Error error = new Error.Builder(config, exception, sessionTracker.getCurrentSession())
                .severityReasonType(HandledState.REASON_HANDLED_EXCEPTION)
                .build();
        notify(error, !BLOCKING);
    }

    public void notify(Throwable exception, Callback callback) {
        Error error = new Error.Builder(config, exception, sessionTracker.getCurrentSession())
                .severityReasonType(HandledState.REASON_HANDLED_EXCEPTION)
                .build();
        notify(error, DeliveryStyle.ASYNC, callback);
    }

    public void notify(String name,
                       String message,
                       StackTraceElement[] stacktrace,
                       Callback callback) {
        Error error = new Error.Builder(config, name, message, stacktrace,
                sessionTracker.getCurrentSession())
                .severityReasonType(HandledState.REASON_HANDLED_EXCEPTION)
                .build();
        notify(error, DeliveryStyle.ASYNC, callback);
    }

    @Deprecated
    public void notify(Throwable exception, Severity severity,
                       MetaData metaData) {
        Error error = new Error.Builder(config, exception, sessionTracker.getCurrentSession())
                .metaData(metaData)
                .severity(severity)
                .build();
        notify(error, !BLOCKING);
    }

    @Deprecated
    public void notify(String name, String message,
                       StackTraceElement[] stacktrace, Severity severity,
                       MetaData metaData) {
        Error error = new Error.Builder(config, name, message,
                stacktrace, sessionTracker.getCurrentSession())
                .severity(severity)
                .metaData(metaData)
                .build();
        notify(error, !BLOCKING);
    }

    @Deprecated
    public void notify(String name,
                       String message,
                       String context,
                       StackTraceElement[] stacktrace,
                       Severity severity,
                       MetaData metaData) {
        Error error = new Error.Builder(config, name, message,
                stacktrace, sessionTracker.getCurrentSession())
                .severity(severity)
                .metaData(metaData)
                .build();
        error.setContext(context);
        notify(error, !BLOCKING);
    }

    private void notify(Error error, boolean blocking) {
        DeliveryStyle style = blocking ? DeliveryStyle.SAME_THREAD : DeliveryStyle.ASYNC;
        notify(error, style, null);
    }

    void notify(Error error,
                DeliveryStyle style,
                Callback callback) {
        // Don't notify if this error class should be ignored
        if (error.shouldIgnoreClass()) {
            return;
        }

        // Don't notify unless releaseStage is in notifyReleaseStages
        if (!config.shouldNotifyForReleaseStage(appData.getReleaseStage())) {
            return;
        }

        // Capture the state of the app and device and attach diagnostics to the error
        error.setAppData(appData);
        error.setDeviceData(deviceData);

        // Attach breadcrumbs to the error
        error.setBreadcrumbs(breadcrumbs);

        // Attach user info to the error
        error.setUser(user);

        // Run beforeNotify tasks, don't notify if any return true
        if (!runBeforeNotifyTasks(error)) {
            Logger.info("Skipping notification - beforeNotify task returned false");
            return;
        }

        // Build the report
        Report report = new Report(config.getApiKey(), error);

        if (callback != null) {
            callback.beforeNotify(report);
        }

        HandledState handledState = report.getError().getHandledState();

        if (handledState.isUnhandled()) {
            sessionTracker.incrementUnhandledError();
        } else {
            sessionTracker.incrementHandledError();
        }

        switch (style) {
            case SAME_THREAD:
                deliver(report, error);
                break;
            case ASYNC:
                final Report finalReport = report;
                final Error finalError = error;

                // Attempt to send the report in the background
                try {
                    Async.run(new Runnable() {
                        @Override
                        public void run() {
                            deliver(finalReport, finalError);
                        }
                    });
                } catch (RejectedExecutionException exception) {
                    errorStore.write(error);
                    Logger.warn("Exceeded max queue count, saving to disk to send later");
                }
                break;
            case ASYNC_WITH_CACHE:
                errorStore.write(error);
                errorStore.flushAsync(errorReportApiClient);
                break;
            default:
                break;
        }

        // Add a breadcrumb for this error occurring
        String exceptionMessage = error.getExceptionMessage();
        Map<String, String> message = Collections.singletonMap("message", exceptionMessage);
        breadcrumbs.add(new Breadcrumb(error.getExceptionName(), BreadcrumbType.ERROR, message));
    }

    public void notifyBlocking(Throwable exception) {
        Error error = new Error.Builder(config, exception, sessionTracker.getCurrentSession())
                .severityReasonType(HandledState.REASON_HANDLED_EXCEPTION)
                .build();
        notify(error, BLOCKING);
    }

    @Deprecated
    public void notifyBlocking(Throwable exception, Severity severity,
                               MetaData metaData) {
        Error error = new Error.Builder(config, exception, sessionTracker.getCurrentSession())
                .metaData(metaData)
                .severity(severity)
                .build();
        notify(error, BLOCKING);
    }

    @Deprecated
    public void notifyBlocking(String name,
                               String message,
                               StackTraceElement[] stacktrace,
                               Severity severity,
                               MetaData metaData) {
        Error error = new Error.Builder(config, name, message,
                stacktrace, sessionTracker.getCurrentSession())
                .severity(severity)
                .metaData(metaData)
                .build();
        notify(error, BLOCKING);
    }

    @Deprecated
    public void notifyBlocking(String name,
                               String message,
                               String context,
                               StackTraceElement[] stacktrace,
                               Severity severity,
                               MetaData metaData) {
        Error error = new Error.Builder(config, name, message,
                stacktrace, sessionTracker.getCurrentSession())
                .severity(severity)
                .metaData(metaData)
                .build();
        error.setContext(context);
        notify(error, BLOCKING);
    }

    public MetaData getMetaData() {
        return config.getMetaData();
    }

    public void setMetaData(MetaData metaData) {
        config.setMetaData(metaData);
    }

    public void leaveBreadcrumb(String breadcrumb) {
        Map<String, String> metaData = Collections.emptyMap();
        Breadcrumb crumb = new Breadcrumb(breadcrumb, BreadcrumbType.MANUAL, metaData);

        if (runBeforeBreadcrumbTasks(crumb)) {
            breadcrumbs.add(crumb);
            notifyHyBidObservers(NotifyType.BREADCRUMB);
        }
    }

    public void leaveBreadcrumb(String name,
                                BreadcrumbType type,
                                Map<String, String> metadata) {
        leaveBreadcrumb(name, type, metadata, true);
    }

    void leaveBreadcrumb(String name,
                         BreadcrumbType type,
                         Map<String, String> metadata,
                         boolean notify) {
        Breadcrumb crumb = new Breadcrumb(name, type, metadata);

        if (runBeforeBreadcrumbTasks(crumb)) {
            breadcrumbs.add(crumb);

            if (notify) {
                notifyHyBidObservers(NotifyType.BREADCRUMB);
            }
        }
    }

    public void enableExceptionHandler() {
        ExceptionHandler.enable(this);
    }

    public void disableExceptionHandler() {
        ExceptionHandler.disable(this);
    }

    void deliver(Report report, Error error) {
        try {
            errorReportApiClient.postReport(config.getEndpoint(), report,
                    config.getErrorApiHeaders());
            Logger.info("Sent 1 new error to HyBidCrashTracker");
        } catch (NetworkException exception) {
            Logger.info("Could not send error(s) to HyBidCrashTracker, saving to disk to send later");

            // Save error to disk for later sending
            errorStore.write(error);
        } catch (BadResponseException exception) {
            Logger.info("Bad response when sending data to HyBidCrashTracker");
        } catch (Exception exception) {
            Logger.warn("Problem sending error to HyBidCrashTracker", exception);
        }
    }

    void cacheAndNotify(Throwable exception, Severity severity, MetaData metaData,
                        @HandledState.SeverityReason String severityReason,
                        String attributeValue) {
        Error error = new Error.Builder(config, exception, sessionTracker.getCurrentSession())
                .severity(severity)
                .metaData(metaData)
                .severityReasonType(severityReason)
                .attributeValue(attributeValue)
                .build();

        notify(error, DeliveryStyle.ASYNC_WITH_CACHE, null);
    }

    private boolean runBeforeNotifyTasks(Error error) {
        for (BeforeNotify beforeNotify : config.getBeforeNotifyTasks()) {
            try {
                if (!beforeNotify.run(error)) {
                    return false;
                }
            } catch (Throwable ex) {
                Logger.warn("BeforeNotify threw an Exception", ex);
            }
        }

        // By default, allow the error to be sent if there were no objections
        return true;
    }

    private boolean runBeforeBreadcrumbTasks(Breadcrumb breadcrumb) {
        Collection<BeforeRecordBreadcrumb> tasks = config.getBeforeRecordBreadcrumbTasks();
        for (BeforeRecordBreadcrumb beforeRecordBreadcrumb : tasks) {
            try {
                if (!beforeRecordBreadcrumb.shouldRecord(breadcrumb)) {
                    return false;
                }
            } catch (Throwable ex) {
                Logger.warn("BeforeRecordBreadcrumb threw an Exception", ex);
            }
        }
        return true;
    }

    private void storeInSharedPrefs(String key, String value) {
        SharedPreferences sharedPref =
                appContext.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        sharedPref.edit().putString(key, value).apply();
    }

    @SuppressWarnings("checkstyle:NoFinalizer")
    protected void finalize() throws Throwable {
        if (eventReceiver != null) {
            try {
                appContext.unregisterReceiver(eventReceiver);
            } catch (IllegalArgumentException exception) {
                Logger.warn("Receiver not registered");
            }
        }
        super.finalize();
    }

    private static void warnIfNotAppContext(Context androidContext) {
        if (!(androidContext instanceof Application)) {
            Logger.warn("Warning - Non-Application context detected! Please ensure that you are "
                    + "initializing HyBidCrashTracker from a custom Application class.");
        }
    }

    public Configuration getConfig() {
        return config;
    }
}
