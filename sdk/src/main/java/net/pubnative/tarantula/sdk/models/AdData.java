package net.pubnative.tarantula.sdk.models;

import java.util.Map;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdData {
    public String type;
    public Map data;

    public String getText() {

        return getStringField("text");
    }

    public Double getNumber() {

        return getNumberField("number");
    }

    public String getURL() {

        return getStringField("url");
    }

    public String getHtml() {

        return getStringField("html");
    }

    public String getStringField(String field) {

        return (String) getDataField(field);
    }

    public Double getNumberField(String field) {

        return (Double) getDataField(field);
    }

    protected Object getDataField(String dataField) {

        Object result = null;
        if (data != null && data.containsKey(dataField)) {
            result = data.get(dataField);
        }
        return result;
    }
}
