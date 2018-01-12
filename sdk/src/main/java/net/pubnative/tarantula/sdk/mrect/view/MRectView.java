package net.pubnative.tarantula.sdk.mrect.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import net.pubnative.tarantula.sdk.mrect.controller.MRectController;

/**
 * Created by erosgarciaponte on 12.01.18.
 */

public class MRectView extends FrameLayout {
    public interface Listener {
        void onMRectLoaded(@NonNull MRectView mRectAdView);

        void onMRectClicked(@NonNull MRectView mRectAdView);

        void onMRectError(@NonNull MRectView mRectAdView);
    }

    @NonNull
    private final MRectController mMRectController;

    public MRectView(@NonNull Context context) {
        this(context, null);
    }

    public MRectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mMRectController = new MRectController(context);

        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
    }

    public void setListener(@Nullable Listener listener) {
        mMRectController.setListener(listener);
    }

    public void load(@NonNull String zoneId) {
        mMRectController.load(zoneId, this);
    }

    public void destroy() {
        removeAllViews();
        mMRectController.destroy();
    }
}
