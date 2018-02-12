package net.pubnative.lite.sdk.tracking;

import android.text.TextUtils;

import java.io.IOException;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public class Error implements JsonStream.Streamable {

    final Configuration config;
    private AppData appData;
    private DeviceData deviceData;
    private Breadcrumbs breadcrumbs;
    private User user;
    private final Throwable exception;
    private Severity severity = Severity.WARNING;
    private MetaData metaData = new MetaData();
    private String groupingHash;
    private String context;
    private final HandledState handledState;
    private final Session session;
    private final ThreadState threadState;

    Error(Configuration config, Throwable exception,
          HandledState handledState, Severity severity, Session session, ThreadState threadState) {
        this.threadState = threadState;
        this.config = config;
        this.exception = exception;
        this.handledState = handledState;
        this.severity = severity;
        this.session = session;
    }

    @Override
    public void toStream(JsonStream writer) throws IOException {
        // Merge error metaData into global metadata and apply filters
        MetaData mergedMetaData = MetaData.merge(config.getMetaData(), metaData);

        // Write error basics
        writer.beginObject();
        writer.name("context").value(getContext());
        writer.name("metaData").value(mergedMetaData);

        writer.name("severity").value(severity);
        writer.name("severityReason").value(handledState);
        writer.name("unhandled").value(handledState.isUnhandled());

        if (config.getProjectPackages() != null) {
            writer.name("projectPackages").beginArray();
            for (String projectPackage : config.getProjectPackages()) {
                writer.value(projectPackage);
            }
            writer.endArray();
        }

        // Write exception info
        writer.name("exceptions").value(new Exceptions(config, exception));

        // Write user info
        writer.name("user").value(user);

        // Write diagnostics
        writer.name("app").value(appData);
        writer.name("device").value(deviceData);
        writer.name("breadcrumbs").value(breadcrumbs);
        writer.name("groupingHash").value(groupingHash);

        if (config.getSendThreads()) {
            writer.name("threads").value(threadState);
        }

        if (session != null) {
            writer.name("session").beginObject();
            writer.name("id").value(session.getId());
            writer.name("startedAt").value(DateUtils.toIso8601(session.getStartedAt()));

            writer.name("events").beginObject();
            writer.name("handled").value(session.getHandledCount());
            writer.name("unhandled").value(session.getUnhandledCount());
            writer.endObject();
            writer.endObject();
        }

        writer.endObject();
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        if (!TextUtils.isEmpty(context)) {
            return context;
        } else if (config.getContext() != null) {
            return config.getContext();
        } else if (appData != null) {
            return appData.getActiveScreenClass();
        } else {
            return null;
        }
    }

    public void setGroupingHash(String groupingHash) {
        this.groupingHash = groupingHash;
    }

    public void setSeverity(Severity severity) {
        if (severity != null) {
            this.severity = severity;
            this.handledState.setCurrentSeverity(severity);
        }
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setUser(String id, String email, String name) {
        this.user = new User(id, email, name);
    }

    void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUserId(String id) {
        this.user = new User(this.user);
        this.user.setId(id);
    }

    public void setUserEmail(String email) {
        this.user = new User(this.user);
        this.user.setEmail(email);
    }

    public void setUserName(String name) {
        this.user = new User(this.user);
        this.user.setName(name);
    }

    public void addToTab(String tabName, String key, Object value) {
        metaData.addToTab(tabName, key, value);
    }

    public void clearTab(String tabName) {
        metaData.clearTab(tabName);
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        //noinspection ConstantConditions
        if (metaData == null) {
            this.metaData = new MetaData();
        } else {
            this.metaData = metaData;
        }
    }

    public String getExceptionName() {
        if (exception instanceof PNLiteCrashTrackerException) {
            return ((PNLiteCrashTrackerException) exception).getName();
        } else {
            return exception.getClass().getName();
        }
    }

    public String getExceptionMessage() {
        return exception.getLocalizedMessage();
    }

    public Throwable getException() {
        return exception;
    }

    public void setDeviceId(String id) {
        deviceData.id = id;
    }

    void setAppData(AppData appData) {
        this.appData = appData;
    }

    void setDeviceData(DeviceData deviceData) {
        this.deviceData = deviceData;
    }

    void setBreadcrumbs(Breadcrumbs breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

    boolean shouldIgnoreClass() {
        return config.shouldIgnoreClass(getExceptionName());
    }

    HandledState getHandledState() {
        return handledState;
    }

    static class Builder {
        private final Configuration config;
        private final Throwable exception;
        private final Session session;
        private final ThreadState threadState;
        private Severity severity = Severity.WARNING;
        private MetaData metaData;
        private String attributeValue;

        @HandledState.SeverityReason
        private String severityReasonType;

        Builder(Configuration config, Throwable exception, Session session) {
            this.threadState = new ThreadState(config);
            this.config = config;
            this.exception = exception;
            this.severityReasonType = HandledState.REASON_USER_SPECIFIED; // default

            if (session != null
                    && !config.shouldAutoCaptureSessions() && session.isAutoCaptured()) {
                this.session = null;
            } else {
                this.session = session;
            }
        }

        Builder(Configuration config, String name,
                String message, StackTraceElement[] frames, Session session) {
            this(config, new PNLiteCrashTrackerException(name, message, frames), session);
        }

        Builder severityReasonType(@HandledState.SeverityReason String severityReasonType) {
            this.severityReasonType = severityReasonType;
            return this;
        }

        Builder attributeValue(String value) {
            this.attributeValue = value;
            return this;
        }

        Builder severity(Severity severity) {
            this.severity = severity;
            return this;
        }

        Builder metaData(MetaData metaData) {
            this.metaData = metaData;
            return this;
        }

        Error build() {
            HandledState handledState =
                    HandledState.newInstance(severityReasonType, severity, attributeValue);
            Error error = new Error(config, exception, handledState,
                    severity, session, threadState);

            if (metaData != null) {
                error.setMetaData(metaData);
            }
            return error;
        }
    }
}
