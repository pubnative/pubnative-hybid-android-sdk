package net.pubnative.lite.sdk.api;

import net.pubnative.lite.sdk.models.AdSize;

public class LeaderboardRequestManager extends RequestManager {
    @Override
    protected AdSize getAdSize() {
        return AdSize.SIZE_728x90;
    }
}
