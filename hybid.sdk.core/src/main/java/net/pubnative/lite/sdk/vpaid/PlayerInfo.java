package net.pubnative.lite.sdk.vpaid;

import android.text.TextUtils;

@SuppressWarnings("unused")
public class PlayerInfo {

    private final String mMessage;
    private boolean mNoAdsFound;

    public PlayerInfo(String message) {
        if (TextUtils.isEmpty(message)) {
            message = "Unknown error";
        }
        this.mMessage = message;
    }

    public void setNoAdsFound() {
        mNoAdsFound = true;
    }

    public String getMessage() {
        return mMessage;
    }

    public boolean isNoAdsFound() {
        return mNoAdsFound;
    }

    @Override
    public String toString() {
        return "PlayerInfo{" +
                "message='" + mMessage + '\'' +
                '}';
    }
}
