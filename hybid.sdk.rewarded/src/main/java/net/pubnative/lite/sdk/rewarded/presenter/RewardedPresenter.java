// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.presenter;

import net.pubnative.lite.sdk.CustomEndCardListener;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.models.Ad;

import org.json.JSONObject;

public interface RewardedPresenter {
    interface Listener {
        void onRewardedLoaded(RewardedPresenter rewardedPresenter);

        void onRewardedOpened(RewardedPresenter rewardedPresenter);

        void onRewardedClicked(RewardedPresenter rewardedPresenter);

        void onRewardedClosed(RewardedPresenter rewardedPresenter);

        void onRewardedFinished(RewardedPresenter rewardedPresenter);

        void onRewardedError(RewardedPresenter rewardedPresenter);
    }

    void setListener(RewardedPresenter.Listener listener);

    void setVideoListener(VideoListener listener);

    void setCustomEndCardListener(CustomEndCardListener listener);

    Ad getAd();

    void load();

    void show();

    boolean isReady();

    void destroy();

    JSONObject getPlacementParams();
}
