package net.pubnative.lite.sdk.vpaid.helpers;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.enums.VastError;

import java.util.ArrayList;
import java.util.List;

public class ErrorLog {

    private static final String ERROR_CODE = "[ERRORCODE]";
    private static final String LOG_TAG = ErrorLog.class.getSimpleName();

    private static List<String> sErrorLogUrls;

    private ErrorLog() {
    }

    public static void initErrorLog(String url) {
        if (sErrorLogUrls == null) {
            sErrorLogUrls = new ArrayList<>();
        } else {
            sErrorLogUrls.clear();
        }
        sErrorLogUrls.add(url);
    }

    public static void initErrorLog(List<String> urls) {
        if (sErrorLogUrls == null) {
            sErrorLogUrls = new ArrayList<>();
        } else {
            sErrorLogUrls.clear();
        }
        sErrorLogUrls.addAll(urls);
    }

    public static synchronized void postError(final Context context, final VastError error) {
        if (sErrorLogUrls == null || sErrorLogUrls.isEmpty()) {
            return;
        }

        for (String errorLogUrl: sErrorLogUrls) {
            if (!TextUtils.isEmpty(errorLogUrl)) {
                String url = errorLogUrl;
                if (url.contains(ERROR_CODE)) {
                    url = url.replace(ERROR_CODE, error.getValue());
                }
                Logger.d(LOG_TAG, url);

                PNHttpClient.makeRequest(context, url, null, null, new PNHttpClient.Listener() {
                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onFailure(Throwable error) {

                    }
                });
            }
        }
    }
}
