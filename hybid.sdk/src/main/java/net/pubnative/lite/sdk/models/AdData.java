// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AdData extends JsonModel implements Serializable {

    @BindField
    public String type;
    @BindField
    public Map<String, Object> data;

    public AdData() {

    }

    public AdData(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public AdData(String key, String apiAsset, String assetValue) {
        data = new HashMap<>();
        data.put(key, assetValue);
        type = apiAsset;
    }

    public String getText() {

        return getStringField("text");
    }

    public Double getNumber() {

        return getDoubleField("number");
    }

    public Boolean getBoolean() {

        return getBooleanField("boolean");
    }

    public String getURL() {

        return getStringField("url");
    }

    public String getJS() {

        return getStringField("js");
    }

    public String getHtml() {

        return getStringField("html");
    }

    public int getWidth() {
        Integer width = getIntField("w");
        return width == null ? 0 : width;
    }

    public int getHeight() {
        Integer height = getIntField("h");
        return height == null ? 0 : height;
    }

    public String getStringField(String field) {
        Object object = getDataField(field);
        try {
            return (String) object;
        } catch (ClassCastException e) {
            return "";
        }
    }

    public JSONObject getJSONObjectField(String field) {
        Object value = getDataField(field);
        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }
        return null;
    }

    public Double getDoubleField(String field) {
        Object value = getDataField(field);
        if (value instanceof Number) {
            return ((Number) getDataField(field)).doubleValue();
        }

        return null;
    }

    public Integer getIntField(String field) {
        Object value = getDataField(field);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return null;
    }

    public Boolean getBooleanField(String field) {
        if (getDataField(field) instanceof Boolean)
            return (Boolean) getDataField(field);
        else
            return null;
    }

    public Boolean hasField(String field) {
        return data != null && data.containsKey(field);
    }

    protected Object getDataField(String dataField) {

        Object result = null;
        if (data != null && data.containsKey(dataField)) {
            result = data.get(dataField);
        }
        return result;
    }
}
