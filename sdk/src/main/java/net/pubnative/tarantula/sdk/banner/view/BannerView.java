package net.pubnative.tarantula.sdk.banner.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import net.pubnative.tarantula.sdk.banner.controller.BannerController;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class BannerView extends FrameLayout {
    public interface Listener {
        void onBannerLoaded(@NonNull BannerView bannerAdView);

        void onBannerClicked(@NonNull BannerView bannerAdView);

        void onBannerError(@NonNull BannerView bannerAdView);
    }

    @NonNull
    private final BannerController mBannerController;

    public BannerView(@NonNull Context context) {
        this(context, null);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mBannerController = new BannerController(context);

        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
    }

    public void setListener(@Nullable Listener listener) {
        mBannerController.setListener(listener);
    }

    public void load(@NonNull String zoneId) {
        mBannerController.load(zoneId, this);
    }

    public void destroy() {
        removeAllViews();
        mBannerController.destroy();
    }
}
