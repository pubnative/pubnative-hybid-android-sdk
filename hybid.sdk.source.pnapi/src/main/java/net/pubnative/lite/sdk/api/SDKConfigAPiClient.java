// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.api;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import net.pubnative.lite.sdk.models.RemoteConfigResponse;
import net.pubnative.lite.sdk.network.PNHttpClient;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class SDKConfigAPiClient {

    Context mContext;

    final String production_url = "https://sdkc.vervegroupinc.net/config";
    private final Boolean ATOM_DEFAULT_VALUE = false;
    private String mAppToken = "";
    private String url = "";
    private ConfigType configType;

    public SDKConfigAPiClient(Context context) {
        this.mContext = context;
        url = production_url;
        configType = ConfigType.PRODUCTION;
    }

    public void fetchConfig(AtomConfigListener listener) {
        PNHttpClient.makeRequest(mContext, buildUrl(), null, null, false, true, new PNHttpClient.Listener() {
            @Override
            public void onSuccess(String response, Map<String, List<String>> headers) {
                processStream(response, listener);
            }

            @Override
            public void onFailure(Throwable error) {
                if (listener != null) {
                    listener.onAtomValueFetched(ATOM_DEFAULT_VALUE);
                }
            }
        });
    }

    public void setAppToken(String appToken) {
        this.mAppToken = appToken;
    }

    public void setURL(String url, ConfigType configType) {
        if (TextUtils.isEmpty(url)) {
            this.url = production_url;
            this.configType = ConfigType.PRODUCTION;
        } else {
            this.url = url;
            this.configType = configType;
        }
    }

    public void processStream(String result, AtomConfigListener listener) {
        RemoteConfigResponse remoteConfigResponse;
        try {
            remoteConfigResponse = new RemoteConfigResponse(new JSONObject(result));
            processStream(remoteConfigResponse, listener);
        } catch (Exception | Error exception) {
            if (listener != null)
                listener.onAtomValueFetched(ATOM_DEFAULT_VALUE);
        }
    }

    public void processStream(RemoteConfigResponse remoteConfigResponse, AtomConfigListener listener) {
        if (remoteConfigResponse == null || remoteConfigResponse.configs == null || remoteConfigResponse.configs.app_level == null) {
            listener.onAtomValueFetched(ATOM_DEFAULT_VALUE);
        } else if (RemoteConfigResponse.Status.OK.equals(remoteConfigResponse.status)) {
            fetchAtomConfigValue(remoteConfigResponse, listener);
        } else {
            if (listener != null)
                listener.onAtomValueFetched(ATOM_DEFAULT_VALUE);
        }
    }

    private void fetchAtomConfigValue(RemoteConfigResponse remoteConfigResponse, AtomConfigListener listener) {
        if (listener != null)
            listener.onAtomValueFetched(remoteConfigResponse.configs.isAtomEnabled());
    }

    public interface AtomConfigListener {
        void onAtomValueFetched(Boolean isAtomEnabled);
    }

    private String buildUrl() {
        if (this.configType == ConfigType.PRODUCTION) {
            Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
            if (!TextUtils.isEmpty(mAppToken)) {
                uriBuilder.appendQueryParameter("app_token", mAppToken);
            }
            return uriBuilder.build().toString();
        } else {
            return url.trim();
        }
    }

    public String getUrl() {
        return url;
    }

    public ConfigType getConfigType() {
        return configType;
    }

    public enum ConfigType {
        PRODUCTION, TESTING
    }
}