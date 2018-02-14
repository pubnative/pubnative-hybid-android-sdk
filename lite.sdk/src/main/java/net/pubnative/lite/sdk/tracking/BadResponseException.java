package net.pubnative.lite.sdk.tracking;

import java.util.Locale;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public class BadResponseException extends Exception {
    public BadResponseException(String url, int responseCode) {
        super(String.format(Locale.US,
                "Got non-200 response code (%d) from %s", responseCode, url));
    }
}
