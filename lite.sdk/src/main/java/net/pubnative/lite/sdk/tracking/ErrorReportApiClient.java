package net.pubnative.lite.sdk.tracking;

import java.util.Map;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

@SuppressWarnings("WeakerAccess")
public interface ErrorReportApiClient {
    void postReport(String urlString, Report report, Map<String, String> headers)
            throws NetworkException, BadResponseException;

}
