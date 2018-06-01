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

package net.pubnative.lite.sdk.vast.util;

import android.text.TextUtils;

import java.net.HttpURLConnection;
import java.net.URL;

public class HttpTools {

    private static final String TAG = HttpTools.class.getName();

    public static void httpGetURL(final String url) {

        if (!TextUtils.isEmpty(url)) {
            new Thread() {

                @Override
                public void run() {

                    HttpURLConnection conn = null;

                    try {
                        VASTLog.v(TAG, "connection to URL:" + url);
                        URL httpUrl = new URL(url);

                        HttpURLConnection.setFollowRedirects(true);
                        conn = (HttpURLConnection) httpUrl.openConnection();
                        conn.setConnectTimeout(5000);
                        conn.setRequestProperty("Connection", "close");
                        conn.setRequestMethod("GET");
                        conn.connect();

                        int code = conn.getResponseCode();
                        VASTLog.v(TAG, "response code:" + code + ", for URL:" + url);

                        conn.getInputStream().close();
                        conn.getOutputStream().close();

                    } catch (Exception e) {

                        VASTLog.w(TAG, url + ": " + e.getMessage() + ":" + e.toString());

                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }

                }
            }.start();

        } else {

            VASTLog.w(TAG, "url is null or empty");
        }
    }
}
