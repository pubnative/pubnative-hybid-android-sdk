package net.pubnative.lite.sdk.utils;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UrlUtils {
    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';

    public static String createQueryStringForParameters(JSONObject jsonObject) throws Exception {
        Map<String, String> params = new HashMap<>();
        Iterator<String> keyIterator = jsonObject.keys();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            params.put(key, jsonObject.getString(key));
        }
        return createQueryStringForParameters(params);
    }

    public static String createQueryStringForParameters(Map<String, String> parameters) throws Exception {
        StringBuilder parametersAsQueryString = new StringBuilder();
        if (parameters != null) {
            boolean firstParameter = true;

            for (String parameterName : parameters.keySet()) {
                if (!firstParameter) {
                    parametersAsQueryString.append(PARAMETER_DELIMITER);
                }

                parametersAsQueryString.append(parameterName)
                        .append(PARAMETER_EQUALS_CHAR)
                        .append(URLEncoder.encode(
                                parameters.get(parameterName), "UTF-8"));

                firstParameter = false;
            }
        }
        return parametersAsQueryString.toString();
    }
}
