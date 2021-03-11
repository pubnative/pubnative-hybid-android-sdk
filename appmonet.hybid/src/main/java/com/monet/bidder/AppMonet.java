package com.monet.bidder;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.utils.Logger;

/**
 * The {@code AppMonet} class contains static methods that are entry points to the AppMonet library.
 * All interactions will happen through this class.
 */
public class AppMonet {
    private static final String TAG = AppMonet.class.getSimpleName();

    private AppMonet() {
    }

    /**
     * This method initializes the AppMonet library and all its internal components.
     * <p/>
     * You need to define {@code appmonet.application.id} {@code meta-data} in your
     * {@code AndroidManifest.xml}:
     * <pre>
     * &lt;manifest ...&gt;
     *
     * ...
     *
     *   &lt;application ...&gt;
     *     &lt;meta-data
     *       android:name="appmonet.application.id"
     *       android:value="@string/app_monet_app_id" /&gt;
     *
     *       ...
     *
     *   &lt;/application&gt;
     * &lt;/manifest&gt;
     * </pre>
     * <p/>
     * This must be called before your application can use the AppMonet library. The recommended
     * way is to call {@code AppMonet.init} at the {@code Application}'s {@code onCreate} method:
     * <p/>
     * <pre>
     * public class YourApplication extends Application {
     *     public void onCreate() {
     *          AppMonet.init(this);
     *     }
     * }
     * </pre>
     *
     * @param context The context ({@link Context}) of your application.
     */
    public static void init(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String appToken = applicationInfo.metaData
                    .getString("appmonet.application.id", "");
            init(context, new AppMonetConfiguration.Builder().applicationId(appToken).build());
        } catch (PackageManager.NameNotFoundException exception) {
            Logger.e(TAG, "Error initialising the AppMonet SDK: ", exception);
        }
    }

    /**
     * This method initializes the AppMonet library and all its internal components.
     * <p/>
     * This method is required if you do not wish to define {@code appmonet.application.id}
     * {@code meta-data} in your {@code AndroidManifest.xml}
     * <p/>
     * This must be called before your application can use the AppMonet library. The recommended
     * way is to call {@code AppMonet.init} at the {@code Application}'s {@code onCreate} method:
     * <p/>
     * <pre>
     * public class YourApplication extends Application {
     *     public void onCreate() {
     *          AppMonet.init(this, &quot;AppMonet application id&quot;);
     *     }
     * }
     * </pre>
     *
     * @param context               The context ({@link Context}) of your application.
     * @param appMonetConfiguration The application configurations needed to initialize the sdk.
     */
    public static void init(Context context, AppMonetConfiguration appMonetConfiguration) {
        AppMonetConfiguration internalConfiguration = appMonetConfiguration;
        if (appMonetConfiguration == null) {
            internalConfiguration = new AppMonetConfiguration.Builder().build();
        }
        if (TextUtils.isEmpty(internalConfiguration.applicationId)) {
            Logger.e(TAG, "Error initialising the AppMonet SDK. Invalid application ID provided");
        } else {
            HyBid.initialize(internalConfiguration.applicationId, (Application) context.getApplicationContext());
        }
    }

    /**
     * Register application state callbacks (e.g. foreground/background)
     *
     * @param application your Application instance
     */
    public static void registerCallbacks(Application application) {

    }

    /**
     * This method allows you to enable or disable verbose logging coming from the AppMonet library.
     *
     * @param verboseLogging This boolean indicates if verbose logging should be activated.
     */
    public static void enableVerboseLogging(boolean verboseLogging) {
        HyBid.setLogLevel(verboseLogging ? Logger.Level.verbose : Logger.Level.info);
    }

    /**
     * This method allows the SDK to get test demand that always fills. Use it only during development.
     */
    public static void testMode() {
        HyBid.setTestMode(true);
    }

    /**
     * Check if the AppMonet SDK has already been initialized
     *
     * @return true of init() has already successfully been called
     */
    public static boolean isInitialized() {
        return HyBid.isInitialized();
    }

    /**
     * Set the level of the logger
     *
     * @param level a log level (integer)
     */
    public static void setLogLevel(int level) {
        switch (level) {
            case 1:
                HyBid.setLogLevel(Logger.Level.verbose);
                break;
            case 2:
                HyBid.setLogLevel(Logger.Level.debug);
                break;
            case 3:
                HyBid.setLogLevel(Logger.Level.info);
                break;
            case 4:
                HyBid.setLogLevel(Logger.Level.warning);
                break;
            case 5:
                HyBid.setLogLevel(Logger.Level.error);
                break;
            case 6:
                HyBid.setLogLevel(Logger.Level.none);
                break;
            default:
                HyBid.setLogLevel(Logger.Level.info);
        }
    }
}
