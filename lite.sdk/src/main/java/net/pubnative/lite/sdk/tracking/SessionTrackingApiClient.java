package net.pubnative.lite.sdk.tracking;

import java.util.Map;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public interface SessionTrackingApiClient {

    void postSessionTrackingPayload(String urlString,
                                    SessionTrackingPayload payload,
                                    Map<String, String> headers)
            throws NetworkException, BadResponseException;
}
