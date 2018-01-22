package net.pubnative.tarantula.sdk.api;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.models.AdRequest;
import net.pubnative.tarantula.sdk.models.AdResponse;
import net.pubnative.tarantula.sdk.network.TarantulaHttpRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        // Base URL
        Uri.Builder uriBuilder = Uri.parse(Tarantula.BASE_URL).buildUpon();
        // Appending parameters
        if (!TextUtils.isEmpty(adRequest.apptoken)) {
            uriBuilder.appendQueryParameter("apptoken", adRequest.apptoken);
        }

        if (!TextUtils.isEmpty(adRequest.os)) {
            uriBuilder.appendQueryParameter("os", adRequest.os);
        }

        if (!TextUtils.isEmpty(adRequest.osver)) {
            uriBuilder.appendQueryParameter("osver", adRequest.osver);
        }

        if (!TextUtils.isEmpty(adRequest.devicemodel)) {
            uriBuilder.appendQueryParameter("devicemodel", adRequest.devicemodel);
        }

        if (!TextUtils.isEmpty(adRequest.dnt)) {
            uriBuilder.appendQueryParameter("dnt", adRequest.dnt);
        }

        if (!TextUtils.isEmpty(adRequest.al)) {
            uriBuilder.appendQueryParameter("al", adRequest.al);
        }

        if (!TextUtils.isEmpty(adRequest.mf)) {
            uriBuilder.appendQueryParameter("mf", adRequest.mf);
        }

        if (!TextUtils.isEmpty(adRequest.zoneid)) {
            uriBuilder.appendQueryParameter("zoneid", adRequest.zoneid);
        }

        if (!TextUtils.isEmpty(adRequest.testMode)) {
            uriBuilder.appendQueryParameter("test", adRequest.testMode);
        }

        if (!TextUtils.isEmpty(adRequest.locale)) {
            uriBuilder.appendQueryParameter("locale", adRequest.locale);
        }

        if (!TextUtils.isEmpty(adRequest.latitude)) {
            uriBuilder.appendQueryParameter("lat", adRequest.latitude);
        }

        if (!TextUtils.isEmpty(adRequest.longitude)) {
            uriBuilder.appendQueryParameter("long", adRequest.longitude);
        }

        if (!TextUtils.isEmpty(adRequest.gender)) {
            uriBuilder.appendQueryParameter("gender", adRequest.gender);
        }

        if (!TextUtils.isEmpty(adRequest.age)) {
            uriBuilder.appendQueryParameter("age", adRequest.age);
        }

        if (!TextUtils.isEmpty(adRequest.bundleid)) {
            uriBuilder.appendQueryParameter("bundleid", adRequest.bundleid);
        }

        if (!TextUtils.isEmpty(adRequest.keywords)) {
            uriBuilder.appendQueryParameter("keywords", adRequest.keywords);
        }

        if (!TextUtils.isEmpty(adRequest.coppa)) {
            uriBuilder.appendQueryParameter("coppa", adRequest.coppa);
        }

        if (!TextUtils.isEmpty(adRequest.gid)) {
            uriBuilder.appendQueryParameter("gid", adRequest.gid);
        }

        if (!TextUtils.isEmpty(adRequest.gidmd5)) {
            uriBuilder.appendQueryParameter("gidmd5", adRequest.gidmd5);
        }

        if (!TextUtils.isEmpty(adRequest.gidsha1)) {
            uriBuilder.appendQueryParameter("gidsha1", adRequest.gidsha1);
        }

        return uriBuilder.build().toString();
    }

    protected void processStream(String result, AdRequestListener listener) {
        AdResponse apiResponseModel = null;
        Exception parseException = null;
        try {
            apiResponseModel = new AdResponse(new JSONObject(result));
        } catch (Exception exception) {
            parseException = exception;
        } catch (Error error) {
            parseException = new Exception("Response can not be parsed!", error);
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
            listener.onFailure(new Exception("PNAPIRequest - Server error: " + apiResponseModel.error_message));
        }
    }
}
