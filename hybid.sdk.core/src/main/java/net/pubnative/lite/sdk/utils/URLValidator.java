package net.pubnative.lite.sdk.utils;

import android.os.Handler;
import android.os.Looper;

import net.pubnative.lite.sdk.models.ContentInfo;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

public class URLValidator {

    public static void isValidURL(String stringURL, URLValidatorListener listener) {
        final android.os.Handler mHandler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            try {
                URL url = new URL(stringURL);
                URLConnection c = url.openConnection();
                String contentType = c.getContentType();

                if (contentType == null) {
                    mHandler.postDelayed(() -> listener.isValidURL(false), 0L);
                    return;
                }

                contentType = contentType.toLowerCase(Locale.ENGLISH);

                if(contentType.contains("text")){
                    mHandler.postDelayed(() -> listener.isValidURL(true), 0L);
                } else {
                    mHandler.postDelayed(() -> listener.isValidURL(false), 0L);
                }
            } catch (IOException e) {
                mHandler.postDelayed(() -> listener.isValidURL(false), 0L);
            }
        }).start();
    }

    public interface URLValidatorListener {
        void isValidURL(boolean isValid);
    }
}