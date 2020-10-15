package net.pubnative.lite.sdk.rewarded.presenter;
import net.pubnative.lite.sdk.models.Ad;

public interface RewardedVideoPresenter {
    interface Listener {
        void onRewardedLoaded(RewardedVideoPresenter interstitialPresenter);

        void onRewardedOpened(RewardedVideoPresenter interstitialPresenter);

        void onRewardedClicked(RewardedVideoPresenter interstitialPresenter);

        void onRewardedClosed(RewardedVideoPresenter interstitialPresenter);

        void onRewardedFinished(RewardedVideoPresenter interstitialPresenter);

        void onRewardedError(RewardedVideoPresenter interstitialPresenter);
    }

    void setListener(RewardedVideoPresenter.Listener listener);

    Ad getAd();

    void load();

    void show();

    boolean isReady();

    void destroy();
}
