// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
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
    private static final String DEEPLINK_URL_SCHEMA = "vrvdl";
    private static final String DEEPLINK_PARAM = "deeplinkUrl";
    private static final String FALLBACK_PARAM = "fallbackUrl";

    private final IntentHandler mIntentHandler;

    public UrlHandler(Context context) {
        mIntentHandler = new IntentHandler(context);
    }

    public void handleUrl(String url, String adLink, String navigationMode) {
        url = handleDeeplinkIfPresent(url, adLink);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Logger.d(TAG, "Handling url: " + url);
        handleNavigation(url, navigationMode);
    }

    String handleDeeplinkIfPresent(String url, String adLink) {
        if (!TextUtils.isEmpty(adLink) && adLink.contains(DEEPLINK_URL_SCHEMA)) {
            try {
                Uri parsedLink = Uri.parse(adLink);
                String deeplinkUrl = getDeeplinkUrl(parsedLink);
                String fallbackUrl = getFallbackUrl(parsedLink);
                if (!TextUtils.isEmpty(deeplinkUrl)) {
                    Uri deeplinkUri = Uri.parse(deeplinkUrl);
                    String scheme = deeplinkUri.getScheme();
                    if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
                        if (mIntentHandler.canHandleIntent(deeplinkUri)) {
                            mIntentHandler.handleDeepLink(deeplinkUri);
                            return null;
                        } else {
                            return TextUtils.isEmpty(fallbackUrl) ? url : fallbackUrl;
                        }
                    } else {
                        boolean isStartActivitySuccess = mIntentHandler.handleDeepLink(deeplinkUri);
                        if(isStartActivitySuccess) {
                            return null;
                        } else {
                            return TextUtils.isEmpty(fallbackUrl) ? url : fallbackUrl;
                        }
                    }
                } else {
                    return TextUtils.isEmpty(fallbackUrl) ? url : fallbackUrl;
                }
            } catch (RuntimeException exception) {
                Logger.e(TAG, "Error parsing deeplink url: " + exception.getMessage());
            }
        }
        return url;
    }

    void handleNavigation(String url, String navigationMode) {
        final Uri uri = Uri.parse(url);
        final String scheme = uri.getScheme();
        final String host = uri.getHost();
        final String uriLower = uri.toString().toLowerCase(Locale.ROOT);

        if (isPlayStoreLink(scheme, host, uriLower)) {
            mIntentHandler.handleDeepLink(uri);
        } else if (isHttpOrHttps(scheme)) {
            if ("internal".equalsIgnoreCase(navigationMode)) {
                mIntentHandler.handleBrowserLinkBrowserActivity(uri);
            } else {
                mIntentHandler.handleBrowserLink(uri);
            }
        } else if (!TextUtils.isEmpty(scheme)) {
            mIntentHandler.handleDeepLink(uri);
        }
    }

    boolean isPlayStoreLink(String scheme, String host, String uriLower) {
        return "play.google.com".equalsIgnoreCase(host)
                || "market.android.com".equalsIgnoreCase(host)
                || "market".equalsIgnoreCase(scheme)
                || uriLower.startsWith("play.google.com")
                || uriLower.startsWith("market.android.com/");
    }

    boolean isHttpOrHttps(String scheme) {
        return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
    }

    String getDeeplinkUrl(Uri parsedLink) {
        String deeplinkString = parsedLink.getQueryParameter(DEEPLINK_PARAM);
        if(!TextUtils.isEmpty(deeplinkString) && !deeplinkString.equalsIgnoreCase("\"\""))
            return deeplinkString;
        else
            return null;
    }

    String getFallbackUrl(Uri parsedLink) {
        String fallbackString = parsedLink.getQueryParameter(FALLBACK_PARAM);
        if(!TextUtils.isEmpty(fallbackString) && !fallbackString.equalsIgnoreCase("\"\""))
            return fallbackString;
        else
            return null;
    }
}
