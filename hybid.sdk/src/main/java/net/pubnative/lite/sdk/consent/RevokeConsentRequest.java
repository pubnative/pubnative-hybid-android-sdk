// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk.consent;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.models.UserConsentResponseModel;
import net.pubnative.lite.sdk.network.PNHttpRequest;
import net.pubnative.lite.sdk.utils.Logger;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RevokeConsentRequest {
    private static final String TAG = RevokeConsentRequest.class.getSimpleName();

    public interface RevokeConsentListener {
        void onSuccess(UserConsentResponseModel model);

        void onFailure(Throwable error);
    }

    public void revokeConsent(Context context, String appToken, String deviceId, String deviceIdType, final RevokeConsentListener listener) {
        if (TextUtils.isEmpty(appToken) || TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(deviceIdType)) {
            listener.onFailure(new Exception("Invalid parameters for check user consent request."));
        } else {
            String url = PNConsentEndpoints.getRevokeConsentUrl(deviceId, deviceIdType);
            PNHttpRequest httpRequest = new PNHttpRequest();

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", String.format(Locale.ENGLISH, "Bearer %s", appToken));

            httpRequest.setHeaders(headers);

            httpRequest.start(context, PNHttpRequest.Method.DELETE, url, new PNHttpRequest.Listener() {
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

    private void handleResponse(String result, RevokeConsentListener listener) {
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
