package net.pubnative.lite.sdk.tracking;

import java.util.Map;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public interface SessionTrackingApiClient {

    /**
     * Posts an array of sessions to the Bugsnag API.
     *
     * @param urlString the Bugsnag endpoint
     * @param payload   The session tracking
     * @param headers   the HTTP headers
     * @throws NetworkException     if the client was unable to complete the request
     * @throws BadResponseException when a non-202 response code is received from the server
     */
    void postSessionTrackingPayload(String urlString,
                                    SessionTrackingPayload payload,
                                    Map<String, String> headers)
            throws NetworkException, BadResponseException;
}
