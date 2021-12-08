package net.pubnative.lite.sdk.config;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.config.encryption.AESCrypto;
import net.pubnative.lite.sdk.models.RemoteConfigModel;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.utils.Logger;

import org.json.JSONObject;

import java.security.GeneralSecurityException;

public class ConfigRequest {
    private static final String TAG = ConfigRequest.class.getSimpleName();

    public interface Listener {
        void onConfigFetched(RemoteConfigModel configModel);

        void onConfigError(Throwable error);
    }

    public void doRequest(Context context, final String appToken, final Listener listener) {
        String url = ConfigEndpoints.getConfigUrl();

        try {
            PNHttpClient.makeRequest(context, url, null, null, new PNHttpClient.Listener() {
                @Override
                public void onSuccess(String response) {
                    if (listener != null) {
                        handleResponse(response, listener, appToken);
                    }
                }

                @Override
                public void onFailure(Throwable error) {
                    if (listener != null) {
                        listener.onConfigError(error);
                    }
                }
            });
        } catch (Exception exception) {
            Logger.e(TAG, exception.getMessage());
            listener.onConfigError(exception);
        }
    }

    private void handleResponse(String result, Listener listener, String appToken) {
        if (TextUtils.isEmpty(result)) {
            Exception exception = new Exception("Empty config response received from server");
            Logger.e(TAG, exception.getMessage());
            listener.onConfigError(exception);
        } else {
            try {
                RemoteConfigModel model = new RemoteConfigModel(new JSONObject(decryptJsonFile(appToken, result)));
                listener.onConfigFetched(model);
            } catch (Exception exception) {
                Logger.e(TAG, exception.getMessage());
                listener.onConfigError(exception);
            }
        }
    }

    private String decryptJsonFile(String key, String result) throws GeneralSecurityException {
        return AESCrypto.decrypt(key, result);
    }
}