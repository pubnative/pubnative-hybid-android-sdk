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

public final class MRAIDResizeProperties {
    public static final int CUSTOM_CLOSE_POSITION_TOP_LEFT = 0;
    public static final int CUSTOM_CLOSE_POSITION_TOP_CENTER = 1;
    public static final int CUSTOM_CLOSE_POSITION_TOP_RIGHT = 2;
    public static final int CUSTOM_CLOSE_POSITION_CENTER = 3;
    public static final int CUSTOM_CLOSE_POSITION_BOTTOM_LEFT = 4;
    public static final int CUSTOM_CLOSE_POSITION_BOTTOM_CENTER = 5;
    public static final int CUSTOM_CLOSE_POSITION_BOTTOM_RIGHT = 6;

    public int width;
    public int height;
    public int offsetX;
    public int offsetY;
    public int customClosePosition;
    public boolean allowOffscreen;

    public MRAIDResizeProperties() {
        this(0, 0, 0, 0, CUSTOM_CLOSE_POSITION_TOP_RIGHT, true);
    }

    public MRAIDResizeProperties(
            int width,
            int height,
            int offsetX,
            int offsetY,
            int customClosePosition,
            boolean allowOffscreen) {
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.customClosePosition = customClosePosition;
        this.allowOffscreen = allowOffscreen;
    }

    public static int customClosePositionFromString(String name) {
        final List<String> names = Arrays.asList(
                "top-left",
                "top-center",
                "top-right",
                "center",
                "bottom-left",
                "bottom-center",
                "bottom-right"
        );
        int idx = names.indexOf(name);
        if (idx != -1) {
            return idx;
        }
        // Use top-right for the default value.
        return CUSTOM_CLOSE_POSITION_TOP_RIGHT;
    }
}
