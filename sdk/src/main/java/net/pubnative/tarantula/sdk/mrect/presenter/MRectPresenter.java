package net.pubnative.tarantula.sdk.mrect.presenter;

import android.view.View;

import net.pubnative.tarantula.sdk.models.Ad;

/**
 * Created by erosgarciaponte on 12.01.18.
 */

public interface MRectPresenter {
    interface Listener {
        void onMRectLoaded(MRectPresenter mRectPresenter, View mRect);

        void onMRectClicked(MRectPresenter mRectPresenter);

        void onMRectError(MRectPresenter mRectPresenter);
    }

    void setListener(Listener listener);

    Ad getAd();

    void load();

    void destroy();
}
