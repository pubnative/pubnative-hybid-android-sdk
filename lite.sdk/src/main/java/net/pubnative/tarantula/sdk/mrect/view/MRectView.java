package net.pubnative.tarantula.sdk.mrect.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import net.pubnative.tarantula.sdk.mrect.controller.MRectController;

/**
 * Created by erosgarciaponte on 12.01.18.
 */

public class MRectView extends FrameLayout {
    public interface Listener {
        void onMRectLoaded(MRectView mRectAdView);

        void onMRectClicked(MRectView mRectAdView);

        void onMRectError(MRectView mRectAdView);
    }

    private final MRectController mMRectController;

    public MRectView(Context context) {
        this(context, null);
    }

    public MRectView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mMRectController = new MRectController(context);

        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
    }

    public void setListener(Listener listener) {
        mMRectController.setListener(listener);
    }

    public void load(String zoneId) {
        mMRectController.load(zoneId, this);
    }

    public void destroy() {
        removeAllViews();
        mMRectController.destroy();
    }
}
