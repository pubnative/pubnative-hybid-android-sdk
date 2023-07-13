package net.pubnative.lite.sdk.vpaid.widget;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.pubnative.lite.sdk.core.R;

public class PieChartCountdownView extends CountDownView {

    private ProgressBar progressBarView;
    private TextView progressTextView;
    private boolean isBackgroundOn = false;

    public PieChartCountdownView(Context context) {
        super(context);
        init(context);
    }

    public PieChartCountdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PieChartCountdownView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context ctx) {
        View rootView = inflate(ctx, R.layout.player_count_down, this);
        progressBarView = rootView.findViewById(R.id.view_progress_bar);
        progressTextView = rootView.findViewById(R.id.view_progress_text);
        RotateAnimation makeVertical = new RotateAnimation(0, -90, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        makeVertical.setFillAfter(true);
        progressBarView.startAnimation(makeVertical);
    }

    public void setProgress(int currentMs, int totalMs) {
        initBackground();
        progressBarView.setMax(totalMs);
        progressBarView.setSecondaryProgress(totalMs);
        progressBarView.setProgress(currentMs);
        int remainSec = (totalMs - currentMs) / 1000 + 1;
        progressTextView.setText(String.valueOf(remainSec));
    }

    private void initBackground() {
        if (!isBackgroundOn) {
            isBackgroundOn = true;
            progressBarView.setBackground(getResources().getDrawable(R.drawable.circle_progress_background));
        }
    }
}
