package net.pubnative.lite.sdk.consent;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.models.UserConsentRequestModel;
import net.pubnative.lite.sdk.models.UserConsentResponseModel;
import net.pubnative.lite.sdk.network.PNHttpRequest;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.UrlUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserConsentRequest {
    private static final String TAG = CheckConsentRequest.class.getSimpleName();

    public interface UserConsentListener {
        void onSuccess(UserConsentResponseModel model);

        void onFailure(Throwable error);
    }

    public void doRequest(Context context, String appToken, UserConsentRequestModel request, final UserConsentListener listener) {
        String url = PNConsentEndpoints.getConsentUrl();

        try {
            PNHttpRequest httpRequest = new PNHttpRequest();
            httpRequest.setPOSTString(request.toJson().toString());

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", String.format(Locale.ENGLISH, "Bearer %s", appToken));

            httpRequest.setHeaders(headers);

            httpRequest.start(context, url, new PNHttpRequest.Listener() {
                @Override
                public void onPNHttpRequestFinish(PNHttpRequest request, String result) {
                    if (listener != null) {
                        handleResponse(result, listener);
                    }
                }

                @Override
                public void onPNHttpRequestFail(PNHttpRequest request, Exception exception) {
                    if (listener != null) {
                        listener.onFailure(exception);
                    }
                }
            });
        } catch (Exception exception) {
            Logger.e(TAG, exception.getMessage());
            listener.onFailure(exception);
        }
    }

    private void handleResponse(String result, UserConsentListener listener) {
        if (TextUtils.isEmpty(result)) {
            Exception exception = new Exception("Empty response received from server");
            Logger.e(TAG, exception.getMessage());
            listener.onFailure(exception);
        } else {
            try {
                UserConsentResponseModel model = new UserConsentResponseModel(new JSONObject(result));
                listener.onSuccess(model);
            } catch (Exception exception) {
                Logger.e(TAG, exception.getMessage());
                listener.onFailure(exception);
            }
        }
    }
}
