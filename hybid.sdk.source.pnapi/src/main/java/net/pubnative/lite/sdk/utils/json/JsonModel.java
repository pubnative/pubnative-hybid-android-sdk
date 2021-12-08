// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
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
        final String key;
        final Class entityType;
        final Class<?> collectionType;
        final Class<?> keyType;
        boolean isArray;
        boolean isMap;
        final boolean isDescendantFromModel;

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
                fields = new HashMap<>();
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
                            field.set(this, castNumber(fromJsonLiteral(t, input), field.getType()));
                        }
                    }
                }
            }
        }
    }

    private Object castNumber(Object literal, Class type) {
        if (literal instanceof Number) {
            Number number = (Number) literal;
            if (type == Integer.class) {
                return number.intValue();
            } else if (type == Long.class) {
                return number.longValue();
            } else if (type == Float.class) {
                return number.floatValue();
            } else if (type == Double.class) {
                return number.doubleValue();
            } else if (type == Short.class) {
                return number.shortValue();
            } else if (type == Byte.class) {
                return number.byteValue();
            } else {
                return literal;
            }
        } else {
            return literal;
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
            return type.getConstructor(JSONObject.class).newInstance(object);
        }
    }
}
