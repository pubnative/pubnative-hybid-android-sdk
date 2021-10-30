package net.pubnative.lite.sdk.reporting;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.source.pnapi.BuildConfig;

import java.util.HashMap;
import java.util.Map;

public class EventLoggingDelegate {

    private static final String TAG = EventLoggingDelegate.class.getSimpleName();

    private final Context mContext;
    private final String mUrl;

    public EventLoggingDelegate(Context context, String url) {
        mContext = context;
        mUrl = url;
    }

    public void reportEventLog(Ad ad, String ad_type, String message) {

        Uri.Builder builtUri = Uri.parse(mUrl)
                .buildUpon();

//        params.put("dsp_id", "99");

        int isDebug = 0;
        if (BuildConfig.DEBUG) {
            isDebug = 1;
        }

        builtUri.appendQueryParameter("debug", String.valueOf(isDebug));

        if (HyBid.getBundleId() != null)
            builtUri.appendQueryParameter("pub_app_id", HyBid.getBundleId());

        if (ad != null) {
            builtUri.appendQueryParameter("apptoken", HyBid.getAppToken());
            builtUri.appendQueryParameter("creative_id", ad.getCreativeId());
            builtUri.appendQueryParameter("imp_id", ad.getImpressionId());
        }

        if (ad_type != null) {
            builtUri.appendQueryParameter("type", ad_type);
        }

        if (message != null)
            builtUri.appendQueryParameter("message", message);

        if (HyBid.getDeviceInfo() != null) {
            String userAgent = HyBid.getDeviceInfo().getUserAgent();
            if (!TextUtils.isEmpty(userAgent)) {
                builtUri.appendQueryParameter("User-Agent", userAgent);
            }
        }

        PNHttpClient.makeRequest(mContext, builtUri.build().toString(), null, null, new PNHttpClient.Listener() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, response);
            }

            @Override
            public void onFailure(Throwable error) {
                Log.d(TAG, error.toString());
            }
        });
    }
}