package net.pubnative.lite.sdk.vpaid.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.pubnative.lite.sdk.utils.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class EndCardFileDownloader {
    private static final String TAG = EndCardFileDownloader.class.getSimpleName();

    public static Bitmap mLoad(String string) {
        URL url = mStringToURL(string);
        if (url == null) {
            return null;
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            return BitmapFactory.decodeStream(bufferedInputStream);
        } catch (IOException e) {
            Logger.e(TAG, e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    private static URL mStringToURL(String string) {
        try {
            return new URL(string);
        } catch (MalformedURLException e) {
            Logger.e(TAG, e.getMessage());
        }
        return null;
    }
}