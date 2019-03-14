package net.pubnative.lite.sdk.vpaid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class VideoBannerView extends FrameLayout {

    private VisibilityListener mVisibilityListener;

    public interface VisibilityListener {
        void onVisibilityChanged(int visibility);
    }

    public VideoBannerView(Context context) {
        super(context);
    }

    public VideoBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoBannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (mVisibilityListener != null) {
            mVisibilityListener.onVisibilityChanged(visibility);
        }
    }

    public void setVisibilityListener(VisibilityListener visibilityListener) {
        if (visibilityListener != null) {
            this.mVisibilityListener = visibilityListener;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mVisibilityListener != null) {
            mVisibilityListener.onVisibilityChanged(INVISIBLE);
        }
    }

}
