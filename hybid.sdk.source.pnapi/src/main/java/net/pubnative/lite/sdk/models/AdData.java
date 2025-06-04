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
        return getIntField("w");
    }

    public int getHeight() {
        return getIntField("h");
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

        return (JSONObject) getDataField(field);
    }

    public Double getDoubleField(String field) {
        Object value = getDataField(field);
        if (value instanceof Number) {
            return ((Number) getDataField(field)).doubleValue();
        }

        return null;
    }

    public Integer getIntField(String field) {

        return (Integer) getDataField(field);
    }

    public Boolean getBooleanField(String field) {

        return (Boolean) getDataField(field);
    }

    public Boolean hasField(String field) {
        return data.containsKey(field);
    }

    protected Object getDataField(String dataField) {

        Object result = null;
        if (data != null && data.containsKey(dataField)) {
            result = data.get(dataField);
        }
        return result;
    }
}
