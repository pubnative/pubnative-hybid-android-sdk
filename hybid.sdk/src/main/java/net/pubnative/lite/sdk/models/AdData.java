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

        return (String) getDataField(field);
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
        if (data != null && data.containsKey(dataField)) {
            result = data.get(dataField);
        }
        return result;
    }
}
