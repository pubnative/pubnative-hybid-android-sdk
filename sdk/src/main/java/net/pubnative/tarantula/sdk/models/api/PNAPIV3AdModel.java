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
import java.util.ArrayList;
import java.util.List;

public class PNAPIV3AdModel implements Serializable {

    //==============================================================================================
    // Fields
    //==============================================================================================

    public String link;
    public int                    assetgroupid;
    public List<PNAPIV3DataModel> assets;
    public List<PNAPIV3DataModel> beacons;
    public List<PNAPIV3DataModel> meta;

    //==============================================================================================
    // Interfaces
    //==============================================================================================

    /**
     * Interface containing all possible Beacons
     */
    public interface Beacon {

        String IMPRESSION = "impression";
        String CLICK      = "click";
    }

    //==============================================================================================
    // Asset
    //==============================================================================================
    public PNAPIV3DataModel getAsset(String type) {

        return find(type, assets);
    }

    public PNAPIV3DataModel getMeta(String type) {

        return find(type, meta);
    }

    public List<PNAPIV3DataModel> getBeacons(String type) {

        return findAll(type, beacons);
    }

    protected PNAPIV3DataModel find(String type, List<PNAPIV3DataModel> list) {

        PNAPIV3DataModel result = null;
        if (list != null) {
            for (PNAPIV3DataModel data : list) {
                if (type.equals(data.type)) {
                    result = data;
                    break;
                }
            }
        }
        return result;
    }

    protected List<PNAPIV3DataModel> findAll(String type, List<PNAPIV3DataModel> list) {

        List<PNAPIV3DataModel> result = null;
        if (list != null) {
            for (PNAPIV3DataModel data : list) {
                if (type.equals(data.type)) {
                    if (result == null) {
                        result = new ArrayList<PNAPIV3DataModel>();
                    }
                    result.add(data);
                }
            }
        }
        return result;
    }
}
