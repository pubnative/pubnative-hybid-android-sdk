package net.pubnative.lite.sdk.vast2.util;

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

                        int code = conn.getResponseCode();
                        VASTLog.v(TAG, "response code:" + code
                                + ", for URL:" + url);
                    } catch (Exception e) {
                        VASTLog.w(TAG, url + ": " + e.getMessage() + ":"
                                + e.toString());
                    } finally {
                        if (conn != null) {
                            try {
                                conn.disconnect();
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }.start();
        } else {
            VASTLog.w(TAG, "url is null or empty");

        }

    }
}
