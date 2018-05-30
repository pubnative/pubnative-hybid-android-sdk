package net.pubnative.lite.sdk.consent;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.models.UserConsentResponseModel;
import net.pubnative.lite.sdk.network.PNHttpRequest;
import net.pubnative.lite.sdk.utils.Logger;

import org.json.JSONObject;

public class CheckConsentRequest {
    private static final String TAG = CheckConsentRequest.class.getSimpleName();

    public interface CheckConsentListener {
        void onSuccess(UserConsentResponseModel model);

        void onFailure(Throwable error);
    }

    public void checkConsent(Context context, String appToken, String deviceId, final CheckConsentListener listener) {
        if (TextUtils.isEmpty(appToken) || TextUtils.isEmpty(deviceId)) {
            listener.onFailure(new Exception("Invalid parameters for check user consent request."));
        } else {
            String url = PNConsentEndpoints.getCheckConsentUrl(appToken, deviceId);
            PNHttpRequest httpRequest = new PNHttpRequest();
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
        }
    }

    private void handleResponse(String result, CheckConsentListener listener) {
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
