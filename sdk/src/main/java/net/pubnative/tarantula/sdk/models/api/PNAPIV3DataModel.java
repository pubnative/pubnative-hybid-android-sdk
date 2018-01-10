// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
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

package net.pubnative.tarantula.sdk.models.api;

import java.io.Serializable;
import java.util.Map;

public class PNAPIV3DataModel implements Serializable {

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
