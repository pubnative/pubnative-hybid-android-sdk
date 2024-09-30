package net.pubnative.lite.sdk.api;

import android.content.Context;

import net.pubnative.lite.sdk.models.AtomConfigResponse;
import net.pubnative.lite.sdk.network.PNHttpClient;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class SDKConfigAPiClient {

    Context mContext;
    String mAppToken;

    String url = "";
    private final Boolean ATOM_DEFAULT_VALUE = false;

    public SDKConfigAPiClient(Context context) {
        this.mContext = context;
    }

    public void fetchConfig(AtomConfigListener listener) {
        if (mAppToken == null || mAppToken.isEmpty() || mContext == null) {
            listener.onAtomValueFetched(ATOM_DEFAULT_VALUE);
            return;
        }

        url = String.format(url, mAppToken);

        PNHttpClient.makeRequest(mContext, url, null, null, false, true, new PNHttpClient.Listener() {
            @Override
            public void onSuccess(String response, Map<String, List<String>> headers) {
                processStream(response, listener);
            }

            @Override
            public void onFailure(Throwable error) {
                listener.onAtomValueFetched(ATOM_DEFAULT_VALUE);
            }
        });
    }

    public void processStream(String result, AtomConfigListener listener) {
        AtomConfigResponse atomConfigResponse;
        try {
            atomConfigResponse = new AtomConfigResponse(new JSONObject(result));
            processStream(atomConfigResponse, listener);
        } catch (Exception | Error exception) {
            listener.onAtomValueFetched(ATOM_DEFAULT_VALUE);
        }
    }

    public void processStream(AtomConfigResponse atomConfigResponse, AtomConfigListener listener) {
        if (atomConfigResponse == null || atomConfigResponse.configs == null || atomConfigResponse.configs.app_level == null) {
            listener.onAtomValueFetched(ATOM_DEFAULT_VALUE);
        } else if (AtomConfigResponse.Status.OK.equals(atomConfigResponse.status)) {
            fetchAtomConfigValue(atomConfigResponse, listener);
        } else {
            listener.onAtomValueFetched(ATOM_DEFAULT_VALUE);
        }
    }

    private void fetchAtomConfigValue(AtomConfigResponse atomConfigResponse, AtomConfigListener listener) {
        listener.onAtomValueFetched(atomConfigResponse.configs.isAtomEnabled());
    }

    public void setAppToken(String appToken) {
        this.mAppToken = appToken;
    }

    public interface AtomConfigListener {
        void onAtomValueFetched(Boolean isAtomEnabled);
    }
}