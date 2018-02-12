package net.pubnative.lite.sdk.tracking;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.IOException;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

class AppDataSummary implements JsonStream.Streamable {

    static final String RELEASE_STAGE_DEVELOPMENT = "development";
    static final String RELEASE_STAGE_PRODUCTION = "production";

    protected final Configuration config;

    protected final Integer versionCode;

    protected final String versionName;

    private final String guessedReleaseStage;

    private String notifierType = "android";

    private String codeBundleId;

    AppDataSummary(Context appContext, Configuration config) {
        versionCode = getVersionCode(appContext);
        versionName = getVersionName(appContext);
        guessedReleaseStage = guessReleaseStage(appContext);
        this.config = config;

        codeBundleId = config.getCodeBundleId();
        String configType = config.getNotifierType();

        if (configType != null) {
            notifierType = configType;
        }
    }

    @Override
    public void toStream(JsonStream writer) throws IOException {
        writer.beginObject();
        serialiseMinimalAppData(writer);
        writer.endObject();
    }

    void serialiseMinimalAppData(JsonStream writer) throws IOException {
        writer
                .name("type").value(notifierType)
                .name("releaseStage").value(getReleaseStage())
                .name("version").value(getAppVersion())
                .name("versionCode").value(versionCode)
                .name("codeBundleId").value(codeBundleId);
    }

    String getReleaseStage() {
        if (config.getReleaseStage() != null) {
            return config.getReleaseStage();
        } else {
            return guessedReleaseStage;
        }
    }

    String getAppVersion() {
        if (config.getAppVersion() != null) {
            return config.getAppVersion();
        } else {
            return versionName;
        }
    }

    private static Integer getVersionCode(Context appContext) {
        try {
            String packageName = appContext.getPackageName();
            return appContext.getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException exception) {
            Logger.warn("Could not get versionCode");
        }
        return null;
    }

    private static String getVersionName(Context appContext) {
        try {
            String packageName = appContext.getPackageName();
            return appContext.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException exception) {
            Logger.warn("Could not get versionName");
        }
        return null;
    }
    
    static String guessReleaseStage(Context appContext) {
        try {
            String packageName = appContext.getPackageName();
            PackageManager packageManager = appContext.getPackageManager();
            int appFlags = packageManager.getApplicationInfo(packageName, 0).flags;
            if ((appFlags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                return RELEASE_STAGE_DEVELOPMENT;
            }
        } catch (PackageManager.NameNotFoundException exception) {
            Logger.warn("Could not get releaseStage");
        }
        return RELEASE_STAGE_PRODUCTION;
    }
}
