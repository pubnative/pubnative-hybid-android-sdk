package net.pubnative.lite.sdk.vast2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import net.pubnative.lite.sdk.vast2.activity.VASTActivity;
import net.pubnative.lite.sdk.vast2.model.VASTModel;
import net.pubnative.lite.sdk.vast2.processor.VASTMediaPicker;
import net.pubnative.lite.sdk.vast2.processor.VASTProcessor;
import net.pubnative.lite.sdk.vast2.util.DefaultMediaPicker;
import net.pubnative.lite.sdk.vast2.util.NetworkTools;
import net.pubnative.lite.sdk.vast2.util.VASTLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class VASTPlayer {
    private static final String TAG = VASTPlayer.class.getSimpleName();

    public static final String VERSION = "1.3";

    // errors that can be returned in the vastError callback method of the
    // VASTPlayerListener
    public static final int ERROR_NONE = 0;
    public static final int ERROR_NO_NETWORK = 1;
    public static final int ERROR_XML_OPEN_OR_READ = 2;
    public static final int ERROR_XML_PARSE = 3;
    public static final int ERROR_SCHEMA_VALIDATION = 4; // not used in SDK, only in sourcekit
    public static final int ERROR_POST_VALIDATION = 5;
    public static final int ERROR_EXCEEDED_WRAPPER_LIMIT = 6;
    public static final int ERROR_VIDEO_PLAYBACK = 7;

    private Context context;

    public interface VASTPlayerListener {
        void vastReady();

        void vastError(int error);

        void vastClick();

        void vastComplete();

        void vastDismiss();
    }

    public static VASTPlayerListener listener;
    private VASTModel vastModel;

    public VASTPlayer(Context context, VASTPlayerListener listener) {
        this.context = context;
        VASTPlayer.listener = listener;
    }

    public void loadVideoWithUrl(final String urlString) {
        VASTLog.d(TAG, "loadVideoWithUrl " + urlString);
        vastModel = null;
        if (NetworkTools.connectedToInternet(context)) {
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedReader in = null;
                    StringBuffer sb;
                    try {
                        URL url = new URL(urlString);
                        in = new BufferedReader(new InputStreamReader(url.openStream()));
                        sb = new StringBuffer();
                        String line;
                        while ((line = in.readLine()) != null) {
                            sb.append(line).append(System.getProperty("line.separator"));
                        }
                    } catch (Exception e) {
                        sendError(ERROR_XML_OPEN_OR_READ);
                        VASTLog.e(TAG, e.getMessage(), e);
                        return;
                    } finally {
                        try {
                            if (in != null) {
                                in.close();
                            }
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                    loadVideoWithData(sb.toString());
                }
            })).start();
        } else {
            sendError(ERROR_NO_NETWORK);
        }
    }

    public void loadVideoWithData(final String xmlData) {
        VASTLog.v(TAG, "loadVideoWithData\n" + xmlData);
        vastModel = null;
        if (NetworkTools.connectedToInternet(context)) {
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    VASTMediaPicker mediaPicker = new DefaultMediaPicker(context);
                    VASTProcessor processor = new VASTProcessor(mediaPicker);
                    int error = processor.process(xmlData);
                    if (error == ERROR_NONE) {
                        vastModel = processor.getModel();
                        sendReady();
                    } else {
                        sendError(error);
                    }
                }
            })).start();
        } else {
            sendError(ERROR_NO_NETWORK);
        }
    }

    public void play() {
        VASTLog.d(TAG, "play");
        if (vastModel != null) {
            if (NetworkTools.connectedToInternet(context)) {
                Intent vastPlayerIntent = new Intent(context, VASTActivity.class);
                vastPlayerIntent.putExtra(VASTActivity.EXTRA_VAST_MODEL, vastModel);
                context.startActivity(vastPlayerIntent);
            } else {
                sendError(ERROR_NO_NETWORK);
            }
        } else {
            VASTLog.w(TAG, "vastModel is null; nothing to play");
        }
    }

    private void sendReady() {
        VASTLog.d(TAG, "sendReady");
        if (listener != null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.vastReady();
                }
            });
        }
    }

    private void sendError(final int error) {
        VASTLog.d(TAG, "sendError");
        if (listener != null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.vastError(error);
                }
            });
        }
    }
}