package net.pubnative.lite.sdk.vpaid;

import android.view.View;

import com.iab.omid.library.pubnativenet.adsession.FriendlyObstructionPurpose;

import net.pubnative.lite.sdk.viewability.HyBidViewabilityFriendlyObstruction;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityNativeVideoAdSession;
import net.pubnative.lite.sdk.vpaid.models.CloseCardData;
import net.pubnative.lite.sdk.vpaid.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.response.AdParams;

import java.util.List;

public interface VideoAdController {

    void resumeEndCardCloseButtonTimer();

    void pauseEndCardCloseButtonTimer();

    interface OnPreparedListener {
        void onPrepared();
    }

    void prepare(OnPreparedListener listener);

    void setVideoFilePath(String filePath);

    void setEndCardData(EndCardData endCardData);

    void setCloseCardData(CloseCardData closeCardData);

    void setEndCardFilePath(String filePath);

    void buildVideoAdView(VideoAdView bannerView);

    void openUrl(String url);

    AdParams getAdParams();

    HyBidViewabilityNativeVideoAdSession getViewabilityAdSession();

    void addViewabilityFriendlyObstruction(View view, FriendlyObstructionPurpose purpose, String reason);

    List<HyBidViewabilityFriendlyObstruction> getViewabilityFriendlyObstructions();

    int getProgress();

    void toggleMute();

    void setVolume(boolean mute);

    void skipVideo();

    void closeEndCard();

    void closeSelf();

    void playAd();

    void pause();

    void resume();

    void dismiss();

    void destroy();

    boolean adFinishedPlaying();

    boolean isRewarded();

    boolean isVideoVisible();

    void setVideoVisible(boolean isVisible);
}
