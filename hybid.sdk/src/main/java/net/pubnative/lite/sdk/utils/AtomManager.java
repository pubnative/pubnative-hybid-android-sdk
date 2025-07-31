// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.content.Context;

import net.pubnative.lite.sdk.HyBid;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtomManager {
    public static final String ATOM_PACKAGE_NAME = "com.verve.atom.sdk";
    public static final String ATOM_CLASS_NAME = ATOM_PACKAGE_NAME + ".Atom";
    public static final String ATOM_INIT_LISTENER_CLASS_NAME = ATOM_CLASS_NAME + "$AtomInitialisationListener";
    public static final String ATOM_STOP_LISTENER_CLASS_NAME = ATOM_CLASS_NAME + "$AtomStopListener";
    public static final String ATOM_START_METHOD_NAME = "start";
    public static final String ATOM_STOP_METHOD_NAME = "stop";
    public static final String ATOM_GET_CALCULATED_COHORTS_METHOD_NAME = "getCalculatedCohorts";
    public static final String ATOM_GET_ID_METHOD_NAME = "id";
    public static final String ATOM_ON_INITIALISED_METHOD_NAME = "onInitialised";
    public static final String ATOM_ON_STOPPED_METHOD_NAME = "onStopped";
    public static final String ATOM_IS_DISABLED_METHOD_NAME = "isAtomDisabled";
    public static final String ATOM_IS_CONFIG_FETCHED_METHOD_NAME = "isConfigurationFetchSuccessful";
    public static final String ATOM_SET_AD_SESSION_DATA_METHOD_NAME = "sendAdSessionData";
    public static final String ATOM_NOT_FOUND_MESSAGE = "Atom not found";
    public static final String CREATIVE_ID = "creative_id";
    public static final String CAMPAIGN_ID = "campaign_id";
    public static final String BID_PRICE = "Bid price";
    public static final String AD_FORMAT = "Ad format";
    public static final String RENDERING_STATUS = "Rendering_status";
    public static final String VIEWABILITY = "Viewability";
    public static final String AD_SESSION_DATA = "Ad_Session_Data";
    public static final String RENDERING_SUCCESS = "rendering success";
    private static final String TAG = AtomManager.class.getSimpleName();

    private static AtomManager instance;

    private AtomManager() {
    }

    public static AtomManager getInstance() {
        if (instance == null) {
            instance = new AtomManager();
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> getAtomCohorts() {
        try {
            Class<?> atomClass = Class.forName(ATOM_CLASS_NAME);
            Method getCalculatedCohortsMethod = atomClass.getDeclaredMethod(ATOM_GET_CALCULATED_COHORTS_METHOD_NAME);
            Object result = getCalculatedCohortsMethod.invoke(null);
            if (result instanceof List) {
                return (List<Object>) result;
            }
        } catch (Exception e) {
            Logger.d(TAG, ATOM_NOT_FOUND_MESSAGE);
        }
        return new ArrayList<>();
    }

    public static boolean isAtomSdkDisabled() {
        boolean isDisabled = true;
        try {
            Class<?> atomClass = Class.forName(ATOM_CLASS_NAME);
            Method getIsDisabledMethod = atomClass.getDeclaredMethod(ATOM_IS_DISABLED_METHOD_NAME);
            Object result = getIsDisabledMethod.invoke(null);
            if (result instanceof Boolean) {
                isDisabled = (Boolean) result;
            }
        } catch (Exception e) {
            Logger.d(TAG, ATOM_NOT_FOUND_MESSAGE);
        }
        return isDisabled;
    }

    public static void setAdSessionData(HashMap<String, Object> adSessionData){
        try {
            Class<?> atomClass = Class.forName(ATOM_CLASS_NAME);
            Method adSessionDataMethod = atomClass.getDeclaredMethod(ATOM_SET_AD_SESSION_DATA_METHOD_NAME, Map.class);
            adSessionDataMethod.invoke(null ,adSessionData);
        } catch (Exception e) {
            Logger.d(TAG, ATOM_NOT_FOUND_MESSAGE+" "+e);
        }
    }

    public static boolean isAtomSdkConfigurationFetchSuccessful() {
        boolean configFetched = false;
        try {
            Class<?> atomClass = Class.forName(ATOM_CLASS_NAME);
            Method getConfigFetchedMethod = atomClass.getDeclaredMethod(ATOM_IS_CONFIG_FETCHED_METHOD_NAME);
            Object result = getConfigFetchedMethod.invoke(null);
            if (result instanceof Boolean) {
                configFetched = (Boolean) result;
            }
        } catch (Exception e) {
            Logger.d(TAG, ATOM_NOT_FOUND_MESSAGE);
        }
        return configFetched;
    }

    public static void initializeAtom(Context context) {
        String packageName = context.getPackageName();
        try {
            Class<?> atomClass = Class.forName(ATOM_CLASS_NAME);
            Method startMethod = atomClass.getDeclaredMethod(ATOM_START_METHOD_NAME, Context.class, String.class, boolean.class, Class.forName(ATOM_INIT_LISTENER_CLASS_NAME));

            Object listener = ProxyUtils.createProxy(atomClass.getClassLoader(), Class.forName(ATOM_INIT_LISTENER_CLASS_NAME), (proxy, method, args) -> {
                String methodName = method.getName();
                if (ATOM_ON_INITIALISED_METHOD_NAME.equals(methodName) && args.length == 1 && args[0] instanceof Boolean) {
                    HyBid.setAtomStarted((Boolean) args[0]);
                }
                return null;
            });
            startMethod.invoke(null, context, packageName, HyBid.isTestMode(), listener);
        } catch (Exception e) {
            HyBid.setAtomStarted(false);
            Logger.d(TAG, ATOM_NOT_FOUND_MESSAGE);
        }
    }

    public static void stopAtom() {
        try {
            Class<?> atomClass = Class.forName(ATOM_CLASS_NAME);

            Method stopMethod = atomClass.getDeclaredMethod(ATOM_STOP_METHOD_NAME, Class.forName(ATOM_STOP_LISTENER_CLASS_NAME));

            Object listener = ProxyUtils.createProxy(atomClass.getClassLoader(), Class.forName(ATOM_STOP_LISTENER_CLASS_NAME), (proxy, method, args) -> {
                String methodName = method.getName();
                if (ATOM_ON_STOPPED_METHOD_NAME.equals(methodName) && args.length == 1 && args[0] instanceof Boolean) {
                    HyBid.setAtomStarted(!((Boolean) args[0]));
                }
                return null;
            });
            stopMethod.invoke(null, listener);
        } catch (Exception e) {
            Logger.d(TAG, ATOM_NOT_FOUND_MESSAGE);
        }
    }
}