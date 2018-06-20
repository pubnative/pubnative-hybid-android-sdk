package net.pubnative.lite.sdk.api;

import android.content.Context;

import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.AdResponse;
import net.pubnative.lite.sdk.network.PNHttpRequest;
import net.pubnative.lite.sdk.utils.PNApiUrlComposer;

import org.json.JSONObject;

/**
 * Created by erosgarciaponte on 17.01.18.
 */

public class PNApiClient {
    private static final String TAG = PNApiClient.class.getSimpleName();

    public interface AdRequestListener {
        void onSuccess(Ad ad);

        void onFailure(Throwable exception);
    }

    public interface TrackUrlListener {
        void onSuccess();

        void onFailure(Throwable throwable);
    }

    private Context mContext;

    public PNApiClient(Context context) {
        this.mContext = context;
    }

    public void getAd(AdRequest request, final AdRequestListener listener) {
        String url = getAdRequestURL(request);
        if (url == null) {
            if (listener != null) {
                listener.onFailure(new Exception("PNApiClient - Error: invalid request URL"));
            }
        } else {
            PNHttpRequest httpRequest = new PNHttpRequest();
            httpRequest.start(mContext, PNHttpRequest.Method.GET, url, new PNHttpRequest.Listener() {
                @Override
                public void onPNHttpRequestFinish(PNHttpRequest request, String result) {
                    processStream(result, listener);
                }

                @Override
                public void onPNHttpRequestFail(PNHttpRequest request, Exception exception) {
                    if (listener != null) {
                        listener.onFailure(exception);
                    }
                }
            });
        }
    }

    public void trackUrl(String url, final TrackUrlListener listener) {
        PNHttpRequest httpRequest = new PNHttpRequest();
        httpRequest.start(mContext, PNHttpRequest.Method.GET, url, new PNHttpRequest.Listener() {
            @Override
            public void onPNHttpRequestFinish(PNHttpRequest request, String result) {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onPNHttpRequestFail(PNHttpRequest request, Exception exception) {
                if (listener != null) {
                    listener.onFailure(exception);
                }
            }
        });
    }

    protected String getAdRequestURL(AdRequest adRequest) {
        return PNApiUrlComposer.buildUrl(PNLite.BASE_URL, adRequest);
    }

    protected void processStream(String result, AdRequestListener listener) {
        AdResponse apiResponseModel = null;
        Exception parseException = null;
        try {
            apiResponseModel = new AdResponse(new JSONObject(result));
        } catch (Exception exception) {
            parseException = exception;
        } catch (Error error) {
            parseException = new Exception("Response cannot be parsed", error);
        }
        if (parseException != null) {
            listener.onFailure(parseException);
        } else if (apiResponseModel == null) {
            listener.onFailure(new Exception("PNApiClient - Parse error"));
        } else if (AdResponse.Status.OK.equals(apiResponseModel.status)) {
            // STATUS 'OK'
            if (apiResponseModel.ads != null && !apiResponseModel.ads.isEmpty()) {
                listener.onSuccess(apiResponseModel.ads.get(0));
            } else {
                listener.onFailure(new Exception("PNLite - No fill"));
            }
        } else {
            // STATUS 'ERROR'
            listener.onFailure(new Exception("PNLite - Server error: " + apiResponseModel.error_message));
        }
    }
}
