package net.pubnative.lite.sdk.tracking;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public class NativeInterface {
    @SuppressLint("StaticFieldLeak")
    private static Client client;

    private static Client getClient() {
        if (client != null) {
            return client;
        } else {
            return HyBidCrashTracker.getClient();
        }
    }

    public static void setClient(Client client) {
        NativeInterface.client = client;
        configureClientObservers(client);
    }

    public static void configureClientObservers(Client client) {

        // Ensure that the bugsnag observer is registered
        // Should only happen if the NDK library is present
        try {
            String className = "com.bugsnag.android.ndk.BugsnagObserver";
            Class clz = Class.forName(className);
            Observer observer = (Observer) clz.newInstance();
            client.addObserver(observer);
        } catch (ClassNotFoundException exception) {
            // ignore this one, will happen if the NDK plugin is not present
            Logger.info("Bugsnag NDK integration not available");
        } catch (InstantiationException exception) {
            Logger.warn("Failed to instantiate NDK observer", exception);
        } catch (IllegalAccessException exception) {
            Logger.warn("Could not access NDK observer", exception);
        }

        // Should make NDK components configure
        client.notifyHyBidObservers(NotifyType.ALL);
    }

    public static String getContext() {
        return getClient().getContext();
    }

    public static Map<String, Object> getMetaData() {
        return getClient().getMetaData().store;
    }

    public static String[] getFilters() {
        return getClient().config.getFilters();
    }
}
