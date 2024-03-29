// The MIT License (MIT)
//
// Copyright (c) 2023 PubNative GmbH
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class BuyerSignal extends JsonModel {
    @BindField
    public String origin;
    @BindField
    public List<String> buyerdata;
    @BindField
    public String buyer_experiment_group_id;

    public BuyerSignal() {

    }

    public BuyerSignal(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getOrigin() {
        return origin;
    }

    public List<String> getBuyerData() {
        return buyerdata;
    }

    public String getBuyerExperimentGroupId() {
        return buyer_experiment_group_id;
    }

    public String getBuyerDataJson() {
        JSONArray array = new JSONArray();
        if (buyerdata != null && !buyerdata.isEmpty()) {
            for (String data : buyerdata) {
                array.put(data);
            }
        }
        return array.toString();
    }
}
