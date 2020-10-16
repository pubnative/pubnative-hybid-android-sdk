package net.pubnative.lite.sdk.rewarded.presenter;
import net.pubnative.lite.sdk.models.Ad;

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

    Ad getAd();

    void load();

    void show();

    boolean isReady();

    void destroy();
}
