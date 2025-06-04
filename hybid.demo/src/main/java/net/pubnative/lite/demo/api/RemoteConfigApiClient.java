// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.api;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdResponse;
import net.pubnative.lite.demo.models.RemoteConfigParam;
import net.pubnative.lite.demo.models.RemoteConfigRequest;
import net.pubnative.lite.sdk.network.PNHttpClient;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteConfigApiClient {

    private static final String TAG = "RemoteConfigApiClient";

    private final String url = "http://creative-sampler.herokuapp.com/customisation/config";

    public RemoteConfigApiClient() {
    }

    public void sendBannerRequest(Context context, String adm, String format, String adm_type, String custom_cta_value, String custom_cta_app_name, String bundle_id_value, String custom_endcard_value, Integer width, Integer height, List<RemoteConfigParam> params, OnConfigFetchListener listener) {
        RemoteConfigRequest remoteConfigRequest = formatBannerRequest(adm, format, adm_type, custom_cta_value, custom_cta_app_name, bundle_id_value, custom_endcard_value, width, height, params);
        if (remoteConfigRequest == null)
            listener.onFetchError(new HyBidError(HyBidErrorCode.PARSER_ERROR));
        try {
            String postBody;
            if (remoteConfigRequest != null) {
                postBody = remoteConfigRequest.toJson().toString();
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                PNHttpClient.makeRequest(context, url, headers, postBody,
                        true, false,
                        new PNHttpClient.Listener() {
                            @Override
                            public void onSuccess(String response, Map<String, List<String>> headers) {
                                processStream(response, listener);
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                listener.onFetchError(new HyBidError(HyBidErrorCode.ERROR_RENDERING_BANNER));
                            }
                        });
            } else {
                listener.onFetchError(new HyBidError(HyBidErrorCode.ERROR_RENDERING_BANNER));
            }
        } catch (Exception e) {
            listener.onFetchError(new HyBidError(HyBidErrorCode.ERROR_RENDERING_BANNER));
        }
    }

    public void sendInterstitialRequest(Context context, String adm, String format, String adm_type, String custom_cta_value, String custom_cta_app_name, String bundle_id_value, String custom_endcard_value, List<RemoteConfigParam> params, OnConfigFetchListener listener) {
        RemoteConfigRequest remoteConfigRequest = formatInterstitialRequest(adm, format, adm_type, custom_cta_value, custom_cta_app_name, bundle_id_value, custom_endcard_value, params);
        if (remoteConfigRequest == null)
            listener.onFetchError(new HyBidError(HyBidErrorCode.PARSER_ERROR));
        try {
            String postBody;
            if (remoteConfigRequest != null) {
                postBody = remoteConfigRequest.toJson().toString();
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                PNHttpClient.makeRequest(context, url, headers, postBody,
                        true, false,
                        new PNHttpClient.Listener() {
                            @Override
                            public void onSuccess(String response, Map<String, List<String>> headers) {
                                processStream(response, listener);
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                listener.onFetchError(new HyBidError(HyBidErrorCode.ERROR_RENDERING_INTERSTITIAL));
                            }
                        });
            } else {
                listener.onFetchError(new HyBidError(HyBidErrorCode.ERROR_RENDERING_INTERSTITIAL));
            }
        } catch (Exception e) {
            listener.onFetchError(new HyBidError(HyBidErrorCode.ERROR_RENDERING_INTERSTITIAL));
        }
    }

    public void sendRewardedRequest(Context context, String adm, String format, String adm_type, String custom_cta_value, String custom_cta_app_name, String bundle_id_value, String custom_endcard_value, List<RemoteConfigParam> params, OnConfigFetchListener listener) {
        RemoteConfigRequest remoteConfigRequest = formatRewardedRequest(adm, format, adm_type, custom_cta_value, custom_cta_app_name, bundle_id_value, custom_endcard_value, params);
        if (remoteConfigRequest == null)
            listener.onFetchError(new HyBidError(HyBidErrorCode.PARSER_ERROR));
        try {
            String postBody;
            if (remoteConfigRequest != null) {
                postBody = remoteConfigRequest.toJson().toString();
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                PNHttpClient.makeRequest(context, url, headers, postBody,
                        true, false,
                        new PNHttpClient.Listener() {
                            @Override
                            public void onSuccess(String response, Map<String, List<String>> headers) {
                                processStream(response, listener);
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                listener.onFetchError(new HyBidError(HyBidErrorCode.ERROR_RENDERING_REWARDED));
                            }
                        });
            } else {
                listener.onFetchError(new HyBidError(HyBidErrorCode.ERROR_RENDERING_REWARDED));
            }
        } catch (Exception e) {
            listener.onFetchError(new HyBidError(HyBidErrorCode.ERROR_RENDERING_REWARDED));
        }
    }

    private RemoteConfigRequest formatBannerRequest(String adm, String format, String adm_type, String custom_cta_value, String custom_cta_app_name, String bundle_id_value, String custom_endcard_value, Integer width, Integer height, List<RemoteConfigParam> params) {
        RemoteConfigRequest remoteConfigRequest;
        try {
            remoteConfigRequest = new RemoteConfigRequest();
            remoteConfigRequest.format = format;
            remoteConfigRequest.os = "android";
            remoteConfigRequest.fullscreen = false;
            remoteConfigRequest.rewarded = false;
            remoteConfigRequest.width = width;
            remoteConfigRequest.height = height;
            remoteConfigRequest.adm_type = adm_type;
            remoteConfigRequest.encoded_adm = encodeAdm(adm);
            remoteConfigRequest.custom_cta_app_name = custom_cta_app_name;
            remoteConfigRequest.custom_cta_value = custom_cta_value;
            remoteConfigRequest.bundle_id_value = bundle_id_value;
            remoteConfigRequest.custom_endcard_value = custom_endcard_value;
            remoteConfigRequest.configs = params;
            return remoteConfigRequest;
        } catch (Exception e) {
            return null;
        }
    }

    private RemoteConfigRequest formatInterstitialRequest(String adm, String format, String adm_type, String custom_cta_value, String custom_cta_app_name, String bundle_id_value, String custom_endcard_value, List<RemoteConfigParam> params) {
        RemoteConfigRequest remoteConfigRequest;
        try {
            remoteConfigRequest = new RemoteConfigRequest();
            remoteConfigRequest.format = format;
            remoteConfigRequest.os = "android";
            remoteConfigRequest.fullscreen = true;
            remoteConfigRequest.rewarded = false;
            remoteConfigRequest.adm_type = adm_type;
            remoteConfigRequest.encoded_adm = encodeAdm(adm);
            remoteConfigRequest.custom_cta_app_name = custom_cta_app_name;
            remoteConfigRequest.custom_cta_value = custom_cta_value;
            remoteConfigRequest.bundle_id_value = bundle_id_value;
            remoteConfigRequest.custom_endcard_value = custom_endcard_value;
            remoteConfigRequest.configs = params;
            return remoteConfigRequest;
        } catch (Exception e) {
            return null;
        }
    }

    private RemoteConfigRequest formatRewardedRequest(String adm, String format, String adm_type, String custom_cta_value, String custom_cta_app_name, String bundle_id_value, String custom_endcard_value, List<RemoteConfigParam> params) {
        RemoteConfigRequest remoteConfigRequest;
        try {
            remoteConfigRequest = new RemoteConfigRequest();
            remoteConfigRequest.format = format;
            remoteConfigRequest.os = "android";
            remoteConfigRequest.fullscreen = true;
            remoteConfigRequest.rewarded = true;
            remoteConfigRequest.adm_type = adm_type;
            remoteConfigRequest.encoded_adm = encodeAdm(adm);
            remoteConfigRequest.custom_cta_app_name = custom_cta_app_name;
            remoteConfigRequest.custom_cta_value = custom_cta_value;
            remoteConfigRequest.bundle_id_value = bundle_id_value;
            remoteConfigRequest.custom_endcard_value = custom_endcard_value;
            remoteConfigRequest.configs = params;
            return remoteConfigRequest;
        } catch (Exception e) {
            return null;
        }
    }

    public void processStream(String result, OnConfigFetchListener listener) {
        AdResponse apiResponseModel = null;
        Exception parseException = null;
        try {
            apiResponseModel = new AdResponse(new JSONObject(result));
        } catch (Exception exception) {
            parseException = exception;
        } catch (Error error) {
            parseException = new HyBidError(HyBidErrorCode.PARSER_ERROR, error);
        }

        processStream(apiResponseModel, result, parseException, listener);
    }

    public void processStream(AdResponse apiResponseModel, String response, Exception parseException, OnConfigFetchListener listener) {
        if (parseException != null) {
            listener.onFetchError(new HyBidError(HyBidErrorCode.PARSER_ERROR, parseException));
        } else if (apiResponseModel == null) {
            listener.onFetchError(new HyBidError(HyBidErrorCode.PARSER_ERROR));
        } else if (AdResponse.Status.OK.equals(apiResponseModel.status)) {
            // STATUS 'OK'
            if (apiResponseModel.ads != null && !apiResponseModel.ads.isEmpty()) {
                listener.onFetchSuccess(apiResponseModel.ads.get(0), response);
            } else {
                listener.onFetchError(new HyBidError(HyBidErrorCode.NO_FILL));
            }
        } else {
            // STATUS 'ERROR'
            Log.d(TAG, HyBidErrorCode.SERVER_ERROR_PREFIX.getMessage() + apiResponseModel.error_message);
            listener.onFetchError(new HyBidError(HyBidErrorCode.SERVER_ERROR_PREFIX, new Exception(apiResponseModel.error_message)));
        }
    }

    private String encodeAdm(String adm) {
        byte[] admBytes;
        admBytes = adm.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeToString(admBytes, Base64.DEFAULT);
    }

    public interface OnConfigFetchListener {
        void onFetchSuccess(Ad ad, String response);

        void onFetchError(HyBidError error);
    }
}