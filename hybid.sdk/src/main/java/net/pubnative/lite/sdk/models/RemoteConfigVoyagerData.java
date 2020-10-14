package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RemoteConfigVoyagerData extends JsonModel {


    // todo maybe rename this map
    @BindField
    public Map<String, Object> name = new HashMap<>();


    public RemoteConfigVoyagerData() {

    }

    public RemoteConfigVoyagerData(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getAppStoreId(){
        return getStringField("app_store_id");
    }

    // todo this one must be an array of strings
    /*public String getIabCategories(){
        return getStringField("iab_categories");
    }*/

    public double getPf(){
        return getDoubleField("pf");
    }

    public double getPm(){
        return getDoubleField("pm");
    }

    public String getName(){
        return getStringField("name");
    }

    public int getMinScore(){
        return getIntField("min_score");
    }

    public String getPublishDate(){
        return getStringField("publish_date");
    }

    public String getVersionNo(){
        return getStringField("version_no");
    }

    public int getInputSize(){
        return getIntField("input_size");
    }

    public boolean isRequiringGeolocation(){
        return getBooleanField("requires_geolocation");
    }

    // todo models and metadata arrays which are within dependencies


    public String getStringField(String field) {
        return (String) getDataField(field);
    }

    public boolean getBooleanField(String field) {
        return (Boolean) getDataField(field);
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

    protected Object getDataField(String dataField) {

        Object result = null;
        if (name != null && name.containsKey(dataField)) {
            result = name.get(dataField);
        }
        return result;
    }

}
