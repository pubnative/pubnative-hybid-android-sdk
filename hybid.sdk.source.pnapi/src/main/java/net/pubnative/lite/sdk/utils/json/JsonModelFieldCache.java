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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by erosgarciaponte on 17.01.18.
 */

public class JsonModelFieldCache {
    private static final Map<String, HashMap<String, JsonModel.JsonModelMetadata>> cachedModels;

    static {
        cachedModels = new HashMap<>();
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
