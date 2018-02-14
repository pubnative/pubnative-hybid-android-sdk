package net.pubnative.lite.sdk.tracking;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;

import java.io.IOException;
import java.util.List;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

class AppData extends AppDataSummary {

    private static final long startTimeMs = SystemClock.elapsedRealtime();

    final String appName;

    private final Context appContext;
    private final SessionTracker sessionTracker;

    protected final String packageName;

    AppData(Context appContext,
            Configuration config,
            SessionTracker sessionTracker) {
        super(appContext, config);
        this.appContext = appContext;
        this.sessionTracker = sessionTracker;
        appName = getAppName(appContext);
        packageName = getPackageName(appContext);
    }

    @Override
    public void toStream(JsonStream writer) throws IOException {
        writer.beginObject();
        serialiseMinimalAppData(writer);

        writer.name("id").value(packageName);
        writer.name("buildUUID").value(config.getBuildUUID());
        writer.name("duration").value(getDurationMs());
        long foregroundMs = sessionTracker.getDurationInForegroundMs(System.currentTimeMillis());
        writer.name("durationInForeground").value(foregroundMs);
        writer.name("inForeground").value(sessionTracker.isInForeground());

        writer.name("name").value(appName);
        writer.name("packageName").value(packageName);
        writer.name("versionName").value(versionName);
        writer.name("activeScreen").value(getActiveScreenClass());
        writer.name("memoryUsage").value(getMemoryUsage());
        writer.name("lowMemory").value(isLowMemory(appContext));
        writer.endObject();
    }

    private static String getAppName(Context appContext) {
        try {
            PackageManager packageManager = appContext.getPackageManager();
            String packageName = appContext.getPackageName();
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);

            return (String) packageManager.getApplicationLabel(appInfo);
        } catch (PackageManager.NameNotFoundException exception) {
            Logger.warn("Could not get app name");
        }
        return null;
    }

    String getActiveScreenClass() {
        try {
            ActivityManager activityManager =
                    (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasks =
                    activityManager.getRunningTasks(1);
            ActivityManager.RunningTaskInfo runningTask = tasks.get(0);
            return runningTask.topActivity.getClassName();
        } catch (Exception exception) {
            Logger.warn("Could not get active screen information,"
                    + " we recommend granting the 'android.permission.GET_TASKS' permission");
        }
        return null;
    }

    private static Long getMemoryUsage() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    private static Boolean isLowMemory(Context appContext) {
        try {
            ActivityManager activityManager =
                    (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memInfo);

            return memInfo.lowMemory;
        } catch (Exception exception) {
            Logger.warn("Could not check lowMemory status");
        }
        return null;
    }

    static long getDurationMs() {
        return SystemClock.elapsedRealtime() - startTimeMs;
    }

    private static String getPackageName(Context appContext) {
        return appContext.getPackageName();
    }
}
