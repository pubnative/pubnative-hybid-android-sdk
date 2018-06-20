package net.pubnative.lite.sdk.utils.json;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by erosgarciaponte on 17.01.18.
 */

public class JsonModelFieldCache {
    private static final Map<String, HashMap<String, JsonModel.JsonModelMetadata>> cachedModels;

    static {
        cachedModels = new HashMap<String, HashMap<String, JsonModel.JsonModelMetadata>>();
    }

    /**
     * @param model class that extends PNJSONModel
     * @return true if fields are already cached, false otherwise
     */
    public static boolean checkIfModelCached(Class<?> model) {
        return cachedModels.containsKey(model.getName()) && cachedModels.get(model.getName()) != null;
    }

    /**
     * @param model class that extends PNJSONModel
     * @return Map with cached field metadata
     */
    public static HashMap<String, JsonModel.JsonModelMetadata> getFields(Class<?> model) {
        return cachedModels.get(model.getName());
    }

    /**
     * @param model  class that extends PNJSONModel
     * @param fields Map with metadata of the class fields
     */
    public static void setFields(Class<?> model, HashMap<String, JsonModel.JsonModelMetadata> fields) {
        cachedModels.put(model.getName(), fields);
    }
}
