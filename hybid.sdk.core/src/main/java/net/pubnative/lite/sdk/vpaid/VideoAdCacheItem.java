package net.pubnative.lite.sdk.vpaid;

import net.pubnative.lite.sdk.vpaid.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.response.AdParams;

public class VideoAdCacheItem {
    private final AdParams mAdParams;
    private final String mVideoFilePath;
    private final EndCardData mEndCardData;
    private final String mEndCardFilePath;

    public VideoAdCacheItem(AdParams adParams, String videoFilePath, EndCardData endCardData, String endCardFilePath) {
        this.mAdParams = adParams;
        this.mVideoFilePath = videoFilePath;
        this.mEndCardData = endCardData;
        this.mEndCardFilePath = endCardFilePath;
    }

    public AdParams getAdParams() {
        return mAdParams;
    }

    public String getVideoFilePath() {
        return mVideoFilePath;
    }

    public EndCardData getEndCardData() {
        return mEndCardData;
    }

    public String getEndCardFilePath() {
        return mEndCardFilePath;
    }
}
