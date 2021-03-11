package com.monet.bidder;

import android.text.TextUtils;

public enum AppMonetErrorCode {
    NO_FILL("No ads found."),
    INTERNAL_ERROR("Unable to serve ad due to invalid internal state."),
    NETWORK_TIMEOUT("Third-party network failed to respond in a timely manner."),
    NETWORK_NO_FILL("Third-party network failed to provide an ad."),
    NETWORK_INVALID_STATE("Third-party network failed due to invalid internal state."),
    CUSTOM_BANNER_LOAD_ERROR("Custom Banner not found."),
    UNSPECIFIED("Unspecified error.");

    private final String message;

    AppMonetErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }

    public static AppMonetErrorCode parseHyBidException(Throwable error) {
        if (error != null && !TextUtils.isEmpty(error.getMessage())) {
            if (error.getMessage().contains("HyBid - No fill")) {
                return NO_FILL;
            } else if (error.getMessage().contains("HyBid - Server error")
                    || error.getMessage().contains("PNApiClient - Parse error")) {
                return INTERNAL_ERROR;
            }
        }
        return UNSPECIFIED;
    }
}
