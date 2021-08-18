package net.pubnative.lite.sdk;

public enum HyBidErrorCode {
    NO_FILL(1, "HyBid - No fill"),
    PARSER_ERROR(2, "PNApiClient - Parse error"),
    SERVER_ERROR_PREFIX(3, "HyBid - Server error: "),
    INVALID_ASSET(4, "The server has returned an invalid ad asset"),
    UNSUPPORTED_ASSET(5, "The server has returned an unsupported ad asset"),
    NULL_AD(6, "Server returned null ad"),
    INVALID_AD(7, "The provided ad is invalid"),
    INVALID_ZONE_ID(8, "Invalid zone id provided"),
    INVALID_SIGNAL_DATA(9, "Invalid signal data provided"),
    NOT_INITIALISED(10, "The HyBid SDK has not been initialised"),
    AUCTION_NO_AD(11, "The auction returned no ad"),
    ERROR_RENDERING_BANNER(12, "An error has occurred while rendering the ad"),
    ERROR_RENDERING_INTERSTITIAL(13, "An error has occurred while rendering the interstitial"),
    ERROR_RENDERING_REWARDED(14, "An error has occurred while rendering the rewarded ad"),
    MRAID_PLAYER_ERROR(15, "Error rendering HTML/MRAID ad"),
    VAST_PLAYER_ERROR(16, "Error rendering VAST ad"),
    ERROR_TRACKING_URL(17, "Error reporting URL tracker"),
    ERROR_TRACKING_JS(18, "Error reporting JS tracker"),
    INVALID_URL(19, "PNApiClient - Error: invalid request URL"),
    INTERNAL_ERROR(20, "An internal error has occurred in the HyBid SDK"),
    UNKNOWN_ERROR(21, "An unknown error has occurred in the HyBid SDK");

    private int code;
    private String message;

    HyBidErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
