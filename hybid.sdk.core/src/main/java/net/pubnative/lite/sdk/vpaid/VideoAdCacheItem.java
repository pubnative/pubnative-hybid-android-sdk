package net.pubnative.lite.sdk.vpaid;

import net.pubnative.lite.sdk.vpaid.response.AdParams;

public class VideoAdCacheItem {
    private final AdParams mAdParams;
    private final String mVideoFilePath;
    private final String mEndCardFilePath;

    public VideoAdCacheItem(AdParams adParams, String videoFilePath, String endCardFilePath) {
        this.mAdParams = adParams;
        this.mVideoFilePath = videoFilePath;
        this.mEndCardFilePath = endCardFilePath;
    }

    public AdParams getAdParams() {
        return mAdParams;
    }

    public String getVideoFilePath() {
        return mVideoFilePath;
    }

    public String getEndCardFilePath() {
        return mEndCardFilePath;
    }
}
