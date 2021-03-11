package com.monet.bidder.mapper;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Mappings extends JsonModel {
    private static final String TAG = Mappings.class.getSimpleName();

    @BindField
    private Map<Double, String> mappings;

    public Mappings() {

    }

    public Mappings(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);

        if (mappings != null) {
            Set<Object> keySet = new LinkedHashSet<Object>(mappings.keySet());
            for (Object key : keySet) {
                if (key instanceof String) {
                    String stringKey = (String) key;
                    try {
                        Double parsedKey = Double.parseDouble(stringKey);
                        String value = mappings.remove(key);
                        mappings.put(parsedKey, value);
                    } catch (NumberFormatException formatException) {
                        Logger.e(TAG, formatException.getMessage());
                    }
                }
            }
        }
    }

    public Map<Double, String> getMappings() {
        return mappings;
    }
}
