package net.pubnative.lite.sdk.reporting;

import android.content.Context;
import android.util.Log;

import net.pubnative.lite.sdk.network.PNHttpClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReportingDelegate {
    private static final String TAG = ReportingDelegate.class.getSimpleName();

    private final Context mContext;
    private final String mReportingUrl;



    public ReportingDelegate(Context context, String reportingUrl){
        mContext = context;
        mReportingUrl = reportingUrl;
    }

    public void reportEventRaw(JSONObject body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", getAuthString());

        PNHttpClient.makeRequest(mContext, mReportingUrl, headers, body.toString(), new PNHttpClient.Listener() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "Request succeded with response: " + response);
            }

            @Override
            public void onFailure(Throwable error) {
                Log.d(TAG, "Request failed", error);
            }
        });
    }

    // todo : Define Auth String
    private String getAuthString(){
        return "";
    }
}
