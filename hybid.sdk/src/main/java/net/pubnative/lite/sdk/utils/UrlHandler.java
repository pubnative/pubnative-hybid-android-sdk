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
package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Locale;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class UrlHandler {
    private static final String TAG = UrlHandler.class.getSimpleName();

    private final IntentHandler mIntentHandler;

    public UrlHandler(Context context) {
        mIntentHandler = new IntentHandler(context);
    }

    /**
     * https://developer.android.com/distribute/marketing-tools/linking-to-google-play.html
     */
    public void handleUrl(String url) {
        if (url == null) {
            return;
        }

        Logger.d(TAG, "Handling url: " + url);

        final Uri uri = Uri.parse(url);
        final String scheme = uri.getScheme();
        final String host = uri.getHost();
        final String uriLower = uri.toString().toLowerCase(Locale.ROOT);

        final Uri processedUri = uri.buildUpon().appendQueryParameter("redirect", "false").build();

        // NOTE: currently these all handle the same, but we might want different behavior in the future

        // Play store deep links
        if ("play.google.com".equalsIgnoreCase(host)
                || "market.android.com".equalsIgnoreCase(host)
                || "market".equalsIgnoreCase(scheme)
                || uriLower.startsWith("play.google.com")
                || uriLower.startsWith("market.android.com/")) {
            mIntentHandler.handleDeepLink(processedUri);
        }

        // Device browser
        else if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
            mIntentHandler.handleBrowserLink(processedUri);
        }

        // App deep links
        else if (!TextUtils.isEmpty(scheme)) {
            mIntentHandler.handleDeepLink(processedUri);
        }
    }
}
