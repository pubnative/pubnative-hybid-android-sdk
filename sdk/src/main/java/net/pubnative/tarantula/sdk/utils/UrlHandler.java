package net.pubnative.tarantula.sdk.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Locale;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class UrlHandler {
    @NonNull private static final String TAG = UrlHandler.class.getSimpleName();

    @NonNull private final IntentHandler mIntentHandler;

    public UrlHandler(@NonNull Context context) {
        mIntentHandler = new IntentHandler(context);
    }

    /**
     * https://developer.android.com/distribute/marketing-tools/linking-to-google-play.html
     */
    public void handleUrl(@Nullable String url) {
        if (url == null) {
            return;
        }

        Logger.d(TAG, "Handling url: " + url);

        final Uri uri = Uri.parse(url);
        final String scheme = uri.getScheme();
        final String host = uri.getHost();
        final String uriLower = uri.toString().toLowerCase(Locale.ROOT);

        // NOTE: currently these all handle the same, but we might want different behavior in the future

        // Play store deep links
        if ("play.google.com".equalsIgnoreCase(host)
                || "market.android.com".equalsIgnoreCase(host)
                || "market".equalsIgnoreCase(scheme)
                || uriLower.startsWith("play.google.com")
                || uriLower.startsWith("market.android.com/")) {
            mIntentHandler.handleDeepLink(uri);
        }

        // Device browser
        else if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
            mIntentHandler.handleDeepLink(uri);
        }

        // App deep links
        else if (!scheme.isEmpty()) {
            mIntentHandler.handleDeepLink(uri);
        }
    }
}
