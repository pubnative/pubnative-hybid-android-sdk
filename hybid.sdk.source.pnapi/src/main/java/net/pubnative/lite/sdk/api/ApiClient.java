// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.api;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.AdResponse;

import org.json.JSONObject;

public interface ApiClient {

    public interface AdRequestListener {
        void onSuccess(Ad ad);

        void onFailure(Throwable exception);
    }

    public interface TrackUrlListener {
        void onSuccess();

        void onFailure(Throwable throwable);

        void onFinally(String requestUrl, String trackTypeName, int responseCode);
    }

    public interface TrackJSListener {
        void onSuccess(String js);

        void onFailure(Throwable throwable);
    }

    public String getApiUrl();

    public void setApiUrl(String url);

    void setCustomUrl(String url);

    public void getAd(AdRequest request, String userAgent, final AdRequestListener listener);

    public void getAd(final String url, String userAgent, final AdRequestListener listener);

    public void trackUrl(String url, String userAgent, String trackTypeName, final TrackUrlListener listener);

    public void trackJS(String js, final TrackJSListener listener);

    public void processStream(String result, AdRequestListener listener);

    public void processStream(String result, AdRequest request, Integer width, Integer height, AdRequestListener listener);

    public void processStream(AdResponse apiResponseModel, Exception parseException, AdRequestListener listener);

    public JSONObject getPlacementParams();

    public Context getContext();
}
