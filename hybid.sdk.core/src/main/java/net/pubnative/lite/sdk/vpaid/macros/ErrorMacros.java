package net.pubnative.lite.sdk.vpaid.macros;

import android.text.TextUtils;

public class ErrorMacros {
    private static final String MACRO_ERROR_CODE = "[ERRORCODE]";

    public String processUrl(String url, String errorCode) {
        if (TextUtils.isEmpty(errorCode)) {
            return url;
        } else {
            return url.replace(MACRO_ERROR_CODE, errorCode);
        }
    }
}
