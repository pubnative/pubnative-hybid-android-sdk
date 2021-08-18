package net.pubnative.lite.sdk;

import android.annotation.TargetApi;
import android.os.Build;

public class HyBidError extends Exception {
    private HyBidErrorCode errorCode = HyBidErrorCode.INTERNAL_ERROR;

    public HyBidError() {
        super();
    }

    public HyBidError(HyBidErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public HyBidError(String message) {
        super(message);
    }

    public HyBidError(HyBidErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public HyBidError(HyBidErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public HyBidError(String message, Throwable cause) {
        super(message, cause);
    }

    public HyBidError(Throwable cause) {
        super(cause);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public HyBidError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public HyBidErrorCode getErrorCode() {
        return errorCode;
    }
}
