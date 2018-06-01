//
// Copyright (c) 2016, PubNative, Nexage Inc.
// All rights reserved.
// Provided under BSD-3 license as follows:
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
// Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
//
// Redistributions in binary form must reproduce the above copyright notice, this
// list of conditions and the following disclaimer in the documentation and/or
// other materials provided with the distribution.
//
// Neither the name of Nexage, PubNative nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
// ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

package net.pubnative.lite.sdk.vast.model;

import java.util.ArrayList;
import java.util.List;

public class VideoClicks {

    private String clickThrough;
    private List<String> clickTracking;
    private List<String> customClick;

    public String getClickThrough() {

        return clickThrough;
    }

    public void setClickThrough(String clickThrough) {

        this.clickThrough = clickThrough;
    }

    public List<String> getClickTracking() {

        if (clickTracking == null) {

            clickTracking = new ArrayList<String>();
        }

        return this.clickTracking;
    }

    public List<String> getCustomClick() {

        if (customClick == null) {

            customClick = new ArrayList<String>();
        }

        return this.customClick;
    }

    @Override
    public String toString() {

        return "VideoClicks [clickThrough=" + clickThrough + ", clickTracking=[" + listToString(clickTracking) + "], customClick=[" + listToString(customClick) + "] ]";
    }

    private String listToString(List<String> list) {

        StringBuffer sb = new StringBuffer();

        if (list == null) {

            return "";
        }

        for (int x = 0; x < list.size(); x++) {

            sb.append(list.get(x).toString());
        }
        return sb.toString();
    }
}
