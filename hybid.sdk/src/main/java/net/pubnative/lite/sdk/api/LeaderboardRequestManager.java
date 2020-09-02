package net.pubnative.lite.sdk.api;

import java.util.Arrays;
import java.util.List;

public class LeaderboardRequestManager extends RequestManager {
    @Override
    protected String getAdSize() {
        return "s";
    }

    @Override
    protected List<String> getSupportedFrameworks(){
        return Arrays.asList("5","7");
    }
}
