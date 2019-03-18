package net.pubnative.lite.sdk.vpaid.helpers;

import android.text.TextUtils;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.enums.VastError;
import net.pubnative.lite.sdk.vpaid.utils.HttpUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ErrorLog {

    private static final String ERROR_CODE = "[ERRORCODE]";
    private static final String LOG_TAG = ErrorLog.class.getSimpleName();

    private static ExecutorService sExecutor = Executors.newCachedThreadPool();
    private static String sErrorLogUrl;

    private ErrorLog() {
    }

    public static void initErrorLog(String url) {
        sErrorLogUrl = url;
    }

    public static synchronized void postError(final VastError error) {
        if (TextUtils.isEmpty(sErrorLogUrl)) {
            return;
        }
        sExecutor.submit(new Runnable() {
            @Override
            public void run() {
                String url = sErrorLogUrl;
                if (sErrorLogUrl.contains(ERROR_CODE)) {
                    url = url.replace(ERROR_CODE, error.getValue());

                }
                Logger.d(LOG_TAG, url);
                HttpUtil.sendRequest(url, null, null);
            }
        });
    }
}
