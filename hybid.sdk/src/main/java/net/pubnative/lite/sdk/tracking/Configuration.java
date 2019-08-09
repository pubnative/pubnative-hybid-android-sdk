package net.pubnative.lite.sdk.tracking;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public class Configuration extends Observable implements Observer {

    private static final String HEADER_API_PAYLOAD_VERSION = "Bugsnag-Payload-Version";
    private static final String HEADER_API_KEY = "Bugsnag-Api-Key";
    private static final String HEADER_BUGSNAG_SENT_AT = "Bugsnag-Sent-At";

    private final String apiKey;
    private String buildUuid;
    private String appVersion;
    private String context;
    private String endpoint = "https://notify.bugsnag.com";
    private String sessionEndpoint = "https://sessions.bugsnag.com";

    private String[] filters = new String[]{"password"};
    private String[] ignoreClasses;
    private String[] notifyReleaseStages = null;
    private String[] projectPackages;
    private String releaseStage;
    private boolean sendThreads = true;
    private boolean enableExceptionHandler = true;
    private boolean persistUserBetweenSessions = false;
    private long launchCrashThresholdMs = 5 * 1000;
    private boolean autoCaptureSessions = false;
    private boolean automaticallyCollectBreadcrumbs = true;

    String defaultExceptionType = "android";

    private MetaData metaData;
    private final Collection<BeforeNotify> beforeNotifyTasks = new LinkedHashSet<>();
    private final Collection<BeforeRecordBreadcrumb> beforeRecordBreadcrumbTasks
            = new LinkedHashSet<>();
    private String codeBundleId;
    private String notifierType;

    public Configuration(String apiKey) {
        this.apiKey = apiKey;
        this.metaData = new MetaData();
        this.metaData.addObserver(this);
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
        notifyHyBidObservers(NotifyType.APP);
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
        notifyHyBidObservers(NotifyType.CONTEXT);
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getSessionEndpoint() {
        return sessionEndpoint;
    }

    public void setSessionEndpoint(String endpoint) {
        this.sessionEndpoint = endpoint;
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public String getBuildUUID() {
        return buildUuid;
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void setBuildUUID(String buildUuid) {
        this.buildUuid = buildUuid;
        notifyHyBidObservers(NotifyType.APP);
    }

    public String[] getFilters() {
        return filters;
    }

    public void setFilters(String[] filters) {
        this.filters = filters;
        this.metaData.setFilters(filters);
    }

    public void setIgnoreClasses(String[] ignoreClasses) {
        this.ignoreClasses = ignoreClasses;
    }

    public void setNotifyReleaseStages(String[] notifyReleaseStages) {
        this.notifyReleaseStages = notifyReleaseStages;
        notifyHyBidObservers(NotifyType.RELEASE_STAGES);
    }


    public String[] getProjectPackages() {
        return projectPackages;
    }

    public void setProjectPackages(String[] projectPackages) {
        this.projectPackages = projectPackages;
    }

    public String getReleaseStage() {
        return releaseStage;
    }

    public void setReleaseStage(String releaseStage) {
        this.releaseStage = releaseStage;
        notifyHyBidObservers(NotifyType.APP);
    }

    public boolean getSendThreads() {
        return sendThreads;
    }

    public void setSendThreads(boolean sendThreads) {
        this.sendThreads = sendThreads;
    }

    public boolean getEnableExceptionHandler() {
        return enableExceptionHandler;
    }

    public void setEnableExceptionHandler(boolean enableExceptionHandler) {
        this.enableExceptionHandler = enableExceptionHandler;
    }

    public boolean shouldAutoCaptureSessions() {
        return autoCaptureSessions;
    }

    public void setAutoCaptureSessions(boolean autoCapture) {
        this.autoCaptureSessions = autoCapture;
    }

    protected MetaData getMetaData() {
        return metaData;
    }

    protected void setMetaData(MetaData metaData) {
        this.metaData.deleteObserver(this);

        //noinspection ConstantConditions
        if (metaData == null) {
            this.metaData = new MetaData();
        } else {
            this.metaData = metaData;
        }

        this.metaData.addObserver(this);
        notifyHyBidObservers(NotifyType.META);
    }

    protected Collection<BeforeNotify> getBeforeNotifyTasks() {
        return beforeNotifyTasks;
    }

    public boolean getPersistUserBetweenSessions() {
        return persistUserBetweenSessions;
    }

    public void setPersistUserBetweenSessions(boolean persistUserBetweenSessions) {
        this.persistUserBetweenSessions = persistUserBetweenSessions;
    }

    public long getLaunchCrashThresholdMs() {
        return launchCrashThresholdMs;
    }

    public void setLaunchCrashThresholdMs(long launchCrashThresholdMs) {
        if (launchCrashThresholdMs <= 0) {
            this.launchCrashThresholdMs = 0;
        } else {
            this.launchCrashThresholdMs = launchCrashThresholdMs;
        }
    }

    public boolean isAutomaticallyCollectingBreadcrumbs() {
        return automaticallyCollectBreadcrumbs;
    }

    public void setAutomaticallyCollectBreadcrumbs(boolean automaticallyCollectBreadcrumbs) {
        this.automaticallyCollectBreadcrumbs = automaticallyCollectBreadcrumbs;
    }

    String getCodeBundleId() {
        return codeBundleId;
    }

    String getNotifierType() {
        return notifierType;
    }

    Map<String, String> getErrorApiHeaders() {
        Map<String, String> map = new HashMap<>();
        map.put(HEADER_API_PAYLOAD_VERSION, "4.0");
        map.put(HEADER_API_KEY, apiKey);
        map.put(HEADER_BUGSNAG_SENT_AT, DateUtils.toIso8601(new Date()));
        return map;
    }

    Map<String, String> getSessionApiHeaders() {
        Map<String, String> map = new HashMap<>();
        map.put(HEADER_API_PAYLOAD_VERSION, "1.0");
        map.put(HEADER_API_KEY, apiKey);
        map.put(HEADER_BUGSNAG_SENT_AT, DateUtils.toIso8601(new Date()));
        return map;
    }

    protected boolean shouldNotifyForReleaseStage(String releaseStage) {
        if (this.notifyReleaseStages == null) {
            return true;
        }

        List<String> stages = Arrays.asList(this.notifyReleaseStages);
        return stages.contains(releaseStage);
    }

    protected boolean shouldIgnoreClass(String className) {
        if (this.ignoreClasses == null) {
            return false;
        }

        List<String> classes = Arrays.asList(this.ignoreClasses);
        return classes.contains(className);
    }

    protected void beforeNotify(BeforeNotify beforeNotify) {
        this.beforeNotifyTasks.add(beforeNotify);
    }

    protected void beforeRecordBreadcrumb(BeforeRecordBreadcrumb beforeRecordBreadcrumb) {
        this.beforeRecordBreadcrumbTasks.add(beforeRecordBreadcrumb);
    }

    protected boolean inProject(String className) {
        if (projectPackages != null) {
            for (String packageName : projectPackages) {
                if (packageName != null && className.startsWith(packageName)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void notifyHyBidObservers(NotifyType type) {
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

    protected Collection<BeforeRecordBreadcrumb> getBeforeRecordBreadcrumbTasks() {
        return beforeRecordBreadcrumbTasks;
    }
}
