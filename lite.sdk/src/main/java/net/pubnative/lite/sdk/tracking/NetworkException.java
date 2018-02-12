package net.pubnative.lite.sdk.tracking;

import java.io.IOException;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public class NetworkException extends IOException {
    public NetworkException(String url, Exception ex) {
        super(String.format("Network error when posting to %s", url));
        initCause(ex);
    }
}
