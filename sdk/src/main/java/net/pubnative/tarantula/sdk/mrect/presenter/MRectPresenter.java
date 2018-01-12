package net.pubnative.tarantula.sdk.mrect.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import net.pubnative.tarantula.sdk.models.Ad;

/**
 * Created by erosgarciaponte on 12.01.18.
 */

public interface MRectPresenter {
    interface Listener {
        void onMRectLoaded(@NonNull MRectPresenter mRectPresenter, @NonNull View mRect);

        void onMRectClicked(@NonNull MRectPresenter mRectPresenter);

        void onMRectError(@NonNull MRectPresenter mRectPresenter);
    }

    void setListener(@Nullable Listener listener);

    @NonNull
    Ad getAd();

    void load();

    void destroy();
}
