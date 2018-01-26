package net.pubnative.lite.sdk.utils.json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by erosgarciaponte on 17.01.18.
 */

public abstract class JsonModel {
    private static final String TAG = JsonModel.class.getSimpleName();

    static class JsonModelMetadata {
        String key;
        Class entityType;
        Class<?> collectionType;
        Class<?> keyType;
        boolean isArray = false;
        boolean isMap = false;
        boolean isDescendantFromModel;

        JsonModelMetadata(String key, Class entityType) {
            this(key, void.class, entityType, void.class, false, false);
        }

        JsonModelMetadata(String key, Class entityType, Class collectionType) {
            this(key, void.class, entityType, collectionType, true, false);
        }

        JsonModelMetadata(String key, Class keyType, Class entityType, Class collectionType) {
            this(key, keyType, entityType, collectionType, false, true);
        }

        JsonModelMetadata(String key, Class keyType, Class entityType, Class collectionType, boolean isArray, boolean isMap) {
            this.key = key;
            this.keyType = keyType;
            this.entityType = entityType;
            this.collectionType = collectionType;
            this.isArray = isArray;
            this.isMap = isMap;
            this.isDescendantFromModel = JsonModel.class.isAssignableFrom(entityType);
        }
    }

    //----------------------------------------------------------------------------------------------
    // FIELD METADATA BINDING
    //----------------------------------------------------------------------------------------------
    protected HashMap<String, JsonModelMetadata> fields;

