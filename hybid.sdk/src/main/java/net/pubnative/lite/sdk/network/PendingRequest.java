package net.pubnative.lite.sdk.network;

import java.util.Map;

public class PendingRequest {
    private final String url;
    private final Map<String, String> headers;
    private final String postBody;

    public PendingRequest(String url, String postBody, Map<String, String> headers) {
        this.url = url;
        this.headers = headers;
        this.postBody = postBody;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getPostBody() {
        return postBody;
    }
}
