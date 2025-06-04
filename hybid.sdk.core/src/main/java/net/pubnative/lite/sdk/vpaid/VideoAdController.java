// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid;

import android.view.View;

import com.iab.omid.library.pubnativenet.adsession.FriendlyObstructionPurpose;

import net.pubnative.lite.sdk.viewability.HyBidViewabilityFriendlyObstruction;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityNativeVideoAdSession;
import net.pubnative.lite.sdk.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.response.AdParams;

import java.util.List;

public interface VideoAdController {

    void resumeEndCardCloseButtonTimer();

    void pauseEndCardCloseButtonTimer();

    void onEndCardClosed(Boolean isCustomEndCard);

    interface OnPreparedListener {
        void onPrepared();
    }

    void prepare(OnPreparedListener listener);

    void setVideoFilePath(String filePath);

    void addEndCardData(EndCardData endCardData);

    void setEndCardFilePath(String filePath);

    void buildVideoAdView(VideoAdView bannerView);

    void openUrl(String url, Boolean isCustomEndCard, Boolean isCTAClick);

    void onCustomEndCardShow(String endCardType);

    void onDefaultEndCardShow(String endCardType);

    void onCustomEndCardClick(String endCardType);

    void onCustomCTAShow();

    void onCustomCTAClick(boolean isEndcardVisible);

    void onCustomCTALoadFail();

    void onDefaultEndCardClick(String endCardType);

    void onEndCardLoadSuccess(Boolean isCustomEndCard);

    void onEndCardLoadFail(Boolean isCustomEndCard);

    AdParams getAdParams();

    HyBidViewabilityNativeVideoAdSession getViewabilityAdSession();

    void addViewabilityFriendlyObstruction(View view, FriendlyObstructionPurpose purpose, String reason);

    List<HyBidViewabilityFriendlyObstruction> getViewabilityFriendlyObstructions();

    int getProgress();

    void toggleMute();

    void setVolume(boolean mute);

    void skipVideo();

    void skipEndCard();

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
