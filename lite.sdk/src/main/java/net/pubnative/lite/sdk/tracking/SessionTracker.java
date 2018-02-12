package net.pubnative.lite.sdk.tracking;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public class SessionTracker implements Application.ActivityLifecycleCallbacks {

    private static final String KEY_LIFECYCLE_CALLBACK = "ActivityLifecycle";
    private static final int DEFAULT_TIMEOUT_MS = 30000;

    private final ConcurrentHashMap<String, Boolean>
            foregroundActivities = new ConcurrentHashMap<>();
    private final Configuration configuration;
    private final long timeoutMs;
    private final Client client;
    private final SessionStore sessionStore;
    private final SessionTrackingApiClient apiClient;

    // This most recent time an Activity was stopped.
    private AtomicLong activityLastStoppedAtMs = new AtomicLong(0);

    // The first Activity in this 'session' was started at this time.
    private AtomicLong activityFirstStartedAtMs = new AtomicLong(0);
    private AtomicReference<Session> currentSession = new AtomicReference<>();
    private Semaphore flushingRequest = new Semaphore(1);

    SessionTracker(Configuration configuration, Client client, SessionStore sessionStore,
                   SessionTrackingApiClient apiClient) {
        this(configuration, client, DEFAULT_TIMEOUT_MS, sessionStore, apiClient);
    }

    SessionTracker(Configuration configuration, Client client, long timeoutMs,
                   SessionStore sessionStore, SessionTrackingApiClient apiClient) {
        this.configuration = configuration;
        this.client = client;
        this.timeoutMs = timeoutMs;
        this.sessionStore = sessionStore;
        this.apiClient = apiClient;
    }

    void startNewSession(Date date, User user, boolean autoCaptured) {
        Session session = new Session(UUID.randomUUID().toString(), date, user, autoCaptured);
        currentSession.set(session);
        trackSessionIfNeeded(session);
    }

    private void trackSessionIfNeeded(final Session session) {
        boolean notifyForRelease = configuration.shouldNotifyForReleaseStage(getReleaseStage());

        if (notifyForRelease
                && (configuration.shouldAutoCaptureSessions() || !session.isAutoCaptured())
                && session.isTracked().compareAndSet(false, true)) {
            try {
                final String endpoint = configuration.getSessionEndpoint();
                Async.run(new Runnable() {
                    @Override
                    public void run() {
                        //FUTURE:SM It would be good to optimise this
                        flushStoredSessions();

                        SessionTrackingPayload payload =
                                new SessionTrackingPayload(session, client.appData);

                        try {
                            apiClient.postSessionTrackingPayload(endpoint, payload,
                                    configuration.getSessionApiHeaders());
                        } catch (NetworkException exception) { // store for later sending
                            Logger.info("Failed to post session payload");
                            sessionStore.write(session);
                        } catch (BadResponseException exception) { // drop bad data
                            Logger.warn("Invalid session tracking payload", exception);
                        }
                    }
                });
            } catch (RejectedExecutionException exception) {
                // This is on the current thread but there isn't much else we can do
                sessionStore.write(session);
            }
        }
    }

    void onAutoCaptureEnabled() {
        Session session = currentSession.get();
        if (session != null && !foregroundActivities.isEmpty()) {
            // If there is no session we will wait for one to be created
            trackSessionIfNeeded(session);
        }
    }

    private String getReleaseStage() {
        return client.appData.getReleaseStage();
    }

    Session getCurrentSession() {
        return currentSession.get();
    }

    void incrementUnhandledError() {
        Session session = currentSession.get();
        if (session != null) {
            session.incrementUnhandledErrCount();
        }
    }

    void incrementHandledError() {
        Session session = currentSession.get();
        if (session != null) {
            session.incrementHandledErrCount();
        }
    }

    void flushStoredSessions() {
        if (flushingRequest.tryAcquire(1)) {
            try {
                List<File> storedFiles;

                storedFiles = sessionStore.findStoredFiles();

                if (!storedFiles.isEmpty()) {
                    SessionTrackingPayload payload =
                            new SessionTrackingPayload(storedFiles, client.appData);

                    //FUTURE:SM Reduce duplication here and above
                    try {
                        final String endpoint = configuration.getSessionEndpoint();
                        apiClient.postSessionTrackingPayload(endpoint, payload,
                                configuration.getSessionApiHeaders());
                        deleteStoredFiles(storedFiles);
                    } catch (NetworkException exception) { // store for later sending
                        Logger.info("Failed to post stored session payload");
                    } catch (BadResponseException exception) { // drop bad data
                        Logger.warn("Invalid session tracking payload", exception);
                        deleteStoredFiles(storedFiles);
                    }
                }
            } finally {
                flushingRequest.release(1);
            }
        }
    }

    private void deleteStoredFiles(Collection<File> storedFiles) {
        for (File storedFile : storedFiles) {
            storedFile.delete();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        leaveLifecycleBreadcrumb(getActivityName(activity), "onCreate()");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        String activityName = getActivityName(activity);
        leaveLifecycleBreadcrumb(activityName, "onStart()");
        updateForegroundTracker(activityName, true, System.currentTimeMillis());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        leaveLifecycleBreadcrumb(getActivityName(activity), "onResume()");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        leaveLifecycleBreadcrumb(getActivityName(activity), "onPause()");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        String activityName = getActivityName(activity);
        leaveLifecycleBreadcrumb(activityName, "onStop()");
        updateForegroundTracker(activityName, false, System.currentTimeMillis());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        leaveLifecycleBreadcrumb(getActivityName(activity), "onSaveInstanceState()");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        leaveLifecycleBreadcrumb(getActivityName(activity), "onDestroy()");
    }

    private String getActivityName(Activity activity) {
        return activity.getClass().getSimpleName();
    }

    void leaveLifecycleBreadcrumb(String activityName, String lifecycleCallback) {
        leaveBreadcrumb(activityName, lifecycleCallback);
    }

    private void leaveBreadcrumb(String activityName, String lifecycleCallback) {
        if (configuration.isAutomaticallyCollectingBreadcrumbs()) {
            Map<String, String> metadata = new HashMap<>();
            metadata.put(KEY_LIFECYCLE_CALLBACK, lifecycleCallback);
            client.leaveBreadcrumb(activityName, BreadcrumbType.NAVIGATION, metadata);
        }
    }

    void startFirstSession(Activity activity) {
        Session session = currentSession.get();
        if (session == null) {
            long nowMs = System.currentTimeMillis();
            activityFirstStartedAtMs.set(nowMs);
            startNewSession(new Date(nowMs), client.user, true);
            foregroundActivities.put(getActivityName(activity), true);
        }
    }

    void updateForegroundTracker(String activityName, boolean activityStarting, long nowMs) {
        if (activityStarting) {
            long noActivityRunningForMs = nowMs - activityLastStoppedAtMs.get();

            //FUTURE:SM Race condition between isEmpty and put
            if (foregroundActivities.isEmpty()
                    && noActivityRunningForMs >= timeoutMs
                    && configuration.shouldAutoCaptureSessions()) {

                activityFirstStartedAtMs.set(nowMs);
                startNewSession(new Date(nowMs), client.user, true);
            }
            foregroundActivities.put(activityName, true);
        } else {
            foregroundActivities.remove(activityName);
            activityLastStoppedAtMs.set(nowMs);
        }
    }

    boolean isInForeground() {
        return !foregroundActivities.isEmpty();
    }

    long getDurationInForegroundMs(long nowMs) {
        long durationMs = 0;
        long sessionStartTimeMs = activityFirstStartedAtMs.get();

        if (isInForeground() && sessionStartTimeMs != 0) {
            durationMs = nowMs - sessionStartTimeMs;
        }
        return durationMs > 0 ? durationMs : 0;
    }
}
