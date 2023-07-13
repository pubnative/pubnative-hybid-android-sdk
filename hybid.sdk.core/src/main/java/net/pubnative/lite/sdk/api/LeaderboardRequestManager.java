package net.pubnative.lite.sdk.api;

import net.pubnative.lite.sdk.models.AdRequestFactory;
import net.pubnative.lite.sdk.models.AdSize;

public class LeaderboardRequestManager extends RequestManager {

    public LeaderboardRequestManager(){
        super();
    }

    public LeaderboardRequestManager(ApiClient apiClient, AdRequestFactory requestFactory) {
        super(apiClient, requestFactory);
    }

    @Override
    public AdSize getAdSize() {
        return AdSize.SIZE_728x90;
    }
}
