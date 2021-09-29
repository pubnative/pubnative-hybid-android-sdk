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
package net.pubnative.lite.sdk.presenter;

import android.view.View;

import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.models.Ad;

import org.json.JSONObject;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public interface AdPresenter {
    interface Listener {
        void onAdLoaded(AdPresenter adPresenter, View adView);

        void onAdClicked(AdPresenter adPresenter);

        void onAdError(AdPresenter adPresenter);
    }

    interface ImpressionListener {
        void onImpression();
    }

    void setListener(Listener listener);

    void setImpressionListener(ImpressionListener listener);

    void setVideoListener(VideoListener listener);

    Ad getAd();

    void load();

    void destroy();

    void startTracking();

    void stopTracking();

    JSONObject getPlacementParams();
}
