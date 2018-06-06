package net.pubnative.lite.sdk.location;

import android.content.Context;
import android.net.Uri;

import net.pubnative.lite.sdk.R;
import net.pubnative.lite.sdk.models.GeoIpResponse;
import net.pubnative.lite.sdk.network.PNHttpRequest;

import org.json.JSONObject;

public class GeoIpRequest {
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_FAILED = "fail";

    public interface GeoIpRequestListener {
        void onSuccess(GeoIpResponse geoIpResponse);

        void onFailure(Throwable exception);
    }

    public void fetchGeoIp(Context context, final GeoIpRequestListener listener) {
        PNHttpRequest request = new PNHttpRequest();
        request.start(context, PNHttpRequest.Method.GET, getEndpointUrl(), new PNHttpRequest.Listener() {
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

    private String getEndpointUrl() {
        return new Uri.Builder()
                .scheme("https")
                .authority("pro.ip-api.com")
                .appendPath("json")
                .appendQueryParameter("key", "4ykqS3YU062TII3")
                .build()
                .toString();
    }

    private void processStream(String result, GeoIpRequestListener listener) {
        GeoIpResponse responseModel = null;
        Exception parseException = null;
        try {
            responseModel = new GeoIpResponse(new JSONObject(result));
        } catch (Exception exception) {
            parseException = exception;
        } catch (Error error) {
            parseException = new Exception("Response cannot be parsed", error);
        }
        if (parseException != null) {
            listener.onFailure(parseException);
        } else if (responseModel == null) {
            listener.onFailure(new Exception("GeoIpRequest - Parse error"));
        } else if (STATUS_SUCCESS.equals(responseModel.status)) {
            listener.onSuccess(responseModel);
        } else {
            listener.onFailure(new Exception("GeoIPRequest - Server error: " + responseModel.message));
        }
    }
}
