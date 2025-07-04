// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonOperations {

    public static void putJsonString(JSONObject jsonObject, String key, String value) {
        if (jsonObject != null) {
            try {
                jsonObject.put(key, value);
            } catch (JSONException ignored) {
            }
        }
    }

    public static void putJsonString(JSONArray jsonArray, String value) {
        if (jsonArray != null) {
            jsonArray.put(value);
        }
    }

    public static void putJsonLong(JSONObject jsonObject, String key, long value) {
        if (jsonObject != null) {
            try {
                jsonObject.put(key, value);
            } catch (JSONException ignored) {
            }
        }
    }

    public static void putJsonBoolean(JSONObject jsonObject, String key, boolean value) {
        if (jsonObject != null) {
            try {
                jsonObject.put(key, value);
            } catch (JSONException ignored) {
            }
        }
    }

    public static void putJsonValue(JSONObject jsonObject, String key, Integer value) {
        if (jsonObject != null) {
            try {
                jsonObject.put(key, value);
            } catch (JSONException ignored) {
            }
        }
    }

    public static void putJsonValue(JSONObject jsonObject, String key, Double value) {
        if (jsonObject != null) {
            try {
                jsonObject.put(key, value);
            } catch (JSONException ignored) {
            }
        }
    }

    public static void putStringArray(JSONObject jsonObject, String key, List<String> values) {
        if (jsonObject != null) {
            try {
                if (values != null && !values.isEmpty()) {
                    JSONArray stringArray = new JSONArray();
                    for (String value : values) {
                        stringArray.put(value);
                    }
                    jsonObject.put(key, stringArray);
                }
            } catch (JSONException ignored) {
            }
        }
    }

    public static void putJsonArray(JSONObject jsonObject, String key, JSONArray value) {
        if (jsonObject != null) {
            try {
                jsonObject.put(key, value);
            } catch (JSONException ignored) {
            }
        }
    }

    public static void removeJsonValue(JSONObject jsonObject, String key) {
        if (jsonObject != null) {
            jsonObject.remove(key);
        }
    }

    private static synchronized JSONObject merge(JSONObject target, JSONObject source) {
        if (source == null || target == null || source.length() == 0) {
            return target;
        }
        JSONObject myTarget = target;
        JSONObject mySource = source;
        try {
            JSONArray names = mySource.names();
            if (names != null) {
                for (int i = 0; i < names.length(); i++) {
                    String name = names.getString(i);
                    myTarget.put(name, source.get(name));
                }
            }
        } catch (JSONException ignored) {

        } catch (ArrayIndexOutOfBoundsException ex) {

        } catch (RuntimeException ex) {

        }
        return myTarget;
    }

    public static void mergeJsonObjects(JSONObject target, JSONObject source) {
        if (target == null || source == null || source.length() == 0) {
            return;
        }
        target = merge(target, source);
    }
}