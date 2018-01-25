package net.pubnative.tarantula.sdk.banner.presenter;

import android.view.View;

import net.pubnative.tarantula.sdk.models.Ad;

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
}
