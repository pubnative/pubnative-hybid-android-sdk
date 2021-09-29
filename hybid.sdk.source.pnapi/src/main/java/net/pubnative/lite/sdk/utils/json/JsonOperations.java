package net.pubnative.lite.sdk.utils.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static void mergeJsonObjects(JSONObject target, JSONObject source) {
        if (target == null || source == null || source.length() == 0) {
            return;
        }

        JSONArray names = source.names();
        try {
            for (int i = 0; i < names.length(); i++) {
                String name = names.getString(i);
                target.put(name, source.get(name));
            }
        } catch (JSONException ignored) {
        }
    }
}
