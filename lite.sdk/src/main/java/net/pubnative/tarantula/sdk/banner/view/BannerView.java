package net.pubnative.tarantula.sdk.banner.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import net.pubnative.tarantula.sdk.banner.controller.BannerController;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class BannerView extends FrameLayout {
    public interface Listener {
        void onBannerLoaded(BannerView bannerAdView);

        void onBannerClicked(BannerView bannerAdView);

        void onBannerError(BannerView bannerAdView);
    }

    private final BannerController mBannerController;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBannerController = new BannerController(context);

        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
    }

    public void setListener(Listener listener) {
        mBannerController.setListener(listener);
    }

    public void load(String zoneId) {
        mBannerController.load(zoneId, this);
    }

    public void destroy() {
        removeAllViews();
        mBannerController.destroy();
    }
}
