package net.pubnative.lite.sdk.banner.presenter;

import android.view.View;

import net.pubnative.lite.sdk.models.Ad;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public interface BannerPresenter {
    interface Listener {
        void onBannerLoaded(BannerPresenter bannerPresenter, View banner);

        void onBannerClicked(BannerPresenter bannerPresenter);

        void onBannerError(BannerPresenter bannerPresenter);
    }

    void setListener(Listener listener);

    Ad getAd();

    void load();

    void destroy();

    void startTracking();

    void stopTracking();
}
