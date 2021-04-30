package net.pubnative.lite.sdk.vpaid.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.pubnative.lite.sdk.core.R;
import net.pubnative.lite.sdk.utils.ProgressTimeFormatter;

public class LinearCountDownView extends FrameLayout {

    private ProgressBar progressBarView;
    private TextView progressTextView;

    public LinearCountDownView(Context context) {
        super(context);
        init(context);
    }

    public LinearCountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LinearCountDownView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context ctx) {
        View rootView = inflate(ctx, R.layout.linear_player_count_down, this);
        progressBarView = rootView.findViewById(R.id.view_progress_bar);
        progressTextView = rootView.findViewById(R.id.view_progress_text);
    }

    public void setProgress(int currentMs, int totalMs) {
        //It is happening because of timer issue with Android version 9
        progressBarView.setMax(totalMs);
        progressBarView.setSecondaryProgress(totalMs);
        int remainSec;
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.P) {
            progressBarView.setProgress(currentMs);
            remainSec = (totalMs - currentMs) / 1000 + 1;
        } else {
            progressBarView.setProgress(currentMs + 2000);
            remainSec = (totalMs - currentMs) / 1000 - 1;
        }
        progressTextView.setText(ProgressTimeFormatter.formatSeconds(remainSec));
    }

    public void reset() {
        progressTextView.setText(ProgressTimeFormatter.formatSeconds(0));
    }
}