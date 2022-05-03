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
package net.pubnative.lite.sdk.mraid.properties;

import java.util.Arrays;
import java.util.List;

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public final class MRAIDOrientationProperties {
    public static final int FORCE_ORIENTATION_PORTRAIT = 0;
    public static final int FORCE_ORIENTATION_LANDSCAPE = 1;
    public static final int FORCE_ORIENTATION_NONE = 2;

    // whether or not the ad orientation changes when the device orientation changes
    public boolean allowOrientationChange;

    // what orientation the ad should be displayed in regardless of allowOrientationChange or actual device orientation
    public int forceOrientation;

    public MRAIDOrientationProperties() {
        this(true, FORCE_ORIENTATION_NONE);
    }

    public MRAIDOrientationProperties(boolean allowOrientationChange, int forceOrienation) {
        this.allowOrientationChange = allowOrientationChange;
        this.forceOrientation = forceOrienation;
    }

    public static int forceOrientationFromString(String name) {
        final List<String> names = Arrays.asList("portrait", "landscape", "none");
        int idx = names.indexOf(name);
        if (idx != -1) {
            return idx;
        }
        // Use none for the default value.
        return FORCE_ORIENTATION_NONE;
    }

    public String forceOrientationString() {
        switch (forceOrientation) {
            case FORCE_ORIENTATION_PORTRAIT:
                return "portrait";
            case FORCE_ORIENTATION_LANDSCAPE:
                return "landscape";
            case FORCE_ORIENTATION_NONE:
                return "none";
            default:
                return "error";
        }
    }
}