    void bind(JsonModel modelClass) {
        if (JsonModelFieldCache.checkIfModelCached(modelClass.getClass())) {
            fields = JsonModelFieldCache.getFields(modelClass.getClass());
        } else {
            try {
                fields = new HashMap<String, JsonModelMetadata>();
                for (Field field : modelClass.getClass().getDeclaredFields()) {
                    final BindField bindField = field.getAnnotation(BindField.class);
                    if (bindField != null) {
                        Class typeClass = field.getType();
                        if (Iterable.class.isAssignableFrom(typeClass)) {
                            fields.put(field.getName(), parseArray(field, typeClass));
                        } else if (Map.class.isAssignableFrom(typeClass)) {
                            fields.put(field.getName(), parseMap(field, typeClass));
                        } else {
                            fields.put(field.getName(), parseLiteral(field, typeClass));
                        }
                    }
                }
                JsonModelFieldCache.setFields(modelClass.getClass(), fields);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    protected JsonModelMetadata parseArray(Field field, Class typeClass) {
        Type type = field.getGenericType();
        ParameterizedType pt = (ParameterizedType) type;
        Type innerType = pt.getActualTypeArguments()[0];
        Class<?> innerClass = (Class<?>) innerType;

        return new JsonModelMetadata(field.getName(), innerClass, typeClass);
    }

    protected JsonModelMetadata parseMap(Field field, Class typeClass) {
        Type type = field.getGenericType();
        ParameterizedType pt = (ParameterizedType) type;

        Type keyType = pt.getActualTypeArguments()[0];
        Class<?> keyClass = (Class<?>) keyType;

        Type valueType = pt.getActualTypeArguments()[1];
        Class<?> valueClass = (Class<?>) valueType;

        return new JsonModelMetadata(field.getName(), keyClass, valueClass, typeClass);
    }

    protected JsonModelMetadata parseLiteral(Field field, Class typeClass) {
        return new JsonModelMetadata(field.getName(), typeClass);
    }

    protected JsonModel() {
        bind(this);
    }

    //----------------------------------------------------------------------------------------------
    // MODEL to JSON
    //----------------------------------------------------------------------------------------------

    public JSONObject toJson() throws Exception {
        JSONObject outputObject = new JSONObject();

        for (Field field : this.getClass().getDeclaredFields()) {
            String fieldName = field.getName();

            if (fields.containsKey(fieldName)) {
                field.setAccessible(true);
                JsonModelMetadata t = fields.get(fieldName);
                Object fieldValue = field.get(this);
                if (fieldValue != null) {
                    if (t.isArray) {
                        outputObject.put(t.key, toJsonArray(t, fieldValue));
                    } else if (t.isMap) {
                        outputObject.put(t.key, toJsonMap(t, fieldValue));
                    } else {
                        outputObject.put(t.key, toJsonLiteral(t, fieldValue));
                        if (t.isDescendantFromModel) {
                            outputObject.put(t.key, ((JsonModel) fieldValue).toJson());
                        } else {
                            outputObject.put(t.key, fieldValue);
                        }
                    }
                }
            }
        }

        return outputObject;
    }

    protected JSONArray toJsonArray(JsonModelMetadata meta, Object fieldValue) throws Exception {
        Iterable iterable = (Iterable) fieldValue;
        JSONArray array = new JSONArray();
        if (meta.isDescendantFromModel) {
            for (Object current : iterable) {
                array.put(((JsonModel) current).toJson());
            }
        } else {
            for (Object current : iterable) {
                array.put(current);
            }
        }
        return array;
    }

    protected JSONObject toJsonMap(JsonModelMetadata meta, Object fieldValue) throws Exception {
        Map<String, Object> map = (Map<String, Object>) fieldValue;
        JSONObject mapObject = new JSONObject();
        if (meta.isDescendantFromModel) {
            for (String key : map.keySet()) {
                mapObject.put(key, ((JsonModel) map.get(key)).toJson());
            }
        } else {
            for (String key : map.keySet()) {
                mapObject.put(key, map.get(key));
            }
        }
        return mapObject;
    }

    protected Object toJsonLiteral(JsonModelMetadata meta, Object fieldValue) throws Exception {
        Object object;
        if (meta.isDescendantFromModel) {
            object = ((JsonModel) fieldValue).toJson();
        } else {
            object = fieldValue;
        }
        return object;
    }

    //----------------------------------------------------------------------------------------------
    // JSON to MODEL
    //----------------------------------------------------------------------------------------------

    public void fromJson(JSONObject input) throws Exception {
        if (input == null) {
            Log.e(TAG, "Couldn't parse JSON object because of null input");
        } else {
            for (Field field : this.getClass().getDeclaredFields()) {
                String fieldName = field.getName();
                if (fields.containsKey(fieldName)) {
                    field.setAccessible(true);
                    JsonModelMetadata t = fields.get(fieldName);
                    if (input.has(t.key)) {
                        if (t.isArray) {
                            JSONArray array;
                            try {
                                array = input.getJSONArray(t.key);
                            } catch (Exception e) {
                                array = null;
                            }

                            if (array != null) {
                                field.set(this, fromJsonArray(t, array));
                            }
                        } else if (t.isMap) {
                            JSONObject mapObject;
                            try {
                                mapObject = input.getJSONObject(t.key);
                            } catch (Exception e) {
                                mapObject = null;
                            }

                            if (mapObject != null) {
                                field.set(this, fromJsonMap(t, mapObject));
                            }
                        } else {
                            field.set(this, fromJsonLiteral(t, input));
                        }
                    }
                }
            }
        }
    }

    protected List fromJsonArray(JsonModelMetadata meta, JSONArray array) throws Exception {
        List list = new ArrayList();
        if (meta.isDescendantFromModel) {
            for (int i = 0; i < array.length(); i++) {
                list.add(castObject(array.getJSONObject(i), meta.entityType));
            }
        } else {
            for (int i = 0; i < array.length(); i++) {
                Object data = array.get(i);
                list.add(data);
            }
        }
        return list;
    }

    protected Map fromJsonMap(JsonModelMetadata meta, JSONObject mapObject) throws Exception {
        Map result = new LinkedHashMap();
        Iterator iterator = mapObject.keys();
        if (meta.isDescendantFromModel) {
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                result.put(key, castObject(mapObject.getJSONObject(key), meta.entityType));
            }
        } else {
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                Object data = mapObject.get(key);
                result.put(key, data);
            }
        }
        return result;
    }

    protected Object fromJsonLiteral(JsonModelMetadata meta, JSONObject input) throws Exception {
        Object object;
        if (meta.isDescendantFromModel) {
            object = castObject(input.getJSONObject(meta.key), meta.entityType);
        } else {
            object = input.get(meta.key);
        }
        return object;
    }

    protected <T extends JsonModel> T castObject(JSONObject object, Class<T> type) throws Exception {
        if (object == null) {
            return null;
        } else {
            T w = type.getConstructor(JSONObject.class).newInstance(object);
            return w;
        }
    }
}
