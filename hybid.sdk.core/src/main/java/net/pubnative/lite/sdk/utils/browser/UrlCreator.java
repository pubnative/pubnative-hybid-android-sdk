// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.browser;

import android.net.Uri;

import java.util.Map;

public final class UrlCreator {

    /**
     * Adds {@code queryParams} to {@code url}
     *
     * @param url
     * @param queryParams
     * @return
     */

    public String createUrl(String url,
                            Map<String, String> queryParams) {
        if (url == null) {
            throw new NullPointerException("Url can not be null");
        }
        if (queryParams == null) {
            throw new NullPointerException("queryparams can not be null");
        }

        Uri uri = applyParameters(Uri.parse(url), queryParams);
        return uri.toString();
    }

    private Uri applyParameters(Uri baseUri, Map<String, String> queryItems) {
        Uri.Builder builder = baseUri.buildUpon();
        for (Map.Entry<String, String> queryItem : queryItems.entrySet()) {
            builder.appendQueryParameter(queryItem.getKey(), queryItem.getValue());
        }
        return builder.build();
    }

    /**
     * Gets the hostname of this url. Example: "example.com"
     *
     * @return the hostname or null if this is a relative url
     */

    public String extractHostname(String url) {
        if (url != null) {
            return Uri.parse(url).getHost();
        } else throw new NullPointerException("Url can not be null");
    }

    /**
     * Gets the scheme of this url. Example: "http"
     *
     * @return the scheme or null if this is a relative url or url is null
     */
    public String extractScheme(String url) {
        if (url != null) {
            return Uri.parse(url).getScheme();
        } else throw new NullPointerException("Url can not be null");
    }

    /**
     * Checks if provided scheme is secure
     *
     * @param scheme
     * @return true if scheme is https, false otherwise
     */
    public boolean isSecureScheme(String scheme) {
        return "https".equalsIgnoreCase(scheme);
    }

    /**
     * Checks if provided scheme is insecure
     *
     * @param scheme
     * @return true if scheme is http, false otherwise
     */
    public boolean isInsecureScheme(String scheme) {
        return "http".equalsIgnoreCase(scheme);
    }

    /**
     * Checks that scheme is in validatable range. (HTTP, HTTPS)
     * Custom schemes fail this check
     *
     * @param url to check
     * @return
     */
    public boolean isSupportedForNetworking(String url) {
        String scheme = extractScheme(url);
        return isSecureScheme(scheme) || isInsecureScheme(scheme);
    }
}

