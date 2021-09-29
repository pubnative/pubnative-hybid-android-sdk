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
package net.pubnative.lite.sdk.mraid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by erosgarciaponte on 08.01.18.
 */
@SuppressLint("ViewConstructor")
public class MRAIDBanner extends MRAIDView {

    public MRAIDBanner(
            Context context,
            String baseUrl,
            String data,
            String[] supportedNativeFeatures,
            MRAIDViewListener viewListener,
            MRAIDNativeFeatureListener nativeFeatureListener,
            ViewGroup contentInfo
    ) {
        super(context, baseUrl, data, supportedNativeFeatures, viewListener, nativeFeatureListener, contentInfo, false);
        if (webView != null) {
            webView.setBackgroundColor(Color.TRANSPARENT);
            addView(webView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            if (viewListener != null) {
                viewListener.mraidViewError(this);
            }
        }
    }


    @Override
    public boolean onBackPressed() {
        return state != STATE_DEFAULT && super.onBackPressed();
    }

    @Override
    protected void close() {
        if (state == STATE_LOADING || state == STATE_DEFAULT || state == STATE_HIDDEN) {
            return;
        }
        super.close();
    }

    @Deprecated
    @Override
    protected void expand(String url) {
        // The only time it is valid to call expand on a banner ad is
        // when the ad is currently in either default or resized state.
        if (state != STATE_DEFAULT && state != STATE_RESIZED) {
            return;
        }

        super.expand(url);
    }

    @Override
    protected void expandHelper(WebView webView) {
        state = STATE_EXPANDED;
        super.expandHelper(webView);
        this.fireStateChangeEvent();
    }

    @Override
    protected void onLayoutCompleted() {
        if (state == STATE_LOADING && isPageFinished) {
            state = STATE_DEFAULT;
            fireStateChangeEvent();
            fireReadyEvent();
            if (isViewable) {
                fireViewableChangeEvent();
            }
            fireExposureChangeEvent();
        }
    }
}
