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
package net.pubnative.lite.sdk.models;

/**
 * Created by erosgarciaponte on 24.01.18.
 */

public interface ApiAssetGroupType {
    int MRAID_BANNER_1 = 10;
    int MRAID_BANNER_2 = 12;
    int MRAID_MRECT = 8;
    int MRAID_INTERSTITIAL_1 = 21;
    int MRAID_INTERSTITIAL_2 = 22;
    int MRAID_INTERSTITIAL_3 = 23;
    int MRAID_LEADERBOARD = 24;

    int VAST_MRECT = 4;
    int VAST_INTERSTITIAL_1 = 15;
    int VAST_INTERSTITIAL_2 = 18;
    int VAST_INTERSTITIAL_3 = 19;
    int VAST_INTERSTITIAL_4 = 20;
}
