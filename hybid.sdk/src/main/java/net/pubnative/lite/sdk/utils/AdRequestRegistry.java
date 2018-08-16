package net.pubnative.lite.sdk.utils;

public class AdRequestRegistry {
    private RequestItem mLastAdRequest;

    private static AdRequestRegistry sInstance;

    private AdRequestRegistry() {

    }

    public static AdRequestRegistry getInstance() {
        if (sInstance == null) {
            synchronized (AdRequestRegistry.class) {
                if (sInstance == null) {
                    sInstance = new AdRequestRegistry();
                }
            }
        }
        return sInstance;
    }

    public void setLastAdRequest(String url, String response, long latency) {
        this.mLastAdRequest = new RequestItem(url, response, latency);;
    }

    public RequestItem getLastAdRequest() {
        return mLastAdRequest;
    }

    public final class RequestItem {
        private String mUrl;
        private String mPostParams;
        private String mResponse;
        private long mLatency;

        public RequestItem(String url, String response, long latency) {
            this(url, null, response, latency);
        }

        public RequestItem(String url, String postParams, String response, long latency) {
            this.mUrl = url;
            this.mPostParams = postParams;
            this.mResponse = response;
            this.mLatency = latency;
        }

        public String getUrl() {
            return mUrl;
        }

        public String getPostParams() {
            return mPostParams;
        }

        public String getResponse() {
            return mResponse;
        }

        public long getLatency() {
            return mLatency;
        }
    }
}
