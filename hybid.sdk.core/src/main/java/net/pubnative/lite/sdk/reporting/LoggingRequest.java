package net.pubnative.lite.sdk.reporting;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.utils.Logger;

import org.json.JSONObject;

public class LoggingRequest {
    private static final String TAG = LoggingRequest.class.getSimpleName();

    public interface Listener {
        void onLogSubmitted();

        void onLogError(Throwable error);
    }

    public void doRequest(Context context, final String appToken, final JSONObject report, final Listener listener) {
        if (context == null || report == null || TextUtils.isEmpty(appToken)) {
            if (listener != null) {
                listener.onLogError(new Exception("Invalid log body."));
            }
        } else {
            String url = LoggingEndpoints.getLoggingUrl(appToken);

            try {
                String jsonBody = report.toString();
                PNHttpClient.makeRequest(context, url, null, jsonBody, new PNHttpClient.Listener() {
                    @Override
                    public void onSuccess(String response) {
                        if (listener != null) {
                            listener.onLogSubmitted();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        if (listener != null) {
                            listener.onLogError(error);
                        }
                    }
                });
            } catch (Exception exception) {
                Logger.e(TAG, exception.getMessage());
                listener.onLogError(exception);
            }
        }
    }
}
