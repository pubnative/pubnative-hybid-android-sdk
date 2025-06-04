// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.pubnative.lite.sdk.core.R;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public abstract class CountDownView extends FrameLayout {

    public CountDownView(Context context) {
        super(context);
    }

    public CountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CountDownView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public abstract void setProgress(int currentMs, int totalMs);
}
