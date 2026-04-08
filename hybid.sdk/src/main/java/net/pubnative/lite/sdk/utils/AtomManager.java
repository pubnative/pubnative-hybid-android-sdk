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
    public static final String ATOM_GET_JS_DATA_METHOD_NAME = "getAtomJSData";
    public static final String ATOM_SET_JS_DATA_METHOD_NAME = "setAtomJSData";
    public static final String ATOM_NOT_FOUND_MESSAGE = "Atom not found";
    public static final String CREATIVE_ID = "creative_id";
    public static final String CAMPAIGN_ID = "campaign_id";
    public static final String BID_PRICE = "Bid price";
    public static final String AD_FORMAT = "Ad format";
    public static final String RENDERING_STATUS = "Rendering_status";
    public static final String VIEWABILITY = "Viewability";
    public static final String AD_SESSION_DATA = "Ad_Session_Data";
    public static final String RENDERING_SUCCESS = "rendering success";
    public static final String SURVEY_DATA_KEY = "SurveyData";
    public static final String SURVEY_HTML_KEY = "SurveyHtml";
    private static final String TAG = AtomManager.class.getSimpleName();

    private static AtomManager instance;

    AtomManager() {
    }

    public static AtomManager getInstance() {
        if (instance == null) {
            instance = new AtomManager();
        }
        return instance;
    }

    // Reflection helpers for testability
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }
    protected Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... params) throws NoSuchMethodException {
        return clazz.getDeclaredMethod(name, params);
    }
    protected Object invokeMethod(Method method, Object obj, Object... args) throws Exception {
        return method.invoke(obj, args);
    }
    protected Object createProxy(ClassLoader loader, Class<?> iface, ProxyUtils.ProxyMethodHandler handler) {
        return ProxyUtils.createProxy(loader, iface, handler);
    }

    @SuppressWarnings("unchecked")
    public List<Object> getAtomCohortsInstance() {
        try {
            Class<?> atomClass = findClass(ATOM_CLASS_NAME);
            Method getCalculatedCohortsMethod = getDeclaredMethod(atomClass, ATOM_GET_CALCULATED_COHORTS_METHOD_NAME);
            Object result = invokeMethod(getCalculatedCohortsMethod, null);
            if (result instanceof List) {
                return (List<Object>) result;
            }
        } catch (Exception e) {
            Logger.d(TAG, ATOM_NOT_FOUND_MESSAGE);
        }
        return new ArrayList<>();
    }
    public static List<Object> getAtomCohorts() {
        return getInstance().getAtomCohortsInstance();
    }

    public boolean isAtomSdkDisabledInstance() {
        boolean isDisabled = true;
        try {
            Class<?> atomClass = findClass(ATOM_CLASS_NAME);
            Method getIsDisabledMethod = getDeclaredMethod(atomClass, ATOM_IS_DISABLED_METHOD_NAME);
            Object result = invokeMethod(getIsDisabledMethod, null);
            if (result instanceof Boolean) {
                isDisabled = (Boolean) result;
            }
        } catch (Exception e) {
            Logger.d(TAG, ATOM_NOT_FOUND_MESSAGE);
        }
        return isDisabled;
    }
    public static boolean isAtomSdkDisabled() {
        return getInstance().isAtomSdkDisabledInstance();
    }

    public HashMap<String, String> getAtomJSData() {
        HashMap<String, String> jsData = null;
        try {
            Class<?> atomClass = findClass(ATOM_CLASS_NAME);
            Method getIsDisabledMethod = getDeclaredMethod(atomClass, ATOM_GET_JS_DATA_METHOD_NAME);
            Object result = invokeMethod(getIsDisabledMethod, null);
            if (result instanceof HashMap) {
                jsData = (HashMap<String, String>) result;
            }
        } catch (Exception e) {
            Logger.d(TAG, ATOM_NOT_FOUND_MESSAGE);
        }
        return jsData;
    }

    public void putAtomJSData(String key, String value) {
        HashMap<String, String> jsData = getAtomJSData();
        if (jsData != null) {
            jsData.put(key, value);
        }
    }

    public void setAdSessionDataInstance(HashMap<String, Object> adSessionData){
        try {
            Class<?> atomClass = findClass(ATOM_CLASS_NAME);
            Method adSessionDataMethod = getDeclaredMethod(atomClass, ATOM_SET_AD_SESSION_DATA_METHOD_NAME, Map.class);
            invokeMethod(adSessionDataMethod, null, adSessionData);
        } catch (Exception e) {
            Logger.d(TAG, ATOM_NOT_FOUND_MESSAGE+" "+e);
        }
    }
    public static void setAdSessionData(HashMap<String, Object> adSessionData){
        getInstance().setAdSessionDataInstance(adSessionData);
    }

    public boolean isAtomSdkConfigurationFetchSuccessfulInstance() {
        boolean configFetched = false;
        try {
            Class<?> atomClass = findClass(ATOM_CLASS_NAME);
            Method getConfigFetchedMethod = getDeclaredMethod(atomClass, ATOM_IS_CONFIG_FETCHED_METHOD_NAME);
            Object result = invokeMethod(getConfigFetchedMethod, null);
            if (result instanceof Boolean) {
                configFetched = (Boolean) result;
            }
        } catch (Exception e) {
            Logger.d(TAG, ATOM_NOT_FOUND_MESSAGE);
        }
        return configFetched;
    }
    public static boolean isAtomSdkConfigurationFetchSuccessful() {
        return getInstance().isAtomSdkConfigurationFetchSuccessfulInstance();
    }

    public void initializeAtomInstance(Context context) {
        String packageName = context.getPackageName();
        try {
            Class<?> atomClass = findClass(ATOM_CLASS_NAME);
            Class<?> listenerClass = findClass(ATOM_INIT_LISTENER_CLASS_NAME);
            Method startMethod = getDeclaredMethod(atomClass, ATOM_START_METHOD_NAME, Context.class, String.class, boolean.class, listenerClass);
            Object listener = createProxy(atomClass.getClassLoader(), listenerClass, (proxy, method, args) -> {
                String methodName = method.getName();
                if (ATOM_ON_INITIALISED_METHOD_NAME.equals(methodName) && args.length == 1 && args[0] instanceof Boolean) {
                    HyBid.setAtomStarted((Boolean) args[0]);
                }
                return null;
            });
            invokeMethod(startMethod, null, context, packageName, HyBid.isTestMode(), listener);
        } catch (Exception e) {
            HyBid.setAtomStarted(false);
            Logger.d(TAG, ATOM_NOT_FOUND_MESSAGE);
        }
    }
    public static void initializeAtom(Context context) {
        getInstance().initializeAtomInstance(context);
    }

    public void stopAtomInstance() {
        try {
            Class<?> atomClass = findClass(ATOM_CLASS_NAME);
            Class<?> listenerClass = findClass(ATOM_STOP_LISTENER_CLASS_NAME);
            Method stopMethod = getDeclaredMethod(atomClass, ATOM_STOP_METHOD_NAME, listenerClass);
            Object listener = createProxy(atomClass.getClassLoader(), listenerClass, (proxy, method, args) -> {
                String methodName = method.getName();
                if (ATOM_ON_STOPPED_METHOD_NAME.equals(methodName) && args.length == 1 && args[0] instanceof Boolean) {
                    HyBid.setAtomStarted(!((Boolean) args[0]));
                }
                return null;
            });
            invokeMethod(stopMethod, null, listener);
        } catch (Exception e) {
            Logger.d(TAG, ATOM_NOT_FOUND_MESSAGE);
        }
    }
    public static void stopAtom() {
        getInstance().stopAtomInstance();
    }
}