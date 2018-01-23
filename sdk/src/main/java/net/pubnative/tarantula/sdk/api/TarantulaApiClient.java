package net.pubnative.tarantula.sdk.api;

import android.content.Context;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.models.AdRequest;
import net.pubnative.tarantula.sdk.models.AdResponse;
import net.pubnative.tarantula.sdk.network.TarantulaHttpRequest;
import net.pubnative.tarantula.sdk.utils.TarantulaApiUrlComposer;

import org.json.JSONObject;

/**
 * Created by erosgarciaponte on 17.01.18.
 */

public class TarantulaApiClient {
    private static final String TAG = TarantulaApiClient.class.getSimpleName();

    public interface AdRequestListener {
        void onSuccess(Ad ad);

        void onFailure(Throwable exception);
    }

    public interface TrackUrlListener {
        void onSuccess();

        void onFailure(Throwable throwable);
    }

    private Context mContext;

    public TarantulaApiClient(Context context) {
        this.mContext = context;
    }

    public void getAd(AdRequest request, final AdRequestListener listener) {
        String url = getAdRequestURL(request);
        if (url == null) {
            if (listener != null) {
                listener.onFailure(new Exception("TarantulaApiClient - Error: invalid request URL"));
            }
        } else {
            TarantulaHttpRequest httpRequest = new TarantulaHttpRequest();
            httpRequest.start(mContext, url, new TarantulaHttpRequest.Listener() {
                @Override
                public void onPNHttpRequestFinish(TarantulaHttpRequest request, String result) {
                    processStream(result, listener);
                }

                @Override
                public void onPNHttpRequestFail(TarantulaHttpRequest request, Exception exception) {
                    if (listener != null) {
                        listener.onFailure(exception);
                    }
                }
            });
        }
    }

    public void trackUrl(String url, final TrackUrlListener listener) {
        TarantulaHttpRequest httpRequest = new TarantulaHttpRequest();
        httpRequest.start(mContext, url, new TarantulaHttpRequest.Listener() {
            @Override
            public void onPNHttpRequestFinish(TarantulaHttpRequest request, String result) {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onPNHttpRequestFail(TarantulaHttpRequest request, Exception exception) {
                if (listener != null) {
                    listener.onFailure(exception);
                }
            }
        });
    }

    protected String getAdRequestURL(AdRequest adRequest) {
        return TarantulaApiUrlComposer.buildUrl(Tarantula.BASE_URL, adRequest);
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
            listener.onFailure(new Exception("TarantulaApiClient - Parse error"));
        } else if (AdResponse.Status.OK.equals(apiResponseModel.status)) {
            // STATUS 'OK'
            if (apiResponseModel.ads != null && !apiResponseModel.ads.isEmpty()) {
                listener.onSuccess(apiResponseModel.ads.get(0));
            } else {
                listener.onFailure(new Exception("Tarantula - No fill"));
            }
        } else {
            // STATUS 'ERROR'
            listener.onFailure(new Exception("Tarantula - Server error: " + apiResponseModel.error_message));
        }
    }
}
