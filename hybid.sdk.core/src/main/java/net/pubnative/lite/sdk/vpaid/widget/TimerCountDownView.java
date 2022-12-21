package net.pubnative.lite.sdk.vpaid.widget;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.pubnative.lite.sdk.core.R;

public class TimerCountDownView extends CountDownView {

    private TextView progressTextView;
    private RelativeLayout timerContainer;

    public TimerCountDownView(Context context) {
        super(context);
        init(context);
    }

    public TimerCountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimerCountDownView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context ctx) {
        View rootView = inflate(ctx, R.layout.timer_count_down, this);
        progressTextView = rootView.findViewById(R.id.view_progress_text);
        timerContainer = rootView.findViewById(R.id.timer_container);
    }

    @Override
    public void setProgress(int currentMs, int totalMs) {
        if (timerContainer.getVisibility() == View.GONE) {
            timerContainer.setVisibility(View.VISIBLE);
        }
        int remainSec = (totalMs - currentMs) / 1000;
        int minutes = remainSec / 60;
        int seconds = remainSec % 60;
        String minutesText = (minutes >= 10) ? Integer.toString(minutes) : "0" + minutes;
        String secondsText = (seconds >= 10) ? Integer.toString(seconds) : "0" + seconds;
        String result = minutesText + ":" + secondsText;
        progressTextView.setText(result);
    }
}
